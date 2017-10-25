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

  private Model transformTrigFileToModel(Resource trigFile) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    RDFWriter turtleWriter = Rio.createWriter(RDFFormat.TURTLE, byteArrayOutputStream);
    RDFParser trigParser = Rio.createParser(RDFFormat.TRIG);

    trigParser.setRDFHandler(turtleWriter);
    trigParser.parse(trigFile.getInputStream(), trigFile.getFile().getAbsolutePath());

    Model model = JenaUtil.createMemoryModel();
    model.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), "",
        FileUtils.langTurtle);
    return model;
  }

  @Override
  public void getValidationReport(Model reportModel) throws SHACLValdiationException {
    NodeIterator validatorResult = reportModel.listObjects();
    StmtIterator iterator = reportModel.listStatements();

    Boolean isValid = false;
    String resultPath = "";
    String resultMessage = "";
    String focusNode = "";

    while (iterator.hasNext()) {
      Statement statement = iterator.nextStatement();
      Property predicate = statement.getPredicate();
      RDFNode object = statement.getObject();

      if (predicate.getLocalName().equals("conforms")) {
        if (object instanceof Literal) {
          Literal literal = object.asLiteral();
          isValid = literal.getBoolean();
        }
      }
      if (predicate.getLocalName().equals("resultPath")) {
        if (object instanceof org.apache.jena.rdf.model.Resource) {
          org.apache.jena.rdf.model.Resource resource = object.asResource();
          resultPath = resource.toString();
        }
      }
      if (predicate.getLocalName().equals("resultMessage")) {
        if (object instanceof Literal) {
          Literal literal = object.asLiteral();
          resultMessage = literal.getString();
        }
      }
      if (predicate.getLocalName().equals("focusNode")) {
        if (object instanceof RDFNode) {
          RDFNode rdfNode = (RDFNode) object;
          focusNode = rdfNode.toString();
        }
      }
    }
    if (!isValid) {
      LOG.error(String
          .format("Invalid configuration at path [%s] on node [%s] with error message [%s]",
              resultPath, focusNode, resultMessage));
      throw new SHACLValdiationException(String
          .format("Invalid configuration at path [%s] on node [%s] with error message [%s]",
              resultPath, focusNode, resultMessage));
    }
  }

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
