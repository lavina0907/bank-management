package com.cgi.bank_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateId implements java.io.Serializable {
  private String baseCurrency;
  private String targetCurrency;
}
