package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.QueryGraphApiService;
import com.obdasystems.sparqling.api.impl.QueryGraphApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-11-29T11:28:53.694Z[GMT]")public class QueryGraphApiServiceFactory {
    private final static QueryGraphApiService service = new QueryGraphApiServiceImpl();

    public static QueryGraphApiService getQueryGraphApi() {
        return service;
    }
}
