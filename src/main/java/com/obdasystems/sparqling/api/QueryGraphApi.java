package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.api.QueryGraphApiService;
import com.obdasystems.sparqling.api.factories.QueryGraphApiServiceFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.obdasystems.sparqling.model.QueryGraph;

import java.util.Map;
import java.util.List;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;

@Path("/queryGraph")



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-11-29T11:28:53.694Z[GMT]")public class QueryGraphApi  {
   private final QueryGraphApiService delegate;

   public QueryGraphApi(@Context ServletConfig servletContext) {
      QueryGraphApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("QueryGraphApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (QueryGraphApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = QueryGraphApiServiceFactory.getQueryGraphApi();
      }

      this.delegate = delegate;
   }

    @PUT
    @Path("/head/add/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Add the head term to the query graph.", description = "Explicitley add a term to the query head. All the data property variables are added automatically to the head during the query graph construction. This will add to the head only variables associated to classes (`rdf:type` triple pattern) or data properties.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Graph Node not found") })
    public Response addHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The id of the graph node that should be added to the head",required=true) @PathParam("graphElementId") String graphElementId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.addHeadTerm(body,graphElementId,securityContext);
    }
    @PUT
    @Path("/path")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Get the query graph that will be rendered by Sparqling, the query head, the sparql code based on the chosen path.", description = "This path should be used to build the query graph using the path interaction. As a result there will be added to the query several triple pattern (depending on the length of the path) as a sequence of classes and object properties. Data properties never appear in paths, in order to add them use the simple PUT route.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),
        
        @ApiResponse(responseCode = "404", description = "Entity not found") })
    public Response addPathToQueryGraph(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "Serialization of Path object.",required=true) @QueryParam("path") String path
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.addPathToQueryGraph(body,path,securityContext);
    }
    @PUT
    @Path("/node/delete/{graphElementId}")
    
    @Produces({ "application/json" })
    @Operation(summary = "Delete the GraphElement (and all its children) from the query graph and head.", description = "This route is used when the user wants to delete a node from the query graph. All the children of this node will be deleted as well as we do not want to create query with completly separated branches. All the variables that are going to be deleted should also be deleted from the head of the query. **WARNING**, if the node has multiple occurrences (due to join operations) every node should be deleted.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response deleteGraphElementId(@Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be delete",required=true) @PathParam("graphElementId") String graphElementId
