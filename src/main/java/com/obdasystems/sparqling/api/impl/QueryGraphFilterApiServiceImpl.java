package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;

import com.obdasystems.sparqling.model.QueryGraph;

import com.obdasystems.sparqling.api.NotFoundException;

import com.obdasystems.sparqling.query.QueryGraphHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-02-15T15:04:14.983Z[GMT]")public class QueryGraphFilterApiServiceImpl extends QueryGraphFilterApiService {
    Logger logger = LoggerFactory.getLogger(QueryGraphFilterApiServiceImpl.class);
    @Override
    public Response editFilter(QueryGraph body, Integer filterId, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            QueryGraph res = qgb.removeFilter(body, filterId, false);
            res = qgb.newFilter(body, filterId);
            return Response.ok().entity(res).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response newFilter(QueryGraph body, Integer filterId, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.newFilter(body, filterId)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response removeAllFilters(QueryGraph body, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.removeFilters(body)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response removeFilter(QueryGraph body, Integer filterId, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.removeFilter(body, filterId, true)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
}
