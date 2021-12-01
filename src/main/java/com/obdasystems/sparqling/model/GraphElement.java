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
import com.obdasystems.sparqling.model.Entity;
import com.obdasystems.sparqling.model.GraphElement;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * GraphElement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-12-01T16:52:13.648Z[GMT]")public class GraphElement  implements Serializable  {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("entities")
  private List<Entity> entities = null;

  @JsonProperty("children")
  private List<GraphElement> children = null;

  public GraphElement id(String id) {
    this.id = id;
    return this;
  }

  /**
   * This id corrensopond to the variable when entity type is a class.
   * @return id
   **/
  @JsonProperty("id")
  @Schema(description = "This id corrensopond to the variable when entity type is a class.")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public GraphElement entities(List<Entity> entities) {
    this.entities = entities;
    return this;
  }

  public GraphElement addEntitiesItem(Entity entitiesItem) {
    if (this.entities == null) {
      this.entities = new ArrayList<Entity>();
    }
    this.entities.add(entitiesItem);
    return this;
  }

  /**
   * It could have more than one entity only when entity type is a class (could be derived after clicking on two borther classes)
   * @return entities
   **/
  @JsonProperty("entities")
  @Schema(description = "It could have more than one entity only when entity type is a class (could be derived after clicking on two borther classes)")
  @Valid
  public List<Entity> getEntities() {
    return entities;
  }

  public void setEntities(List<Entity> entities) {
    this.entities = entities;
  }

  public GraphElement children(List<GraphElement> children) {
    this.children = children;
    return this;
  }

  public GraphElement addChildrenItem(GraphElement childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<GraphElement>();
    }
    this.children.add(childrenItem);
    return this;
  }

  /**
   * Get children
   * @return children
   **/
  @JsonProperty("children")
  @Schema(description = "")
  @Valid
  public List<GraphElement> getChildren() {
    return children;
  }

  public void setChildren(List<GraphElement> children) {
    this.children = children;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GraphElement graphElement = (GraphElement) o;
    return Objects.equals(this.id, graphElement.id) &&
        Objects.equals(this.entities, graphElement.entities) &&
        Objects.equals(this.children, graphElement.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, entities, children);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GraphElement {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    entities: ").append(toIndentedString(entities)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
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
