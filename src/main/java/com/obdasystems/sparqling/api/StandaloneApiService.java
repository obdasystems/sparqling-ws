package com.obdasystems.sparqling.api;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.File;

import java.util.Map;
import java.util.List;
import com.obdasystems.sparqling.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-02-04T11:47:40.527Z[GMT]")public abstract class StandaloneApiService {
    public abstract Response standaloneOntologyGrapholGet(SecurityContext securityContext) throws NotFoundException;
    public abstract Response standaloneOntologyUploadPost(InputStream fileInputStream, FormDataContentDisposition fileDetail,SecurityContext securityContext) throws NotFoundException;
}
