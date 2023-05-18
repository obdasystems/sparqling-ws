/*
 * Swagger Sparqling WS
 * This server will expose an API to Sparqling front end to create new SPARQL queries with a combinations of point and click on the [GRAPHOLscape](https://github.com/obdasystems/grapholscape) graph.  Sparqling will be released as a standalone appication but also the server will embedded in [MWS](https://github.com/obdasystems/mws) and Sparqling will be integrated in [Monolith](https://www.monolith.obdasystems.com/).
 *
 * OpenAPI spec version: 1.0.2
 * Contact: info@obdasystems.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.obdasystems.sparqling.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * GroupByElement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2023-01-04T12:09:21.062Z[GMT]")public class GroupByElement   {
  @JsonProperty("distinct")
  private Boolean distinct = null;

  /**
   * Gets or Sets aggregateFunction
   */
  public enum AggregateFunctionEnum {
    COUNT("count"),
    
    SUM("sum"),
    
    MIN("min"),
    
    MAX("max"),
    
    AVERAGE("average");

    private String value;

    AggregateFunctionEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AggregateFunctionEnum fromValue(String text) {
      for (AggregateFunctionEnum b : AggregateFunctionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("aggregateFunction")
  private AggregateFunctionEnum aggregateFunction = null;

  public GroupByElement distinct(Boolean distinct) {
    this.distinct = distinct;
    return this;
  }

  /**
   * Get distinct
   * @return distinct
   **/
  @JsonProperty("distinct")
  @Schema(description = "")
  public Boolean isDistinct() {
    return distinct;
  }

  public void setDistinct(Boolean distinct) {
    this.distinct = distinct;
  }

  public GroupByElement aggregateFunction(AggregateFunctionEnum aggregateFunction) {
    this.aggregateFunction = aggregateFunction;
    return this;
  }

  /**
   * Get aggregateFunction
   * @return aggregateFunction
   **/
  @JsonProperty("aggregateFunction")
  @Schema(description = "")
  public AggregateFunctionEnum getAggregateFunction() {
    return aggregateFunction;
  }

  public void setAggregateFunction(AggregateFunctionEnum aggregateFunction) {
    this.aggregateFunction = aggregateFunction;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupByElement groupByElement = (GroupByElement) o;
    return Objects.equals(this.distinct, groupByElement.distinct) &&
        Objects.equals(this.aggregateFunction, groupByElement.aggregateFunction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(distinct, aggregateFunction);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupByElement {\n");
    
    sb.append("    distinct: ").append(toIndentedString(distinct)).append("\n");
    sb.append("    aggregateFunction: ").append(toIndentedString(aggregateFunction)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
