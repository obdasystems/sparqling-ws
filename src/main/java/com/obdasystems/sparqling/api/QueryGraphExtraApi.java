package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.api.QueryGraphExtraApiService;
import com.obdasystems.sparqling.api.factories.QueryGraphExtraApiServiceFactory;

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



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-08T10:35:15.892Z[GMT]")public class QueryGraphExtraApi  {
   private final QueryGraphExtraApiService delegate;

   public QueryGraphExtraApi(@Context ServletConfig servletContext) {
      QueryGraphExtraApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("QueryGraphExtraApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (QueryGraphExtraApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = QueryGraphExtraApiServiceFactory.getQueryGraphExtraApi();
      }

      this.delegate = delegate;
   }

    @PUT
    @Path("/distinct")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set the distinct value.", description = "The distinct value is defined in the query graph in the request body.", tags={ "QueryGraphExtra" })
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
    @Path("/limit")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set the limit value.", description = "The limit value is defined in the query graph in the request body.", tags={ "QueryGraphExtra" })
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
    @Path("/offset")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Set the offset value.", description = "The offset value is defined in the query graph in the request body.", tags={ "QueryGraphExtra" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "Head term not found") })
    public Response offsetQueryGraph(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.QUERY, description = "",required=true) @QueryParam("offset") Boolean offset
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.offsetQueryGraph(body,offset,securityContext);
    }
}
