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
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-12-01T16:52:13.648Z[GMT]")public abstract class QueryGraphApiService {
    public abstract Response addHeadTerm(QueryGraph body,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response addPathToQueryGraph(QueryGraph body, @NotNull String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response deleteGraphElementId(QueryGraph body,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response deleteHeadTerm(QueryGraph body,String headTerm,SecurityContext securityContext) throws NotFoundException;
    public abstract Response getQueryGraph( @NotNull String clickedClassIRI,SecurityContext securityContext) throws NotFoundException;
    public abstract Response modifyFilters(QueryGraph body,SecurityContext securityContext) throws NotFoundException;
    public abstract Response putQueryGraphClass(QueryGraph body, @NotNull String sourceClassIRI, @NotNull String targetClassIRI,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response putQueryGraphDataProperty(QueryGraph body, @NotNull String sourceClassIRI, @NotNull String predicateIRI,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response putQueryGraphJoin(QueryGraph body,String graphElementId1,String graphElementId2,SecurityContext securityContext) throws NotFoundException;
    public abstract Response putQueryGraphObjectProperty(QueryGraph body, @NotNull String sourceClassIRI, @NotNull String predicateIRI, @NotNull String targetClassIRI, @NotNull Boolean isPredicateDirect,String graphElementId,SecurityContext securityContext) throws NotFoundException;
}
