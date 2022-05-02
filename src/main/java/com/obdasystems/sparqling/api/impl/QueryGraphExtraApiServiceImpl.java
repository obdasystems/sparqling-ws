package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.model.*;

import com.obdasystems.sparqling.model.QueryGraph;

import java.util.Map;
import java.util.List;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import com.obdasystems.sparqling.query.QueryGraphHandler;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T09:25:55.884Z[GMT]")public class QueryGraphExtraApiServiceImpl extends QueryGraphExtraApiService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Response countStarQueryGraph(QueryGraph body,  @NotNull Boolean distinct, SecurityContext securityContext) throws NotFoundException {
        logger.info("Count(*)");
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.countStar(body, distinct)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response distinctQueryGraph(QueryGraph body,  @NotNull Boolean distinct, SecurityContext securityContext) throws NotFoundException {
        logger.info("Count(*)");
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.setDistinct(body, distinct)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response limitQueryGraph(QueryGraph body,  @NotNull Integer limit, SecurityContext securityContext) throws NotFoundException {
        logger.info("Count(*)");
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.setLimit(body, limit)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response offsetQueryGraph(QueryGraph body,  @NotNull Integer offset, SecurityContext securityContext) throws NotFoundException {
        logger.info("Count(*)");
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.setOffset(body, offset)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
}
