package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.GraphElement;
import com.obdasystems.sparqling.model.HeadElement;
import com.obdasystems.sparqling.model.QueryGraph;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.arq.querybuilder.handlers.AggregationHandler;
import org.apache.jena.arq.querybuilder.handlers.SelectHandler;
import org.apache.jena.arq.querybuilder.handlers.WhereHandler;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.Syntax;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.syntax.*;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashMap;
import java.util.Iterator;
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

    private static String guessNewVarFromIRI(IRI iri, Query q) {
        String res = iri.getFragment();
        if(res.isEmpty()) {
            res = "x" + System.currentTimeMillis();
        } else {
            res = getNewCountedVarFromQuery(res, q);
        }
        return "?" + res;
    }

    public static String getNewCountedVarFromQuery(String varName, Query q) {
        final int[] count = {0};
        String ret = varName + count[0];
        if (q == null) return ret;
        final boolean[] found = {true};
        while (found[0]) {
            ret = varName + count[0];
            String finalRet = ret;
            ElementWalker.walk(
                    q.getQueryPattern(),
                    new ElementVisitorBase() {
                        @Override
                        public void visit(ElementPathBlock el) {
                            Iterator<TriplePath> it = el.patternElts();
                            while (it.hasNext()) {
                                TriplePath triple = it.next();
                                if (triple.getSubject().isVariable()) {
                                    if (((Var) triple.getSubject()).getVarName().equals(finalRet)) {
                                        found[0] = true;
                                        count[0]++;
                                        return;
                                    }
                                }
                                if (triple.getObject().isVariable()) {
                                    if (((Var) triple.getObject()).getVarName().equals(finalRet)) {
                                        found[0] = true;
                                        count[0]++;
                                        return;
                                    }
                                }
                            }
                            found[0] = false;
                        }
                    }
            );
        }
        return ret;
    }

    /********************
     ROUTES METHODS IMPL
     ********************/

    public QueryGraph getQueryGraph(String clickedClassIRI) {
        IRI iri = IRI.create(clickedClassIRI);
        if(!ontology.containsClassInSignature(iri)) {
            throw new RuntimeException("Iri " + clickedClassIRI + " not found in ontology " + ontology.getOntologyID());
        }
        // Modify SPARQL
        String var = guessNewVarFromIRI(iri, null);
        SelectBuilder sb = new SelectBuilder();
        sb.addPrefixes(prefixes);
        sb.addVar(var).addWhere(var, "a", iri.toQuotedString());
        // Modify Graph
        QueryGraph qg = new QueryGraph();
        HeadElement headElement = new HeadElement();
        headElement.setVar(var);
        qg.addHeadItem(headElement);
        qg.setSparql(sb.build().serialize());
        GraphElement root = new GraphElement();
        root.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(iri, pdf));
        root.setId(var.substring(1));
        qg.setGraph(root);
        return qg;
    }

    public QueryGraph putQueryGraphClass(QueryGraph body, String sourceClassIRI, String targetClassIRI, String graphElementId) {
        IRI iri = IRI.create(targetClassIRI);
        if(!ontology.containsClassInSignature(iri)) {
            throw new RuntimeException("Iri " + targetClassIRI + " not found in ontology " + ontology.getOntologyID());
        }
        //Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        wh.addWhere(new TriplePath(new Triple(
                AbstractQueryBuilder.makeNode("?" + graphElementId, p),
                (Node)AbstractQueryBuilder.makeNodeOrPath("a", p),
                AbstractQueryBuilder.makeNode(iri.toQuotedString(), p)
        )));
        body.setSparql(q.serialize());
        //Modify graph
        GraphElementFinder gef = new GraphElementFinder();
        gef.findElementById(graphElementId, body.getGraph());
        gef.getFound().addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(iri, pdf));
        return body;
    }

    public QueryGraph putQueryGraphObjectProperty(QueryGraph body, String sourceClassIRI, String predicateIRI, String targetClassIRI, Boolean isPredicateDirect, String graphElementId) {
        IRI target = IRI.create(targetClassIRI);
        if(!ontology.containsClassInSignature(target)) {
            throw new RuntimeException("Class " + targetClassIRI + " not found in ontology " + ontology.getOntologyID());
        }
        IRI predicate = IRI.create(predicateIRI);
        if(!ontology.containsObjectPropertyInSignature(predicate)) {
            throw new RuntimeException("Predicate " + predicateIRI + " not found in ontology " + ontology.getOntologyID());
        }
        //Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        String var = guessNewVarFromIRI(target, q);
        if(isPredicateDirect) {
            wh.addWhere(new TriplePath(new Triple(
                    AbstractQueryBuilder.makeNode("?" + graphElementId, p),
                    (Node)AbstractQueryBuilder.makeNodeOrPath(predicate.toQuotedString(), p),
                    AbstractQueryBuilder.makeNode(var, p)
            )));
        } else {
            wh.addWhere(new TriplePath(new Triple(
                    AbstractQueryBuilder.makeNode(var, p),
                    (Node)AbstractQueryBuilder.makeNodeOrPath(predicate.toQuotedString(), p),
                    AbstractQueryBuilder.makeNode("?" + graphElementId, p)
            )));
        }
        wh.addWhere(new TriplePath(new Triple(
                AbstractQueryBuilder.makeNode(var, p),
                (Node)AbstractQueryBuilder.makeNodeOrPath("a", p),
                AbstractQueryBuilder.makeNode(target.toQuotedString(), p)
        )));
        body.setSparql(q.serialize());
        //Modify graph
        GraphElementFinder gef = new GraphElementFinder();
        gef.findElementById(graphElementId, body.getGraph());
        GraphElement ge = new GraphElement();
        ge.setId("x" + System.currentTimeMillis());
        ge.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(predicate, pdf));
        GraphElement ge1 = new GraphElement();
        ge1.setId(var.substring(1));
        ge1.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(target, pdf));
        ge.addChildrenItem(ge1);
        gef.getFound().addChildrenItem(ge);
        return body;
    }

    public QueryGraph putQueryGraphDataProperty(QueryGraph body, String sourceClassIRI, String predicateIRI, String graphElementId) {
        IRI iri = IRI.create(predicateIRI);
        if(!ontology.containsDataPropertyInSignature(iri)) {
            throw new RuntimeException("Predicate " + predicateIRI + " not found in ontology " + ontology.getOntologyID());
        }
        //Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        AggregationHandler aggHandler = new AggregationHandler(q);
        SelectHandler sh = new SelectHandler(aggHandler);
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        String var = guessNewVarFromIRI(iri, q);
        sh.addVar(AbstractQueryBuilder.makeVar(var));
        wh.addWhere(new TriplePath(new Triple(
                AbstractQueryBuilder.makeNode("?" + graphElementId, p),
                (Node)AbstractQueryBuilder.makeNodeOrPath(iri.toQuotedString(), p),
                AbstractQueryBuilder.makeNode(var, p)
        )));
        body.setSparql(q.serialize());
        //Modify graph
        GraphElementFinder gef = new GraphElementFinder();
        gef.findElementById(graphElementId, body.getGraph());
        GraphElement ge = new GraphElement();
        ge.setId(var);
        ge.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(iri, pdf));
        gef.getFound().addChildrenItem(ge);
        HeadElement headItem = new HeadElement();
        headItem.setVar(var);
        body.addHeadItem(headItem);
        return body;
    }
}
