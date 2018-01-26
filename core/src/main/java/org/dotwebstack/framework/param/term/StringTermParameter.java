package org.dotwebstack.framework.param.term;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;

public class StringTermParameter extends TermParameter<String> {

  public StringTermParameter(IRI identifier, String name, boolean required) {
    this(identifier, name, required, null);
  }

  StringTermParameter(IRI identifier, String name, boolean required, String defaultValue) {
    this(identifier, name, required, defaultValue, ImmutableList.of());
  }

  StringTermParameter(IRI identifier, String name, boolean required, String defaultValue,
      Collection<Literal> in) {
    super(identifier, name, required, defaultValue, in);
  }

  @Override
  public Literal getValue(String value) {
    return VALUE_FACTORY.createLiteral(value);
  }

  @Override
  protected String handleInner(String value) {
    return value;
  }

}
