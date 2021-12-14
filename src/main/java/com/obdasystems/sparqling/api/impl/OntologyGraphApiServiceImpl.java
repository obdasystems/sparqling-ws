package com.obdasystems.sparqling.api.impl;

import com.obdasystems.sparqling.api.*;
import com.obdasystems.sparqling.model.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import com.obdasystems.sparqling.api.NotFoundException;
import com.obdasystems.sparqling.engine.OntologyProximityManager;
import com.obdasystems.sparqling.engine.SWSOntologyManager;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-11-29T11:28:53.694Z[GMT]")public class OntologyGraphApiServiceImpl extends OntologyGraphApiService {
    @Override
    public Response highligths( @NotNull String clickedClassIRI,  List<String> params, SecurityContext securityContext) throws NotFoundException {
        try {
            OntologyProximityManager opm = SWSOntologyManager.getOntologyManager().getOntologyProximityManager();
            Highlights ret = new Highlights();
            OWLClass cl = new OWLClassImpl(IRI.create(clickedClassIRI));
            
            Set<OWLClass> classes = new HashSet<>();
            classes.addAll(opm.getClassDescendants(cl));
            classes.addAll(opm.getClassAncestors(cl));
            classes.addAll(opm.getClassNonDisjointSiblings(cl));
            ret.setClasses(classes.stream().map(i -> i.getIRI().toString()).collect(Collectors.toList()));

            for(OWLObjectProperty i : opm.getClassRoles(cl)) {
                Branch b = new Branch();
                b.setObjectPropertyIRI(i.getIRI().toString());
                if(opm.getObjPropDomain(i).contains(cl)) {
                    if(opm.getObjPropRange(i).contains(cl)) {
                        b.setCyclic(true);
                    }
                    for(OWLClass c:opm.getObjPropRange(i)) {
                        b.addRelatedClassesItem(c.getIRI().toString());
                    }
                    b.setDirect(true);
                } else if(opm.getObjPropRange(i).contains(cl)) {
                    if(opm.getObjPropDomain(i).contains(cl)) {
                        b.setCyclic(true);
                    }
                    for(OWLClass c:opm.getObjPropDomain(i)) {
                        b.addRelatedClassesItem(c.getIRI().toString());
                    }
                    b.setDirect(false);
                }
                ret.addObjectPropertiesItem(b);
            }

            for(OWLDataProperty i : opm.getClassAttributes(cl)) {
                ret.addDataPropertiesItem(i.getIRI().toString());
            }

            return Response.ok().entity(ret).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
        }
    }
    @Override
    public Response highligthsPaths( @NotNull String lastSelectedIRI,  @NotNull String clickedIRI, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
