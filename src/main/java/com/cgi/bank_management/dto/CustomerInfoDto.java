package com.cgi.bank_management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CustomerInfoDto {

  @NotBlank(message = "First name is required")
  @Size(max = 100, message = "First name cannot exceed 100 characters")
  private String firstName;

  @NotBlank(message = "First name is required")
  @Size(max = 100, message = "First name cannot exceed 100 characters")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10-15 digits")
  private String phone;

  @NotNull(message = "Balance required to create customer")
  private @Valid MoneyView balance;
}
