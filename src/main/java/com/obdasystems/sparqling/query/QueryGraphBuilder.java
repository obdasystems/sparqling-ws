package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.Entity;
import com.obdasystems.sparqling.model.GraphElement;
import com.obdasystems.sparqling.model.HeadElement;
import com.obdasystems.sparqling.model.QueryGraph;
import com.obdasystems.sparqling.query.visitors.DeleteElementVisitor;
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
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.walker.ElementWalker_New;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.Rename;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.syntax.*;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

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
        headElement.setId(0);
        headElement.setVar(var);
        headElement.setGraphElementId(var.substring(1));
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
        ge.setId(var.substring(1));
        ge.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(iri, pdf));
        gef.getFound().addChildrenItem(ge);
        HeadElement headItem = new HeadElement();
        headItem.setId(body.getHead().size());
        headItem.setVar(var);
        headItem.setGraphElementId(var.substring(1));
        body.addHeadItem(headItem);
        return body;
    }

    public QueryGraph putQueryGraphJoin(QueryGraph body, String graphElementId1, String graphElementId2) {
        GraphElementFinder gef = new GraphElementFinder();
        gef.findElementById(graphElementId1, body.getGraph());
        GraphElement ge1 = gef.getFound();
        gef.findElementById(graphElementId2, body.getGraph());
        GraphElement ge2 = gef.getFound();
        // Check if nodes refer to same entities
        RuntimeException diffEntities = new RuntimeException("The two nodes refer to different entities");
        if(ge1.getEntities().size() != ge2.getEntities().size()) {
            throw diffEntities;
        }
        for (Entity e:ge1.getEntities()) {
            if (!ge2.getEntities().contains(e)) {
                throw diffEntities;
            }
        }
        // Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        Op renamed = Rename.renameVar(Algebra.compile(q), AbstractQueryBuilder.makeVar(graphElementId2), AbstractQueryBuilder.makeVar(graphElementId1));
        body.setSparql(OpAsQuery.asQuery(renamed).serialize());
        // Modify graph
        if(ge1.getChildren() != null && ge2.getChildren() != null)
            ge1.getChildren().addAll(ge2.getChildren());
        if(ge1.getChildren() == null && ge2.getChildren() != null)
            ge1.setChildren(ge2.getChildren());
        ge2.setId(graphElementId1);
        ge2.setChildren(null);
        for(HeadElement h:body.getHead()) {
            if(h.getVar().equals(graphElementId2)) {
                h.setVar("?" + graphElementId1);
                h.setGraphElementId(graphElementId1);
            }
        }
        return body;
    }

    public QueryGraph deleteQueryGraphElement(QueryGraph body, String graphElementId) {
        // DELETE ROOT NODE => DELETE EVERYTHING
        if(body.getGraph().getId().equals(graphElementId)) {
            return new QueryGraph();
        }
        //Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        DeleteElementVisitor deleteQueryGraphElementVisitor = new DeleteElementVisitor(graphElementId);
        ElementWalker_New.walk(q.getQueryPattern(), deleteQueryGraphElementVisitor);
        q.getProject().remove(AbstractQueryBuilder.makeVar(graphElementId));
        body.setSparql(q.serialize());
        // Modify graph
        GraphElementFinder gef = new GraphElementFinder();
        gef.deleteElementById(graphElementId, body.getGraph());
        gef.deleteObjectPropertiesWithNoChild(body.getGraph());
        body.setHead(body.getHead().stream().filter(headElement -> !headElement.getVar().equals("?" + graphElementId)).collect(Collectors.toList()));
        return body;
    }
}
