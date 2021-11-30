package com.obdasystems.sparqling.engine;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.InputStream;

public class SWSOntologyManager {
    private static SWSOntologyManager ontologyManager;
    private OWLOntology owlOntology;
    private OWLOntologyManager owlOntologyManager;
    static {
        SWSOntologyManager.ontologyManager = new SWSOntologyManager();
    }

    public static SWSOntologyManager getOntologyManager() {
        return SWSOntologyManager.ontologyManager;
    }

    private SWSOntologyManager() {
        owlOntologyManager = OWLManager.createOWLOntologyManager();
    }

    public void loadOWLOntology(InputStream upfileInputStream) throws OWLOntologyCreationException {
        owlOntology = owlOntologyManager.loadOntologyFromOntologyDocument(upfileInputStream);
    }
}
