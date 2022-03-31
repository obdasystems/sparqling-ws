package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.ApiResponseMessage;
import com.obdasystems.sparqling.api.NotFoundException;
import com.obdasystems.sparqling.api.OntologyGraphApiService;
import com.obdasystems.sparqling.engine.OntologyProximityManager;
import com.obdasystems.sparqling.engine.SWSOntologyManager;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

public class OntologyGraphApiServiceImpl extends OntologyGraphApiService {
    @Override
    public Response highligths( @NotNull String clickedClassIRI,  List<String> params, SecurityContext securityContext) throws NotFoundException {
        try {
            OntologyProximityManager opm = SWSOntologyManager.getOntologyManager().getOntologyProximityManager();
            if(opm == null) throw new RuntimeException("Cannot find ontology, please upload a new ontology.");
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
