package com.cgi.bank_management.repository;

import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.model.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {

  Optional<Account> findAccountByCustomer(Customer customer);

}
