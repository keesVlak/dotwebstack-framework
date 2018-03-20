package org.dotwebstack.framework.frontend.http.stage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.dotwebstack.framework.frontend.http.layout.Layout;
import org.dotwebstack.framework.frontend.http.site.Site;
import org.dotwebstack.framework.test.DBEERPEDIA;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StageTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private Layout layout;

  @Mock
  private Site siteMock;

  private Site site;

  private ValueFactory valueFactory = SimpleValueFactory.getInstance();

  @Test
  public void build_CreatesStage_WithValidData() {
    // Arrange/Act
    site = new Site.Builder(DBEERPEDIA.SITE).domain(DBEERPEDIA.NL_HOST).build();
    Stage stage = new Stage.Builder(DBEERPEDIA.BREWERIES, site).basePath(
        DBEERPEDIA.BASE_PATH.stringValue()).layout(layout).title(DBEERPEDIA.TITLE).build();

    // Assert
    assertThat(stage.getIdentifier(), equalTo(DBEERPEDIA.BREWERIES));
    assertThat(stage.getSite(), equalTo(site));
    assertThat(stage.getBasePath(), equalTo(DBEERPEDIA.BASE_PATH.stringValue()));
    assertThat(stage.getTitle(), equalTo(DBEERPEDIA.TITLE));
    assertThat(stage.getFullPath(),
        equalTo("/" + DBEERPEDIA.NL_HOST + DBEERPEDIA.BASE_PATH.stringValue()));
  }

  @Test
  public void build_CreatesStage_WithValidDataAndBNode() {
    // Act
    site = new Site.Builder(DBEERPEDIA.SITE).domain(DBEERPEDIA.NL_HOST).build();
    final BNode blankNode = valueFactory.createBNode();
    Stage stage =
        new Stage.Builder(blankNode, site).basePath(DBEERPEDIA.BASE_PATH.stringValue()).layout(
            layout).title(DBEERPEDIA.TITLE).build();

    // Assert
    assertThat(stage.getIdentifier(), equalTo(blankNode));
    assertThat(stage.getSite(), equalTo(site));
    assertThat(stage.getBasePath(), equalTo(DBEERPEDIA.BASE_PATH.stringValue()));
    assertThat(stage.getTitle(), equalTo(DBEERPEDIA.TITLE));
    assertThat(stage.getFullPath(),
        equalTo("/" + DBEERPEDIA.NL_HOST + DBEERPEDIA.BASE_PATH.stringValue()));
  }

  @Test
  public void build_CreatesStage_WhenMatchAllDomain() {
    // Act
    site = new Site.Builder(DBEERPEDIA.SITE).build();
    Stage stage = new Stage.Builder(DBEERPEDIA.BREWERIES, site).basePath(
        DBEERPEDIA.BASE_PATH.stringValue()).layout(layout).build();

    // Assert
    assertThat(stage.getIdentifier(), equalTo(DBEERPEDIA.BREWERIES));
    assertThat(stage.getSite(), equalTo(site));
    assertThat(stage.getBasePath(), equalTo(DBEERPEDIA.BASE_PATH.stringValue()));
    assertThat(stage.getFullPath(),
        equalTo("/" + Site.PATH_DOMAIN_PARAMETER + DBEERPEDIA.BASE_PATH.stringValue()));
  }

  @Test
  public void build_CreatesStageDefaults_WhenBasePathNotProvided() {
    // Act
    site = new Site.Builder(DBEERPEDIA.SITE).domain(DBEERPEDIA.NL_HOST).build();
    Stage stage = new Stage.Builder(DBEERPEDIA.BREWERIES, site).layout(layout).build();

    // Assert
    assertThat(stage.getIdentifier(), equalTo(DBEERPEDIA.BREWERIES));
    assertThat(stage.getSite(), equalTo(site));
    assertThat(stage.getBasePath(), equalTo(Stage.DEFAULT_BASE_PATH));
    assertThat(stage.getFullPath(), equalTo("/" + DBEERPEDIA.NL_HOST + Stage.DEFAULT_BASE_PATH));
  }

  @Test
  public void build_ThrowsException_WithMissingIdentifier() {
    // Assert
    thrown.expect(NullPointerException.class);

    // Act
    new Stage.Builder(null, siteMock).build();
  }

  @Test
  public void build_ThrowsException_WithMissingSite() {
    // Assert
    thrown.expect(NullPointerException.class);

    // Act
    new Stage.Builder(DBEERPEDIA.BREWERIES, null).build();
  }

  @Test
  public void basePath_ThrowsException_WithMissingValue() {
    // Assert
    thrown.expect(NullPointerException.class);

    // Act
    new Stage.Builder(DBEERPEDIA.BREWERIES, siteMock).basePath(null).build();
  }

  @Test
  public void layout_ThrowsException_WithMissingValue() {
    // Assert
    thrown.expect(NullPointerException.class);

    // Act
    new Stage.Builder(DBEERPEDIA.BREWERIES, siteMock).layout(null).build();
  }

}
