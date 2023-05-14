package com.example.greenapp.transactions.fast;

import com.example.greenapp.transactions.acc.AccountId;

public class Account implements Comparable<Account> {
  final AccountId account;
  int debitCount;
  int creditCount;
  /**
   * @see Transaction#amount
   */
  long balance;

  public Account(AccountId account) {
    this.account = account;
  }

  public AccountId getAccount() {
    return account;
  }

  public int getDebitCount() {
    return debitCount;
  }

  public int getCreditCount() {
    return creditCount;
  }

  public long getBalance() {
    return balance;
  }

  @Override
  public int compareTo(Account o) {
    return this.account.compareTo(o.account);
  }

  @Override
  public int hashCode() {
    return account.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    var other = (Account) obj;
    return account.equals(other.account);
  }

}
