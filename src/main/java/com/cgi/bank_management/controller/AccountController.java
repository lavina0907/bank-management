package com.cgi.bank_management.controller;

import com.cgi.bank_management.dto.AccountDto;
import com.cgi.bank_management.dto.BalanceInfo;
import com.cgi.bank_management.dto.CustomerEmail;
import com.cgi.bank_management.dto.ResponseDto;
import com.cgi.bank_management.model.Account;
import com.cgi.bank_management.service.AccountService;
import com.cgi.bank_management.utility.ResponseStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/account")
@AllArgsConstructor
public class AccountController {

  private AccountService accountService;

  @PostMapping(value = "create")
  public ResponseEntity<ResponseDto> createAccount(@RequestBody @Valid AccountDto accountDto) {
    Account account = accountService.createAccount(accountDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(
        ResponseStatus.SUCCESS.toString(),
        String.format("Customer account created successfully, account Id : %s and currency : %s",
            account.getAccountId(), accountDto.getBalance().getCurrency())));
  }

  @PutMapping(value = "creditBalance")
  public ResponseEntity<ResponseDto> creditBalance(@RequestBody @Valid AccountDto accountDto) {
    accountService.creditBalance(accountDto);
    return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(
        ResponseStatus.SUCCESS.toString(), "Amount credited successfully"));
  }

  @PutMapping(value = "debitBalance")
  public ResponseEntity<ResponseDto> debitBalance(@RequestBody @Valid AccountDto accountDto) {
    accountService.debitBalance(accountDto);
    return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(
        ResponseStatus.SUCCESS.toString(), "Amount debited successfully"));
  }

  @GetMapping(value = "getBalance")
  public ResponseEntity<BalanceInfo> getBalance(@RequestBody @Valid CustomerEmail customerEmail) {
    return ResponseEntity.status(HttpStatus.OK).body(accountService.getBalance(customerEmail));
  }
}
