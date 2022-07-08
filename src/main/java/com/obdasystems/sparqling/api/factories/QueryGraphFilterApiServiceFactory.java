package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.QueryGraphFilterApiService;
import com.obdasystems.sparqling.api.impl.QueryGraphFilterApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T09:25:55.884Z[GMT]")public class QueryGraphFilterApiServiceFactory {
    private final static QueryGraphFilterApiService service = new QueryGraphFilterApiServiceImpl();

    public static QueryGraphFilterApiService getQueryGraphFilterApi() {
        return service;
    }
}
