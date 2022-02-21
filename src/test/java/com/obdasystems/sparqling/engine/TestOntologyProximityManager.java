package com.obdasystems.sparqling.engine;

import com.obdasystems.sparqling.parsers.GraphOLParser_v3;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class TestOntologyProximityManager {

    static String owlFilePath = "src/test/resources/istat/istat_1.1.38/ISTAT-SIR_v1.1.38.owl";
    static String grapholFilePath =  "src/test/resources/books/books_ontology.graphol";
    //static String grapholFilePath =   null;//"src/test/resources/books/books_ontology.graphol";

    public static void main(String[] args) throws IOException, OWLOntologyCreationException {
        //TODO CONTROLLA Set relativi a domain e range delle propertis
        long t = System.currentTimeMillis();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        if(grapholFilePath!=null) {
            GraphOLParser_v3 parser = new GraphOLParser_v3();
            ontology = parser.parseOWLOntology(
                    Files.readAllLines(Paths.get(grapholFilePath)).stream().collect(Collectors.joining()),
                    manager);

            ontology.getLogicalAxioms().forEach(ax->{
                System.out.println(ax);
            });
        }
        else {
            ontology = manager.loadOntologyFromOntologyDocument(new File(owlFilePath));
        }
        OntologyProximityManager proximityManager = new OntologyProximityManager(ontology);
        t = System.currentTimeMillis() - t;

        String[] pathSplit = owlFilePath.split("/");
        String testFilePath = "";
        for (int i = 0; i < pathSplit.length - 1; i++) {
            if(i!=0) {
                testFilePath += "/";
            }
            testFilePath += pathSplit[i];
        }
        testFilePath += "/" + pathSplit[pathSplit.length - 1].substring(0, pathSplit[pathSplit.length - 1].lastIndexOf('.')) + ".txt";
        BufferedWriter bf = new BufferedWriter(new FileWriter(testFilePath));
        bf.write("Init time: (" + t + " ms)\n\n");

        ontology.getClassesInSignature(Imports.INCLUDED).forEach(cl -> {
            try {
                bf.write("\n########################## Class " + cl.getIRI().getShortForm() + " ##########################");
                bf.write("\n------- DESCENDANTS -------");
                for (OWLClass op : proximityManager.getClassDescendants(cl)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- FATHERS -------");
                for (OWLClass op : proximityManager.getClassFathers(cl)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- ANCESTORS -------");
                for (OWLClass op : proximityManager.getClassAncestors(cl)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- NON DISJOINT SIBLINGS -------");
                for (OWLClass op : proximityManager.getClassNonDisjointSiblings(cl)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- DISJOINT -------");
                for (OWLClass op : proximityManager.getClassDisjoint(cl)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- ROLES -------");
                for (OWLObjectProperty op : proximityManager.getClassRoles(cl)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- ATTRIBUTES -------");
                for (OWLDataProperty op : proximityManager.getClassAttributes(cl)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bf.write("\n");
        ontology.getObjectPropertiesInSignature().forEach(objProp -> {
            try {
                bf.write("\n########################## ObjectProperty " + objProp.getIRI().getShortForm() + " ##########################");
                bf.write("\n------- DOMAIN -------");
                for (OWLClass op : proximityManager.getObjPropDomain(objProp)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- RANGE -------");
                for (OWLClass op : proximityManager.getObjPropRange(objProp)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- CHILDREN -------");
                for (OWLObjectProperty op : proximityManager.getObjPropChildren(objProp)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- ANCESTORS -------");
                for (OWLObjectProperty op : proximityManager.getObjPropAncestors(objProp)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bf.write("\n");
        ontology.getDataPropertiesInSignature().forEach(dtProp -> {
            try {
                bf.write("\n########################## DataProperty " + dtProp.getIRI().getShortForm() + " ##########################");
                bf.write("\n------- DOMAIN -------");
                for (OWLClass op : proximityManager.getDataPropDomainMap(dtProp)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- CHILDREN -------");
                for (OWLDataProperty op : proximityManager.getDataPropChildrenMap(dtProp)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.write("\n------- ANCESTORS -------");
                for (OWLDataProperty op : proximityManager.getDataPropAncestorsMap(dtProp)) {
                    bf.write("\n" + op.getIRI().getShortForm());
                }
                bf.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        bf.close();
    }

}
