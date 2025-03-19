package com.cgi.bank_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cgi.bank_management.dto.CustomerInfoDto;
import com.cgi.bank_management.dto.MoneyView;
import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.model.AccountBalance;
import com.cgi.bank_management.model.Customer;
import com.cgi.bank_management.notification.NotificationService;
import com.cgi.bank_management.repository.CustomerRepository;
import com.cgi.bank_management.utility.Currency;
import com.cgi.bank_management.utility.TransactionStatus;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class CustomerServiceTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private AccountService accountService;

  @Mock
  private AccountBalanceService accountBalanceService;

  @Mock
  private TransactionService transactionService;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private CustomerService customerService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldCreateCustomer_WhenCustomerDoesNotExist() {
    // Given
    CustomerInfoDto customerInfoDto = new CustomerInfoDto("John", "Doe", "john.doe@example.com", "1234567890",
        new MoneyView(Currency.USD.name(), new BigDecimal("1000")));
    Customer customer = new Customer(UUID.randomUUID(),"John", "Doe", "john.doe@example.com", "1234567890", Instant.now());
    Account account = new Account();
    AccountBalance accountBalance = new AccountBalance();
    accountBalance.setCurrency(Currency.USD.name());

    when(customerRepository.findCustomerByEmail(customerInfoDto.getEmail())).thenReturn(Optional.empty());
    when(customerRepository.save(any(Customer.class))).thenReturn(customer);
    when(accountService.enrich(any(Customer.class))).thenReturn(account);
    when(accountBalanceService.enrich(any(), any())).thenReturn(accountBalance);
    when(notificationService.sendNotification(any(), any(), any()))
        .thenReturn(CompletableFuture.completedFuture(null));

    // When
    Customer createdCustomer = customerService.enrich(customerInfoDto);

    // Then
    assertNotNull(createdCustomer);
    assertEquals("john.doe@example.com", createdCustomer.getEmail());
    verify(customerRepository, times(1)).save(any(Customer.class));
    verify(accountService, times(1)).enrich(any(Customer.class));
    verify(accountBalanceService, times(1)).enrich(any(), any());
    verify(transactionService, times(1)).enrichCredit(eq(account), eq(Currency.USD), eq(new BigDecimal("1000")), eq(TransactionStatus.COMPLETED));
    verify(notificationService, times(1)).sendNotification(eq("john.doe@example.com"), eq(NotificationService.NotificationType.USER_CREATED), any(Map.class));
  }

  @Test
  void shouldThrowException_WhenCustomerAlreadyExists() {
    // Given
    CustomerInfoDto customerInfoDto = new CustomerInfoDto("John", "Doe", "john.doe@example.com", "1234567890",
        new MoneyView(Currency.USD.name(), new BigDecimal("1000")));
    Customer existingCustomer = new Customer(UUID.randomUUID(),"John", "Doe", "john.doe@example.com", "1234567890", Instant.now());

    when(customerRepository.findCustomerByEmail(customerInfoDto.getEmail())).thenReturn(Optional.of(existingCustomer));

    // When & Then
    assertThrows(DataIntegrityViolationException.class, () -> customerService.enrich(customerInfoDto));
    verify(customerRepository, never()).save(any(Customer.class));
    verify(accountService, never()).enrich(any(Customer.class));
    verify(accountBalanceService, never()).enrich(any(), any());
    verify(transactionService, never()).enrichCredit(any(), any(), any(), any());
    verify(notificationService, never()).sendNotification(any(), any(), any());
  }
}
