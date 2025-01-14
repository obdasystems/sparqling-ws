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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.obdasystems.sparqling.model.VarOrConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * FilterExpression
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2023-01-04T12:09:21.062Z[GMT]")public class FilterExpression   {
  /**
   * Gets or Sets operator
   */
  public enum OperatorEnum {
    EQUAL("="),
    
    NOT_EQUAL("!="),
    
    LESS_THAN("<"),
    
    GREATER_THAN(">"),
    
    LESS_THAN_OR_EQUAL_TO("<="),
    
    GREATER_THAN_OR_EQUAL_TO(">="),
    
    IN("IN"),
    
    NOT_IN("NOT IN"),
    
    REGEX("REGEX"),
    
    ISBLANK("ISBLANK"),
    
    NOT_ISBLANK("NOT ISBLANK");

    private String value;

    OperatorEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static OperatorEnum fromValue(String text) {
      for (OperatorEnum b : OperatorEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("operator")
  private OperatorEnum operator = null;

  @JsonProperty("parameters")
  private List<VarOrConstant> parameters = null;

  public FilterExpression operator(OperatorEnum operator) {
    this.operator = operator;
    return this;
  }

  /**
   * Get operator
   * @return operator
   **/
  @JsonProperty("operator")
  @Schema(description = "")
  public OperatorEnum getOperator() {
    return operator;
  }

  public void setOperator(OperatorEnum operator) {
    this.operator = operator;
  }

  public FilterExpression parameters(List<VarOrConstant> parameters) {
    this.parameters = parameters;
    return this;
  }

  public FilterExpression addParametersItem(VarOrConstant parametersItem) {
    if (this.parameters == null) {
      this.parameters = new ArrayList<VarOrConstant>();
    }
    this.parameters.add(parametersItem);
    return this;
  }

  /**
   * Get parameters
   * @return parameters
   **/
  @JsonProperty("parameters")
  @Schema(description = "")
  @Valid
  public List<VarOrConstant> getParameters() {
    return parameters;
  }

  public void setParameters(List<VarOrConstant> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterExpression filterExpression = (FilterExpression) o;
    return Objects.equals(this.operator, filterExpression.operator) &&
        Objects.equals(this.parameters, filterExpression.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operator, parameters);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterExpression {\n");
    
    sb.append("    operator: ").append(toIndentedString(operator)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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
