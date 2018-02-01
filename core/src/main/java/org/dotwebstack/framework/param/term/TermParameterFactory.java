package org.dotwebstack.framework.param.term;

import static org.eclipse.rdf4j.model.vocabulary.XMLSchema.ANYURI;
import static org.eclipse.rdf4j.model.vocabulary.XMLSchema.BOOLEAN;
import static org.eclipse.rdf4j.model.vocabulary.XMLSchema.INTEGER;
import static org.eclipse.rdf4j.model.vocabulary.XMLSchema.STRING;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.dotwebstack.framework.config.ConfigurationException;
import org.dotwebstack.framework.param.ShaclShape;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.slf4j.Logger;

@UtilityClass
public final class TermParameterFactory {

  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TermParameterFactory.class);

  public static TermParameter newTermParameter(@NonNull IRI identifier, @NonNull String name,
      @NonNull ShaclShape shape, boolean required) {

    Value defaultValue = shape.getDefaultValue();
    IRI type = shape.getDatatype();
    Collection<Literal> in = shape.getIn();
    LOG.debug("Creating TermParameter of type: {}", type);
    if (type.equals(STRING)) {
      String defVal = defaultValue != null ? defaultValue.stringValue() : null;
      return new StringTermParameter(identifier, name, required, defVal, in);
    } else if (type.equals(INTEGER)) {
      Integer defVal = defaultValue != null ? ((Literal) defaultValue).intValue() : null;
      return new IntegerTermParameter(identifier, name, required, defVal, in);
    } else if (type.equals(BOOLEAN)) {
      Boolean defVal = defaultValue != null ? ((Literal) defaultValue).booleanValue() : null;
      return new BooleanTermParameter(identifier, name, required, defVal, in);
    } else if (type.equals(ANYURI)) {
      return new IriTermParameter(identifier, name, required, (IRI) defaultValue, in);
    }
    throw new ConfigurationException(
        String.format("Unsupported data type: <%s>. Supported types: %s", type, ImmutableList.of(
            BOOLEAN, STRING, INTEGER, ANYURI)));
  }

}
