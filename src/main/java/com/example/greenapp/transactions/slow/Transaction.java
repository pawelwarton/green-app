package com.example.greenapp.transactions.slow;

import com.example.greenapp.transactions.acc.AccountId;

import java.math.BigDecimal;

public class Transaction {

  AccountId debitAccount;
  AccountId creditAccount;
  BigDecimal amount;

  public Transaction() {
  }

  public Transaction(AccountId debitAccount, AccountId creditAccount, BigDecimal amount) {
    this.debitAccount = debitAccount;
    this.creditAccount = creditAccount;
    this.amount = amount;
  }

  public AccountId getDebitAccount() {
    return debitAccount;
  }

  public void setDebitAccount(AccountId debitAccount) {
    this.debitAccount = debitAccount;
  }

  public AccountId getCreditAccount() {
    return creditAccount;
  }

  public void setCreditAccount(AccountId creditAccount) {
    this.creditAccount = creditAccount;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

}
