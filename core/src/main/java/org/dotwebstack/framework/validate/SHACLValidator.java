package org.dotwebstack.framework.validate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.spin.util.JenaUtil;

public class SHACLValidator implements Validator<Resource, Model> {

  private static final Logger LOG = LoggerFactory.getLogger(SHACLValidator.class);

  @Override
  public void validate(Resource data, Resource shapes) throws SHACLValdiationException {

    try {
      Model dataShape = JenaUtil.createMemoryModel();
      dataShape.read(shapes.getInputStream(), "", FileUtils.langTurtle);

      Model dataModel = JenaUtil.createMemoryModel();
      dataModel.read(data.getInputStream(), "", FileUtils.langTurtle);

      org.apache.jena.rdf.model.Resource report = ValidationUtil
          .validateModel(dataModel, dataShape, true);
      NodeIterator validatorResult = report.getModel().listObjects();
      // list the statements in the Model
      StmtIterator iterator = report.getModel().listStatements();
      while (iterator.hasNext()) {
        Statement statement = iterator.nextStatement();
        Property predicate = statement.getPredicate();
        RDFNode object = statement.getObject();

        if (predicate.getLocalName().equals("conforms")) {
          if (object instanceof Literal) {
            Literal literal = object.asLiteral();
            Boolean isValid = literal.getBoolean();
            if (!isValid) {
              System.out.println("SHACL error report: " + report.getModel().toString());
              LOG.error("SHACL error report: " + report.getModel().toString());
              throw new SHACLValdiationException("Invalid SHACL :(");
            } else {
              break;
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
