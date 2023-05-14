package com.example.greenapp;

/**
 * Assumptions about transactions amount are pretty solid, nonetheless
 * the test cases might be malicious.
 */
public class OptimizationAssumptionException extends RuntimeException {

  public OptimizationAssumptionException() {
  }

  public OptimizationAssumptionException(String message) {
    super(message);
  }

  public OptimizationAssumptionException(String message, Throwable cause) {
    super(message, cause);
  }

  public OptimizationAssumptionException(Throwable cause) {
    super(cause);
  }

}
