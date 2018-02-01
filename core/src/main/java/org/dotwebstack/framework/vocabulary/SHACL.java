package org.dotwebstack.framework.vocabulary;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class SHACL {

  private static final SimpleValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

  public static final String NAMESPACE = "http://www.w3.org/ns/shacl#";
  public static final IRI DATATYPE;
  public static final IRI DEFAULT_VALUE;
  public static final IRI IN;

  static {
    DATATYPE = VALUE_FACTORY.createIRI(NAMESPACE, "datatype");
    DEFAULT_VALUE = VALUE_FACTORY.createIRI(NAMESPACE, "defaultValue");
    IN = VALUE_FACTORY.createIRI(NAMESPACE, "in");
  }

  private SHACL() {
    throw new UnsupportedOperationException("Utility classes should not be instantiated");
  }
}
