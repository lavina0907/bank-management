package com.cgi.bank_management.controller;

import com.cgi.bank_management.dto.CustomerInfoDto;
import com.cgi.bank_management.dto.ResponseDto;
import com.cgi.bank_management.model.Customer;
import com.cgi.bank_management.service.CustomerService;
import com.cgi.bank_management.utility.ResponseStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/customer")
@AllArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping(value = "create")
  public ResponseEntity<ResponseDto> create(@Valid @RequestBody CustomerInfoDto request) {
    Customer createdCustomer = customerService.enrich(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(
        ResponseStatus.SUCCESS.toString(),
        String.format("Customer created successfully, Customer Id : %s",
            createdCustomer.getCustomerId())));
  }
}
