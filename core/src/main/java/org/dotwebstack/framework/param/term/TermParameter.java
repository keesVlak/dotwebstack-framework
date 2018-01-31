package org.dotwebstack.framework.param.term;

import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.NONE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dotwebstack.framework.backend.BackendException;
import org.dotwebstack.framework.param.AbstractParameter;
import org.dotwebstack.framework.param.BindableParameter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

@Slf4j
@Getter
@FieldDefaults(makeFinal = true, level = PRIVATE)
public abstract class TermParameter<T> extends AbstractParameter<T>
    implements BindableParameter<T> {

  @Getter(value = NONE)
  static SimpleValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

  T defaultValue;
  Collection<Literal> in;

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
    String value = parameterValues.get(getName());
    LOG.debug("Validate {} using sh:in: {}", getName(), in);

    // XXX (PvH) De ifs kan je zonder logging samenvoegen. Dit voorkomt arrow code.
    if (value != null && !in.isEmpty()) {
      if (!in.contains(VALUE_FACTORY.createLiteral(value))) {

        // XXX (PvH) Ik zou hier Literal::stringValue aanroepen. Value komt hier uit de lucht
        // vallen.
        String options = in.stream().map(Value::stringValue).collect(joining(", "));

        throw new BackendException(String.format(
            "Value for parameter '%s' not an enum value: [%s]. Supplied parameterValue: %s",
            getIdentifier(), options, value));
      } else {
        LOG.debug("Parameter has valid value: {}", value);
      }
    } else {
      LOG.debug("Parameter has valid value: {}", value);
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
