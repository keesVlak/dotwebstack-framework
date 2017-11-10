package org.dotwebstack.framework.frontend.ld.mappers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import org.dotwebstack.framework.backend.ResultType;
import org.dotwebstack.framework.frontend.http.HttpConfiguration;
import org.dotwebstack.framework.frontend.http.site.Site;
import org.dotwebstack.framework.frontend.http.stage.Stage;
import org.dotwebstack.framework.frontend.ld.SupportedMediaTypesScanner;
import org.dotwebstack.framework.frontend.ld.representation.Representation;
import org.dotwebstack.framework.frontend.ld.representation.RepresentationResourceProvider;
import org.dotwebstack.framework.informationproduct.InformationProduct;
import org.dotwebstack.framework.test.DBEERPEDIA;
import org.eclipse.rdf4j.model.IRI;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LdRepresentationRequestMapperTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Mock
  private Stage stage;

  @Mock
  private Site site;

  @Mock
  private InformationProduct informationProduct;

  @Mock
  private Representation representation;

  @Mock
  private RepresentationResourceProvider representationResourceProvider;

  @Mock
  private SupportedMediaTypesScanner supportedMediaTypesScanner;

  private LdRepresentationRequestMapper requestMapper;

  private HttpConfiguration httpConfiguration;

  @Before
  public void setUp() {
    site = new Site.Builder(DBEERPEDIA.BREWERIES).domain(DBEERPEDIA.DOMAIN.stringValue()).build();

    stage = new Stage.Builder(DBEERPEDIA.BREWERIES, site).basePath(
        DBEERPEDIA.BASE_PATH.stringValue()).build();

    when(informationProduct.getResultType()).thenReturn(ResultType.GRAPH);

    representation = new Representation.Builder(DBEERPEDIA.BREWERIES).informationProduct(
        informationProduct).stage(stage).urlPatterns(DBEERPEDIA.URL_PATTERN_VALUE).build();
    Map<IRI, Representation> representationMap = new HashMap<>();
    representationMap.put(representation.getIdentifier(), representation);

    when(representationResourceProvider.getAll()).thenReturn(representationMap);

    requestMapper = new LdRepresentationRequestMapper(representationResourceProvider,
        supportedMediaTypesScanner);

    httpConfiguration = new HttpConfiguration(ImmutableList.of());
  }

  @Test
  public void constructor_ThrowsException_WithMissingRepresentationResourceProvider() {
    // Assert
    thrown.expect(NullPointerException.class);

    // Act
    new LdRepresentationRequestMapper(null, supportedMediaTypesScanner);
  }

  @Test
  public void constructor_ThrowsException_WithMissingMediaTypesScanner() {
    // Assert
    thrown.expect(NullPointerException.class);

    // Act
    new LdRepresentationRequestMapper(representationResourceProvider, null);
  }

  @Test
  public void constructor_DoesNotThrowExceptions_WithValidData() {
    // Arrange / Act
    LdRepresentationRequestMapper requestMapper = new LdRepresentationRequestMapper(
        representationResourceProvider, supportedMediaTypesScanner);

    // Assert
    assertThat(requestMapper, not(nullValue()));
  }

  @Test
  public void loadRepresentations_MapRepresentation_WithValidData() {
    // Arrange
    when(supportedMediaTypesScanner.getMediaTypes(ResultType.GRAPH)).thenReturn(
        new MediaType[] {MediaType.valueOf("text/turtle")});

    // Act
    requestMapper.loadRepresentations(httpConfiguration);

    // Assert
    Resource resource = (Resource) httpConfiguration.getResources().toArray()[0];
    final ResourceMethod method = resource.getResourceMethods().get(0);
    assertThat(httpConfiguration.getResources(), hasSize(1));
    assertThat(resource.getPath(), equalTo("/" + DBEERPEDIA.ORG_HOST
        + DBEERPEDIA.BASE_PATH.getLabel() + DBEERPEDIA.URL_PATTERN_VALUE));
    assertThat(resource.getResourceMethods(), hasSize(1));
    assertThat(method.getHttpMethod(), equalTo(HttpMethod.GET));
  }

  @Test
  public void loadRepresentations_MapRepresentation_WithoutStage() {
    // Arrange
    representation = new Representation.Builder(DBEERPEDIA.BREWERIES).informationProduct(
        informationProduct).urlPatterns(DBEERPEDIA.URL_PATTERN_VALUE).build();
    Map<IRI, Representation> representationMap = new HashMap<>();
    representationMap.put(representation.getIdentifier(), representation);
    when(representationResourceProvider.getAll()).thenReturn(representationMap);

    // Act
    requestMapper.loadRepresentations(httpConfiguration);

    // Assert
    assertThat(httpConfiguration.getResources(), hasSize(0));
  }

  @Test
  public void loadRepresentations_MapRepresentation_WithoutNullStage() {
    // Arrange
    representation = new Representation.Builder(DBEERPEDIA.BREWERIES).informationProduct(
        informationProduct).urlPatterns(DBEERPEDIA.URL_PATTERN_VALUE).stage(null).build();
    Map<IRI, Representation> representationMap = new HashMap<>();
    representationMap.put(representation.getIdentifier(), representation);
    when(representationResourceProvider.getAll()).thenReturn(representationMap);

    // Act
    requestMapper.loadRepresentations(httpConfiguration);

    // Assert
    assertThat(httpConfiguration.getResources(), hasSize(0));
  }

  @Test
  public void loadRepresentations_IgnoreSecondRepresentation_WhenAddedTwice() {
    // Arrange
    when(supportedMediaTypesScanner.getMediaTypes(ResultType.GRAPH)).thenReturn(
        new MediaType[] {MediaType.valueOf("text/turtle")});

    Representation representation =
        new Representation.Builder(DBEERPEDIA.BREWERIES).informationProduct(
            informationProduct).urlPatterns(DBEERPEDIA.URL_PATTERN_VALUE).stage(stage).build();
    Representation samePathRepresentation =
        new Representation.Builder(DBEERPEDIA.GRAPH_BREWERY_LIST_REPRESENTATION).informationProduct(
            informationProduct).urlPatterns(DBEERPEDIA.URL_PATTERN_VALUE).stage(stage).build();
    Map<IRI, Representation> representationMap = new HashMap<>();
    representationMap.put(representation.getIdentifier(), representation);
    representationMap.put(samePathRepresentation.getIdentifier(), samePathRepresentation);
    when(representationResourceProvider.getAll()).thenReturn(representationMap);

    // Act
    requestMapper.loadRepresentations(httpConfiguration);

    // Assert
    assertThat(httpConfiguration.getResources(), hasSize(1));
  }

  @Test
  public void loadRepresentations_UsesPathDomainParameter_WithMatchAllDomain() {
    // Arrange
    when(supportedMediaTypesScanner.getMediaTypes(ResultType.GRAPH)).thenReturn(
        new MediaType[] {MediaType.valueOf("text/turtle")});

    Site site = new Site.Builder(DBEERPEDIA.BREWERIES).build();
    Stage stage = new Stage.Builder(DBEERPEDIA.BREWERIES, site).basePath(
        DBEERPEDIA.BASE_PATH.stringValue()).build();
    Representation representation =
        new Representation.Builder(DBEERPEDIA.BREWERIES).informationProduct(
            informationProduct).urlPatterns(DBEERPEDIA.URL_PATTERN_VALUE).stage(stage).build();

    Map<IRI, Representation> representationMap = new HashMap<>();
    representationMap.put(representation.getIdentifier(), representation);
    when(representationResourceProvider.getAll()).thenReturn(representationMap);

    // Act
    requestMapper.loadRepresentations(httpConfiguration);

    // Assert
    assertThat(httpConfiguration.getResources(), hasSize(1));
    Resource resource = (Resource) httpConfiguration.getResources().toArray()[0];
    assertThat(resource.getPath(), equalTo(
        "/{DOMAIN_PARAMETER}" + DBEERPEDIA.BASE_PATH.getLabel() + DBEERPEDIA.URL_PATTERN_VALUE));

  }

}