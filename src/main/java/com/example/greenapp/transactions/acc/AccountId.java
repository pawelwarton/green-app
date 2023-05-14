package com.example.greenapp.transactions.acc;

import static com.example.greenapp.transactions.Const.ACCOUNT_LEN;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Way more performant representation of an account identifier, than a String.
 */
public class AccountId implements Comparable<AccountId> {
  static final int LEFT_DIGIT_COUNT = 18;
  static final int RIGHT_DIGIT_COUNT = ACCOUNT_LEN - LEFT_DIGIT_COUNT;

  static {
    if (ACCOUNT_LEN < 19 || 27 < ACCOUNT_LEN) {
      throw new LinkageError("Account ID assumptions broken");
    }
  }

  /**
   * Digits 1-18 coded as a number
   */
  long left;
  /**
   * Digits 19-26 coded as a number
   */
  int right;

  public AccountId() {}

  public AccountId(long left, int right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public int compareTo(AccountId other) {
    if (this.left == other.left) {
      return this.right - other.right;
    }
    // We cannot just substract and cast
    return this.left < other.left ? -1 : 1;
  }

  @Override
  public int hashCode() {
    int result = (int) (left ^ (left >>> 32));
    result = 31 * result + right;
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AccountId other = (AccountId) o;
    return left == other.left && right == other.right;
  }

  @Override
  public String toString() {
    var digits = new byte[ACCOUNT_LEN];
    var left = this.left;
    for (int i = LEFT_DIGIT_COUNT - 1; i >= 0; i--) {
      digits[i] = (byte) ((left % 10L) + '0');
      left /= 10L;
    }
    var right = this.right;
    for (int i = RIGHT_DIGIT_COUNT - 1; i >= 0; i--) {
      digits[LEFT_DIGIT_COUNT + i] = (byte) ((right % 10) + '0');
      right /= 10;
    }
    return new String(digits, UTF_8);
  }
}
