package com.cgi.bank_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cgi.bank_management.dto.ExchangeCurrencyRate;
import com.cgi.bank_management.model.ExchangeRate;
import com.cgi.bank_management.model.ExchangeRateId;
import com.cgi.bank_management.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

class ExchangeRateServiceTest {

  @Mock
  private ExchangeRateRepository exchangeRateRepository;

  @InjectMocks
  private ExchangeRateService exchangeRateService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
    void shouldCreateExchangeRate_WhenItDoesNotExist() {
    // Given
    ExchangeCurrencyRate exchangeCurrencyRate = new ExchangeCurrencyRate("USD", "EUR", new BigDecimal("1.1"));
    ExchangeRateId exchangeRateId = new ExchangeRateId("USD", "EUR");

    when(exchangeRateRepository.findById(exchangeRateId)).thenReturn(Optional.empty());

    // When
    exchangeRateService.create(exchangeCurrencyRate);

    // Then
    verify(exchangeRateRepository, times(1)).save(any(ExchangeRate.class));
  }

  @Test
  void shouldThrowException_WhenExchangeRateAlreadyExists() {
    // Given
    ExchangeCurrencyRate exchangeCurrencyRate = new ExchangeCurrencyRate("USD", "EUR", new BigDecimal("1.1"));
    ExchangeRateId exchangeRateId = new ExchangeRateId("USD", "EUR");
    ExchangeRate existingRate = new ExchangeRate("USD", "EUR", new BigDecimal("1.1"));

    when(exchangeRateRepository.findById(exchangeRateId)).thenReturn(Optional.of(existingRate));

    // When & Then
    assertThrows(DataIntegrityViolationException.class, () -> exchangeRateService.create(exchangeCurrencyRate));
    verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
  }
}
