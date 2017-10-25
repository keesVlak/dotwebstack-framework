package org.dotwebstack.framework.validate;

public interface Validator<R, M> {

  void validate(R data, R shapes) throws ShaclValdiationException;

  void getValidationReport(M reportModel) throws ShaclValdiationException;

}
