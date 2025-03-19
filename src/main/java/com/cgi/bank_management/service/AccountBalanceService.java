package com.cgi.bank_management.service;

import com.cgi.bank_management.dto.MoneyView;
import com.cgi.bank_management.dto.ExchangeBalance;
import com.cgi.bank_management.dto.ExchangeRateBalance;
import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.model.AccountBalance;
import com.cgi.bank_management.model.Customer;
import com.cgi.bank_management.model.ExchangeRate;
import com.cgi.bank_management.model.ExchangeRateId;
import com.cgi.bank_management.repository.AccountBalanceRepository;
import com.cgi.bank_management.repository.AccountRepository;
import com.cgi.bank_management.repository.CustomerRepository;
import com.cgi.bank_management.repository.ExchangeRateRepository;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountBalanceService {

  private final AccountBalanceRepository accountBalanceRepository;
  private final ExchangeRateRepository exchangeRateRepository;
  private final CustomerRepository customerRepository;
  private final AccountRepository accountRepository;

  public AccountBalance enrich(MoneyView moneyView, Account account) {
    AccountBalance accountBalance = AccountBalance.builder()
        .account(account)
        .balance(moneyView.getAmount())
        .currency(moneyView.getCurrency())
        .build();
    return accountBalanceRepository.save(accountBalance);
  }

  public ExchangeRateBalance getExchangeAmount(ExchangeBalance exchangeBalance) {
    ExchangeRateId exchangeRateId = new ExchangeRateId(
        exchangeBalance.getBaseCurrency(), exchangeBalance.getTargetCurrency());

    ExchangeRate rate = exchangeRateRepository.findById(exchangeRateId)
        .orElseThrow(() -> new DataIntegrityViolationException(
            "Exchange rate not found for base currency: %s and target currency: %s"
                .formatted(exchangeBalance.getBaseCurrency(),
                    exchangeBalance.getTargetCurrency())));

    BigDecimal baseAmount = exchangeBalance.getIsExchangeFullBalance()
        ? getFullBalance(exchangeBalance)
        : exchangeBalance.getAmount();

    return createExchangeRateBalance(exchangeBalance, baseAmount, rate.getRate());
  }

  private BigDecimal getFullBalance(ExchangeBalance exchangeBalance) {
    Customer customer = customerRepository.findCustomerByEmail(exchangeBalance.getEmail())
        .orElseThrow(() -> new DataIntegrityViolationException(
            "Customer not found for email: %s".formatted(exchangeBalance.getEmail())));

    Account account = accountRepository.findAccountByCustomer(customer)
        .orElseThrow(() -> new DataIntegrityViolationException(
            "Account not found for email: %s".formatted(exchangeBalance.getEmail())));

    return accountBalanceRepository.findAllByAccount(account)
        .orElseThrow(() -> new DataIntegrityViolationException(
            "Balances not found for email: %s".formatted(exchangeBalance.getEmail())))
        .stream()
        .filter(bal -> bal.getCurrency().equals(exchangeBalance.getBaseCurrency()))
        .map(AccountBalance::getBalance)
        .findAny()
        .orElseThrow(() -> new DataIntegrityViolationException(
            "Balance not found for currency: %s".formatted(exchangeBalance.getBaseCurrency())));
  }

  private ExchangeRateBalance createExchangeRateBalance(
      ExchangeBalance exchangeBalance, BigDecimal baseAmount, BigDecimal rate) {

    return new ExchangeRateBalance(
        new MoneyView(exchangeBalance.getBaseCurrency(), baseAmount),
        new MoneyView(exchangeBalance.getTargetCurrency(), baseAmount.multiply(rate))
    );
  }
}
