package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.obdasystems.sparqling.model.QueryGraph;

import java.util.Map;
import java.util.List;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-02-15T15:04:14.983Z[GMT]")public abstract class QueryGraphOptionalApiService {
    public abstract Response newOptionalGraphElementId(QueryGraph body,String graphElementId, String classIRI,SecurityContext securityContext) throws NotFoundException;
    public abstract Response removeAllOptional(QueryGraph body,SecurityContext securityContext) throws NotFoundException;
    public abstract Response removeOptionalGraphElementId(QueryGraph body,String graphElementId, String classIRI,SecurityContext securityContext) throws NotFoundException;
}
