package com.cgi.bank_management.controller;

import com.cgi.bank_management.dto.ExchangeCurrencyRate;
import com.cgi.bank_management.dto.ResponseDto;
import com.cgi.bank_management.service.ExchangeRateService;
import com.cgi.bank_management.utility.ResponseStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/exchange-rate")
@AllArgsConstructor
public class ExchangeRateController {

  private final ExchangeRateService exchangeRateService;

  @PutMapping(value = "create")
  public ResponseEntity<ResponseDto> create(
      @RequestBody @Valid ExchangeCurrencyRate exchangeCurrencyRate) {
    exchangeRateService.create(exchangeCurrencyRate);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(
        ResponseStatus.SUCCESS.toString(), "Exchange rate added"));
  }
}
