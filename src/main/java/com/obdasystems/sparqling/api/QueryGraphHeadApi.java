package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.api.QueryGraphHeadApiService;
import com.obdasystems.sparqling.api.factories.QueryGraphHeadApiServiceFactory;

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

@Path("/queryGraph/head")



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-08T10:35:15.892Z[GMT]")public class QueryGraphHeadApi  {
   private final QueryGraphHeadApiService delegate;

   public QueryGraphHeadApi(@Context ServletConfig servletContext) {
      QueryGraphHeadApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("QueryGraphHeadApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (QueryGraphHeadApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = QueryGraphHeadApiServiceFactory.getQueryGraphHeadApi();
      }

      this.delegate = delegate;
   }

    @PUT
    @Path("/add/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Add the head term to the query graph.", description = "Explicitley add a term to the query head. All the data property variables are added automatically to the head during the query graph construction. This will add to the head only variables associated to classes (`rdf:type` triple pattern) or data properties.", tags={ "QueryGraphHead" })
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
    @Path("/aggregation/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set the aggregation function to the head term.", description = "The aggregation function is defined in the group by field of the query graph in the request body along with the HAVING clause. Remember to set the alias of the head based on function name and variable.", tags={ "QueryGraphHead" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response aggregationHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The head term that should be involved in the aggregation function",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.aggregationHeadTerm(body,headTerm,securityContext);
    }
    @PUT
    @Path("/delete/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Delete the head term from the query graph.", description = "The path param should be the id of the HeadElement.", tags={ "QueryGraphHead" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response deleteHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The head term that should be deleted",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteHeadTerm(body,headTerm,securityContext);
    }
    @PUT
    @Path("/function/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set a function to the head term from the query graph.", description = "The function is defined in the head term of the query graph in the request body. Remember to set the alias of the head based on function name and variable.", tags={ "QueryGraphHead" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response functionHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The head term that should be involved inthe function",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.functionHeadTerm(body,headTerm,securityContext);
    }
    @PUT
    @Path("/orderBy/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Order by the head from the query graph.", description = "The OrderBy object is passed in the request body in the Query Graph.", tags={ "QueryGraphHead" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response orderByHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The head term that should be ordered",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.orderByHeadTerm(body,headTerm,securityContext);
    }
    @PUT
    @Path("/rename/{headTerm}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Rename the head term from the query graph using alias.", description = "Put the alias in the HeadElement passed via request body.", tags={ "QueryGraphHead" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response renameHeadTerm(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The head term that should be renamed",required=true) @PathParam("headTerm") String headTerm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.renameHeadTerm(body,headTerm,securityContext);
    }
    @PUT
    @Path("/reorderHeadTerms")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Reorder the head elements accrding to Query GRaph object.", description = "", tags={ "QueryGraphHead" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response reorderHeadTerms(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.reorderHeadTerms(body,securityContext);
    }
}
