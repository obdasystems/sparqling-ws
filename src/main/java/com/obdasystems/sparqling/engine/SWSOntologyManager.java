package com.obdasystems.sparqling.engine;

import com.obdasystems.sparqling.model.Entity;
import com.obdasystems.sparqling.parsers.GraphOLParser_v3;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SWSOntologyManager {
    private static SWSOntologyManager ontologyManager;
    private final GraphOLParser_v3 grapholParser;
    private OWLOntology owlOntology;
    private final OWLOntologyManager owlOntologyManager;

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

    public static Entity extractEntity(OWLOntology owlOntology, IRI iri, PrefixDocumentFormat pdf) {
        Entity entity = new Entity();
        OWLAnnotationProperty labelProp = owlOntology.getOWLOntologyManager().getOWLDataFactory()
                .getOWLAnnotationProperty(IRI.create(RDFS.label.getURI()));
        Map<String, String> labels = new HashMap<>();
        for (OWLAnnotationAssertionAxiom annotation : owlOntology.getAnnotationAssertionAxioms(iri)) {
            if (!annotation.getValue().asLiteral().isPresent())
                continue;
            String value = annotation.getValue().asLiteral().get().getLiteral();
            String lang = annotation.getValue().asLiteral().get().getLang();
            if (annotation.getProperty().equals(labelProp)) {
                labels.put(lang, value);
            }
        }
        entity.setLabels(labels);
        entity.setIri(iri.toString());
        String entityShortIri = pdf.getPrefixIRI(iri);
        if (entityShortIri != null) {
            entity.setPrefixedIri(entityShortIri);
        }
        else {
            Set<String> prefixNames = pdf.getPrefixNames();
            List<String> orderedPrefixes = new LinkedList<>();
            Map<String, String> inv = new HashMap<>();
            for (String p : prefixNames) {
                orderedPrefixes.add(pdf.getPrefix(p));
                inv.put(pdf.getPrefix(p), p);
            }
            orderedPrefixes.sort((o1, o2) -> {
                if (o1 == null)
                    return 1;
                if (o2 == null)
                    return -1;
                return o1.length() - o2.length();
            });
            boolean found = false;
            for (String p : orderedPrefixes) {
                String str = entity.getIri();
                if (str.startsWith(p)) {
                    String prefixName = inv.get(p);
                    entity.setPrefixedIri(str.replace(p, prefixName));
                    found = true;
                    break;
                }
            }
            if (!found) {
                entity.setPrefixedIri(entityShortIri);
            }
        }

        if(IRI.create(RDFS.label.getURI()).equals(iri) || IRI.create(RDFS.label.getURI()).equals(iri)) {
            entity.setType(Entity.TypeEnum.ANNOTATION);
            return entity;
        }

        for(OWLClass c:owlOntology.getClassesInSignature()) {
            if(c.getIRI().equals(iri)) {
                entity.setType(Entity.TypeEnum.CLASS);
                return entity;
            }
        }
        for(OWLObjectProperty c:owlOntology.getObjectPropertiesInSignature()) {
            if(c.getIRI().equals(iri)) {
                entity.setType(Entity.TypeEnum.OBJECTPROPERTY);
                return entity;
            }
        }
        for(OWLDataProperty c:owlOntology.getDataPropertiesInSignature()) {
            if(c.getIRI().equals(iri)) {
                entity.setType(Entity.TypeEnum.DATAPROPERTY);
                return entity;
            }
        }

        return entity;
    }

    public void loadGrapholFile(InputStream upfileInputStream) {
        graphol = new BufferedReader(new InputStreamReader(upfileInputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        clearOntology();
        owlOntology = grapholParser.parseOWLOntology(graphol, owlOntologyManager);
        setProximityManager();
    }

    public String getGraphol() {
        return graphol;
    }

    public void loadOWLOntologyFile(InputStream upfileInputStream) throws OWLOntologyCreationException {
        owlOntologyManager.getOntologyStorers().clear();
        clearOntology();
        owlOntology = owlOntologyManager.loadOntologyFromOntologyDocument(upfileInputStream);
        if (owlOntology.isEmpty()) throw new RuntimeException("OWL Ontology is empty!");
        setProximityManager();
    }

    private void clearOntology() {
        Optional<OWLOntology> oldOntology = owlOntologyManager.getOntologies().stream().findAny();
        if(oldOntology.isPresent()) {
            owlOntologyManager.removeOntology(oldOntology.get());
        }
    }

    public OWLOntology getOwlOntology() {
        return owlOntology;
    }

    private void setProximityManager() {
        proximityManager = new OntologyProximityManager(owlOntology);
    }

    public OntologyProximityManager getOntologyProximityManager() {
        return proximityManager;
    }
}
