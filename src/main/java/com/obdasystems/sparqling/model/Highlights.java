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
import com.obdasystems.sparqling.model.Branch;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Highlights
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T10:38:12.914Z[GMT]")public class Highlights  implements Serializable  {
  @JsonProperty("classes")
  private List<String> classes = null;

  @JsonProperty("objectProperties")
  private List<Branch> objectProperties = null;

  @JsonProperty("dataProperties")
  private List<String> dataProperties = null;

  public Highlights classes(List<String> classes) {
    this.classes = classes;
    return this;
  }

  public Highlights addClassesItem(String classesItem) {
    if (this.classes == null) {
      this.classes = new ArrayList<String>();
    }
    this.classes.add(classesItem);
    return this;
  }

  /**
   * Subclasses or brother classes
   * @return classes
   **/
  @JsonProperty("classes")
  @Schema(description = "Subclasses or brother classes")
  public List<String> getClasses() {
    return classes;
  }

  public void setClasses(List<String> classes) {
    this.classes = classes;
  }

  public Highlights objectProperties(List<Branch> objectProperties) {
    this.objectProperties = objectProperties;
    return this;
  }

  public Highlights addObjectPropertiesItem(Branch objectPropertiesItem) {
    if (this.objectProperties == null) {
      this.objectProperties = new ArrayList<Branch>();
    }
    this.objectProperties.add(objectPropertiesItem);
    return this;
  }

  /**
   * Get objectProperties
   * @return objectProperties
   **/
  @JsonProperty("objectProperties")
  @Schema(description = "")
  @Valid
  public List<Branch> getObjectProperties() {
    return objectProperties;
  }

  public void setObjectProperties(List<Branch> objectProperties) {
    this.objectProperties = objectProperties;
  }

  public Highlights dataProperties(List<String> dataProperties) {
    this.dataProperties = dataProperties;
    return this;
  }

  public Highlights addDataPropertiesItem(String dataPropertiesItem) {
    if (this.dataProperties == null) {
      this.dataProperties = new ArrayList<String>();
    }
    this.dataProperties.add(dataPropertiesItem);
    return this;
  }

  /**
   * Get dataProperties
   * @return dataProperties
   **/
  @JsonProperty("dataProperties")
  @Schema(description = "")
  public List<String> getDataProperties() {
    return dataProperties;
  }

  public void setDataProperties(List<String> dataProperties) {
    this.dataProperties = dataProperties;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Highlights highlights = (Highlights) o;
    return Objects.equals(this.classes, highlights.classes) &&
        Objects.equals(this.objectProperties, highlights.objectProperties) &&
        Objects.equals(this.dataProperties, highlights.dataProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classes, objectProperties, dataProperties);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Highlights {\n");
    
    sb.append("    classes: ").append(toIndentedString(classes)).append("\n");
    sb.append("    objectProperties: ").append(toIndentedString(objectProperties)).append("\n");
    sb.append("    dataProperties: ").append(toIndentedString(dataProperties)).append("\n");
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
