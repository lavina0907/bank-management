package com.cgi.bank_management.execption;

public class InsufficientBalanceException extends RuntimeException {
  public InsufficientBalanceException(String message) {
    super(message);
  }
}
