package org.dotwebstack.framework.validate;

public interface Validator<R> {

  void validate(R data, R shapes) throws SHACLValdiationException;

}
