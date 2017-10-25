package org.dotwebstack.framework;

import org.dotwebstack.framework.validate.SHACLValdiationException;
import org.dotwebstack.framework.validate.SHACLValidator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class SHACLValidatorIntegrationTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private SHACLValidator shaclValidator;

  private Resource representationConfigResource;

  private Resource elmoShapesResource;

  @Test
  public void validate_NoError_ValidRepresenationConfiguration() throws Exception {
    // Arrange
    shaclValidator = new SHACLValidator();
    elmoShapesResource = new ClassPathResource("/elmo-shapes.trig");
    representationConfigResource = new ClassPathResource(
        "/model/frontend/representations.trig");

    // Act / Assert
    shaclValidator.validate(representationConfigResource, elmoShapesResource);
  }

  @Test
  public void validate_ThrowSHACLValidationException_ValidRepresenationConfiguration()
      throws Exception {
    // Arrange
    shaclValidator = new SHACLValidator();
    elmoShapesResource = new ClassPathResource("/elmo-shapes.trig");
    representationConfigResource = new ClassPathResource(
        "/shaclValidationException/model/representations.trig");

    // Assert
    thrown.expect(SHACLValdiationException.class);
    thrown.expectMessage(
        "Invalid configuration at path [http://dotwebstack.org/def/elmo#name] on node [http://dbeerpedia.org#GraphBreweryListRepresentation] with error message [More than 1 values]");

    // Act
    shaclValidator.validate(representationConfigResource, elmoShapesResource);
  }
}
