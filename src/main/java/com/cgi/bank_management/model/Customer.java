package com.cgi.bank_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID customerId;

  @Column(nullable = false, length = 100)
  private String firstName;

  @Column(nullable = false, length = 100)
  private String lastName;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(nullable = false, unique = true, length = 20)
  private String phone;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;
}
