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
 * BinaryExpression
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-12-01T10:38:29.116Z[GMT]")public class BinaryExpression  implements Serializable  {
  @JsonProperty("operator")
  private String operator = null;

  @JsonProperty("leftOperand")
  private String leftOperand = null;

  @JsonProperty("rightOperand")
  private String rightOperand = null;

  public BinaryExpression operator(String operator) {
    this.operator = operator;
    return this;
  }

  /**
   * Get operator
   * @return operator
   **/
  @JsonProperty("operator")
  @Schema(description = "")
  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public BinaryExpression leftOperand(String leftOperand) {
    this.leftOperand = leftOperand;
    return this;
  }

  /**
   * Could be a value or a GraphElement id
   * @return leftOperand
   **/
  @JsonProperty("leftOperand")
  @Schema(description = "Could be a value or a GraphElement id")
  public String getLeftOperand() {
    return leftOperand;
  }

  public void setLeftOperand(String leftOperand) {
    this.leftOperand = leftOperand;
  }

  public BinaryExpression rightOperand(String rightOperand) {
    this.rightOperand = rightOperand;
    return this;
  }

  /**
   * Could be a value or a GraphElement id
   * @return rightOperand
   **/
  @JsonProperty("rightOperand")
  @Schema(description = "Could be a value or a GraphElement id")
  public String getRightOperand() {
    return rightOperand;
  }

  public void setRightOperand(String rightOperand) {
    this.rightOperand = rightOperand;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BinaryExpression binaryExpression = (BinaryExpression) o;
    return Objects.equals(this.operator, binaryExpression.operator) &&
        Objects.equals(this.leftOperand, binaryExpression.leftOperand) &&
        Objects.equals(this.rightOperand, binaryExpression.rightOperand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operator, leftOperand, rightOperand);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BinaryExpression {\n");
    
    sb.append("    operator: ").append(toIndentedString(operator)).append("\n");
    sb.append("    leftOperand: ").append(toIndentedString(leftOperand)).append("\n");
    sb.append("    rightOperand: ").append(toIndentedString(rightOperand)).append("\n");
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
