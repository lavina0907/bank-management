package com.cgi.bank_management.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceId implements java.io.Serializable {

  private UUID account;
  private String currency;
}
