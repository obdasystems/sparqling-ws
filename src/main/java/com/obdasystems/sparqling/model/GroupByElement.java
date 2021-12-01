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
 * GroupByElement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-12-01T10:38:29.116Z[GMT]")public class GroupByElement  implements Serializable  {
  @JsonProperty("headElementIds")
  private List<Integer> headElementIds = null;

  @JsonProperty("function")
  private String function = null;

  public GroupByElement headElementIds(List<Integer> headElementIds) {
    this.headElementIds = headElementIds;
    return this;
  }

  public GroupByElement addHeadElementIdsItem(Integer headElementIdsItem) {
    if (this.headElementIds == null) {
      this.headElementIds = new ArrayList<Integer>();
    }
    this.headElementIds.add(headElementIdsItem);
    return this;
  }

  /**
   * Get headElementIds
   * @return headElementIds
   **/
  @JsonProperty("headElementIds")
  @Schema(description = "")
  public List<Integer> getHeadElementIds() {
    return headElementIds;
  }

  public void setHeadElementIds(List<Integer> headElementIds) {
    this.headElementIds = headElementIds;
  }

  public GroupByElement function(String function) {
    this.function = function;
    return this;
  }

  /**
   * Get function
   * @return function
   **/
  @JsonProperty("function")
  @Schema(description = "")
  public String getFunction() {
    return function;
  }

  public void setFunction(String function) {
    this.function = function;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupByElement groupByElement = (GroupByElement) o;
    return Objects.equals(this.headElementIds, groupByElement.headElementIds) &&
        Objects.equals(this.function, groupByElement.function);
  }

  @Override
  public int hashCode() {
    return Objects.hash(headElementIds, function);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupByElement {\n");
    
    sb.append("    headElementIds: ").append(toIndentedString(headElementIds)).append("\n");
    sb.append("    function: ").append(toIndentedString(function)).append("\n");
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
