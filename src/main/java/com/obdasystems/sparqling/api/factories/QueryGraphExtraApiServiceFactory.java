package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.QueryGraphExtraApiService;
import com.obdasystems.sparqling.api.impl.QueryGraphExtraApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-03-31T16:20:47.492Z[GMT]")public class QueryGraphExtraApiServiceFactory {
    private final static QueryGraphExtraApiService service = new QueryGraphExtraApiServiceImpl();

    public static QueryGraphExtraApiService getQueryGraphExtraApi() {
        return service;
    }
}
