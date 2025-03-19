package com.cgi.bank_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_balances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AccountBalanceId.class)
public class AccountBalance {

  @Id
  @ManyToOne(optional = false)
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @Id
  @Column(name = "currency_code", nullable = false)
  private String currency;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal balance;

  @Version
  private Long version;
}
