package org.dotwebstack.framework.param.term;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;

public class IriTermParameter extends TermParameter<IRI> {

  IriTermParameter(IRI identifier, String name, boolean required) {
    this(identifier, name, required, null);
  }

  IriTermParameter(IRI identifier, String name, boolean required, IRI defaultValue) {
    this(identifier, name, required, defaultValue, ImmutableList.of());
  }

  IriTermParameter(IRI identifier, String name, boolean required, IRI defaultValue,
      Collection<Literal> in) {
    super(identifier, name, required, defaultValue, in);
  }

  @Override
  public Literal getValue(IRI value) {
    return VALUE_FACTORY.createLiteral(value.stringValue());
  }

  @Override
  protected IRI handleInner(String value) {
    return VALUE_FACTORY.createIRI(value);
  }
}
