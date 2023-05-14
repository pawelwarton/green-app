package com.example.greenapp.transactions.fast;

import com.example.greenapp.transactions.acc.AccountId;

public class Transaction {

  AccountId debitAccount;
  AccountId creditAccount;
  /**
   * Number of grosz.
   * All real values, unless we get an inflication like they had in Zimbabwe,
   * will fit no problem.
   * <br>
   * For a long to overflow on grosz, the balance would need to get above
   * 92,233,720,368,547,758 PLN which is way more than there is PLN in the
   * whole world.
   */
  long amount;

  public Transaction() {
  }

  public Transaction(AccountId debitAccount, AccountId creditAccount, long amount) {
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

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

}
