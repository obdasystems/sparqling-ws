package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.*;

import java.io.File;

import java.util.Map;
import java.util.List;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-01-14T08:18:44.959Z[GMT]")public class StandaloneApiServiceImpl extends StandaloneApiService {
    @Override
    public Response standaloneOntologyGrapholGet(SecurityContext securityContext) throws NotFoundException {
        String graphol = SWSOntologyManager.getOntologyManager().getGraphol();
        if (graphol != null) {
            return Response.ok().entity(graphol).build();
        } else {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, "Graphol not present")).build();
        }
    }
    @Override
    public Response standaloneOntologyUploadPost(InputStream upfileInputStream, FormDataContentDisposition upfileDetail, SecurityContext securityContext) throws NotFoundException {
        try {
            if(upfileDetail.getFileName().endsWith(".graphol")) {
                SWSOntologyManager.getOntologyManager().loadGrapholFile(upfileInputStream);
            } else {
                SWSOntologyManager.getOntologyManager().loadOWLOntologyFile(upfileInputStream);
            }
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "File successfully loaded.")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
}
