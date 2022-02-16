package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.QueryGraphHeadApiService;
import com.obdasystems.sparqling.api.impl.QueryGraphHeadApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-02-15T15:04:14.983Z[GMT]")public class QueryGraphHeadApiServiceFactory {
    private final static QueryGraphHeadApiService service = new QueryGraphHeadApiServiceImpl();

    public static QueryGraphHeadApiService getQueryGraphHeadApi() {
        return service;
    }
}
