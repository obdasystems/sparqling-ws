package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;

import com.obdasystems.sparqling.model.QueryGraph;

import com.obdasystems.sparqling.api.NotFoundException;

import com.obdasystems.sparqling.query.QueryGraphHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T09:25:55.884Z[GMT]")public class QueryGraphOptionalApiServiceImpl extends QueryGraphOptionalApiService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Response newOptionalGraphElementId(QueryGraph body, String graphElementId,  String classIRI, SecurityContext securityContext) throws NotFoundException {
        logger.info("New optional for {}", graphElementId);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            QueryGraph res = qgb.newOptional(body, graphElementId, classIRI);
            return Response.ok().entity(res).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response removeAllOptional(QueryGraph body, SecurityContext securityContext) throws NotFoundException {
        logger.info("Remove all optionals");
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            QueryGraph res = qgb.removeAllOptionals(body);
            return Response.ok().entity(res).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response removeOptionalGraphElementId(QueryGraph body, String graphElementId,  String classIRI, SecurityContext securityContext) throws NotFoundException {
        logger.info("Remove optional for {}", graphElementId);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            QueryGraph res = qgb.removeOptional(body, graphElementId, classIRI);
            return Response.ok().entity(res).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
}
