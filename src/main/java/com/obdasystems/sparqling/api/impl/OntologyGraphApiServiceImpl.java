package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.model.*;

import com.obdasystems.sparqling.model.Highlights;
import com.obdasystems.sparqling.model.Paths;

import java.util.Map;
import java.util.List;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-11-29T11:28:53.694Z[GMT]")public class OntologyGraphApiServiceImpl extends OntologyGraphApiService {
    @Override
    public Response highligths( @NotNull String clickedClassIRI,  List<String> params, SecurityContext securityContext) throws NotFoundException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response highligthsPaths( @NotNull String lastSelectedIRI,  @NotNull String clickedIRI, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
