package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.engine.OntologyProximityManager;
import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.*;

import com.obdasystems.sparqling.model.Highlights;
import com.obdasystems.sparqling.model.Paths;

import java.util.Map;
import java.util.List;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-01-14T08:18:44.959Z[GMT]")public class OntologyGraphApiServiceImpl extends OntologyGraphApiService {
    @Override
    public Response highligths( @NotNull String clickedClassIRI,  List<String> params, SecurityContext securityContext) throws NotFoundException {
        try {
            OntologyProximityManager opm = SWSOntologyManager.getOntologyManager().getOntologyProximityManager();
            return Response.ok().entity(opm.getHighlights(clickedClassIRI)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response highligthsPaths( @NotNull String lastSelectedIRI,  @NotNull String clickedIRI, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
