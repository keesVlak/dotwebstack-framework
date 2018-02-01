package org.dotwebstack.framework.param.term;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;

public class BooleanTermParameter extends TermParameter<Boolean> {

  BooleanTermParameter(IRI identifier, String name, boolean required) {
    this(identifier, name, required, null, ImmutableList.of());
  }

  BooleanTermParameter(IRI identifier, String name, boolean required, Boolean defaultValue,
      Collection<Literal> in) {
    super(identifier, name, required, defaultValue, in);
  }

  @Override
  public Literal getValue(Boolean value) {
    return VALUE_FACTORY.createLiteral(value);
  }

  @Override
  protected Boolean handleInner(String value) {
    return Boolean.valueOf(value);
  }
}
