# Sparqling Web Services

## Overview
This server was developed to support the point and click SPARQL query builder [Sparqling](https://github.com/obdasystems/sparqling).
It uses a simple owl reasoner to deduce further information about the ontology and suggest correct paths in the query graph.

## How to run it
1. Install the dependencies via maven: `mvn install`
2. Run the main class: `com.obdasystems.server.SparqlingServer`
3. If you provide as first argument the Sparqling ui it will be served as well (e.g. `/some/path/sparqling/demo`)
4. Now the server (Powered by Jetty) runs on port 7979

## API Implementation
This server was generated by the [swagger-codegen](https://github.com/swagger-api/swagger-codegen) project. By using the 
[OpenAPI-Spec](https://github.com/swagger-api/swagger-core/wiki) from a remote server, you can easily generate a server stub.  This
is an example of building a swagger-enabled JAX-RS server.

This example uses the [JAX-RS](https://jax-rs-spec.java.net/) framework.

You can then view the swagger listing here:

```
Swagger / OpenAPI v2: http://localhost:7979/sparqling/1.0.0/swagger.json
Swagger / OpenAPI v3: http://localhost:7979/sparqling/1.0.0/openapi.json
```

You can test the API calls here:

https://app.swaggerhub.com/apis/OBDASystems/swagger-sparqling_ws

## Main dependencies

- https://github.com/owlcs/owlapi
- https://jena.apache.org/documentation/extras/querybuilder/index.html
