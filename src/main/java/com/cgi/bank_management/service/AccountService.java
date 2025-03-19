package com.cgi.bank_management.service;

import com.cgi.bank_management.dto.AccountDto;
import com.cgi.bank_management.dto.BalanceInfo;
import com.cgi.bank_management.dto.CustomerEmail;
import com.cgi.bank_management.dto.MoneyView;
import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.model.AccountBalance;
import com.cgi.bank_management.model.AccountBalanceId;
import com.cgi.bank_management.model.Customer;
import com.cgi.bank_management.notification.NotificationService;
import com.cgi.bank_management.notification.NotificationService.NotificationType;
import com.cgi.bank_management.repository.AccountBalanceRepository;
import com.cgi.bank_management.repository.AccountRepository;
import com.cgi.bank_management.repository.CustomerRepository;
import com.cgi.bank_management.utility.Currency;
import com.cgi.bank_management.utility.TransactionStatus;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountService {

  private final AccountRepository accountRepository;
  private final CustomerRepository customerRepository;
  private final AccountBalanceService accountBalanceService;
  private final AccountBalanceRepository accountBalanceRepository;
  private final TransactionService transactionService;
  private final NotificationService notificationService;

  private final ExecutorService executorService = Executors.newFixedThreadPool(10);
  private final Lock debitBalanceLock = new ReentrantLock();

  @Value("${account.transaction.max-retries}")
  private int maxRetries;

  @PreDestroy
  public void shutdownExecutorService() {
    if (!executorService.isShutdown()) {
      executorService.shutdown();
      log.info("Executor service shut down successfully.");
    }
  }

  /**
   * Creates and saves an account for the given customer.
   *
   * @param customer The customer for whom the account is created.
   * @return The saved Account entity.
   */
  public Account enrich(Customer customer) {
    return accountRepository.save(Account.builder().customer(customer).build());
  }

  /**
   * Creates a new account and initializes its balance.
   *
   * @param accountDto The account details including email and initial balance.
   * @return The created Account entity.
   * @throws DataIntegrityViolationException if the balance already exists.
   */
  @Transactional
  public Account createAccount(@Valid AccountDto accountDto) {
    Customer customer = findCustomerByEmail(accountDto.getEmail());
    Account account = findAccountByCustomer(customer);

    AccountBalanceId balanceKey = new AccountBalanceId(account.getAccountId(),
        accountDto.getBalance().getCurrency());

    if (accountBalanceRepository.existsById(balanceKey)) {
      throw new DataIntegrityViolationException(
          String.format("Account balance already exists for currency: %s",
              accountDto.getBalance().getCurrency()));
    }

    AccountBalance createdBalance = accountBalanceService.enrich(accountDto.getBalance(), account);
    transactionService.enrichCredit(account, Currency.valueOf(createdBalance.getCurrency()),
        accountDto.getBalance().getAmount(), TransactionStatus.COMPLETED);

    sendNotification(customer.getEmail(), NotificationType.ACCOUNT_CREATED,
        String.format("%s balance account created and balance added",
            createdBalance.getCurrency()));

    return account;
  }

  /**
   * Credits balance to an account asynchronously.
   *
   * @param accountDto The account and balance details.
   */
  @Transactional
  public void creditBalance(@Valid AccountDto accountDto) {
    executorService.submit(() -> retryDeposit(accountDto, maxRetries));
  }

  private void retryDeposit(@Valid AccountDto accountDto, int retries) {
    for (int i = 0; i < retries; i++) {
      try {
        processDeposit(accountDto);
        return;
      } catch (ObjectOptimisticLockingFailureException e) {
        if (i == retries - 1) {
          throw e;
        }
      }
    }
  }

  private void processDeposit(AccountDto accountDto) {
    AccountBalance currentBalance = findAccountBalance(accountDto);
    currentBalance.setBalance(currentBalance.getBalance().add(accountDto.getBalance().getAmount()));
    accountBalanceRepository.save(currentBalance);

    transactionService.enrichCredit(currentBalance.getAccount(),
        Currency.valueOf(currentBalance.getCurrency()), accountDto.getBalance().getAmount(),
        TransactionStatus.COMPLETED);

    sendNotification(accountDto.getEmail(), NotificationType.BALANCE_CREDIT,
        String.format("%s balance added with amount : %s", currentBalance.getCurrency(),
            accountDto.getBalance().getAmount()));
  }

  /**
   * Debit balance to an account.
   *
   * @param accountDto The account and balance details.
   */
  @Transactional
  public void debitBalance(@Valid AccountDto accountDto) {
    debitBalanceLock.lock();
    try {
      AccountBalance currentBalance = findAccountBalance(accountDto);

      if (currentBalance.getBalance().compareTo(accountDto.getBalance().getAmount()) < 0) {
        throw new com.cgi.bank_management.execption.InsufficientBalanceException(
            String.format("Insufficient balance in %s currency",
                accountDto.getBalance().getCurrency()));
      }

      currentBalance.setBalance(
          currentBalance.getBalance().subtract(accountDto.getBalance().getAmount()));
      accountBalanceRepository.save(currentBalance);

      transactionService.enrichDebit(currentBalance.getAccount(),
          Currency.valueOf(currentBalance.getCurrency()), accountDto.getBalance().getAmount(),
          TransactionStatus.COMPLETED);

      sendNotification(accountDto.getEmail(), NotificationType.BALANCE_DEBIT,
          String.format("%s balance debited with amount : %s", currentBalance.getCurrency(),
              accountDto.getBalance().getAmount()));

    } finally {
      debitBalanceLock.unlock();
    }
  }

  private Customer findCustomerByEmail(String email) {
    return customerRepository.findCustomerByEmail(email)
        .orElseThrow(() -> new DataIntegrityViolationException(
            String.format("Customer not found with Email ID: %s", email)));
  }

  private Account findAccountByCustomer(Customer customer) {
    return accountRepository.findAccountByCustomer(customer)
        .orElseThrow(() -> new DataIntegrityViolationException(
            String.format("Customer account not found with email ID: %s",
                customer.getCustomerId())));
  }

  private AccountBalance findAccountBalance(AccountDto accountDto) {
    Account account = findAccountByCustomer(findCustomerByEmail(accountDto.getEmail()));
    AccountBalanceId balanceKey = new AccountBalanceId(account.getAccountId(),
        accountDto.getBalance().getCurrency());

    return accountBalanceRepository.findById(balanceKey)
        .orElseThrow(() -> new DataIntegrityViolationException(
            String.format("Account balance not found for currency: %s",
                accountDto.getBalance().getCurrency())));
  }

  /**
   * Retrieves the balance information for a given customer.
   *
   * @param customerEmail The customer's email.
   * @return The balance information.
   * @throws DataIntegrityViolationException if balance is not found.
   */
  public BalanceInfo getBalance(CustomerEmail customerEmail) {
    Customer customer = findCustomerByEmail(customerEmail.getEmail());
    Account account = findAccountByCustomer(customer);

    List<AccountBalance> balances = accountBalanceRepository.findAllByAccount(account)
        .orElseThrow(() -> new DataIntegrityViolationException("Account balance not created"));

    List<MoneyView> availableBalancesMoney = balances.parallelStream()
        .map(balance -> new MoneyView(balance.getCurrency(), balance.getBalance()))
        .toList();

    return new BalanceInfo(availableBalancesMoney);
  }

  private void sendNotification(String email, NotificationType type, String message) {
    notificationService.sendNotification(email, type, Map.of("message", message))
        .thenAccept(response -> log.info("Notification sent: {}", response.body()))
        .exceptionally(ex -> {
          log.error("Error sending notification", ex);
          throw new RuntimeException("Error sending notification", ex);
        });
  }
}
