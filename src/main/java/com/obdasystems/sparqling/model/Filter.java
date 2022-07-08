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
import com.obdasystems.sparqling.model.FilterExpression;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Filter
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T10:38:12.914Z[GMT]")public class Filter  implements Serializable  {
  @JsonProperty("expression")
  private FilterExpression expression = null;

  public Filter expression(FilterExpression expression) {
    this.expression = expression;
    return this;
  }

  /**
   * Get expression
   * @return expression
   **/
  @JsonProperty("expression")
  @Schema(description = "")
  @Valid
  public FilterExpression getExpression() {
    return expression;
  }

  public void setExpression(FilterExpression expression) {
    this.expression = expression;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Filter filter = (Filter) o;
    return Objects.equals(this.expression, filter.expression);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expression);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Filter {\n");
    
    sb.append("    expression: ").append(toIndentedString(expression)).append("\n");
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
