/*
 * Swagger Sparqling WS
 * This server will expose an API to Sparqling front end to create new SPARQL queries with a combinations of point and click on the [GRAPHOLscape](https://github.com/obdasystems/grapholscape) graph.  Sparqling will be released as a standalone appication but also the server will embedded in [MWS](https://github.com/obdasystems/mws) and Sparqling will be integrated in [Monolith](https://www.monolith.obdasystems.com/).
 *
 * OpenAPI spec version: 1.0.0
 * Contact: info@obdasystems.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.obdasystems.sparqling.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Entity
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-01-14T16:22:04.631Z[GMT]")public class Entity  implements Serializable  {
  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    CLASS("class"),
    
    OBJECTPROPERTY("objectProperty"),
    
    DATAPROPERTY("dataProperty");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("type")
  private TypeEnum type = null;

  @JsonProperty("iri")
  private String iri = null;

  @JsonProperty("prefixedIri")
  private String prefixedIri = null;

  @JsonProperty("labels")
  private Map<String, String> labels = null;

  public Entity type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   **/
  @JsonProperty("type")
  @Schema(description = "")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public Entity iri(String iri) {
    this.iri = iri;
    return this;
  }

  /**
   * Get iri
   * @return iri
   **/
  @JsonProperty("iri")
  @Schema(description = "")
  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public Entity prefixedIri(String prefixedIri) {
    this.prefixedIri = prefixedIri;
    return this;
  }

  /**
   * Get prefixedIri
   * @return prefixedIri
   **/
  @JsonProperty("prefixedIri")
  @Schema(description = "")
  public String getPrefixedIri() {
    return prefixedIri;
  }

  public void setPrefixedIri(String prefixedIri) {
    this.prefixedIri = prefixedIri;
  }

  public Entity labels(Map<String, String> labels) {
    this.labels = labels;
    return this;
  }

  public Entity putLabelsItem(String key, String labelsItem) {
    if (this.labels == null) {
      this.labels = new HashMap<String, String>();
    }
    this.labels.put(key, labelsItem);
    return this;
  }

  /**
   * Get labels
   * @return labels
   **/
  @JsonProperty("labels")
  @Schema(description = "")
  public Map<String, String> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Entity entity = (Entity) o;
    return Objects.equals(this.type, entity.type) &&
        Objects.equals(this.iri, entity.iri) &&
        Objects.equals(this.prefixedIri, entity.prefixedIri) &&
        Objects.equals(this.labels, entity.labels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, iri, prefixedIri, labels);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Entity {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    iri: ").append(toIndentedString(iri)).append("\n");
    sb.append("    prefixedIri: ").append(toIndentedString(prefixedIri)).append("\n");
    sb.append("    labels: ").append(toIndentedString(labels)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
