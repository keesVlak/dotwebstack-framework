package org.dotwebstack.framework.validate;

public class SHACLValdiationException extends Exception {

  private static final long serialVersionUID = 6908579800668544739L;

  public SHACLValdiationException() {
    super();
  }

  public SHACLValdiationException(String message) {
    super(message);
  }

  public SHACLValdiationException(String message, Throwable cause) {
    super(message, cause);
  }

  public SHACLValdiationException(Throwable cause) {
    super(cause);
  }

}
