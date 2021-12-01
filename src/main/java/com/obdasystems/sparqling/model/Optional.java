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
 * Optional
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-12-01T16:52:13.648Z[GMT]")public class Optional  implements Serializable  {
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("graphIds")
  private List<String> graphIds = null;

  public Optional id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/
  @JsonProperty("id")
  @Schema(description = "")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Optional graphIds(List<String> graphIds) {
    this.graphIds = graphIds;
    return this;
  }

  public Optional addGraphIdsItem(String graphIdsItem) {
    if (this.graphIds == null) {
      this.graphIds = new ArrayList<String>();
    }
    this.graphIds.add(graphIdsItem);
    return this;
  }

  /**
   * Get graphIds
   * @return graphIds
   **/
  @JsonProperty("graphIds")
  @Schema(description = "")
  public List<String> getGraphIds() {
    return graphIds;
  }

  public void setGraphIds(List<String> graphIds) {
    this.graphIds = graphIds;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Optional optional = (Optional) o;
    return Objects.equals(this.id, optional.id) &&
        Objects.equals(this.graphIds, optional.graphIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, graphIds);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Optional {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    graphIds: ").append(toIndentedString(graphIds)).append("\n");
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
