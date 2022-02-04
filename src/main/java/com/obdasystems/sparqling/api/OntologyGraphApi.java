package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.api.OntologyGraphApiService;
import com.obdasystems.sparqling.api.factories.OntologyGraphApiServiceFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.obdasystems.sparqling.model.Highlights;
import com.obdasystems.sparqling.model.Paths;

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

@Path("/highlights")



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-02-04T11:47:40.527Z[GMT]")public class OntologyGraphApi  {
   private final OntologyGraphApiService delegate;

   public OntologyGraphApi(@Context ServletConfig servletContext) {
      OntologyGraphApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("OntologyGraphApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (OntologyGraphApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = OntologyGraphApiServiceFactory.getOntologyGraphApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    
    @Produces({ "application/json" })
    @Operation(summary = "Get the IRIs of the ontology entities \"related\" to the clicked and selected.", description = "This route is used to highlight the negihbours of the selected class. The neighbours can be classes (brother classes or child classes), object properties (the class or one of his father partecipate or are typed to domain/range) or data properties (the class or one of its fathers partecipates or is typed to its domain).", tags={ "OntologyGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Highlights.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),
        
        @ApiResponse(responseCode = "404", description = "Entity not found") })
    public Response highligths(@Parameter(in = ParameterIn.QUERY, description = "The IRI of the class just clicked on the GRAPHOLscape ontology graph",required=true) @QueryParam("clickedClassIRI") String clickedClassIRI
,@Parameter(in = ParameterIn.QUERY, description = "") @QueryParam("params") List<String> params
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.highligths(clickedClassIRI,params,securityContext);
    }
    @GET
    @Path("/paths")
    
    @Produces({ "application/json" })
    @Operation(summary = "Find paths between selected class and clicked class.", description = "The results should be based on Dijkstra algorithm for shortest paths. ISA wieght is 0 while role weight is 1.", tags={ "OntologyGraph" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Paths.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid IRI supplied"),
        
        @ApiResponse(responseCode = "404", description = "Entity not found") })
    public Response highligthsPaths(@Parameter(in = ParameterIn.QUERY, description = "The IRI of the entity clicked on the GRAPHOLscape ontology graph",required=true) @QueryParam("lastSelectedIRI") String lastSelectedIRI
,@Parameter(in = ParameterIn.QUERY, description = "The IRI of the entity clicked on the GRAPHOLscape ontology graph",required=true) @QueryParam("clickedIRI") String clickedIRI
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.highligthsPaths(lastSelectedIRI,clickedIRI,securityContext);
    }
}
