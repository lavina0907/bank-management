package com.cgi.bank_management.utility;

public enum Currency {
  EUR("EUR", "Euro"),
  USD("USD", "Dollar"),
  SEK("SEK", "Swedish Krona"),
  RUB("RUB", "Russian Ruble");

  private final String code;
  private final String name;

  Currency(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }
}
