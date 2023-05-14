package com.example.greenapp.transactions;

import com.example.greenapp.GeneratorUtils;
import com.example.greenapp.transactions.acc.AccountId;
import com.example.greenapp.transactions.slow.Transaction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static com.example.greenapp.transactions.Const.ACCOUNT_LEN;

public class TransactionsGenerator {

  private final Random rng;

  public TransactionsGenerator() {
    this(new Random(65175));
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = """
    Yeah it may. That's kind of the point. We want to allow external code to
    control the rng, so that the results are predictable and not repeating
    when creating multiple generators.
    """)
  public TransactionsGenerator(Random rng) {
    this.rng = rng;
  }

  protected AccountId buildAccount(byte[] asciiDigits) {
    var left = new String(asciiDigits, 0, 18, StandardCharsets.UTF_8);
    var right = new String(asciiDigits, 18, 8, StandardCharsets.UTF_8);
    return new AccountId(Long.parseUnsignedLong(left), Integer.parseUnsignedInt(right));
  }

  protected Transaction buildTransaction(AccountId debitAccount, AccountId creditAccount, BigDecimal amount) {
    return new Transaction(debitAccount, creditAccount, amount);
  }

  public List<Transaction> generate(int accountCount, int transactionCount) {
    var accountIds = generateAccountIds(accountCount);
    var transactions = new ArrayList<Transaction>();
    for (int i = 0; i < transactionCount; i++) {
      var amount = genAmount();
      var debitAccountIdx = rng.nextInt(accountCount);
      int creditAccountIdx;
      do {
        creditAccountIdx = rng.nextInt(accountCount);
      } while (debitAccountIdx == creditAccountIdx);

      var debitAccount = accountIds.get(debitAccountIdx);
      var creditAccount = accountIds.get(creditAccountIdx);
      var transaction = buildTransaction(debitAccount, creditAccount, amount);
      transactions.add(transaction);
    }
    return transactions;
  }

  public List<AccountId> generateAccountIds(int accountCount) {
    return Stream.generate(this::genAccountBytes)
      .map(this::buildAccount)
      .limit(accountCount)
      .toList();
  }

  private byte[] genAccountBytes() {
    return GeneratorUtils.genAsciiDigits(rng, ACCOUNT_LEN);
  }

  private BigDecimal genAmount() {
    var lower = 1L;
    for (int i = 0, end = rng.nextInt(7); i < end; i++) {
      lower *= 10;
    }
    var upper = lower * 10L;
    for (; ; ) {
      var amount = GeneratorUtils.genPln(rng, lower, upper);
      if (new BigDecimal(Float.toString(amount.floatValue())).compareTo(amount) == 0) {
        return amount.stripTrailingZeros();
      }
    }
  }
}
