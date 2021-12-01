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
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * OntologyPath
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-12-01T16:52:13.648Z[GMT]")public class OntologyPath  implements Serializable  {
  @JsonProperty("entities")
  private List<String> entities = null;

  public OntologyPath entities(List<String> entities) {
    this.entities = entities;
    return this;
  }

  public OntologyPath addEntitiesItem(String entitiesItem) {
    if (this.entities == null) {
      this.entities = new ArrayList<String>();
    }
    this.entities.add(entitiesItem);
    return this;
  }

  /**
   * Starts with lastSelectedIRI and ends with clickedIRI. In between the nodes and edges traversed in the path (ISA edge are marked as ISA)
   * @return entities
   **/
  @JsonProperty("entities")
  @Schema(description = "Starts with lastSelectedIRI and ends with clickedIRI. In between the nodes and edges traversed in the path (ISA edge are marked as ISA)")
  public List<String> getEntities() {
    return entities;
  }

  public void setEntities(List<String> entities) {
    this.entities = entities;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OntologyPath ontologyPath = (OntologyPath) o;
    return Objects.equals(this.entities, ontologyPath.entities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entities);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OntologyPath {\n");
    
    sb.append("    entities: ").append(toIndentedString(entities)).append("\n");
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
