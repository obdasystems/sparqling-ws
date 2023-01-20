package com.obdasystems.sparqling.engine;

import com.obdasystems.sparqling.parsers.GraphOLParser_v3;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class TestOntologyProximityManager {

    static Logger logger = LoggerFactory.getLogger(TestOntologyProximityManager.class);

    @Test
    public void testIssue_28() throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        GraphOLParser_v3 parser = new GraphOLParser_v3();

        FileInputStream fileInputStream = new FileInputStream("src/test/resources/issue_28/SparqlingTest.graphol");

        String graphol = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        OWLOntology ontology = parser.parseOWLOntology(graphol, manager);

        OntologyProximityManager proximityManager = new OntologyProximityManager(ontology);

        OWLDataFactory df = manager.getOWLDataFactory();
        OWLClass class_A = df.getOWLClass(IRI.create("http://example.com/ontology/A"));
        OWLClass class_B = df.getOWLClass(IRI.create("http://example.com/ontology/B"));
        OWLClass class_C = df.getOWLClass(IRI.create("http://example.com/ontology/C"));
        OWLDataProperty dt_a = df.getOWLDataProperty(IRI.create("http://example.com/ontology/a"));
        OWLDataProperty dt_b = df.getOWLDataProperty(IRI.create("http://example.com/ontology/b"));
        OWLObjectProperty obj_r = df.getOWLObjectProperty(IRI.create("http://example.com/ontology/r"));

        Set<OWLDataProperty> classAttributes = proximityManager.getClassAttributes(class_A);
        TestCase.assertTrue(classAttributes.size()==2);
        TestCase.assertTrue(classAttributes.contains(dt_a));
        TestCase.assertTrue(classAttributes.contains(dt_b));
    }

    @Test
    public void testIssue_29() throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        GraphOLParser_v3 parser = new GraphOLParser_v3();

        FileInputStream fileInputStream = new FileInputStream("src/test/resources/issue_29/italianTerritoryOntology_v1.1.graphol");
        String graphol = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        OWLOntology ontology = parser.parseOWLOntology(graphol, manager);

        OntologyProximityManager proximityManager = new OntologyProximityManager(ontology);

        OWLClass comune = df.getOWLClass(IRI.create("https://w3id.org/italia/onto/ITO/Comune"));
        OWLClass uts = df.getOWLClass(IRI.create("https://w3id.org/italia/onto/ITO/Unita_territoriale_sovracomunale"));
        OWLClass regione = df.getOWLClass(IRI.create("https://w3id.org/italia/onto/ITO/Regione"));
        OWLClass stato = df.getOWLClass(IRI.create("https://w3id.org/italia/onto/ITO/Stato"));
        OWLClass uaEstinta = df.getOWLClass(IRI.create("https://w3id.org/italia/onto/ITO/Unita_amministrativa_estinta"));
        OWLClass uaNonEstinta = df.getOWLClass(IRI.create("https://w3id.org/italia/onto/ITO/Unita_amministrativa_non_estinta"));
        OWLClass unitaAmministrativa = df.getOWLClass(IRI.create("https://w3id.org/italia/onto/ITO/Unita_amministrativa"));
        Set<OWLClass> classes = Stream.of(comune, uts, regione, stato, uaEstinta, uaNonEstinta, unitaAmministrativa)
                .collect(Collectors.toCollection(HashSet::new));

        OWLObjectProperty attualeUAOrigine = df.getOWLObjectProperty(IRI.create("https://w3id.org/italia/onto/ITO/attuale_unita_amministrativa_di_origine"));
        OWLObjectProperty storicoUA = df.getOWLObjectProperty(IRI.create("https://w3id.org/italia/onto/ITO/ha_storico_di_unita_amministrativa"));
        OWLObjectProperty uaOrigine = df.getOWLObjectProperty(IRI.create("https://w3id.org/italia/onto/ITO/unita_amministrativa_di_origine"));
        OWLObjectProperty subisce = df.getOWLObjectProperty(IRI.create("https://w3id.org/italia/onto/ITO/subisce"));
        Set<OWLObjectProperty> roles = Stream.of(attualeUAOrigine, storicoUA, uaOrigine, subisce)
                .collect(Collectors.toCollection(HashSet::new));

        OWLDataProperty denominazione = df.getOWLDataProperty(IRI.create("https://w3id.org/italia/onto/ITO/denominazione_unita_amministrativa_attuale"));
        OWLDataProperty dataCostituzione = df.getOWLDataProperty(IRI.create("https://w3id.org/italia/onto/ITO/data_costituzione"));
        OWLDataProperty codiceIstat = df.getOWLDataProperty(IRI.create("https://w3id.org/italia/onto/ITO/codice_istat_UA"));
        Set<OWLDataProperty> attrs = Stream.of(denominazione, dataCostituzione, codiceIstat)
                .collect(Collectors.toCollection(HashSet::new));

    }



    public static void main(String[] args) throws IOException, OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        GraphOLParser_v3 parser = new GraphOLParser_v3();
        String grapholFilePath =  "src/test/resources/issue_29/italianTerritoryOntology_v1.1.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/ISTAT_SPARQLING_ISSUE_29_0.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NOT_appartiene_attualmente_a_regione.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NOT_contiene_attualmente_sezione_di_censimento.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NOT_OR_Comune_estinto.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NOT_ppartiene_attualmente_a_UTS.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NO_NOTS.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NO_NOTS_CHAIN.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NO_NOTS_CHAIN_ATTRS.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NO_NOTS_CHAIN_ATTRS_Sezione_di_censimento.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NO_NOTS_CHAIN_ATTRS_Sezione_di_censimento_Storico_unita_amministrativa.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/NO_Storico_unita_amministrativa.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/SOLO_STORICO.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/SOLO_STORICO_NO_ISA_RUOLI.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/SOLO_STORICO_MINIMAL.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/SOLO_STORICO_MINIMAL_NO_ISA_ha_storico_di_comune.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/SOLO_STORICO_MINIMAL_SOLO_ISA_ha_storico_di_comune.graphol";
        //String grapholFilePath =  "src/test/resources/issue_29/SOLO_STORICO_MINIMAL_SOLO_ISA_ha_storico_di_stato.graphol";
        FileInputStream fileInputStream = new FileInputStream(grapholFilePath);

        String graphol = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        OWLOntology ontology = parser.parseOWLOntology(graphol, manager);

        OntologyProximityManager proximityManager = new OntologyProximityManager(ontology);

        String[] pathSplit = grapholFilePath.split("/");
        String testFilePath = "";
        for (int i = 0; i < pathSplit.length - 1; i++) {
            if(i!=0) {
                testFilePath += "/";
            }
            testFilePath += pathSplit[i];
        }
        testFilePath += "/" + pathSplit[pathSplit.length - 1].substring(0, pathSplit[pathSplit.length - 1].lastIndexOf('.')) + ".txt";
        BufferedWriter bf = new BufferedWriter(new FileWriter(testFilePath));

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
