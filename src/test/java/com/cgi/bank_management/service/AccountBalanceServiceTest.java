package com.cgi.bank_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cgi.bank_management.dto.ExchangeBalance;
import com.cgi.bank_management.dto.ExchangeRateBalance;
import com.cgi.bank_management.dto.MoneyView;
import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.model.AccountBalance;
import com.cgi.bank_management.model.ExchangeRate;
import com.cgi.bank_management.model.ExchangeRateId;
import com.cgi.bank_management.repository.AccountBalanceRepository;
import com.cgi.bank_management.repository.AccountRepository;
import com.cgi.bank_management.repository.CustomerRepository;
import com.cgi.bank_management.repository.ExchangeRateRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

class AccountBalanceServiceTest {

  @Mock
  private AccountBalanceRepository accountBalanceRepository;
  @Mock
  private ExchangeRateRepository exchangeRateRepository;
  @Mock
  private CustomerRepository customerRepository;

  @InjectMocks
  private AccountBalanceService accountBalanceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this); // Initialize the mocks
  }

  @Test
  void testEnrich_Success() {
    // Given
    MoneyView moneyView = new MoneyView("USD", BigDecimal.valueOf(1000));
    Account account = new Account();
    AccountBalance accountBalance = AccountBalance.builder()
        .account(account)
        .balance(moneyView.getAmount())
        .currency(moneyView.getCurrency())
        .build();

    when(accountBalanceRepository.save(any(AccountBalance.class))).thenReturn(accountBalance);

    // When
    AccountBalance result = accountBalanceService.enrich(moneyView, account);

    // Then
    assertNotNull(result);
    assertEquals(moneyView.getAmount(), result.getBalance());
    assertEquals(moneyView.getCurrency(), result.getCurrency());
    verify(accountBalanceRepository, times(1)).save(any(AccountBalance.class));
  }

  // Test getExchangeAmount method - success scenario
  @Test
  void testGetExchangeAmount_Success() {
    // Given
    ExchangeBalance exchangeBalance = new ExchangeBalance("test@example.com", "USD", "EUR",
        BigDecimal.valueOf(1000),
        false);
    ExchangeRate exchangeRate = new ExchangeRate("USD", "EUR", BigDecimal.valueOf(0.85));

    when(exchangeRateRepository.findById(any(ExchangeRateId.class))).thenReturn(
        java.util.Optional.of(exchangeRate));
    when(accountBalanceRepository.findAllByAccount(any(Account.class))).thenReturn(
        java.util.Optional.of(java.util.List.of(new AccountBalance())));

    // When
    ExchangeRateBalance result = accountBalanceService.getExchangeAmount(exchangeBalance);

    // Then
    assertNotNull(result);
    assertEquals("USD", result.getBaseCurrency().getCurrency());
    assertEquals("EUR", result.getTargetCurrency().getCurrency());
    assertEquals(BigDecimal.valueOf(1000).multiply(BigDecimal.valueOf(0.85)),
        result.getTargetCurrency().getAmount());
  }

  // Test getExchangeAmount method - when exchange rate is not found
  @Test
  void testGetExchangeAmount_ExchangeRateNotFound() {
    // Given
    ExchangeBalance exchangeBalance = new ExchangeBalance("test@example.com", "USD", "EUR",
        BigDecimal.valueOf(1000),
        false);

    when(exchangeRateRepository.findById(any(ExchangeRateId.class))).thenReturn(
        java.util.Optional.empty());

    // When & Then
    DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
        () -> {
          accountBalanceService.getExchangeAmount(exchangeBalance);
        });

    assertEquals("Exchange rate not found for base currency: USD and target currency: EUR",
        exception.getMessage());
  }

  // Test getExchangeAmount method - when customer is not found for full balance exchange
  @Test
  void testGetExchangeAmount_CustomerNotFound() {
    // Given
    ExchangeBalance exchangeBalance = new ExchangeBalance("nonexistent@example.com", "USD", "EUR",
        BigDecimal.valueOf(1000),
        true);

    when(customerRepository.findCustomerByEmail(anyString())).thenReturn(
        java.util.Optional.empty());
    when(exchangeRateRepository.findById(any())).thenReturn(Optional.of(new ExchangeRate()));

    // When & Then
    DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
        () -> {
          accountBalanceService.getExchangeAmount(exchangeBalance);
        });

    assertEquals("Customer not found for email: nonexistent@example.com", exception.getMessage());
  }
}