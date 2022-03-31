package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.model.*;

import com.obdasystems.sparqling.model.QueryGraph;

import java.util.Map;
import java.util.List;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-03-31T16:20:47.492Z[GMT]")public class QueryGraphOptionalApiServiceImpl extends QueryGraphOptionalApiService {
    @Override
    public Response newOptionalGraphElementId(QueryGraph body, String graphElementId,  String classIRI, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response removeAllOptional(QueryGraph body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response removeOptionalGraphElementId(QueryGraph body, String graphElementId,  String classIRI, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
