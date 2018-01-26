package org.dotwebstack.framework.param.term;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import org.dotwebstack.framework.backend.BackendException;
import org.dotwebstack.framework.param.AbstractParameter;
import org.dotwebstack.framework.param.BindableParameter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public abstract class TermParameter<T> extends AbstractParameter<T>
    implements BindableParameter<T> {

  protected static final SimpleValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

  @Getter
  protected final T defaultValue;
  @Getter
  protected final Collection<Literal> in;

  protected TermParameter(@NonNull IRI identifier, @NonNull String name, boolean required,
      T defaultValue, Collection<Literal> in) {
    super(identifier, name, required);
    this.defaultValue = defaultValue;
    this.in = in;
  }

  @Override
  protected T handleInner(Map<String, String> parameterValues) {
    String value = parameterValues.get(getName());
    return value != null ? handleInner(value) : defaultValue;
  }

  protected abstract T handleInner(String value);

  @Override
  protected void validateRequired(Map<String, String> parameterValues) {
    if (handleInner(parameterValues) == null) {
      throw new BackendException(
          String.format("No value found for required parameter '%s'. Supplied parameterValues: %s",
              getIdentifier(), parameterValues));
    }
  }

}
