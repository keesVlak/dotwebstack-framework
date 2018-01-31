package org.dotwebstack.framework.param;

import static lombok.AccessLevel.PRIVATE;

import java.util.Collection;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
public final class ShaclShape {

  @NonNull
  IRI datatype;

  Value defaultValue;

  // XXX (PvH) Werken Literals wel met de IriTermParameter?
  @NonNull
  Collection<Literal> in;
}

