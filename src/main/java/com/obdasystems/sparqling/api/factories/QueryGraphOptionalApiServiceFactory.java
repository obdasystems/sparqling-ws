package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.QueryGraphOptionalApiService;
import com.obdasystems.sparqling.api.impl.QueryGraphOptionalApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T09:25:55.884Z[GMT]")public class QueryGraphOptionalApiServiceFactory {
    private final static QueryGraphOptionalApiService service = new QueryGraphOptionalApiServiceImpl();

    public static QueryGraphOptionalApiService getQueryGraphOptionalApi() {
        return service;
    }
}