,@Parameter(in = ParameterIn.DEFAULT, description = "",required=true) QueryGraph body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteGraphElementId(graphElementId,body,securityContext);
    }
    @PUT
    @Path("/head/delete/{headTerm}")
    
    @Produces({ "application/json" })
    @Operation(summary = "Delete the head term from the query graph.", description = "", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response deleteHeadTerm(@Parameter(in = ParameterIn.PATH, description = "The head term that should be delete",required=true) @PathParam("headTerm") String headTerm
,@Parameter(in = ParameterIn.DEFAULT, description = "",required=true) QueryGraph body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteHeadTerm(headTerm,body,securityContext);
    }
    @GET
    @Path("/node")
    
    @Produces({ "application/json" })
    @Operation(summary = "This is the first route to call in order to build the query graph.", description = "Starting from only the clicked class get the query graph that will be rendered by Sparqling, the query head, the sparql code. The sparql query returned will be somthing like `select ?x { ?x a <clickedClassIRI>` }. The variable `?x` should be called according to the entity remainder or label. The variable will be added to the head of the query in order to create a valid SPARQL query.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),
        
        @ApiResponse(responseCode = "404", description = "Entity not found") })
    public Response getQueryGraph(@Parameter(in = ParameterIn.QUERY, description = "The IRI of the entity clicked on the GRAPHOLscape ontology graph",required=true) @QueryParam("clickedClassIRI") String clickedClassIRI
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getQueryGraph(clickedClassIRI,securityContext);
    }
    @PUT
    @Path("/translate")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Translate the query graph in sparql.", description = "This route will return the same query graph passed in the body but the SPARQL code with the new filters optionals group by and havings. Note that if the filters are related to optional variables they should be put inside the optional block.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))) })
    public Response modifyFilters(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.modifyFilters(body,securityContext);
    }
    @PUT
    @Path("/node/class/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Starting from the actual query graph continue to build the query graph through a class.", description = "This call is used when the user click on a highlighted class and should add a triple pattern of the form like `?x rdf:type <clickedClass>`. The server should find `?x` in the SPARQL code as the variable associated to the `sourceClassIRI`.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),
        
        @ApiResponse(responseCode = "404", description = "Entity not found") })
    public Response putQueryGraphClass(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the last selected class. It could be selected from the ontology graph or from the query graph.",required=true) @QueryParam("sourceClassIRI") String sourceClassIRI
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the entity clicked on the GRAPHOLscape ontology graph.",required=true) @QueryParam("targetClassIRI") String targetClassIRI
,@Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.",required=true) @PathParam("graphElementId") String graphElementId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.putQueryGraphClass(body,sourceClassIRI,targetClassIRI,graphElementId,securityContext);
    }
    @PUT
    @Path("/node/dataProperty/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Starting from the actual query graph continue to build the query graph through a data property.", description = "This route is used when the user click a highlighted data property. The triple pattern to add is something like `?x <predicateIRI> ?y` where `?x` should be derived from `selectedClassIRI`. Note that `?y` is fresh new variable that should be added also to the head of the query (we assume data property values are interesting). The variable `?y` should be called according to the entity remainder or label and should add a counter if there is an already defined variable for that data property.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),
        
        @ApiResponse(responseCode = "404", description = "Entity not found") })
    public Response putQueryGraphDataProperty(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the last selected class. It could be selected from the ontology graph or from the query graph.",required=true) @QueryParam("sourceClassIRI") String sourceClassIRI
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the clicked data property.",required=true) @QueryParam("predicateIRI") String predicateIRI
,@Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.",required=true) @PathParam("graphElementId") String graphElementId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.putQueryGraphDataProperty(body,sourceClassIRI,predicateIRI,graphElementId,securityContext);
    }
    @PUT
    @Path("/node/join/{graphElementId1}/{graphElementId2}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Join two GraphNodeElement in one. graph through a data property.", description = "Starting from a query graph which has two nodes representing the same class, it returns the query graph in which the two nodes have been joined into a single one. The children of the selected nodes will be grouped in `graphElementId1` and each time we add a children through the previous routes they will be added to this node.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),
        
        @ApiResponse(responseCode = "404", description = "Entity not found") })
    public Response putQueryGraphJoin(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.",required=true) @PathParam("graphElementId1") String graphElementId1
,@Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.",required=true) @PathParam("graphElementId2") String graphElementId2
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.putQueryGraphJoin(body,graphElementId1,graphElementId2,securityContext);
    }
    @PUT
    @Path("/node/objectProperty/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Starting from the actual query graph continue to build the query graph through a object property.", description = "This route is used when the user click a highlighted object property with ornly one `relatedClasses` or, in the case of more than one `relatedClasses` immediatly after choosing one of them. In this case the triple pattern to add is something like `?x <predicateIRI> ?y` where `?x` and `?y` should be derived from the direction of the object property with respect to `sourceClassIRI` and `targetClassIRI`. If there is a cyclic object property the user also should specify the direction if order to correctly assign `?x` and `?y`. Either `?x` or `?y` should be a fresh new variable which should be linked to a new triple pattern `?y rdf:type <targetClassIRI>`. The variable `?y` should be called according to the entity remainder or label and should add a counter if there is an already defined variable for that class.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),
        
        @ApiResponse(responseCode = "404", description = "Entity not found") })
    public Response putQueryGraphObjectProperty(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the last selected class. It could be selected from the ontology graph or from the query graph.",required=true) @QueryParam("sourceClassIRI") String sourceClassIRI
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the predicate which links source class and target class",required=true) @QueryParam("predicateIRI") String predicateIRI
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the entity clicked on the GRAPHOLscape ontology graph.",required=true) @QueryParam("targetClassIRI") String targetClassIRI
,@Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.",required=true) @PathParam("graphElementId") String graphElementId
,@Parameter(in = ParameterIn.QUERY, description = "When the predicate is cyclic pass the value to true if you want to traverse the predicate from domain to object, false otherwise.") @QueryParam("isPredicateCyclicDirect") Boolean isPredicateCyclicDirect
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.putQueryGraphObjectProperty(body,sourceClassIRI,predicateIRI,targetClassIRI,graphElementId,isPredicateCyclicDirect,securityContext);
    }
}
