package com.example.greenapp.transactions.fast;

import com.example.greenapp.BadRequestException;
import com.example.greenapp.algorithms.ArrayIterator;
import com.example.greenapp.annotations.Unsafe;
import com.example.greenapp.transactions.acc.AccountId;

import java.util.Iterator;

import static com.example.greenapp.transactions.Const.MAX_TRANSACTION_COUNT;

public class Transactions implements Iterable<Transaction> {
  private final Transaction[] transactions = new Transaction[MAX_TRANSACTION_COUNT];
  private int size;

  /**
   * The returned transactions might have old values, so it needs to be fully
   * recycled. That's why this method is package private.
   */
  @Unsafe
  Transaction next() {
    if (size == MAX_TRANSACTION_COUNT) {
      throw new BadRequestException("Too many transactions");
    }
    var transaction = transactions[size++];
    if (transaction == null) {
      transaction = transactions[size - 1] = new Transaction();
      transaction.creditAccount = new AccountId();
      transaction.debitAccount = new AccountId();
    }
    return transaction;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void clear() {
    size = 0;
  }

  @Override
  public Iterator<Transaction> iterator() {
    return new ArrayIterator<>(transactions, 0, size);
  }

}
