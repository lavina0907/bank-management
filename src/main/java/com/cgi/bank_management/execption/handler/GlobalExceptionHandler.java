package com.cgi.bank_management.execption.handler;

import com.cgi.bank_management.dto.ResponseDto;
import com.cgi.bank_management.execption.InsufficientBalanceException;
import com.cgi.bank_management.utility.ResponseStatus;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ResponseDto> handleIntegrityExceptions(
      DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.builder()
        .status(ResponseStatus.FAILURE.name())
        .message(ex.getMessage())
        .build());
  }

  @ExceptionHandler(InsufficientBalanceException.class)
  public ResponseEntity<ResponseDto> handleInsufficientExceptions(
      InsufficientBalanceException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.builder()
        .status(ResponseStatus.FAILURE.name())
        .message(ex.getMessage())
        .build());
  }
}
