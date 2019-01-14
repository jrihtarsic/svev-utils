
package com.as4mail.svev.exception;

/**
 *
 * @author Joze Rihtarsic <joze.rihtarsic@sodisce.si>
 */
public class SignRuntimeException extends RuntimeException {

  /**
   *
   * @param message
   */
  public SignRuntimeException(String message) {
    super(message);
  }

  /**
   *
   * @param message
   * @param cause
   */
  public SignRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   *
   * @param cause
   */
  public SignRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   *
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public SignRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
