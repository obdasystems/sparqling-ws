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
 * VarOrConstant
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-01-14T16:22:04.631Z[GMT]")public class VarOrConstant  implements Serializable  {
  @JsonProperty("type")
  private String type = null;

  @JsonProperty("value")
  private String value = null;

  @JsonProperty("constantType")
  private String constantType = null;

  public VarOrConstant type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   **/
  @JsonProperty("type")
  @Schema(description = "")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public VarOrConstant value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
   **/
  @JsonProperty("value")
  @Schema(description = "")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public VarOrConstant constantType(String constantType) {
    this.constantType = constantType;
    return this;
  }

  /**
   * Get constantType
   * @return constantType
   **/
  @JsonProperty("constantType")
  @Schema(description = "")
  public String getConstantType() {
    return constantType;
  }

  public void setConstantType(String constantType) {
    this.constantType = constantType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VarOrConstant varOrConstant = (VarOrConstant) o;
    return Objects.equals(this.type, varOrConstant.type) &&
        Objects.equals(this.value, varOrConstant.value) &&
        Objects.equals(this.constantType, varOrConstant.constantType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value, constantType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VarOrConstant {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    constantType: ").append(toIndentedString(constantType)).append("\n");
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
