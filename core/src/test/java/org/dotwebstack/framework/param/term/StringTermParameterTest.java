package org.dotwebstack.framework.param.term;

import static org.dotwebstack.framework.param.term.TermParameter.VALUE_FACTORY;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.dotwebstack.framework.backend.BackendException;
import org.dotwebstack.framework.param.BindableParameter;
import org.dotwebstack.framework.test.DBEERPEDIA;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StringTermParameterTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private BindableParameter<String> requiredParameter;
  private BindableParameter<String> optionalParameter;

  @Before
  public void setUp() {
    requiredParameter = new StringTermParameter(DBEERPEDIA.NAME_PARAMETER_ID,
        DBEERPEDIA.NAME_PARAMETER_VALUE_STRING, true, null);
    optionalParameter = new StringTermParameter(DBEERPEDIA.PLACE_PARAMETER_ID,
        DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, false, null);
  }

  @Test
  public void handle_ReturnsNonNullValue_ForRequiredParameter() {
    // Arrange
    Map<String, String> parameterValues =
        ImmutableMap.of(DBEERPEDIA.NAME_PARAMETER_VALUE_STRING, "string");

    // Act
    String result = requiredParameter.handle(parameterValues);

    // Assert
    assertThat(result, is("string"));
  }

  @Test
  public void handle_ReturnsNull_ForOptionalParameter() {
    // Arrange
    Map<String, String> parameterValues =
        Collections.singletonMap(DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, null);

    // Act
    String result = optionalParameter.handle(parameterValues);

    // Assert
    assertThat(result, nullValue());
  }

  @Test
  public void handle_ReturnsDefaultValue_ForOptionalParameterWithNullInput() {
    // Arrange
    String defaultValue = "Apeldoorn";

    BindableParameter<String> parameter = new StringTermParameter(DBEERPEDIA.PLACE_PARAMETER_ID,
        DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, false, defaultValue);

    Map<String, String> parameterValues =
        Collections.singletonMap(DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, null);

    // Act
    String result = parameter.handle(parameterValues);

    // Assert
    assertThat(result, is(defaultValue));
  }

  @Test
  public void handle_RejectsNullValue_ForRequiredParameter() {
    // Assert
    thrown.expect(BackendException.class);
    thrown.expectMessage(
        String.format("No value found for required parameter '%s'. Supplied parameterValues:",
            DBEERPEDIA.NAME_PARAMETER_ID));

    // Act
    requiredParameter.handle(ImmutableMap.of());
  }

  @Test
  public void getValue_ReturnsLiteral_ForValue() {
    // Arrange
    Map<String, String> parameterValues =
        ImmutableMap.of(DBEERPEDIA.NAME_PARAMETER_VALUE_STRING, "string");

    // Act
    String value = requiredParameter.handle(parameterValues);
    Value result = requiredParameter.getValue(value);

    // Assert
    assertThat(result, is(SimpleValueFactory.getInstance().createLiteral("string")));
  }

  @Test
  public void handle_RejectsUnknownValue_ForEnumParameter() {
    // Arrange
    List<Literal> acceptedValues = ImmutableList.of(VALUE_FACTORY.createLiteral("Apeldoorn"),
        VALUE_FACTORY.createLiteral("Veenendaal"), VALUE_FACTORY.createLiteral("Nunspeet"));

    BindableParameter<String> parameter = new StringTermParameter(DBEERPEDIA.PLACE_PARAMETER_ID,
        DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, false, null, acceptedValues);

    String place = "Amersfoort";
    Map<String, String> parameterValues =
        Collections.singletonMap(DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, place);

    String errorMessage = String.format("Value for parameter '%s' not an enum value: "
            + "[Apeldoorn, Veenendaal, Nunspeet]. Supplied parameterValue: %s",
        DBEERPEDIA.PLACE_PARAMETER_ID, place);

    // Assert
    thrown.expect(BackendException.class);
    thrown.expectMessage(errorMessage);

    // Act
    parameter.handle(parameterValues);
  }

  @Test
  public void handle_AcceptsKnownValue_ForEnumParameter() {
    // Arrange
    List<Literal> acceptedValues = ImmutableList.of(VALUE_FACTORY.createLiteral("Apeldoorn"),
        VALUE_FACTORY.createLiteral("Veenendaal"), VALUE_FACTORY.createLiteral("Nunspeet"));

    BindableParameter<String> parameter = new StringTermParameter(DBEERPEDIA.PLACE_PARAMETER_ID,
        DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, false, null, acceptedValues);

    Map<String, String> parameterValues =
        Collections.singletonMap(DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, "Veenendaal");

    // Act
    String result = parameter.handle(parameterValues);

    // Assert
    assertThat(result, is("Veenendaal"));
  }

  @Test
  public void handle_AcceptsNullValue_ForOptionalEnumParameter() {
    // Arrange
    List<Literal> acceptedValues = ImmutableList.of(VALUE_FACTORY.createLiteral("Apeldoorn"),
        VALUE_FACTORY.createLiteral("Veenendaal"), VALUE_FACTORY.createLiteral("Nunspeet"));

    BindableParameter<String> parameter = new StringTermParameter(DBEERPEDIA.PLACE_PARAMETER_ID,
        DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, false, null, acceptedValues);

    Map<String, String> parameterValues =
        Collections.singletonMap(DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, null);

    // Act
    String result = parameter.handle(parameterValues);

    // Assert
    assertThat(result, nullValue());
  }

  @Test
  public void handle_RejectsNullValue_ForRequiredEnumParameter() {
    // Assert
    thrown.expect(BackendException.class);
    thrown.expectMessage(
        String.format("No value found for required parameter '%s'. Supplied parameterValues:",
            DBEERPEDIA.PLACE_PARAMETER_ID));

    // Arrange
    List<Literal> acceptedValues = ImmutableList.of(VALUE_FACTORY.createLiteral("Apeldoorn"),
        VALUE_FACTORY.createLiteral("Veenendaal"), VALUE_FACTORY.createLiteral("Nunspeet"));

    BindableParameter<String> parameter = new StringTermParameter(DBEERPEDIA.PLACE_PARAMETER_ID,
        DBEERPEDIA.PLACE_PARAMETER_VALUE_STRING, true, null, acceptedValues);

    Map<String, String> parameterValues = ImmutableMap.of();

    // Act
    parameter.handle(parameterValues);
  }
}
