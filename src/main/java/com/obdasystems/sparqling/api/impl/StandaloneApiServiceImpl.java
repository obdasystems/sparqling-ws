package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
public class StandaloneApiServiceImpl extends StandaloneApiService {
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
