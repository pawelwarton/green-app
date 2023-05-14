package com.example.greenapp.transactions.fast;

import com.example.greenapp.OptimizationAssumptionException;
import com.example.greenapp.transactions.acc.AccountId;

import java.util.Arrays;
import java.util.HashMap;

import static com.example.greenapp.algorithms.Algorithms.sort;
import static java.lang.Math.addExact;
import static java.lang.Math.subtractExact;

public class Solver {

  public Iterable<Account> solve(Iterable<Transaction> transactions) {
    var accounts = new HashMap<AccountId, Account>();
    for (var transaction : transactions) {
      var creditAccount = accounts.computeIfAbsent(transaction.creditAccount, Account::new);
      creditAccount.creditCount += 1;
      var debitAccount = accounts.computeIfAbsent(transaction.debitAccount, Account::new);
      debitAccount.debitCount += 1;
      try {
        creditAccount.balance = addExact(creditAccount.balance, transaction.amount);
        debitAccount.balance = subtractExact(debitAccount.balance, transaction.amount);
      } catch (ArithmeticException e) {
        throw new OptimizationAssumptionException(e);
      }
    }
    var accountsArray = accounts.values().toArray(Account[]::new);
    sort(accountsArray, 0, accountsArray.length);
    return Arrays.asList(accountsArray);
  }

}
