package org.dotwebstack.framework.validate;

public interface Validator<R, M, I> {

  void validate(I data, R shapes) throws ShaclValdiationException;

  void validate(I data, R shapes, R prefixes) throws ShaclValdiationException;

  void getValidationReport(M reportModel) throws ShaclValdiationException;

}
