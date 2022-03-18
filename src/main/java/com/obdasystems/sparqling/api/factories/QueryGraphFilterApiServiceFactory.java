package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.QueryGraphFilterApiService;
import com.obdasystems.sparqling.api.impl.QueryGraphFilterApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-03-18T08:15:14.674Z[GMT]")public class QueryGraphFilterApiServiceFactory {
    private final static QueryGraphFilterApiService service = new QueryGraphFilterApiServiceImpl();

    public static QueryGraphFilterApiService getQueryGraphFilterApi() {
        return service;
    }
}
