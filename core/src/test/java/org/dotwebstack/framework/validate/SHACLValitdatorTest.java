package org.dotwebstack.framework.validate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SHACLValitdatorTest {

  SHACLValidator shaclValidator;

  @Before
  public void init() {
    shaclValidator = new SHACLValidator();
  }

  @Test
  public void test() throws IOException {
    // Arrange
    String dataContent = "@prefix sh: <http://www.w3.org/ns/shacl#> .\n"
        + "@prefix kkg: <http://bp4mc2.org/def/kkg/id/begrip> .\n"
        + "@prefix uml: <http://bp4mc2.org/def/uml#> .\n"
        + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n"
        + "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n"
        + "@prefix ex: <http://example.org#> .\n"
        + "\n"
        + "ex:Marco a ex:Persoon;\n"
        + "\tex:naam \"Marco\";\n"
        + ".\n"
        + "\n"
        + "ex:Nanda a ex:Persoon;\n"
        + " \tex:naam \"Nanda\";\n"
        + " .\n"
        + "\n"
        + " ex:Bobby a ex:Persoon;\n"
        + " \tex:naam \"Bobby\";\n"
        + " .\n"
        + "\n"
        + "ex:HuwelijkMarcoNanda a ex:Huwelijk;\n"
        + "\tex:lid ex:Marco;\n"
        + "\tex:lid ex:Nanda;\n"
        + ".";
    Resource dataResource = mock(Resource.class);

    String shapesContent = "@prefix sh:\t\t\t<http://www.w3.org/ns/shacl#> .\n"
        + "@prefix kkg:\t\t<http://bp4mc2.org/def/kkg/id/begrip> .\n"
        + "@prefix uml:\t\t<http://www.omg.org/spec/UML/20131001/>.\n"
        + "@prefix rdfs:\t\t<http://www.w3.org/2000/01/rdf-schema#> .\n"
        + "@prefix owl:\t\t<http://www.w3.org/2002/07/owl#> .\n"
        + "@prefix ex:\t\t\t<http://example.org#> .\n"
        + "@prefix exs:\t\t<http://example.org/shape#> .\n"
        + "@prefix skos:\t\t<http://www.w3.org/2004/02/skos/core#>.\n"
        + "@prefix prov:\t\t<http://www.w3.org/ns/prov#>.\n"
        + "@prefix xsd:\t\t<http://www.w3.org/2001/XMLSchema#>.\n"
        + "@prefix dash:   <http://datashapes.org/dash#>.\n"
        + "\n"
        + "ex:Persoon a owl:Class;\n"
        + "\tuml:stereotype kkg:ObjectType;\n"
        + "\tskos:prefLabel \"Persoon\";\n"
        + "\tskos:definition \"Een mens van vlees en bloed\";\n"
        + "\tprov:generatedAtTime \"2017-08-11\"^^xsd:datetime;\n"
        + ".\n"
        + "\n"
        + "ex:Huwelijk a owl:Class;\n"
        + "\tuml:stereotype kkg:Relatieklasse;\n"
        + ".\n"
        + "\n"
        + "exs:Persoon a sh:NodeShape;\n"
        + "\tsh:targetClass ex:Persoon;\n"
        + "\tsh:property [\n"
        + "\t\tsh:name \"Naam\";\n"
        + "\t\tsh:path ex:naam;\n"
        + "\t\tsh:minCount 1;\n"
        + "\t\tsh:maxCount 1;\n"
        + "\t]\n"
        + ".\n"
        + "\n"
        + "exs:Huwelijk a sh:NodeShape;\n"
        + "\tsh:targetClass ex:Huwelijk;\n"
        + "\tsh:property [\n"
        + "\t\tsh:naam \"Lid\";\n"
        + "\t\tsh:path ex:lid;\n"
        + "\t\tsh:node exs:Persoon;\n"
        + "\t\tsh:minCount 3;\n"
        + "\t\tsh:maxCount 2;\n"
        + "\t]\n"
        + ".";
    Resource shapesResource = mock(Resource.class);
    when(shapesResource.getInputStream()).thenReturn(new ByteArrayInputStream(
        shapesContent.getBytes(StandardCharsets.UTF_8)));
    when(dataResource.getInputStream()).thenReturn(new ByteArrayInputStream(
        dataContent.getBytes(StandardCharsets.UTF_8)));

    try {
      shaclValidator.validate(dataResource, shapesResource);
    } catch (SHACLValdiationException e) {
      e.printStackTrace();
    }
  }

}
