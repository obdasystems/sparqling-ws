package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.api.StandaloneApiService;
import com.obdasystems.sparqling.api.factories.StandaloneApiServiceFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.File;

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

@Path("/standalone/ontology")



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-01-14T16:22:04.631Z[GMT]")public class StandaloneApi  {
   private final StandaloneApiService delegate;

   public StandaloneApi(@Context ServletConfig servletContext) {
      StandaloneApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("StandaloneApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (StandaloneApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = StandaloneApiServiceFactory.getStandaloneApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/graphol")
    
    @Produces({ "application/xml" })
    @Operation(summary = "Return the graphol file as a string to be parsed by GRAPHOLscape.", description = "", tags={ "Standalone" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/xml", schema = @Schema(implementation = String.class))),
        
        @ApiResponse(responseCode = "404", description = "Ontology not uploaded") })
    public Response standaloneOntologyGrapholGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.standaloneOntologyGrapholGet(securityContext);
    }
    @POST
    @Path("/upload")
    @Consumes({ "multipart/form-data" })
    
    @Operation(summary = "Uploads a .graphol or .owl file. This will be used only by standalone Sparqling.", description = "", tags={ "Standalone" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation") })
    public Response standaloneOntologyUploadPost(@FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.standaloneOntologyUploadPost(fileInputStream,fileDetail,securityContext);
    }
}
