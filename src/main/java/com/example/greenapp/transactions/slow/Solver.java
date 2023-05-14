package com.example.greenapp.transactions.slow;

import com.example.greenapp.algorithms.Algorithms;
import com.example.greenapp.transactions.acc.AccountId;

import java.util.Arrays;
import java.util.HashMap;

public class Solver {

  public Iterable<Account> solve(Iterable<Transaction> transactions) {
    var accounts = new HashMap<AccountId, Account>();
    for (var transaction : transactions) {
      var creditAccount = accounts.computeIfAbsent(transaction.creditAccount, Account::new);
      creditAccount.creditCount += 1;
      creditAccount.balance = creditAccount.balance.add(transaction.amount);
      var debitAccount = accounts.computeIfAbsent(transaction.debitAccount, Account::new);
      debitAccount.debitCount += 1;
      debitAccount.balance = debitAccount.balance.subtract(transaction.amount);
    }
    var accountsArray = accounts.values().toArray(Account[]::new);
    Algorithms.sort(accountsArray, 0, accountsArray.length);
    return Arrays.asList(accountsArray);
  }

}
