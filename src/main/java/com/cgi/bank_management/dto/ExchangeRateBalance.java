package com.cgi.bank_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRateBalance {
  private MoneyView baseCurrency;
  private MoneyView targetCurrency;
}
