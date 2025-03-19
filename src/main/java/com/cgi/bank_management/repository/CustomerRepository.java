package com.cgi.bank_management.repository;

import com.cgi.bank_management.model.Customer;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  List<Customer> findAllByEmailOrPhone(@NotBlank String email, @NotBlank String phone);

  Optional<Customer> findCustomerByEmail(String email);
}
