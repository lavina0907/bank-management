package com.cgi.bank_management.controller;

import com.cgi.bank_management.dto.ExchangeBalance;
import com.cgi.bank_management.dto.ExchangeRateBalance;
import com.cgi.bank_management.service.AccountBalanceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/account-balance")
@AllArgsConstructor
public class AccountBalanceController {

  private final AccountBalanceService accountBalanceService;

  @GetMapping(value = "getExchangeAmount", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ExchangeRateBalance> getExchangeAmount(@Valid
  @RequestBody ExchangeBalance exchangeBalance) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(accountBalanceService.getExchangeAmount(exchangeBalance));
  }

}
