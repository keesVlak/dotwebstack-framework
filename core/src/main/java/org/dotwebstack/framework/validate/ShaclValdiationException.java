package org.dotwebstack.framework.validate;

public class ShaclValdiationException extends Exception {

  private static final long serialVersionUID = 6908579800668544739L;

  public ShaclValdiationException(String message) {
    super(message);
  }

  public ShaclValdiationException(String message, Throwable cause) {
    super(message, cause);
  }

}
