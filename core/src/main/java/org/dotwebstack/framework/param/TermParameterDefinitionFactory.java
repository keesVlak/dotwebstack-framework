package org.dotwebstack.framework.param;

import static org.eclipse.rdf4j.model.util.Models.object;
import static org.eclipse.rdf4j.model.util.Models.objectIRI;
import static org.eclipse.rdf4j.model.util.Models.objectLiterals;
import static org.eclipse.rdf4j.model.util.Models.objectResource;

import java.util.Set;
import java.util.function.Supplier;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.dotwebstack.framework.config.ConfigurationException;
import org.dotwebstack.framework.vocabulary.ELMO;
import org.dotwebstack.framework.vocabulary.SHACL;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.Models;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
final class TermParameterDefinitionFactory implements ParameterDefinitionFactory {

  @Override
  public ParameterDefinition create(@NonNull Model model, @NonNull IRI id) {
    String name = Models.objectLiteral(model.filter(id, ELMO.NAME_PROP, null)).orElseThrow(
        newConfigurationException(ELMO.NAME_PROP, id, ELMO.TERM_FILTER)).stringValue();

    Resource subj = objectResource(model.filter(id, ELMO.SHAPE_PROP, null)).orElseThrow(
        newConfigurationException(ELMO.SHAPE_PROP, id, ELMO.TERM_FILTER));

    IRI iriShapeType = objectIRI(model.filter(subj, SHACL.DATATYPE, null)).orElseThrow(
        newConfigurationException(SHACL.DATATYPE, id, ELMO.SHAPE_PROP));

    Value defaultValue = object(model.filter(subj, SHACL.DEFAULT_VALUE, null)).orElse(null);

    Set<Literal> in = objectLiterals(model.filter(subj, SHACL.IN, null));

    ShaclShape shape = new ShaclShape(iriShapeType, defaultValue, in);
    return new TermParameterDefinition(id, name, shape);
  }

  @SuppressWarnings("squid:S2275") // Not enough arguments
  private static Supplier<ConfigurationException> newConfigurationException(Object... arguments) {
    return () -> new ConfigurationException(
        String.format("No <%s> property found for <%s> of type <%s>", arguments));
  }

  /**
   * @return {@code true} if {@link ELMO#TERM_FILTER} is supplied; {@code false} otherwise.
   */
  @Override
  public boolean supports(@NonNull IRI type) {
    return type.equals(ELMO.TERM_FILTER);
  }

}
