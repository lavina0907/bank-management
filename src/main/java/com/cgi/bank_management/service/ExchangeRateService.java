package com.cgi.bank_management.service;

import com.cgi.bank_management.dto.ExchangeCurrencyRate;
import com.cgi.bank_management.model.ExchangeRate;
import com.cgi.bank_management.model.ExchangeRateId;
import com.cgi.bank_management.repository.ExchangeRateRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExchangeRateService {

  private final ExchangeRateRepository exchangeRateRepository;

  /**
   * Creates a new exchange rate entry if it does not already exist.
   *
   * @param exchangeCurrencyRate The exchange rate details including base currency, target currency, and rate.
   * @throws DataIntegrityViolationException if an exchange rate for the given currency pair already exists.
   */
  public void create(ExchangeCurrencyRate exchangeCurrencyRate) {
    ExchangeRateId exchangeRateId = new ExchangeRateId(exchangeCurrencyRate.getBaseCurrency(),
        exchangeCurrencyRate.getTargetCurrency());
    exchangeRateRepository.findById(exchangeRateId).ifPresent(exchangeRate -> {
      throw new DataIntegrityViolationException("Exchange rate already exists");
    });
    ExchangeRate rate = ExchangeRate.builder()
        .baseCurrency(exchangeCurrencyRate.getBaseCurrency())
        .targetCurrency(exchangeCurrencyRate.getTargetCurrency())
        .rate(exchangeCurrencyRate.getExchangeRate())
        .build();
    exchangeRateRepository.save(rate);


  }
}
