package com.cgi.bank_management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDto {

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;
  @NotNull(message = "Balance required to create customer")
  private @Valid MoneyView balance;

}
