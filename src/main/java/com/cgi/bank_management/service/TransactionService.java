package com.cgi.bank_management.service;

import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.model.Transaction;
import com.cgi.bank_management.repository.TransactionRepository;
import com.cgi.bank_management.utility.Currency;
import com.cgi.bank_management.utility.TransactionStatus;
import com.cgi.bank_management.utility.TransactionType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;

  /**
   * Creates and saves a credit transaction for the given account.
   *
   * @param account          The account to which the credit transaction is applied.
   * @param currency         The currency of the transaction.
   * @param amount           The amount to be credited.
   * @param transactionStatus The status of the transaction.
   */
  public void enrichCredit(Account account, Currency currency, BigDecimal amount, TransactionStatus transactionStatus) {
    Transaction creditTransaction = Transaction.builder()
        .transactionType(TransactionType.CREDIT.name())
        .account(account)
        .amount(amount)
        .currency(currency.getCode())
        .status(transactionStatus.name())
        .build() ;
    transactionRepository.save(creditTransaction);
  }

  /**
   * Creates and saves a debit transaction for the given account.
   *
   * @param account          The account to which the debit transaction is applied.
   * @param currency         The currency of the transaction.
   * @param amount           The amount to be credited.
   * @param transactionStatus The status of the transaction.
   */
  public void enrichDebit(Account account, Currency currency, BigDecimal amount, TransactionStatus transactionStatus) {
    Transaction debitTransaction = Transaction.builder()
        .transactionType(TransactionType.DEBIT.name())
        .account(account)
        .amount(amount)
        .currency(currency.getCode())
        .status(transactionStatus.name())
        .build() ;
    transactionRepository.save(debitTransaction);
  }
}
