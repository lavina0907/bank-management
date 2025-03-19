package com.cgi.bank_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID accountId;

  @ManyToOne(optional = false)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

}
