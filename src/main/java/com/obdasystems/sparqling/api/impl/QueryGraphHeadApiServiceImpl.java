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
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-02-15T15:04:14.983Z[GMT]")public class QueryGraphHeadApiServiceImpl extends QueryGraphHeadApiService {
    Logger logger = LoggerFactory.getLogger(QueryGraphHeadApiServiceImpl.class);
    @Override
    public Response addHeadTerm(QueryGraph body, String graphElementId, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.addHeadTerm(body, graphElementId)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response aggregationHavingHeadTerm(QueryGraph body,  @NotNull String direction, String headTerm, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response aggregationHeadTerm(QueryGraph body, String headTerm, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response deleteHeadTerm(QueryGraph body, String headTerm, SecurityContext securityContext) throws NotFoundException {
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
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response orderByHeadTerm(QueryGraph body,  @NotNull String direction, String headTerm, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response renameHeadTerm(QueryGraph body, String headTerm, SecurityContext securityContext) throws NotFoundException {
        try {
            QueryGraphHandler qgb = new QueryGraphHandler();
            return Response.ok().entity(qgb.renameHeadTerm(body, headTerm)).build();
        } catch (Exception e) {
            logger.error("Error!", e);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
}
