package org.dotwebstack.framework.vocabulary;

import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

//CHECKSTYLE.OFF: Lombok issue
@UtilityClass
@FieldDefaults(makeFinal = true)
public class SHACL {

  private SimpleValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

  String NAMESPACE = "http://www.w3.org/ns/shacl#";
  public IRI DATATYPE;
  public IRI DEFAULT_VALUE;
  public IRI IN;


  static {
    DATATYPE = VALUE_FACTORY.createIRI(NAMESPACE, "datatype");
    DEFAULT_VALUE = VALUE_FACTORY.createIRI(NAMESPACE, "defaultValue");
    IN = VALUE_FACTORY.createIRI(NAMESPACE, "in");
  }
}
//CHECKSTYLE.ON: Lombok issue
