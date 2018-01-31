package org.dotwebstack.framework.param;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static org.dotwebstack.framework.param.term.TermParameterFactory.newTermParameter;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dotwebstack.framework.param.term.TermParameter;
import org.eclipse.rdf4j.model.IRI;

// XXX (PvH) Logging wordt niet gebruikt, annotatie kan daarom weg
@Slf4j
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter(PACKAGE)
public final class TermParameterDefinition extends AbstractParameterDefinition<TermParameter<?>> {

  ShaclShape shaclShape;

  public TermParameterDefinition(@NonNull IRI identifier, @NonNull String name,
      @NonNull ShaclShape shaclShape) {
    super(identifier, name);

    this.shaclShape = shaclShape;
  }

  @Override
  public TermParameter<?> createOptionalParameter() {
    return createParameter(false);
  }

  @Override
  public TermParameter<?> createRequiredParameter() {
    return createParameter(true);
  }

  private TermParameter<?> createParameter(boolean required) {
    return newTermParameter(getIdentifier(), getName(), shaclShape, required);

  }

}
