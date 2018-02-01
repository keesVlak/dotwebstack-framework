package org.dotwebstack.framework.param.term;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;

public class IntegerTermParameter extends TermParameter<Integer> {

  IntegerTermParameter(IRI identifier, String name, boolean required) {
    this(identifier, name, required, null, ImmutableList.of());
  }

  IntegerTermParameter(IRI identifier, String name, boolean required, Integer defaultValue,
      Collection<Literal> in) {
    super(identifier, name, required, defaultValue, in);
  }

  @Override
  public Literal getValue(Integer value) {
    return VALUE_FACTORY.createLiteral(value);
  }

  @Override
  protected Integer handleInner(String value) {
    return Integer.parseInt(value);
  }
}
