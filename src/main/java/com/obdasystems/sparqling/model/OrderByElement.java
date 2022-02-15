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
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * OrderByElement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-02-15T15:04:14.983Z[GMT]")public class OrderByElement  implements Serializable  {
  @JsonProperty("headElementId")
  private Integer headElementId = null;

  @JsonProperty("ascending")
  private Boolean ascending = null;

  public OrderByElement headElementId(Integer headElementId) {
    this.headElementId = headElementId;
    return this;
  }

  /**
   * Get headElementId
   * @return headElementId
   **/
  @JsonProperty("headElementId")
  @Schema(description = "")
  public Integer getHeadElementId() {
    return headElementId;
  }

  public void setHeadElementId(Integer headElementId) {
    this.headElementId = headElementId;
  }

  public OrderByElement ascending(Boolean ascending) {
    this.ascending = ascending;
    return this;
  }

  /**
   * Get ascending
   * @return ascending
   **/
  @JsonProperty("ascending")
  @Schema(description = "")
  public Boolean isAscending() {
    return ascending;
  }

  public void setAscending(Boolean ascending) {
    this.ascending = ascending;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderByElement orderByElement = (OrderByElement) o;
    return Objects.equals(this.headElementId, orderByElement.headElementId) &&
        Objects.equals(this.ascending, orderByElement.ascending);
  }

  @Override
  public int hashCode() {
    return Objects.hash(headElementId, ascending);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderByElement {\n");
    
    sb.append("    headElementId: ").append(toIndentedString(headElementId)).append("\n");
    sb.append("    ascending: ").append(toIndentedString(ascending)).append("\n");
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
