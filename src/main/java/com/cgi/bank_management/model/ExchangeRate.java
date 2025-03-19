package com.cgi.bank_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exchange_rates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ExchangeRateId.class)
public class ExchangeRate {

  @Id
  @Column(name = "base_currency", nullable = false)
  private String baseCurrency;

  @Id
  @Column(name = "target_currency", nullable = false)
  private String targetCurrency;

  @Column(nullable = false, precision = 10, scale = 4)
  private BigDecimal rate;
}
