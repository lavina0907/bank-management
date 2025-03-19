package com.cgi.bank_management.service;

import com.cgi.bank_management.dto.CustomerInfoDto;
import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.model.AccountBalance;
import com.cgi.bank_management.model.Customer;
import com.cgi.bank_management.notification.NotificationService;
import com.cgi.bank_management.notification.NotificationService.NotificationType;
import com.cgi.bank_management.repository.CustomerRepository;
import com.cgi.bank_management.utility.Currency;
import com.cgi.bank_management.utility.TransactionStatus;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
@Slf4j
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final AccountService accountService;
  private final AccountBalanceService accountBalanceService;
  private final TransactionService transactionService;
  private final NotificationService notificationService;

  /**
   * Creates a new customer, assigns an account, and initializes their balance.
   *
   * @param customerInfoDto The DTO containing customer details such as email, phone, name, and initial balance.
   * @return The created Customer entity.
   * @throws DataIntegrityViolationException if a customer with the given email already exists.
   */
  @Transactional
  public Customer enrich(CustomerInfoDto customerInfoDto) {
    customerRepository.findCustomerByEmail(
        customerInfoDto.getEmail()).ifPresent(
            customer -> {
              throw new DataIntegrityViolationException(
                  String.format("Customer already exists with email: %s",
                      customerInfoDto.getEmail()));
            }
    );
    Customer customer = Customer.builder()
        .phone(customerInfoDto.getPhone())
        .email(customerInfoDto.getEmail())
        .firstName(customerInfoDto.getFirstName())
        .lastName(customerInfoDto.getLastName())
        .build();

    Customer createdCustomer = customerRepository.save(customer);
    Account enrichedAccount = accountService.enrich(createdCustomer);
    AccountBalance accountBalance = accountBalanceService.enrich(
        customerInfoDto.getBalance(), enrichedAccount);
    transactionService.enrichCredit(enrichedAccount, Currency.valueOf(accountBalance.getCurrency()),
        customerInfoDto.getBalance().getAmount(),
        TransactionStatus.COMPLETED);
    notificationService.sendNotification(customer.getEmail(), NotificationType.USER_CREATED,
            Map.of("message", "Customer created successfully and balance added"))
        .thenAccept(response -> log.info("Testing code : {}" , response.body()))
        .exceptionally(ex -> {
          throw new RuntimeException();
        });
    return createdCustomer;
  }
}
