package com.cgi.bank_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID transactionId;

  @ManyToOne(optional = false)
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @Column(name = "currency_code", nullable = false)
  private String currency;

  @Column(precision = 18, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 10)
  private String transactionType;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private Instant transactionDate;

  @Column(length = 20)
  private String status;
}
