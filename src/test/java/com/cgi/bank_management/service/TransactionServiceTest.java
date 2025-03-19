package com.cgi.bank_management.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.model.Transaction;
import com.cgi.bank_management.repository.TransactionRepository;
import com.cgi.bank_management.utility.Currency;
import com.cgi.bank_management.utility.TransactionStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionServiceTest {

  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private TransactionService transactionService;

  private Account mockAccount;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockAccount = new Account();  // Creating a mock Account object
  }

  @Test
  void shouldSaveCreditTransaction() {
    // Given
    BigDecimal amount = new BigDecimal("100.00");
    Currency currency = Currency.USD;
    TransactionStatus status = TransactionStatus.COMPLETED;

    // When
    transactionService.enrichCredit(mockAccount, currency, amount, status);

    // Then
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }

  @Test
  void shouldSaveDebitTransaction() {
    // Given
    BigDecimal amount = new BigDecimal("50.00");
    Currency currency = Currency.EUR;
    TransactionStatus status = TransactionStatus.PENDING;

    // When
    transactionService.enrichDebit(mockAccount, currency, amount, status);

    // Then
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }
}
