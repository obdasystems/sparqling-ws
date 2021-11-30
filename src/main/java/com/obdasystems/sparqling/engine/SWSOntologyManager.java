package com.obdasystems.sparqling.engine;

import com.obdasystems.sparqling.parsers.GraphOLParser_v3;
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
    private final GraphOLParser_v3 grapholParser;
    private OWLOntology owlOntology;
    private OWLOntologyManager owlOntologyManager;

    private OntologyProximityManager proximityManager;

    static {
        SWSOntologyManager.ontologyManager = new SWSOntologyManager();
    }

    private String graphol;

    public static SWSOntologyManager getOntologyManager() {
        return SWSOntologyManager.ontologyManager;
    }

    private SWSOntologyManager() {
        owlOntologyManager = OWLManager.createOWLOntologyManager();
        grapholParser = new GraphOLParser_v3();
    }

    public void loadGrapholFile(InputStream upfileInputStream) {
        graphol = new BufferedReader(new InputStreamReader(upfileInputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        owlOntology = grapholParser.parseOWLOntology(graphol, owlOntologyManager);
        setProximityManager(owlOntology);
    }

    public String getGraphol() {
        return graphol;
    }

    public void loadOWLOntologyFile(InputStream upfileInputStream) throws OWLOntologyCreationException {
        owlOntology = owlOntologyManager.loadOntologyFromOntologyDocument(upfileInputStream);
        setProximityManager(owlOntology);
    }

    public OWLOntology getOwlOntology() {
        return owlOntology;
    }

    private void setProximityManager(OWLOntology ontology) {
        proximityManager = new OntologyProximityManager(owlOntology);
    }




}
