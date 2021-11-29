package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.StandaloneApiService;
import com.obdasystems.sparqling.api.impl.StandaloneApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-11-29T11:28:53.694Z[GMT]")public class StandaloneApiServiceFactory {
    private final static StandaloneApiService service = new StandaloneApiServiceImpl();

    public static StandaloneApiService getStandaloneApi() {
        return service;
    }
}
