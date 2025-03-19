package com.cgi.bank_management.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceInfo {
  List<MoneyView> currentBalance;
}
