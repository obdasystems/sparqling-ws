package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.ApiResponseMessage;
import com.obdasystems.sparqling.api.NotFoundException;
import com.obdasystems.sparqling.api.QueryGraphApiService;
import com.obdasystems.sparqling.model.QueryGraph;
import com.obdasystems.sparqling.query.QueryGraphBuilder;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-12-01T16:52:13.648Z[GMT]")public class QueryGraphApiServiceImpl extends QueryGraphApiService {
    @Override
    public Response addHeadTerm(QueryGraph body, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response addPathToQueryGraph(QueryGraph body,  @NotNull String path, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response deleteGraphElementId(QueryGraph body, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response deleteHeadTerm(QueryGraph body, String headTerm, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response getQueryGraph( @NotNull String clickedClassIRI, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphBuilder qgb = new QueryGraphBuilder();
            return Response.ok().entity(qgb.getQueryGraph(clickedClassIRI)).build();
        } catch (Exception e) {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response modifyFilters(QueryGraph body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response putQueryGraphClass(QueryGraph body,  @NotNull String sourceClassIRI,  @NotNull String targetClassIRI, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphBuilder qgb = new QueryGraphBuilder();
            return Response.ok().entity(qgb.putQueryGraphClass(body, sourceClassIRI, targetClassIRI, graphElementId)).build();
        } catch (Exception e) {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response putQueryGraphDataProperty(QueryGraph body,  @NotNull String sourceClassIRI,  @NotNull String predicateIRI, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphBuilder qgb = new QueryGraphBuilder();
            return Response.ok().entity(qgb.putQueryGraphDataProperty(body, sourceClassIRI, predicateIRI, graphElementId)).build();
        } catch (Exception e) {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response putQueryGraphJoin(QueryGraph body, String graphElementId1, String graphElementId2, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response putQueryGraphObjectProperty(QueryGraph body,  @NotNull String sourceClassIRI,  @NotNull String predicateIRI,  @NotNull String targetClassIRI,  @NotNull Boolean isPredicateDirect, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphBuilder qgb = new QueryGraphBuilder();
            return Response.ok().entity(qgb.putQueryGraphObjectProperty(body, sourceClassIRI, predicateIRI, targetClassIRI, isPredicateDirect, graphElementId)).build();
        } catch (Exception e) {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
}
