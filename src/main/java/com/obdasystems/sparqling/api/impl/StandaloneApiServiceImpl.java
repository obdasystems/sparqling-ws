package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
public class StandaloneApiServiceImpl extends StandaloneApiService {
    final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public Response standaloneOntologyGrapholGet(SecurityContext securityContext) throws NotFoundException {
        logger.info("Get graphol file.");
        String graphol = SWSOntologyManager.getOntologyManager().getGraphol();
        if (graphol != null) {
            return Response.ok().entity(graphol).build();
        } else {
            String message = "Graphol not present";
            logger.error(message);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, message)).build();
        }
    }
    @Override
    public Response standaloneOntologyUploadPost(InputStream upfileInputStream, FormDataContentDisposition upfileDetail, SecurityContext securityContext) throws NotFoundException {
        logger.info("Uploading ontology {} file...", upfileDetail.getFileName());
        try {
            if(upfileDetail.getFileName().endsWith(".graphol")) {
                SWSOntologyManager.getOntologyManager().loadGrapholFile(upfileInputStream);
            } else {
                SWSOntologyManager.getOntologyManager().loadOWLOntologyFile(upfileInputStream);
            }
            logger.info("Done.");
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "File successfully loaded.")).build();
        } catch (Exception e) {
            logger.error("Error uploading ontology", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
}
