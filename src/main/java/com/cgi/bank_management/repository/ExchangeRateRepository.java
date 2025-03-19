package com.cgi.bank_management.repository;

import com.cgi.bank_management.model.ExchangeRate;
import com.cgi.bank_management.model.ExchangeRateId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, ExchangeRateId> {

}
