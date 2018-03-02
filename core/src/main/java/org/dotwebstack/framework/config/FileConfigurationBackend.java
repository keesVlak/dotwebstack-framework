package org.dotwebstack.framework.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.NonNull;
import org.dotwebstack.framework.validation.ShaclValidator;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

public class FileConfigurationBackend
    implements ConfigurationBackend, ResourceLoaderAware, EnvironmentAware {

  private static final Logger LOG = LoggerFactory.getLogger(FileConfigurationBackend.class);

  private final ShaclValidator shaclValidator;
  private final String resourcePath;
  private final Resource elmoConfiguration;
  private final Resource elmoShapes;
  private final SailRepository repository;

  private ResourceLoader resourceLoader;
  private Environment environment;

  public FileConfigurationBackend(@NonNull Resource elmoConfiguration,
      @NonNull SailRepository repository, @NonNull String resourcePath,
      @NonNull Resource elmoShapes, @NonNull ShaclValidator shaclValidator) {
    this.elmoConfiguration = elmoConfiguration;
    this.repository = repository;
    this.resourcePath = resourcePath;
    this.elmoShapes = elmoShapes;
    this.shaclValidator = shaclValidator;
    repository.initialize();
  }

  @Override
  public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void setEnvironment(@NonNull Environment environment) {
    this.environment = environment;
  }

  @Override
  public SailRepository getRepository() {
    return repository;
  }

  @PostConstruct
  public void loadResources() throws IOException {
    List<Resource> projectResources = new ArrayList<>(
        Arrays.asList(ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(
            resourcePath + "/model/**")));
    if (projectResources.isEmpty()) {
      LOG.warn("No model resources found in path:{}/model", resourcePath);
      return;
    }
    ConfigurationValidationHelper validationHelper =
        new ConfigurationValidationHelper(projectResources, environment);

    validationHelper.filterGlobalPrefixesResourceFromResources();

    validationHelper.addProjectResource(elmoConfiguration);
    validationHelper.addProjectResource(elmoShapes);

    validationHelper.prepareResourcesForValidation(repository);

    validationHelper.validateUsing(shaclValidator);
  }
}
