package com.cgi.bank_management.dto;

import com.cgi.bank_management.utility.Currency;
import com.cgi.bank_management.annotation.EnumValue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MoneyView {

  @NotNull(message = "Account currency cannot be null")
  @EnumValue(enumClass = Currency.class, message = "Currency not supported")
  private String currency;
  @Min(value = 0, message = "Balance must be 0 or greater")
  private BigDecimal amount;
}
