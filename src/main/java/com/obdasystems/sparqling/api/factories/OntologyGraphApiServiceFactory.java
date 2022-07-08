package com.obdasystems.sparqling.api.factories;

import com.obdasystems.sparqling.api.OntologyGraphApiService;
import com.obdasystems.sparqling.api.impl.OntologyGraphApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-04-15T09:25:55.884Z[GMT]")public class OntologyGraphApiServiceFactory {
    private final static OntologyGraphApiService service = new OntologyGraphApiServiceImpl();

    public static OntologyGraphApiService getOntologyGraphApi() {
        return service;
    }
}
