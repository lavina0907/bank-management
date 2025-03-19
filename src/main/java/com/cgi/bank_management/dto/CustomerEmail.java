package com.cgi.bank_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerEmail {
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;
}
