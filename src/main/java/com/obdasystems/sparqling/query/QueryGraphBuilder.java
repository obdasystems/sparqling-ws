package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.GraphElement;
import com.obdasystems.sparqling.model.HeadElement;
import com.obdasystems.sparqling.model.QueryGraph;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashMap;
import java.util.Map;

public class QueryGraphBuilder {
    private final OWLOntology ontology;
    private final Map<String, String> prefixes;
    private final PrefixDocumentFormat pdf;

    public QueryGraphBuilder() {
        ontology = SWSOntologyManager.getOntologyManager().getOwlOntology();
        if(ontology == null) throw new RuntimeException("Please load an ontology before start building queries.");
        pdf = (PrefixDocumentFormat) ontology.getOWLOntologyManager().getOntologyFormat(ontology);
        if (pdf != null && pdf.isPrefixOWLOntologyFormat()) {
            prefixes = pdf.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap();
        } else {
            prefixes = new HashMap<>();
        }
    }

    public QueryGraph addClassTriplePattern(String clickedClassIRI) {
        IRI iri = IRI.create(clickedClassIRI);
        if(!ontology.containsClassInSignature(iri)) {
            throw new RuntimeException("Iri " + clickedClassIRI + " not found in ontology " + ontology.getOntologyID());
        }
        String var = guessNewVarFromIRI(iri);
        SelectBuilder sb = new SelectBuilder();
        sb.addPrefixes(prefixes);
        sb.addVar(var).addWhere(var, "a", iri.toQuotedString());
        QueryGraph qg = new QueryGraph();
        HeadElement headElement = new HeadElement();
        headElement.setVar(var);
        qg.addHeadItem(headElement);
        qg.setSparql(sb.build().serialize());
        GraphElement root = new GraphElement();
        root.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(iri, pdf));
        root.setId(var);
        qg.setGraph(root);
        return qg;
    }

    private String guessNewVarFromIRI(IRI iri) {
        String res = iri.getFragment();
        if(res.isEmpty()) {
            res = "x" + System.currentTimeMillis();
        }
        return "?" + res;
    }
}
