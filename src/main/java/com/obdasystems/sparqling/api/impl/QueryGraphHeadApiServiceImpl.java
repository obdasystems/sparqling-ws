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
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-08T10:35:15.892Z[GMT]")public class QueryGraphHeadApiServiceImpl extends QueryGraphHeadApiService {
    final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public Response addHeadTerm(QueryGraph body, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        logger.info("Adding term {} to query head.", graphElementId);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.addHeadTerm(body, graphElementId)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response aggregationHeadTerm(QueryGraph body, String headTerm, SecurityContext securityContext) throws NotFoundException {
        logger.info("Changing aggregation function to head term {}", headTerm);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.aggregationHeadTerm(body, headTerm)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response deleteHeadTerm(QueryGraph body, String headTerm, SecurityContext securityContext) throws NotFoundException {
        logger.info("Deleting head term {}", headTerm);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.deleteHeadTerm(body, headTerm)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response functionHeadTerm(QueryGraph body, String headTerm, SecurityContext securityContext) throws NotFoundException {
        logger.info("Changing function to head term {}", headTerm);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.functionHeadTerm(body, headTerm)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response orderByHeadTerm(QueryGraph body,  String headTerm, SecurityContext securityContext) throws NotFoundException {
        logger.info("Ordering by {}", headTerm);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.orderBy(body, headTerm)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response renameHeadTerm(QueryGraph body, String headTerm, SecurityContext securityContext) throws NotFoundException {
        logger.info("Renaming head term {}", headTerm);
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.renameHeadTerm(body, headTerm)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response reorderHeadTerms(QueryGraph body, SecurityContext securityContext) throws NotFoundException {
        logger.info("Reordering head terms");
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.reorderHeadTerm(body)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
}