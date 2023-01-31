package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.model.QueryGraph;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-05-02T11:18:38.146Z[GMT]")public abstract class QueryGraphExtraApiService {
    public abstract Response countStarQueryGraph(QueryGraph body, @NotNull Boolean active, SecurityContext securityContext) throws NotFoundException;
    public abstract Response distinctQueryGraph(QueryGraph body, @NotNull Boolean distinct,SecurityContext securityContext) throws NotFoundException;
    public abstract Response limitQueryGraph(QueryGraph body, @NotNull Integer limit,SecurityContext securityContext) throws NotFoundException;
    public abstract Response offsetQueryGraph(QueryGraph body, @NotNull Integer offset,SecurityContext securityContext) throws NotFoundException;
}
