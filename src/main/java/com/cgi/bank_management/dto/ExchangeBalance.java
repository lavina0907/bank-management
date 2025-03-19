package com.cgi.bank_management.dto;

import com.cgi.bank_management.annotation.EnumValue;
import com.cgi.bank_management.utility.Currency;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeBalance {

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;
  @NotNull(message = "Account currency cannot be null")
  @EnumValue(enumClass = Currency.class, message = "Currency not supported")
  private String baseCurrency;
  @NotNull(message = "Account currency cannot be null")
  @EnumValue(enumClass = Currency.class, message = "Currency not supported")
  private String targetCurrency;
  @Min(value = 0, message = "Balance must be 0 or greater")
  private BigDecimal amount;
  private Boolean isExchangeFullBalance;

}
