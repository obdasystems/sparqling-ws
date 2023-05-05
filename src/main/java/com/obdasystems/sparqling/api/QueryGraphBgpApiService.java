package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.model.QueryGraph;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T09:25:55.884Z[GMT]")public abstract class QueryGraphBgpApiService {
    public abstract Response addPathToQueryGraph(QueryGraph body, @NotNull String path,SecurityContext securityContext) throws NotFoundException;
    public abstract Response deleteGraphElementId(QueryGraph body,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response deleteGraphElementIdClass(QueryGraph body, @NotNull String classIRI,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response getQueryGraph( @NotNull String clickedClassIRI,SecurityContext securityContext) throws NotFoundException;
    public abstract Response putQueryGraphClass(QueryGraph body, @NotNull String sourceClassIRI, @NotNull String targetClassIRI,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response putQueryGraphDataProperty(QueryGraph body, @NotNull String sourceClassIRI, @NotNull String predicateIRI,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response putQueryGraphAnnotation(QueryGraph body, @NotNull String sourceClassIRI, @NotNull String predicateIRI,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response putQueryGraphJoin(QueryGraph body,String graphElementId1,String graphElementId2,SecurityContext securityContext) throws NotFoundException;
    public abstract Response putQueryGraphObjectProperty(QueryGraph body, @NotNull String sourceClassIRI, @NotNull String predicateIRI, @NotNull String targetClassIRI, @NotNull Boolean isPredicateDirect,String graphElementId,SecurityContext securityContext) throws NotFoundException;
}
