package com.obdasystems.sparqling.engine;

import com.obdasystems.sparqling.parsers.GraphOLParser_v3;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestParserAndClosureOwlGrapholEquivalence {

    public static void main(String[] args) {
        try {
            Path basePath = Paths.get("src/test/resources/");
            File reportFile = new File(basePath + "/owl_graphol_equivalence/test_report.txt");
            PrintWriter writer = new PrintWriter(reportFile);
            GraphOLParser_v3 parser = new GraphOLParser_v3();
            Configuration config = new Configuration();
            Files.walk(basePath).filter(Files::isDirectory).filter(p -> !p.equals(basePath)).forEach(subFolder -> {
                try {
                    writer.println("#########Loading folder: " + subFolder);
                    List<Path> owlFile = Files.walk(subFolder).filter(Files::isRegularFile).filter(f -> f.toString().substring(f.toString().lastIndexOf(".") + 1).toLowerCase().equals("owl")).collect(Collectors.toList());
                    List<Path> grapholFile = Files.walk(subFolder).filter(Files::isRegularFile).filter(f -> f.toString().substring(f.toString().lastIndexOf(".") + 1).toLowerCase().equals("graphol")).collect(Collectors.toList());
                    if (owlFile.isEmpty() || grapholFile.isEmpty()) {
                        writer.println("In the folder it was not possible to find the owl file or the graphol file");
                    } else {
                        writer.println("Loading " + grapholFile.get(0).toString());
                        OWLOntology grapholOntology = parser.parseOWLOntology(
                                Files.readAllLines(Paths.get(grapholFile.get(0).toString())).stream().collect(Collectors.joining()),
                                OWLManager.createOWLOntologyManager());
                        Reasoner grapholHermit = new Reasoner(config, grapholOntology);
                        writer.println("Loaded!!");

                        writer.println("Loading " + owlFile.get(0).toString());
                        OWLOntologyManager man = grapholOntology.getOWLOntologyManager();
                        OWLOntology owlOntology = man.loadOntologyFromOntologyDocument(new File(owlFile.get(0).toString()));
                        Reasoner owlHermit = new Reasoner(config, owlOntology);
                        writer.println("Loaded!!");


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
