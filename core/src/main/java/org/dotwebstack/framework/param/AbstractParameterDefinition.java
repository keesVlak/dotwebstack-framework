package org.dotwebstack.framework.param;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.rdf4j.model.IRI;

@RequiredArgsConstructor(access = PROTECTED)
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
public abstract class AbstractParameterDefinition<T extends Parameter<?>>
    implements ParameterDefinition<T> {

  @NonNull
  IRI identifier;
  @NonNull
  String name;

}
