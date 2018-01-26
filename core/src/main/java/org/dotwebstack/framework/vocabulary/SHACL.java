package org.dotwebstack.framework.vocabulary;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

@UtilityClass
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public final class SHACL {

  private SimpleValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

  String NAMESPACE = "http://www.w3.org/ns/shacl#";
  IRI DATATYPE;
  IRI DEFAULT_VALUE;
  IRI IN;


  static {
    DATATYPE = VALUE_FACTORY.createIRI(NAMESPACE, "datatype");
    DEFAULT_VALUE = VALUE_FACTORY.createIRI(NAMESPACE, "defaultValue");
    IN = VALUE_FACTORY.createIRI(NAMESPACE, "in");
  }
}
