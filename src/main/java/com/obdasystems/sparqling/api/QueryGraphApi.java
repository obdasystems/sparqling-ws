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



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-01-14T16:22:04.631Z[GMT]")public class QueryGraphApi  {
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
    @Path("/node/optional/{optionalId}/{graphElementId}/add")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Add the graphElementId to the optionalId OPTIONAL.", description = "Create a new OPTIONAL in the query and add the triple pattern(s) identified by the GraphElementId. If it is a class the query parameter should be used and the triple pattern `?graphElementId rdf:type <classIRI>` will be moved from the bgp to the new OPTIONAL. If it is a data property the tp `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2` will be added to the OPTIONAL. If it is a object property the tps `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2. ?graphElementIdVar2 rdf:type <classIRI>` will be moved to the OPTIONAL.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response addOptionalGraphElementId(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be delete",required=true) @PathParam("graphElementId") String graphElementId
,@Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be delete",required=true) @PathParam("optionalId") String optionalId
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the class that will be inserted in the OPTIONAL.") @QueryParam("classIRI") String classIRI
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.addOptionalGraphElementId(body,graphElementId,optionalId,classIRI,securityContext);
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
    @Path("/head/aggregation/having/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set the having filter of the aggregation function to the head term.", description = "The having aggregation function is defined in the groupBy field of the query graph in the request body.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response aggregationHavingHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "",required=true) @QueryParam("direction") String direction
,@Parameter(in = ParameterIn.PATH, description = "The head term that should be delete",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.aggregationHavingHeadTerm(body,direction,headTerm,securityContext);
    }
    @PUT
    @Path("/head/aggregation/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set the aggregation function to the head term.", description = "The aggregation function is defined in the group by field of the query graph in the request body.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response aggregationHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "",required=true) @QueryParam("direction") String direction
,@Parameter(in = ParameterIn.PATH, description = "The head term that should be delete",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.aggregationHeadTerm(body,direction,headTerm,securityContext);
    }
    @PUT
    @Path("/node/delete/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Delete the GraphElement (and all its children) from the query graph and head.", description = "This route is used when the user wants to delete a node from the query graph. All the children of this node will be deleted as well as we do not want to create query with completly separated branches. All the variables that are going to be deleted should also be deleted from the head of the query. **WARNING**, if the node has multiple occurrences (due to join operations) every node should be deleted.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response deleteGraphElementId(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be delete",required=true) @PathParam("graphElementId") String graphElementId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteGraphElementId(body,graphElementId,securityContext);
    }
    @PUT
    @Path("/head/delete/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Delete the head term from the query graph.", description = "", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response deleteHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The head term that should be delete",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteHeadTerm(body,headTerm,securityContext);
    }
    @PUT
    @Path("/distinct")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set the distinct value.", description = "The distinct value is defined in the query graph in the request body.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response distinctQueryGraph(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "",required=true) @QueryParam("distinct") Boolean distinct
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.distinctQueryGraph(body,distinct,securityContext);
    }
    @PUT
    @Path("/head/function/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set a function to the head term from the query graph.", description = "The function is defined in the head term of the query graph in the request body.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response functionHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The head term that should be delete",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.functionHeadTerm(body,headTerm,securityContext);
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
    @Path("/head/hide/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Hide the head term from the query graph.", description = "", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response hideHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The head term that should be delete",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.hideHeadTerm(body,headTerm,securityContext);
    }
    @PUT
    @Path("/limit")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set the limit value.", description = "The limit value is defined in the query graph in the request body.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response limitQueryGraph(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "",required=true) @QueryParam("limit") Integer limit
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.limitQueryGraph(body,limit,securityContext);
    }
    @PUT
    @Path("/node/optional/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Add the graphElementId to a new OPTIONAL.", description = "Create a new OPTIONAL in the query and add the triple pattern(s) identified by the GraphElementId. If it is a class the query parameter should be used and the triple pattern `?graphElementId rdf:type <classIRI>` will be moved from the bgp to the new OPTIONAL. If it is a data property the tp `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2` will be added to the OPTIONAL. If it is a object property the tps `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2. ?graphElementIdVar2 rdf:type <classIRI>` will be moved to the OPTIONAL.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response newOptionalGraphElementId(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be added to the OPTIONAL",required=true) @PathParam("graphElementId") String graphElementId
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the class that will be inserted in the OPTIONAL.") @QueryParam("classIRI") String classIRI
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.newOptionalGraphElementId(body,graphElementId,classIRI,securityContext);
    }
    @PUT
    @Path("/offset")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set the offset value.", description = "The offset value is defined in the query graph in the request body.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response offsetQueryGraph(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "",required=true) @QueryParam("offset") Boolean offset
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.offsetQueryGraph(body,offset,securityContext);
    }
    @PUT
    @Path("/head/orderBy/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Order by the head from the query graph.", description = "", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response orderByHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "",required=true) @QueryParam("direction") String direction
