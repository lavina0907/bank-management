package com.cgi.bank_management.repository;

import com.cgi.bank_management.model.Transaction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

}
