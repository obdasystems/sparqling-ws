package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.QueryGraphBgpApiService;
import com.obdasystems.sparqling.api.impl.QueryGraphBgpApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-08T10:35:15.892Z[GMT]")public class QueryGraphBgpApiServiceFactory {
    private final static QueryGraphBgpApiService service = new QueryGraphBgpApiServiceImpl();

    public static QueryGraphBgpApiService getQueryGraphBgpApi() {
        return service;
    }
}
