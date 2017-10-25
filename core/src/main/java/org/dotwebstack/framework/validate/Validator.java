package org.dotwebstack.framework.validate;

public interface Validator<R, M> {

  void validate(R data, R shapes) throws SHACLValdiationException;

  void getValidationReport(M reportModel) throws SHACLValdiationException;

}
