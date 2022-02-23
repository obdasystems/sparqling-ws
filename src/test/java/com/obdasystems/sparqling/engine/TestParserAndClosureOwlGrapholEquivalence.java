package com.obdasystems.sparqling.engine;

import com.obdasystems.sparqling.parsers.GraphOLParser_v3;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestParserAndClosureOwlGrapholEquivalence {

    static Logger logger = LoggerFactory.getLogger(TestParserAndClosureOwlGrapholEquivalence.class);

    public static void main(String[] args) {
        try {
            Path basePath = Paths.get("src/test/resources/istat");
            //Path basePath = Paths.get("src/test/resources/books");
            File reportFile = new File(basePath + "/test_report.txt");

            PrintWriter writer = new PrintWriter(reportFile);
            GraphOLParser_v3 parser = new GraphOLParser_v3();
            Configuration config = new Configuration();
            Files.walk(basePath).filter(Files::isDirectory).filter(p -> !p.equals(basePath)).forEach(subFolder -> {
                try {
                    writer.println("#########Loading folder: " + subFolder);
                    logger.info("#########Loading folder: " + subFolder);
                    List<Path> owlFile = Files.walk(subFolder).filter(Files::isRegularFile).filter(f -> f.toString().substring(f.toString().lastIndexOf(".") + 1).toLowerCase().equals("owl")).collect(Collectors.toList());
                    List<Path> grapholFile = Files.walk(subFolder).filter(Files::isRegularFile).filter(f -> f.toString().substring(f.toString().lastIndexOf(".") + 1).toLowerCase().equals("graphol")).collect(Collectors.toList());
                    if (owlFile.isEmpty() || grapholFile.isEmpty()) {
                        writer.println("In the folder it was not possible to find the owl file or the graphol file");
                    } else {
                        writer.println("Loading " + grapholFile.get(0).toString());
                        logger.info("Loading " + grapholFile.get(0).toString());
                        OWLOntology grapholOntology = parser.parseOWLOntology(
                                Files.readAllLines(Paths.get(grapholFile.get(0).toString())).stream().collect(Collectors.joining()),
                                OWLManager.createOWLOntologyManager());
                        Reasoner grapholHermit = new Reasoner(config, grapholOntology);
                        writer.println("Loaded!!");
                        logger.info("Loaded!!");

                        OWLOntologyManager man = grapholOntology.getOWLOntologyManager();
                        man.saveOntology(grapholOntology, new FunctionalSyntaxDocumentFormat() ,new FileOutputStream(new File(subFolder.toString()+"\\generated_files\\translated_graphol.owl")));

                        writer.println("Loading " + owlFile.get(0).toString());
                        logger.info("Loading " + owlFile.get(0).toString());
                        OWLOntology owlOntology = man.loadOntologyFromOntologyDocument(new File(owlFile.get(0).toString()));
                        Reasoner owlHermit = new Reasoner(config, owlOntology);
                        writer.println("Loaded!!");
                        logger.info("Loaded!!");




                        writer.println("###### List of axioms in the OWL ontology that are NOT implied by the graphol one");
                        Set<OWLLogicalAxiom> owlAxioms = owlOntology.getLogicalAxioms();
                        int i = 1;
                        for (OWLLogicalAxiom ax : owlAxioms) {
                            if (!grapholHermit.isEntailed(ax)) {
                                writer.println("[a" + i++ + "] --> " + ax);
                            }
                        }
                        writer.println("###### List of axioms in the graphol ontology that are NOT implied by the OWL one");
                        Set<OWLLogicalAxiom> grapholAxioms = grapholOntology.getLogicalAxioms();
                        i = 1;
                        for (OWLLogicalAxiom ax : grapholAxioms) {
                            if (!owlHermit.isEntailed(ax)) {
                                writer.println("[a" + i++ + "] --> " + ax);
                            }
                        }

                        writer.println();writer.println();writer.println();
                        logger.info("Computing closure of OWL ontology");
                        writer.println("Computing closure of OWL ontology");
                        OntologyProximityManager owlProximityManager = new OntologyProximityManager(owlOntology);
                        Set<OWLAxiom> owlClosure = owlProximityManager.getSimpleDeductiveClosure();
                        logger.info("Closure of OWL ontology has size: "+owlClosure.size());
                        writer.println("Closure of OWL ontology has size: "+owlClosure.size());
                        OWLOntology owlClosedOntology = man.createOntology(owlClosure);
                        man.saveOntology(owlClosedOntology, new FunctionalSyntaxDocumentFormat() ,new FileOutputStream(new File(subFolder.toString()+"\\generated_files\\expanded_owl.owl")));

                        Reasoner owlClosedHermit = new Reasoner(config, owlClosedOntology);
                        logger.info("Closure of OWL ontology computed");
                        writer.println("Closure of OWL ontology computed");

                        writer.println();

                        logger.info("Computing closure of Graphol ontology");
                        writer.println("Computing closure of Graphol ontology");
                        OntologyProximityManager grapholProximityManager = new OntologyProximityManager(grapholOntology);
                        Set<OWLAxiom> grapholClosure = grapholProximityManager.getSimpleDeductiveClosure();
                        logger.info("Closure of Graphol ontology has size: "+grapholClosure.size());
                        writer.println("Closure of Graphol ontology has size: "+grapholClosure.size());
                        OWLOntology grapholClosedOntology = man.createOntology(grapholClosure);
                        man.saveOntology(grapholClosedOntology, new FunctionalSyntaxDocumentFormat() ,new FileOutputStream(new File(subFolder.toString()+"\\generated_files\\expanded_graphol.owl")));
                        Reasoner grapholClosedHermit = new Reasoner(config, grapholClosedOntology);
                        logger.info("Closure of Graphol ontology computed");
                        writer.println("Closure of Graphol ontology computed");


                        writer.println("###### List of axioms in the OWL closure that are NOT implied by the graphol one");
                        Set<OWLLogicalAxiom> owlClosedAxioms = owlClosedOntology.getLogicalAxioms();
                        i = 1;
                        for (OWLLogicalAxiom ax : owlClosedAxioms) {
                            if (!grapholClosedHermit.isEntailed(ax)) {
                                writer.println("[a" + i++ + "] --> " + ax);
                            }
                        }
                        writer.println("###### List of axioms in the graphol closure that are NOT implied by the OWL one");
                        Set<OWLLogicalAxiom> grapholClosedAxioms = grapholClosedOntology.getLogicalAxioms();
                        i = 1;
                        for (OWLLogicalAxiom ax : grapholClosedAxioms) {
                            if (!owlClosedHermit.isEntailed(ax)) {
                                writer.println("[a" + i++ + "] --> " + ax);
                            }
                        }

                    }
                    writer.println("");
                    writer.println("");
                } catch (IOException e) {
                    writer.println(e.getMessage());
                    e.printStackTrace();
                    writer.println("");
                    writer.println("");
                } catch (OWLOntologyCreationException e) {
                    writer.println(e.getMessage());
                    e.printStackTrace();
                    writer.println("");
                    writer.println("");
                } catch (Exception e) {
                    writer.println(e.getMessage());
                    e.printStackTrace();
                    writer.println("");
                    writer.println("");
                }
            });
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

		/*
		String grapholFilepath = "/Users/lorenzo/Desktop/ACI.graphol";
		//String grapholFilepath = "/Users/lorenzo/EddyProjects/superheroes/superheroes.graphol";
		GraphOLParser_v3 parser = new GraphOLParser_v3();
		OWLOntology newParserOntology = parser.parseOWLOntology(grapholFilepath);

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		FunctionalSyntaxDocumentFormat format = new FunctionalSyntaxDocumentFormat();

		File file = new File("/Users/lorenzo/Desktop/ACI.owl");
		FileOutputStream fos;

			fos = new FileOutputStream(file);
			man.saveOntology(newParserOntology, format, fos);
			fos.flush();
			fos.close();

		Set<OWLLogicalAxiom> newParserAxioms = newParserOntology.getLogicalAxioms();
		for(OWLLogicalAxiom ax:newParserAxioms) {
			(ax);
		}*/
    }

}
