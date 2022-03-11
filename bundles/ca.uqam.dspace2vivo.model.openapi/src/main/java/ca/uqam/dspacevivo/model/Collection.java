/*
 * DSpace - VIVO integration project
 * This is the \"DSpace - VIVO integration project\" based on the OpenAPI 3.0 specification. You can find out more about Swagger at [http://swagger.io](http://swagger.io). In the third iteration of the pet store, we've switched to the design first approach! You can now help us improve the API whether it's by making changes to the definition itself or to the code. That way, with time, we can improve the API in general, and expose some of the new features in OAS3. Some useful links: - [DSpace-VIVO - Integration project of DSpace metadata with VIVO](https://github.com/vivo-community/DSpace-VIVO)  - [The Pet Store repository](https://github.com/swagger-api/swagger-petstore) - [The source API definition for the Pet Store](https://github.com/swagger-api/swagger-petstore/blob/master/src/main/resources/openapi.yaml)
 *
 * OpenAPI spec version: 1.0.0
 * Contact: vivo@uqam.ca
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ca.uqam.dspacevivo.model;

import java.util.Objects;
import ca.uqam.dspacevivo.model.Statement;
import ca.uqam.dspacevivo.model.StatementLiteral;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Collection
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-03-11T10:04:27.674-05:00[America/New_York]")public class Collection   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("uri")
  private String uri = null;

  @JsonProperty("url")
  private String url = null;

  @JsonProperty("hasItem")
  private List<String> hasItem = new ArrayList<>();

  @JsonProperty("isPartOfCommunity")
  private List<String> isPartOfCommunity = new ArrayList<>();

  @JsonProperty("listOfStatements")
  private List<Statement> listOfStatements = null;

  @JsonProperty("listOfStatementLiterals")
  private List<StatementLiteral> listOfStatementLiterals = null;

  public Collection id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/
  @JsonProperty("id")
  @Schema(example = "123456789/2", required = true, description = "")
  @NotNull
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Collection uri(String uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
   **/
  @JsonProperty("uri")
  @Schema(example = "http://localhost:4000/handle/123456789/2", required = true, description = "")
  @NotNull
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Collection url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
   **/
  @JsonProperty("url")
  @Schema(example = "http://localhost:4000/items/948d534a-e1d9-41b2-bb23-1ae2fe9cff4f", required = true, description = "")
  @NotNull
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Collection hasItem(List<String> hasItem) {
    this.hasItem = hasItem;
    return this;
  }

  public Collection addHasItemItem(String hasItemItem) {
    this.hasItem.add(hasItemItem);
    return this;
  }

  /**
   * Get hasItem
   * @return hasItem
   **/
  @JsonProperty("hasItem")
  @Schema(example = "123456789/4,123456789/3", required = true, description = "")
  @NotNull
  public List<String> getHasItem() {
    return hasItem;
  }

  public void setHasItem(List<String> hasItem) {
    this.hasItem = hasItem;
  }

  public Collection isPartOfCommunity(List<String> isPartOfCommunity) {
    this.isPartOfCommunity = isPartOfCommunity;
    return this;
  }

  public Collection addIsPartOfCommunityItem(String isPartOfCommunityItem) {
    this.isPartOfCommunity.add(isPartOfCommunityItem);
    return this;
  }

  /**
   * Get isPartOfCommunity
   * @return isPartOfCommunity
   **/
  @JsonProperty("isPartOfCommunity")
  @Schema(example = "123456789/1", required = true, description = "")
  @NotNull
  public List<String> getIsPartOfCommunity() {
    return isPartOfCommunity;
  }

  public void setIsPartOfCommunity(List<String> isPartOfCommunity) {
    this.isPartOfCommunity = isPartOfCommunity;
  }

  public Collection listOfStatements(List<Statement> listOfStatements) {
    this.listOfStatements = listOfStatements;
    return this;
  }

  public Collection addListOfStatementsItem(Statement listOfStatementsItem) {
    if (this.listOfStatements == null) {
      this.listOfStatements = new ArrayList<>();
    }
    this.listOfStatements.add(listOfStatementsItem);
    return this;
  }

  /**
   * Get listOfStatements
   * @return listOfStatements
   **/
  @JsonProperty("listOfStatements")
  @Schema(description = "")
  @Valid
  public List<Statement> getListOfStatements() {
    return listOfStatements;
  }

  public void setListOfStatements(List<Statement> listOfStatements) {
    this.listOfStatements = listOfStatements;
  }

  public Collection listOfStatementLiterals(List<StatementLiteral> listOfStatementLiterals) {
    this.listOfStatementLiterals = listOfStatementLiterals;
    return this;
  }

  public Collection addListOfStatementLiteralsItem(StatementLiteral listOfStatementLiteralsItem) {
    if (this.listOfStatementLiterals == null) {
      this.listOfStatementLiterals = new ArrayList<>();
    }
    this.listOfStatementLiterals.add(listOfStatementLiteralsItem);
    return this;
  }

  /**
   * Get listOfStatementLiterals
   * @return listOfStatementLiterals
   **/
  @JsonProperty("listOfStatementLiterals")
  @Schema(description = "")
  @Valid
  public List<StatementLiteral> getListOfStatementLiterals() {
    return listOfStatementLiterals;
  }

  public void setListOfStatementLiterals(List<StatementLiteral> listOfStatementLiterals) {
    this.listOfStatementLiterals = listOfStatementLiterals;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Collection collection = (Collection) o;
    return Objects.equals(this.id, collection.id) &&
        Objects.equals(this.uri, collection.uri) &&
        Objects.equals(this.url, collection.url) &&
        Objects.equals(this.hasItem, collection.hasItem) &&
        Objects.equals(this.isPartOfCommunity, collection.isPartOfCommunity) &&
        Objects.equals(this.listOfStatements, collection.listOfStatements) &&
        Objects.equals(this.listOfStatementLiterals, collection.listOfStatementLiterals);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uri, url, hasItem, isPartOfCommunity, listOfStatements, listOfStatementLiterals);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Collection {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    hasItem: ").append(toIndentedString(hasItem)).append("\n");
    sb.append("    isPartOfCommunity: ").append(toIndentedString(isPartOfCommunity)).append("\n");
    sb.append("    listOfStatements: ").append(toIndentedString(listOfStatements)).append("\n");
    sb.append("    listOfStatementLiterals: ").append(toIndentedString(listOfStatementLiterals)).append("\n");
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
