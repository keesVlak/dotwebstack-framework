package org.dotwebstack.framework.param.term;

import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.NONE;

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
import org.slf4j.Logger;

@Getter
public abstract class TermParameter<T> extends AbstractParameter<T>
    implements BindableParameter<T> {

  @Getter(value = NONE)
  protected static final SimpleValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();
  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TermParameter.class);

  private final T defaultValue;
  private final Collection<Literal> in;

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
  protected void validateInner(@NonNull Map<String, String> parameterValues) {
    LOG.debug("Validate {} using sh:in: {}", getName(), in);
    String value = parameterValues.get(getName());
    if ((value != null)
        && !in.isEmpty()
        && in.stream().map(Literal::stringValue).noneMatch(value::equalsIgnoreCase)) {
      String options = in.stream().map(Literal::stringValue).collect(joining(", "));
      throw new BackendException(String.format(
          "Value for parameter '%s' not an enum value: [%s]. Supplied parameterValue: %s",
          getIdentifier(), options, value));
    }
  }

  @Override
  protected void validateRequired(Map<String, String> parameterValues) {
    if (handleInner(parameterValues) == null) {
      throw new BackendException(
          String.format("No value found for required parameter '%s'. Supplied parameterValues: %s",
              getIdentifier(), parameterValues));
    }
  }

}
