package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.QueryGraphHeadApiService;
import com.obdasystems.sparqling.api.impl.QueryGraphHeadApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-03-31T16:20:47.492Z[GMT]")public class QueryGraphHeadApiServiceFactory {
    private final static QueryGraphHeadApiService service = new QueryGraphHeadApiServiceImpl();

    public static QueryGraphHeadApiService getQueryGraphHeadApi() {
        return service;
    }
}
