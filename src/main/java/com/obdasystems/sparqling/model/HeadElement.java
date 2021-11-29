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
 * HeadElement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-11-29T11:28:53.694Z[GMT]")public class HeadElement  implements Serializable  {
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("graphElementId")
  private Integer graphElementId = null;

  @JsonProperty("var")
  private String var = null;

  @JsonProperty("alias")
  private String alias = null;

  @JsonProperty("hidden")
  private Boolean hidden = null;

  @JsonProperty("distinct")
  private Boolean distinct = null;

  public HeadElement id(Integer id) {
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

  public HeadElement graphElementId(Integer graphElementId) {
    this.graphElementId = graphElementId;
    return this;
  }

  /**
   * Get graphElementId
   * @return graphElementId
   **/
  @JsonProperty("graphElementId")
  @Schema(description = "")
  public Integer getGraphElementId() {
    return graphElementId;
  }

  public void setGraphElementId(Integer graphElementId) {
    this.graphElementId = graphElementId;
  }

  public HeadElement var(String var) {
    this.var = var;
    return this;
  }

  /**
   * Get var
   * @return var
   **/
  @JsonProperty("var")
  @Schema(description = "")
  public String getVar() {
    return var;
  }

  public void setVar(String var) {
    this.var = var;
  }

  public HeadElement alias(String alias) {
    this.alias = alias;
    return this;
  }

  /**
   * Get alias
   * @return alias
   **/
  @JsonProperty("alias")
  @Schema(description = "")
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public HeadElement hidden(Boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  /**
   * Get hidden
   * @return hidden
   **/
  @JsonProperty("hidden")
  @Schema(description = "")
  public Boolean isHidden() {
    return hidden;
  }

  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  public HeadElement distinct(Boolean distinct) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HeadElement headElement = (HeadElement) o;
    return Objects.equals(this.id, headElement.id) &&
        Objects.equals(this.graphElementId, headElement.graphElementId) &&
        Objects.equals(this.var, headElement.var) &&
        Objects.equals(this.alias, headElement.alias) &&
        Objects.equals(this.hidden, headElement.hidden) &&
        Objects.equals(this.distinct, headElement.distinct);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, graphElementId, var, alias, hidden, distinct);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HeadElement {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    graphElementId: ").append(toIndentedString(graphElementId)).append("\n");
    sb.append("    var: ").append(toIndentedString(var)).append("\n");
    sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
    sb.append("    hidden: ").append(toIndentedString(hidden)).append("\n");
    sb.append("    distinct: ").append(toIndentedString(distinct)).append("\n");
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
