
package com.as4mail.svev.exception;

/**
 *
 * @author Joze Rihtarsic <joze.rihtarsic@sodisce.si>
 */
public class FOPRuntimeException extends RuntimeException {

  /**
   *
   * @param message
   */
  public FOPRuntimeException(String message) {
    super(message);
  }

  /**
   *
   * @param message
   * @param cause
   */
  public FOPRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   *
   * @param cause
   */
  public FOPRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   *
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public FOPRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
