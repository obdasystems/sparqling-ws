package com.obdasystems.sparqling.engine;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SWSOntologyManager {
    private static SWSOntologyManager ontologyManager;
    private OWLOntology owlOntology;
    private OWLOntologyManager owlOntologyManager;
    static {
        SWSOntologyManager.ontologyManager = new SWSOntologyManager();
    }

    private String graphol;

    public static SWSOntologyManager getOntologyManager() {
        return SWSOntologyManager.ontologyManager;
    }

    private SWSOntologyManager() {
        owlOntologyManager = OWLManager.createOWLOntologyManager();
    }

    public void loadOWLOntology(InputStream upfileInputStream) throws OWLOntologyCreationException {
        owlOntology = owlOntologyManager.loadOntologyFromOntologyDocument(upfileInputStream);
    }

    public OWLOntology getOwlOntology() {
        return owlOntology;
    }

    public void setGrapholFile(InputStream upfileInputStream) {
        graphol = new BufferedReader(new InputStreamReader(upfileInputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
    }

    public String getGraphol() {
        return graphol;
    }
}
