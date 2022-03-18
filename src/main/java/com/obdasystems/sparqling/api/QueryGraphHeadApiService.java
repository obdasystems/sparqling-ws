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
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-03-18T08:15:14.674Z[GMT]")public abstract class QueryGraphHeadApiService {
    public abstract Response addHeadTerm(QueryGraph body,String graphElementId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response aggregationHavingHeadTerm(QueryGraph body, @NotNull String direction,String headTerm,SecurityContext securityContext) throws NotFoundException;
    public abstract Response aggregationHeadTerm(QueryGraph body,String headTerm,SecurityContext securityContext) throws NotFoundException;
    public abstract Response deleteHeadTerm(QueryGraph body,String headTerm,SecurityContext securityContext) throws NotFoundException;
    public abstract Response functionHeadTerm(QueryGraph body,String headTerm,SecurityContext securityContext) throws NotFoundException;
    public abstract Response orderByHeadTerm(QueryGraph body, @NotNull String direction,String headTerm,SecurityContext securityContext) throws NotFoundException;
    public abstract Response renameHeadTerm(QueryGraph body,String headTerm,SecurityContext securityContext) throws NotFoundException;
}
