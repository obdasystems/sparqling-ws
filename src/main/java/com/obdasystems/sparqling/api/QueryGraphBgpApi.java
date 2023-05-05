package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.api.factories.QueryGraphBgpApiServiceFactory;
import com.obdasystems.sparqling.model.QueryGraph;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/queryGraph")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T09:25:55.884Z[GMT]")
public class QueryGraphBgpApi {
    private final QueryGraphBgpApiService delegate;

    public QueryGraphBgpApi(@Context ServletConfig servletContext) {
        QueryGraphBgpApiService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("QueryGraphBgpApi.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (QueryGraphBgpApiService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = QueryGraphBgpApiServiceFactory.getQueryGraphBgpApi();
        }

        this.delegate = delegate;
    }

    @PUT
    @Path("/path")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Get the query graph that will be rendered by Sparqling, the query head, the sparql code based on the chosen path.", description = "This path should be used to build the query graph using the path interaction. As a result there will be added to the query several triple pattern (depending on the length of the path) as a sequence of classes and object properties. Data properties never appear in paths, in order to add them use the simple PUT route.", tags = {"QueryGraphBGP"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),

            @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),

            @ApiResponse(responseCode = "404", description = "Entity not found")})
    public Response addPathToQueryGraph(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true) QueryGraph body

            , @Parameter(in = ParameterIn.QUERY, description = "Serialization of Path object.", required = true) @QueryParam("path") String path
            , @Context SecurityContext securityContext)
            throws NotFoundException {
        return delegate.addPathToQueryGraph(body, path, securityContext);
    }

    @PUT
    @Path("/node/delete/{graphElementId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Delete the GraphElement (and all its children) from the query graph and head.", description = "This route is used when the user wants to delete a node from the query graph. All the children of this node will be deleted as well as we do not want to create query with completly separated branches. All the variables that are going to be deleted should also be deleted from the head of the query. **WARNING**, if the node has multiple occurrences (due to join operations) every node should be deleted.", tags = {"QueryGraphBGP"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),

            @ApiResponse(responseCode = "404", description = "GraphElement not found")})
    public Response deleteGraphElementId(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true) QueryGraph body

            , @Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be deleted", required = true) @PathParam("graphElementId") String graphElementId
            , @Context SecurityContext securityContext)
            throws NotFoundException {
        return delegate.deleteGraphElementId(body, graphElementId, securityContext);
    }

    @PUT
    @Path("/node/delete/{graphElementId}/class")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Delete from GraphElement only the class", description = "", tags = {"QueryGraphBGP"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),

            @ApiResponse(responseCode = "404", description = "GraphElement not found")})
    public Response deleteGraphElementIdClass(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true) QueryGraph body

            , @Parameter(in = ParameterIn.QUERY, description = "The class that should be deleted", required = true) @QueryParam("classIRI") String classIRI
            , @Parameter(in = ParameterIn.PATH, description = "", required = true) @PathParam("graphElementId") String graphElementId
            , @Context SecurityContext securityContext)
            throws NotFoundException {
        return delegate.deleteGraphElementIdClass(body, classIRI, graphElementId, securityContext);
    }

    @GET
    @Path("/node")

    @Produces({"application/json"})
    @Operation(summary = "This is the first route to call in order to build the query graph.", description = "Starting from only the clicked class get the query graph that will be rendered by Sparqling, the query head, the sparql code. The sparql query returned will be somthing like `select ?x { ?x a <clickedClassIRI>` }. The variable `?x` should be called according to the entity remainder or label. The variable will be added to the head of the query in order to create a valid SPARQL query.", tags = {"QueryGraphBGP"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),

            @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),

            @ApiResponse(responseCode = "404", description = "Entity not found")})
    public Response getQueryGraph(@Parameter(in = ParameterIn.QUERY, description = "The IRI of the entity clicked on the GRAPHOLscape ontology graph", required = true) @QueryParam("clickedClassIRI") String clickedClassIRI
            , @Context SecurityContext securityContext)
            throws NotFoundException {
        return delegate.getQueryGraph(clickedClassIRI, securityContext);
    }

    @PUT
    @Path("/node/class/{graphElementId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Starting from the current query graph continue to build the query graph through a class.", description = "This call is used when the user click on a highlighted class and should add a triple pattern of the form like `?x rdf:type <targetClassIRI>`. The server should find `?x` in the SPARQL code as the variable associated to the `sourceClassIRI`.", tags = {"QueryGraphBGP"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),

            @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),

            @ApiResponse(responseCode = "404", description = "Entity not found")})
    public Response putQueryGraphClass(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true) QueryGraph body

            , @Parameter(in = ParameterIn.QUERY, description = "The IRI of the last selected class. It could be selected from the ontology graph or from the query graph.", required = true) @QueryParam("sourceClassIRI") String sourceClassIRI
            , @Parameter(in = ParameterIn.QUERY, description = "The IRI of the entity clicked on the GRAPHOLscape ontology graph.", required = true) @QueryParam("targetClassIRI") String targetClassIRI
            , @Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.", required = true) @PathParam("graphElementId") String graphElementId
            , @Context SecurityContext securityContext)
            throws NotFoundException {
        return delegate.putQueryGraphClass(body, sourceClassIRI, targetClassIRI, graphElementId, securityContext);
    }

    @PUT
    @Path("/node/dataProperty/{graphElementId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Starting from the current query graph continue to build the query graph through a data property.", description = "This route is used when the user click a highlighted data property. The triple pattern to add is something like `?x <predicateIRI> ?y` where `?x` should be derived from `selectedClassIRI`. Note that `?y` is fresh new variable that should be added also to the head of the query (we assume data property values are interesting). The variable `?y` should be called according to the entity remainder or label and should add a counter if there is an already defined variable for that data property.", tags = {"QueryGraphBGP"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),

            @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),

            @ApiResponse(responseCode = "404", description = "Entity not found")})
    public Response putQueryGraphDataProperty(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true) QueryGraph body

            , @Parameter(in = ParameterIn.QUERY, description = "The IRI of the last selected class. It could be selected from the ontology graph or from the query graph.", required = true) @QueryParam("sourceClassIRI") String sourceClassIRI
            , @Parameter(in = ParameterIn.QUERY, description = "The IRI of the clicked data property.", required = true) @QueryParam("predicateIRI") String predicateIRI
            , @Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.", required = true) @PathParam("graphElementId") String graphElementId
            , @Context SecurityContext securityContext)
            throws NotFoundException {
        return delegate.putQueryGraphDataProperty(body, sourceClassIRI, predicateIRI, graphElementId, securityContext);
    }

    @PUT
    @Path("/node/annotation/{graphElementId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Starting from the current query graph continue to build the query graph through a data property.", description = "This route is used when the user click a highlighted data property. The triple pattern to add is something like `?x <predicateIRI> ?y` where `?x` should be derived from `selectedClassIRI`. Note that `?y` is fresh new variable that should be added also to the head of the query (we assume data property values are interesting). The variable `?y` should be called according to the entity remainder or label and should add a counter if there is an already defined variable for that data property.", tags = {"QueryGraphBGP"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),

            @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),

            @ApiResponse(responseCode = "404", description = "Entity not found")})
    public Response putQueryGraphAnnotation(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true) QueryGraph body

            , @Parameter(in = ParameterIn.QUERY, description = "The IRI of the last selected class. It could be selected from the ontology graph or from the query graph.", required = true) @QueryParam("sourceClassIRI") String sourceClassIRI
            , @Parameter(in = ParameterIn.QUERY, description = "The IRI of the clicked annotation property.", required = true) @QueryParam("predicateIRI") String predicateIRI
            , @Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.", required = true) @PathParam("graphElementId") String graphElementId
            , @Context SecurityContext securityContext)
            throws NotFoundException {
        return delegate.putQueryGraphAnnotation(body, sourceClassIRI, predicateIRI, graphElementId, securityContext);
    }

    @PUT
    @Path("/node/join/{graphElementId1}/{graphElementId2}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Join two GraphNodeElement in one.", description = "Starting from a query graph which has two nodes representing the same class(es), it returns the query graph in which the two nodes have been joined into a single one. The children of the selected nodes will be grouped in `graphElementId1` and each time we add a children through the previous routes they will be added to this node.", tags = {"QueryGraphBGP"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),

            @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),

            @ApiResponse(responseCode = "404", description = "Entity not found")})
    public Response putQueryGraphJoin(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true) QueryGraph body

            , @Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.", required = true) @PathParam("graphElementId1") String graphElementId1
            , @Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.", required = true) @PathParam("graphElementId2") String graphElementId2
            , @Context SecurityContext securityContext)
            throws NotFoundException {
        return delegate.putQueryGraphJoin(body, graphElementId1, graphElementId2, securityContext);
    }

    @PUT
    @Path("/node/objectProperty/{graphElementId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Starting from the current query graph continue to build the query graph through a object property.", description = "This route is used when the user click a highlighted object property with ornly one `relatedClasses` or, in the case of more than one `relatedClasses` immediatly after choosing one of them. In this case the triple pattern to add is something like `?x <predicateIRI> ?y` where `?x` and `?y` should be derived from the direction indicated by `isPredicateDirect` of the object property with respect to `sourceClassIRI` and `targetClassIRI`. If there is a cyclic object property the user also should specify the direction if order to correctly assign `?x` and `?y`. Either `?x` or `?y` should be a fresh new variable which should be linked to a new triple pattern `?y rdf:type <targetClassIRI>`. The variable `?y` should be called according to the entity remainder or label and should add a counter if there is an already defined variable for that class.", tags = {"QueryGraphBGP"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),

            @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),

            @ApiResponse(responseCode = "404", description = "Entity not found")})
    public Response putQueryGraphObjectProperty(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true) QueryGraph body

            , @Parameter(in = ParameterIn.QUERY, description = "The IRI of the last selected class. It could be selected from the ontology graph or from the query graph.", required = true) @QueryParam("sourceClassIRI") String sourceClassIRI
            , @Parameter(in = ParameterIn.QUERY, description = "The IRI of the predicate which links source class and target class", required = true) @QueryParam("predicateIRI") String predicateIRI
            , @Parameter(in = ParameterIn.QUERY, description = "The IRI of the entity clicked on the GRAPHOLscape ontology graph.", required = true) @QueryParam("targetClassIRI") String targetClassIRI
            , @Parameter(in = ParameterIn.QUERY, description = "If true sourceClassIRI is the domain of predicateIRI, if false sourceClassIRI is the range of predicateIRI.", required = true) @QueryParam("isPredicateDirect") Boolean isPredicateDirect
            , @Parameter(in = ParameterIn.PATH, description = "The id of the node of the selected class in the query graph.", required = true) @PathParam("graphElementId") String graphElementId
            , @Context SecurityContext securityContext)
            throws NotFoundException {
        return delegate.putQueryGraphObjectProperty(body, sourceClassIRI, predicateIRI, targetClassIRI, isPredicateDirect, graphElementId, securityContext);
    }
}