,@Parameter(in = ParameterIn.PATH, description = "The head term that should be delete",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.orderByHeadTerm(body,direction,headTerm,securityContext);
    }
    @PUT
    @Path("/node/class/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Starting from the current query graph continue to build the query graph through a class.", description = "This call is used when the user click on a highlighted class and should add a triple pattern of the form like `?x rdf:type <targetClassIRI>`. The server should find `?x` in the SPARQL code as the variable associated to the `sourceClassIRI`.", tags={ "QueryGraph" })
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
    @Operation(summary = "Starting from the current query graph continue to build the query graph through a data property.", description = "This route is used when the user click a highlighted data property. The triple pattern to add is something like `?x <predicateIRI> ?y` where `?x` should be derived from `selectedClassIRI`. Note that `?y` is fresh new variable that should be added also to the head of the query (we assume data property values are interesting). The variable `?y` should be called according to the entity remainder or label and should add a counter if there is an already defined variable for that data property.", tags={ "QueryGraph" })
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
    @Operation(summary = "Starting from the current query graph continue to build the query graph through a object property.", description = "This route is used when the user click a highlighted object property with ornly one `relatedClasses` or, in the case of more than one `relatedClasses` immediatly after choosing one of them. In this case the triple pattern to add is something like `?x <predicateIRI> ?y` where `?x` and `?y` should be derived from the direction indicated by `isPredicateDirect` of the object property with respect to `sourceClassIRI` and `targetClassIRI`. If there is a cyclic object property the user also should specify the direction if order to correctly assign `?x` and `?y`. Either `?x` or `?y` should be a fresh new variable which should be linked to a new triple pattern `?y rdf:type <targetClassIRI>`. The variable `?y` should be called according to the entity remainder or label and should add a counter if there is an already defined variable for that class.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),
        
        @ApiResponse(responseCode = "404", description = "Entity not found") })
    public Response putQueryGraphObjectProperty(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the last selected class. It could be selected from the ontology graph or from the query graph.",required=true) @QueryParam("sourceClassIRI") String sourceClassIRI
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the predicate which links source class and target class",required=true) @QueryParam("predicateIRI") String predicateIRI
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the entity clicked on the GRAPHOLscape ontology graph.",required=true) @QueryParam("targetClassIRI") String targetClassIRI
,@Parameter(in = ParameterIn.QUERY, description = "If true sourceClassIRI is the domain of predicateIRI, if false sourceClassIRI is the range of predicateIRI.",required=true) @QueryParam("isPredicateDirect") Boolean isPredicateDirect
,@Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.",required=true) @PathParam("graphElementId") String graphElementId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.putQueryGraphObjectProperty(body,sourceClassIRI,predicateIRI,targetClassIRI,isPredicateDirect,graphElementId,securityContext);
    }
    @PUT
    @Path("/node/optional/{optionalId}/{graphElementId}/remove")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Remove the graphElementId from the optionalId OPTIONAL and move it back to the bgp.", description = "Create a new OPTIONAL in the query and add the triple pattern(s) identified by the GraphElementId. If it is a class the query parameter should be used and the triple pattern `?graphElementId rdf:type <classIRI>` will be moved from the bgp to the new OPTIONAL. If it is a data property the tp `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2` will be added to the OPTIONAL. If it is a object property the tps `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2. ?graphElementIdVar2 rdf:type <classIRI>` will be moved to the OPTIONAL.", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response removeOptionalGraphElementId(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be delete",required=true) @PathParam("graphElementId") String graphElementId
,@Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be delete",required=true) @PathParam("optionalId") String optionalId
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the class that will be inserted in the OPTIONAL.") @QueryParam("classIRI") String classIRI
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.removeOptionalGraphElementId(body,graphElementId,optionalId,classIRI,securityContext);
    }
    @PUT
    @Path("/head/rename/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Rename the head term from the query graph using alias.", description = "", tags={ "QueryGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response renameHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The head term that should be delete",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.renameHeadTerm(body,headTerm,securityContext);
    }
}
