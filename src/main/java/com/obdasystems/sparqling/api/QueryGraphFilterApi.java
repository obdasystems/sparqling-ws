package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.api.QueryGraphFilterApiService;
import com.obdasystems.sparqling.api.factories.QueryGraphFilterApiServiceFactory;

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

@Path("/queryGraph/node/filter")



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T09:25:55.884Z[GMT]")public class QueryGraphFilterApi  {
   private final QueryGraphFilterApiService delegate;

   public QueryGraphFilterApi(@Context ServletConfig servletContext) {
      QueryGraphFilterApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("QueryGraphFilterApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (QueryGraphFilterApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = QueryGraphFilterApiServiceFactory.getQueryGraphFilterApi();
      }

      this.delegate = delegate;
   }

    @PUT
    @Path("/edit/{filterId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Modify a filter in the query.", description = "Translate the filter at index `filterId` to a filter in SPARQL.", tags={ "QueryGraphFilter" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response editFilter(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "",required=true) @PathParam("filterId") Integer filterId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.editFilter(body,filterId,securityContext);
    }
    @PUT
    @Path("/{filterId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Create a new filter in the query.", description = "Translate the filter at index `filterId` to a new filter in SPARQL.", tags={ "QueryGraphFilter" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response newFilter(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "",required=true) @PathParam("filterId") Integer filterId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.newFilter(body,filterId,securityContext);
    }
    @PUT
    @Path("/remove/all")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Remove the filters.", description = "", tags={ "QueryGraphFilter" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response removeAllFilters(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.removeAllFilters(body,securityContext);
    }
    @PUT
    @Path("/remove/{filterId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Remove the filter at index `filterId` from the query.", description = "", tags={ "QueryGraphFilter" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryGraph.class))),
        
        @ApiResponse(responseCode = "404", description = "GraphElement not found") })
    public Response removeFilter(@Parameter(in = ParameterIn.DEFAULT, description = "" ,required=true) QueryGraph body

,@Parameter(in = ParameterIn.PATH, description = "",required=true) @PathParam("filterId") Integer filterId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.removeFilter(body,filterId,securityContext);
    }
}
