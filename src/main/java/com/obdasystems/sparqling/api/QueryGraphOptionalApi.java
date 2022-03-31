package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.api.QueryGraphOptionalApiService;
import com.obdasystems.sparqling.api.factories.QueryGraphOptionalApiServiceFactory;

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

@Path("/queryGraph/node/optional")



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-03-31T16:20:47.492Z[GMT]")public class QueryGraphOptionalApi  {
   private final QueryGraphOptionalApiService delegate;

   public QueryGraphOptionalApi(@Context ServletConfig servletContext) {
      QueryGraphOptionalApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("QueryGraphOptionalApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (QueryGraphOptionalApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = QueryGraphOptionalApiServiceFactory.getQueryGraphOptionalApi();
      }

      this.delegate = delegate;
   }

    @PUT
    @Path("/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Add the `graphElementId` to a new optional.", description = "Create a new optional in the query and add the triple pattern(s) identified by the GraphElementId. - If it is a class the query parameter should be used and the triple pattern `?graphElementId rdf:type <classIRI>` will be moved from the bgp to the new optional. - If it is a data property the tp `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2` will be added to the new optional. - If it is a object property the tps `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2. ?graphElementIdVar2 rdf:type <classIRI>` till the leaves will be moved to the new optional.", tags={ "QueryGraphOptional" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response newOptionalGraphElementId(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be added to the optional",required=true) @PathParam("graphElementId") String graphElementId
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the class that will be inserted in the optional.") @QueryParam("classIRI") String classIRI
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.newOptionalGraphElementId(body,graphElementId,classIRI,securityContext);
    }
    @PUT
    @Path("/remove/all")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Remove the optionals and move them back to the bgp.", description = "", tags={ "QueryGraphOptional" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response removeAllOptional(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.removeAllOptional(body,securityContext);
    }
    @PUT
    @Path("/remove/{graphElementId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Remove the graphElementId from the optional and move it back to the bgp.", description = "Remove the triple pattern(s) identified by the `graphElementId` from all the optional that contains the graphElementId. - If it is a class the query parameter should be used and the triple pattern `?graphElementId rdf:type <classIRI>` will be moved from the optional to the bgp. - If it is a data property the tp `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2` will be moved from the optional to the bgp. - If it is a object property the tps `?graphElementIdVar1 <graphElementIdDataPropertyIRI> ?graphElementIdVar2. ?graphElementIdVar2 rdf:type <classIRI>` will be moved from the optional to the bgp.", tags={ "QueryGraphOptional" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response removeOptionalGraphElementId(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "The GraphElement that should be removed from the optional",required=true) @PathParam("graphElementId") String graphElementId
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the class that will be inserted in the optional.") @QueryParam("classIRI") String classIRI
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.removeOptionalGraphElementId(body,graphElementId,classIRI,securityContext);
    }
}
