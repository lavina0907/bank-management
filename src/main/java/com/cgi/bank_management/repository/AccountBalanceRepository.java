package com.cgi.bank_management.repository;

import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.model.AccountBalance;
import com.cgi.bank_management.model.AccountBalanceId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBalanceRepository extends JpaRepository<AccountBalance, AccountBalanceId> {

  Optional<List<AccountBalance>> findAllByAccount(Account account);
}
