
package com.as4mail.svev.exception;

/**
 *
 * @author Joze Rihtarsic <joze.rihtarsic@sodisce.si>
 */
public class EncRuntimeException extends RuntimeException {

  /**
   *
   * @param message
   */
  public EncRuntimeException(String message) {
    super(message);
  }

  /**
   *
   * @param message
   * @param cause
   */
  public EncRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   *
   * @param cause
   */
  public EncRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   *
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public EncRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
