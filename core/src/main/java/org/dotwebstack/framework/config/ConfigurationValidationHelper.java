package org.dotwebstack.framework.config;

import static java.util.Comparator.comparing;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.dotwebstack.framework.EnvironmentAwareResource;
import org.dotwebstack.framework.validation.RdfModelTransformer;
import org.dotwebstack.framework.validation.ShaclValidationException;
import org.dotwebstack.framework.validation.ShaclValidator;
import org.dotwebstack.framework.validation.ValidationReport;
import org.dotwebstack.framework.vocabulary.ELMO;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

@RequiredArgsConstructor
class ConfigurationValidationHelper {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationValidationHelper.class);

  private final List<Resource> projectResources;
  private final Environment environment;

  private Resource prefixesResource;
  private Model model;
  private List<InputStream> configurations;

  void filterGlobalPrefixesResourceFromResources() {
    // @formatter:off
    projectResources.stream()
        .filter(filenameNotNullAndStartsWith())
        .filter(extensionAccepted())
        .findFirst().ifPresent(this::checkMultiplePrefixesDeclaration);
    // @formatter:on
  }

  private static Predicate<Resource> filenameNotNullAndStartsWith() {
    return resource -> resource.getFilename() != null
        && resource.getFilename().startsWith("_prefixes");
  }

  private static Predicate<Resource> extensionAccepted() {
    return resource -> FileFormats.containsExtension(getExtensionFromResource(resource));
  }

  private static String getExtensionFromResource(Resource resource) {
    return FilenameUtils.getExtension(resource.getFilename());
  }

  private void checkMultiplePrefixesDeclaration(Resource resource) {
    prefixesResource = resource;
    try (InputStreamReader isr = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8)) {
      String[] allPrefixes = CharStreams.toString(isr).split("\n");
      Set<String> prefixSet = new HashSet<>();
      for (int i = 0; i < allPrefixes.length; i++) {
        String[] parts = allPrefixes[i].split(":");
        if (parts.length != 3) {
          throw new ConfigurationException(String.format(
              "Found unknown prefix format <%s> at line <%d>", allPrefixes[i], i + 1));
        }
        String key = parts[0];
        if (prefixSet.contains(key)) {
          throw new ConfigurationException(
              String.format("Found second declaration of <%s> at line <%d>", key, i + 1));
        }
        prefixSet.add(key);
      }
    } catch (IOException e) {
      throw new ConfigurationException("Error while reading _prefixes.trig.", e);
    }
  }


  void prepareResourcesForValidation(SailRepository repository) {
    RepositoryConnection repositoryConnection;
    try {
      repositoryConnection = repository.getConnection();
    } catch (RepositoryException re) {
      throw new ConfigurationException("Error while getting repository connection.", re);
    }
    projectResources.sort(comparing(Resource::getFilename));
    // @formatter:off
    configurations = projectResources.stream()
        .filter(extensionAccepted())
        .map(resource -> addResourceToRepositoryConnection(repositoryConnection, resource))
        .collect(Collectors.toList());
    // @formatter:on
    if (configurations.isEmpty()) {
      LOG.warn("Found no configuration files");
    }
    model = QueryResults.asModel(
        repositoryConnection.getStatements(null, null, null, ELMO.SHACL_GRAPHNAME));
    repositoryConnection.close();
  }

  private InputStream addResourceToRepositoryConnection(RepositoryConnection repositoryConnection,
      Resource resource) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ByteStreams.copy(getInputStreamWithEnv(resource), outputStream);
      byte[] byteArray = outputStream.toByteArray();
      repositoryConnection.add(new ByteArrayInputStream(byteArray), "#",
          FileFormats.getFormat(getExtensionFromResource(resource)));

      return new ByteArrayInputStream(byteArray);

    } catch (IOException ioEx) {
      throw new ConfigurationException(
          String.format("Configuration file <%s> could not be read.", resource.getFilename()));
    } catch (RepositoryException | RDFParseException rdfEx) {
      throw new ConfigurationException("Error while loading RDF data.", rdfEx);
    }
  }

  private InputStream getInputStreamWithEnv(Resource resource) throws IOException {
    if (prefixesResource != null && !resource.getFilename().equals("_prefixes.trig")) {
      return new EnvironmentAwareResource(
          new SequenceInputStream(prefixesResource.getInputStream(), resource.getInputStream()),
          environment).getInputStream();
    } else {
      return new EnvironmentAwareResource(resource.getInputStream(), environment).getInputStream();
    }
  }


  void validateUsing(ShaclValidator validator) {
    try (InputStream stream = new SequenceInputStream(Collections.enumeration(configurations))) {
      Model dataModel = RdfModelTransformer.getModel(stream);
      ValidationReport report = validator.validate(dataModel, this.model);
      if (!report.isValid()) {
        throw new ShaclValidationException(report.printReport());
      }
    } catch (IOException ex) {
      throw new ShaclValidationException("Configuration files could not be read.", ex);
    }
  }

  void addProjectResource(Resource resource) {
    projectResources.add(resource);
  }
}
