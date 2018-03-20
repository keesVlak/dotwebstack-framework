package org.dotwebstack.framework.frontend.http.site;

import lombok.NonNull;
import org.dotwebstack.framework.frontend.http.layout.Layout;
import org.eclipse.rdf4j.model.Resource;

public class Site {

  public static final String PATH_DOMAIN_PARAMETER = "{DOMAIN_PARAMETER}";

  private Resource identifier;

  private String domain = null;

  private String basePath;

  private Layout layout;

  private Site(Builder builder) {
    this.domain = builder.domain;
    this.identifier = builder.identifier;
    this.layout = builder.layout;
    this.basePath = builder.basePath;
  }

  public Resource getIdentifier() {
    return identifier;
  }

  public String getDomain() {
    return domain;
  }

  public String getBasePath() {
    if (isMatchAllDomain()) {
      return PATH_DOMAIN_PARAMETER + basePath;
    }

    return domain + basePath;
  }

  public Boolean isMatchAllDomain() {
    return domain == null;
  }

  public Layout getLayout() {
    return layout;
  }

  public static class Builder {

    private Resource identifier;

    // Default is match all domain
    private String domain = null;

    private String basePath = "";

    private Layout layout;

    public Builder(@NonNull Resource identifier) {
      this.identifier = identifier;
    }

    public Builder domain(@NonNull String domain) {
      this.domain = domain;
      return this;
    }

    public Site build() {
      return new Site(this);
    }

    public Builder basePath(@NonNull String basePath) {
      this.basePath = basePath;
      return this;
    }

    public Builder layout(@NonNull Layout layout) {
      this.layout = layout;
      return this;
    }

  }

}
