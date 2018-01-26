package org.dotwebstack.framework.param.term;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.dotwebstack.framework.config.ConfigurationException;
import org.dotwebstack.framework.param.ShaclShape;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TermParameterFactory {

  public static TermParameter newTermParameter(@NonNull IRI identifier, @NonNull String name,
      @NonNull ShaclShape shape, boolean required) {

    Value defaultValue = shape.getDefaultValue();
    IRI type = shape.getDatatype();
    if (type.equals(XMLSchema.STRING)) {
      String defVal = defaultValue != null ? defaultValue.stringValue() : null;
      return new StringTermParameter(identifier, name, required, defVal, shape.getIn());
    } else if (type.equals(XMLSchema.INTEGER)) {
      Integer defVal = defaultValue != null ? ((Literal) defaultValue).intValue() : null;
      return new IntegerTermParameter(identifier, name, required, defVal, shape.getIn());
    } else if (type.equals(XMLSchema.BOOLEAN)) {
      Boolean defVal = defaultValue != null ? ((Literal) defaultValue).booleanValue() : null;
      return new BooleanTermParameter(identifier, name, required, defVal, shape.getIn());
    } else if (type.equals(XMLSchema.ANYURI)) {
      return new IriTermParameter(identifier, name, required, (IRI) defaultValue, shape.getIn());
    }
    throw new ConfigurationException(
        String.format("Unsupported data type: <%s>. Supported types: %s", type, ImmutableList.of(
            XMLSchema.BOOLEAN, XMLSchema.STRING, XMLSchema.INTEGER, XMLSchema.ANYURI)));
  }

}
