package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.ApiResponseMessage;
import com.obdasystems.sparqling.api.NotFoundException;
import com.obdasystems.sparqling.api.QueryGraphBgpApiService;
import com.obdasystems.sparqling.model.QueryGraph;
import com.obdasystems.sparqling.query.QueryGraphHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class QueryGraphBgpApiServiceImpl extends QueryGraphBgpApiService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Response addPathToQueryGraph(QueryGraph body,  @NotNull String path, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response deleteGraphElementId(QueryGraph body, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        logger.info("Deleting graph element {}", graphElementId);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.deleteQueryGraphElement(body, graphElementId)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }

    @Override
    public Response deleteGraphElementIdClass(QueryGraph body, String classIRI, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        logger.info("Deleting class {} from graph element {}",classIRI, graphElementId);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.deleteQueryGraphElementClass(body, graphElementId, classIRI)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }

    @Override
    public Response getQueryGraph( @NotNull String clickedClassIRI, SecurityContext securityContext) throws NotFoundException {
        logger.info("Adding {} to query graph", clickedClassIRI);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.getQueryGraph(clickedClassIRI)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response putQueryGraphClass(QueryGraph body,  @NotNull String sourceClassIRI,  @NotNull String targetClassIRI, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        logger.info("Adding {} to query graph", targetClassIRI);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.putQueryGraphClass(body, sourceClassIRI, targetClassIRI, graphElementId)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response putQueryGraphDataProperty(QueryGraph body,  @NotNull String sourceClassIRI,  @NotNull String predicateIRI, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        logger.info("Adding {} to query graph", predicateIRI);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.putQueryGraphDataProperty(body, sourceClassIRI, predicateIRI, graphElementId)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response putQueryGraphJoin(QueryGraph body, String graphElementId1, String graphElementId2, SecurityContext securityContext) throws NotFoundException {
        logger.info("Joining {} and {}", graphElementId1, graphElementId2);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.putQueryGraphJoin(body, graphElementId1, graphElementId2)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response putQueryGraphObjectProperty(QueryGraph body,  @NotNull String sourceClassIRI,  @NotNull String predicateIRI,  @NotNull String targetClassIRI,  @NotNull Boolean isPredicateDirect, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        logger.info("Adding {} to query graph", predicateIRI);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.putQueryGraphObjectProperty(body, sourceClassIRI, predicateIRI, targetClassIRI, isPredicateDirect, graphElementId)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
}
