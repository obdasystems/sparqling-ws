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
import com.obdasystems.sparqling.model.Filter;
import com.obdasystems.sparqling.model.GraphElement;
import com.obdasystems.sparqling.model.HeadElement;
import com.obdasystems.sparqling.model.Optional;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * QueryGraph
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T10:38:12.914Z[GMT]")public class QueryGraph  implements Serializable  {
  @JsonProperty("distinct")
  private Boolean distinct = null;

  @JsonProperty("head")
  private List<HeadElement> head = new ArrayList<HeadElement>();

  @JsonProperty("graph")
  private GraphElement graph = null;

  @JsonProperty("filters")
  private List<Filter> filters = null;

  @JsonProperty("optionals")
  private List<Optional> optionals = null;

  @JsonProperty("limit")
  private Integer limit = null;

  @JsonProperty("offset")
  private Integer offset = null;

  @JsonProperty("sparql")
  private String sparql = null;

  public QueryGraph distinct(Boolean distinct) {
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

  public QueryGraph head(List<HeadElement> head) {
    this.head = head;
    return this;
  }

  public QueryGraph addHeadItem(HeadElement headItem) {
    this.head.add(headItem);
    return this;
  }

  /**
   * Get head
   * @return head
   **/
  @JsonProperty("head")
  @Schema(required = true, description = "")
  @NotNull
  @Valid
  public List<HeadElement> getHead() {
    return head;
  }

  public void setHead(List<HeadElement> head) {
    this.head = head;
  }

  public QueryGraph graph(GraphElement graph) {
    this.graph = graph;
    return this;
  }

  /**
   * Get graph
   * @return graph
   **/
  @JsonProperty("graph")
  @Schema(required = true, description = "")
  @NotNull
  @Valid
  public GraphElement getGraph() {
    return graph;
  }

  public void setGraph(GraphElement graph) {
    this.graph = graph;
  }

  public QueryGraph filters(List<Filter> filters) {
    this.filters = filters;
    return this;
  }

  public QueryGraph addFiltersItem(Filter filtersItem) {
    if (this.filters == null) {
      this.filters = new ArrayList<Filter>();
    }
    this.filters.add(filtersItem);
    return this;
  }

  /**
   * Get filters
   * @return filters
   **/
  @JsonProperty("filters")
  @Schema(description = "")
  @Valid
  public List<Filter> getFilters() {
    return filters;
  }

  public void setFilters(List<Filter> filters) {
    this.filters = filters;
  }

  public QueryGraph optionals(List<Optional> optionals) {
    this.optionals = optionals;
    return this;
  }

  public QueryGraph addOptionalsItem(Optional optionalsItem) {
    if (this.optionals == null) {
      this.optionals = new ArrayList<Optional>();
    }
    this.optionals.add(optionalsItem);
    return this;
  }

  /**
   * Get optionals
   * @return optionals
   **/
  @JsonProperty("optionals")
  @Schema(description = "")
  @Valid
  public List<Optional> getOptionals() {
    return optionals;
  }

  public void setOptionals(List<Optional> optionals) {
    this.optionals = optionals;
  }

  public QueryGraph limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Get limit
   * @return limit
   **/
  @JsonProperty("limit")
  @Schema(description = "")
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public QueryGraph offset(Integer offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Get offset
   * @return offset
   **/
  @JsonProperty("offset")
  @Schema(description = "")
  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public QueryGraph sparql(String sparql) {
    this.sparql = sparql;
    return this;
  }

  /**
   * Get sparql
   * @return sparql
   **/
  @JsonProperty("sparql")
  @Schema(required = true, description = "")
  @NotNull
  public String getSparql() {
    return sparql;
  }

  public void setSparql(String sparql) {
    this.sparql = sparql;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryGraph queryGraph = (QueryGraph) o;
    return Objects.equals(this.distinct, queryGraph.distinct) &&
        Objects.equals(this.head, queryGraph.head) &&
        Objects.equals(this.graph, queryGraph.graph) &&
        Objects.equals(this.filters, queryGraph.filters) &&
        Objects.equals(this.optionals, queryGraph.optionals) &&
        Objects.equals(this.limit, queryGraph.limit) &&
        Objects.equals(this.offset, queryGraph.offset) &&
        Objects.equals(this.sparql, queryGraph.sparql);
  }

  @Override
  public int hashCode() {
    return Objects.hash(distinct, head, graph, filters, optionals, limit, offset, sparql);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryGraph {\n");
    
    sb.append("    distinct: ").append(toIndentedString(distinct)).append("\n");
    sb.append("    head: ").append(toIndentedString(head)).append("\n");
    sb.append("    graph: ").append(toIndentedString(graph)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
    sb.append("    optionals: ").append(toIndentedString(optionals)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    sparql: ").append(toIndentedString(sparql)).append("\n");
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
