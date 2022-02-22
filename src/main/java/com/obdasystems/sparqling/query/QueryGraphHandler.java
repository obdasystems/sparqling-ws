package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.query.visitors.DeleteElementVisitor;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.ExprFactory;
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
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.syntax.*;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryGraphHandler {
    private final OWLOntology ontology;
    private final Map<String, String> prefixes;
    private final PrefixDocumentFormat pdf;
    public static String varPrefix = "?";

    public QueryGraphHandler() {
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
        return varPrefix + res;
    }

    public static String getNewCountedVarFromQuery(String varName, Query q) {
        final int[] count = {0};
        String ret = varName + count[0];
        if (q == null) return ret;
        final boolean[] found = {true};
        while (found[0]) {
            ret = varName + count[0];
            String finalRet = ret;
            ElementWalker_New.walk(
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

    private static Expr getVarOrConstant(VarOrConstant varOrConstant, PrefixMapping p) {
        switch (varOrConstant.getType()) {
            case VAR: return new ExprVar(AbstractQueryBuilder.makeVar(varOrConstant.getValue()));
            case IRI: return NodeValue.makeNode(
                    AbstractQueryBuilder.makeNode("<" + varOrConstant.getValue() + ">", p)
            );
            case CONSTANT:
                String constant = "'" + varOrConstant.getValue() + "'^^"+varOrConstant.getConstantType();
                return NodeValue.makeNode(
                        AbstractQueryBuilder.makeNode(constant, p)
                );
            default:
                throw new RuntimeException("Cannot recognize type of var or constant. Found " + varOrConstant.getType());
        }
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
        sb.addVar("*").addWhere(var, "a", iri.toQuotedString());
        // Modify Graph
        QueryGraph qg = new QueryGraph();
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
                AbstractQueryBuilder.makeNode(varPrefix + graphElementId, p),
                (Node)AbstractQueryBuilder.makeNodeOrPath("a", p),
                AbstractQueryBuilder.makeNode(iri.toQuotedString(), p)
        )));
        body.setSparql(q.serialize());
        //Modify graph
        GraphElementFinder gef = new GraphElementFinder();
        GraphElement ge = gef.findElementById(graphElementId, body.getGraph());
        ge.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(iri, pdf));
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
                    AbstractQueryBuilder.makeNode(varPrefix + graphElementId, p),
                    (Node)AbstractQueryBuilder.makeNodeOrPath(predicate.toQuotedString(), p),
                    AbstractQueryBuilder.makeNode(var, p)
            )));
        } else {
            wh.addWhere(new TriplePath(new Triple(
                    AbstractQueryBuilder.makeNode(var, p),
                    (Node)AbstractQueryBuilder.makeNodeOrPath(predicate.toQuotedString(), p),
                    AbstractQueryBuilder.makeNode(varPrefix + graphElementId, p)
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
        GraphElement found = gef.findElementById(graphElementId, body.getGraph());
        GraphElement ge = new GraphElement();
        ge.setId("x" + System.currentTimeMillis());
        ge.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(predicate, pdf));
        GraphElement ge1 = new GraphElement();
        ge1.setId(var.substring(1));
        ge1.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(target, pdf));
        ge.addChildrenItem(ge1);
        found.addChildrenItem(ge);
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
                AbstractQueryBuilder.makeNode(varPrefix + graphElementId, p),
                (Node)AbstractQueryBuilder.makeNodeOrPath(iri.toQuotedString(), p),
                AbstractQueryBuilder.makeNode(var, p)
        )));
        body.setSparql(q.serialize());
        //Modify graph
        GraphElementFinder gef = new GraphElementFinder();
        GraphElement found = gef.findElementById(graphElementId, body.getGraph());
        GraphElement ge = new GraphElement();
        ge.setId(var.substring(1));
        ge.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(iri, pdf));
        found.addChildrenItem(ge);
        HeadElement headItem = new HeadElement();
        headItem.setId(var);
        headItem.setVar(var);
        headItem.setGraphElementId(var.substring(1));
        body.addHeadItem(headItem);
        return body;
    }

    public QueryGraph putQueryGraphJoin(QueryGraph body, String graphElementId1, String graphElementId2) {
        GraphElementFinder gef = new GraphElementFinder();
        GraphElement ge1 = gef.findElementById(graphElementId1, body.getGraph());
        GraphElement ge2 = gef.findElementById(graphElementId2, body.getGraph());
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
                h.setVar(varPrefix + graphElementId1);
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
        GraphElementFinder gef = new GraphElementFinder();
        Set<String> varToBeDeleted = gef.findChildrenIds(graphElementId, body.getGraph());
        varToBeDeleted.add(graphElementId);
        //Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        DeleteElementVisitor deleteQueryGraphElementVisitor = new DeleteElementVisitor(varToBeDeleted);
        ElementWalker_New.walk(q.getQueryPattern(), deleteQueryGraphElementVisitor);
        for(String var:varToBeDeleted) {
            q.getProject().remove(AbstractQueryBuilder.makeVar(var));
        }
        if(q.getProject().isEmpty()) {
            q.setQueryResultStar(true);
        }
        body.setSparql(q.serialize());
        // Modify graph
        gef.deleteElementById(graphElementId, body.getGraph());
        gef.deleteObjectPropertiesWithNoChild(body.getGraph());
        body.setHead(body.getHead().stream().filter(
                headElement -> !varToBeDeleted.contains(headElement.getGraphElementId()))
                .collect(Collectors.toList()));
        return body;
    }

    public QueryGraph addHeadTerm(QueryGraph body, String graphElementId) {
        GraphElementFinder gef = new GraphElementFinder();
        gef.findElementById(graphElementId, body.getGraph());
        //Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        AggregationHandler aggHandler = new AggregationHandler(q);
        SelectHandler sh = new SelectHandler(aggHandler);
        String var = varPrefix + graphElementId;
        sh.addVar(AbstractQueryBuilder.makeVar(var));
        body.setSparql(q.serialize());
        //Modify graph
        HeadElement he = new HeadElement();
        he.setId(var);
        he.setGraphElementId(graphElementId);
        he.setVar(var);
        body.addHeadItem(he);
        return body;
    }

    public QueryGraph deleteHeadTerm(QueryGraph body, String id) {
        //Modify graph
        Iterator<HeadElement> it = body.getHead().iterator();
        HeadElement he = null;
        while (it.hasNext()) {
            he = it.next();
            if (he.getId().equals(id)) {
                it.remove();
                break;
            }
        }
        if (he == null) throw new RuntimeException("Cannot find head element id " + id);
        //Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        q.getProject().remove(AbstractQueryBuilder.makeVar(id));
        if(q.getProject().isEmpty()) {
            q.setQueryResultStar(true);
        }
        body.setSparql(q.serialize());
        return body;
    }

    public QueryGraph renameHeadTerm(QueryGraph body, String id) {
        //Get renaming head
        HeadElement renamedHe = null;
        int index = 0;
        for(HeadElement he : body.getHead()) {
            if(he.getId().equals(id)) {
                renamedHe =  he;
                renamedHe.setId(varPrefix + he.getAlias());
                break;
            }
            index++;
        }
        if (renamedHe == null) throw new RuntimeException("Cannot find head element id " + id);
        if (renamedHe.getAlias() == null) throw new RuntimeException("Alias not defined for element id " + id);
        //Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        Var newVar = AbstractQueryBuilder.makeVar(varPrefix + renamedHe.getAlias());
        q.getProject().remove(AbstractQueryBuilder.makeVar(id));
        q.getProject().getVars().add(index, newVar);
        q.getProject().getExprs().put(
            newVar,
            new ExprVar(AbstractQueryBuilder.makeVar(renamedHe.getVar()))
        );
        body.setSparql(q.serialize());
        return body;
    }

    public QueryGraph newFilter(QueryGraph body, Integer filterId) {
        Filter f = body.getFilters().get(filterId);
        // Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        ExprFactory ef = new ExprFactory(p);
        Expr filterExpr;
        switch (f.getExpression().getOperator()) {
            case EQUAL:
                filterExpr = ef.eq(
                    getVarOrConstant(f.getExpression().getParameters().get(0), p),
                    getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case GREATER_THAN:
                filterExpr = ef.gt(
                        getVarOrConstant(f.getExpression().getParameters().get(0), p),
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case LESS_THAN:
                filterExpr = ef.lt(
                        getVarOrConstant(f.getExpression().getParameters().get(0), p),
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                filterExpr = ef.ge(
                        getVarOrConstant(f.getExpression().getParameters().get(0), p),
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case LESS_THAN_OR_EQUAL_TO:
                filterExpr = ef.le(
                        getVarOrConstant(f.getExpression().getParameters().get(0), p),
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case NOT_EQUAL:
                filterExpr = ef.ne(
                        getVarOrConstant(f.getExpression().getParameters().get(0), p),
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case IN:
                ExprList list = new ExprList();
                for (VarOrConstant v:f.getExpression().getParameters()) {
                    list.add(getVarOrConstant(v,p));
                }
                filterExpr = ef.in(
                        getVarOrConstant(f.getExpression().getParameters().get(0), p),
                        list);
                break;
            case NOT_IN:
                ExprList not_list = new ExprList();
                for (VarOrConstant v:f.getExpression().getParameters()) {
                    not_list.add(getVarOrConstant(v,p));
                }
                filterExpr = ef.notin(
                        getVarOrConstant(f.getExpression().getParameters().get(0), p),
                        not_list);
                break;
            default:
                throw new RuntimeException("Cannot recognize operator of filter. Found " + f.getExpression().getOperator());
        }
        wh.addFilter(filterExpr);
        body.setSparql(q.serialize());
        return body;
    }


    public QueryGraph removeFilter(QueryGraph body, Integer filterId, boolean modifyGraph) {
        // Modify Graph
        if (modifyGraph) body.getFilters().remove(filterId);
        // Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        final int[] index = {-1};
        final boolean[] removed = {false};
        q.getQueryPattern().visit(new ElementVisitorBase() {
            @Override
            public void visit(ElementFilter el) {
                index[0]++;
            }

            @Override
            public void visit(ElementGroup elementGroup) {
                Iterator<Element> it = elementGroup.getElements().iterator();
                while(it.hasNext()) {
                    Element el = it.next();
                    el.visit(this);
                    if(index[0] == filterId) {
                        it.remove();
                        removed[0] = true;
                    }
                }
            }
        });
        if (!removed[0]) throw new RuntimeException("Cannot find FILTER " + filterId);
        body.setSparql(q.serialize());
        return body;
    }

    public QueryGraph removeFilters(QueryGraph body) {
        // Modify Graph
        body.getFilters().clear();
        // Modify SPARQL
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), body.getSparql());
        q.getQueryPattern().visit(new ElementVisitorBase() {
            @Override
            public void visit(ElementGroup elementGroup) {
                Iterator<Element> it = elementGroup.getElements().iterator();
                while(it.hasNext()) {
                    Element el = it.next();

                    if(el instanceof ElementFilter) {
                        it.remove();
                    }
                }
            }
        });
        body.setSparql(q.serialize());
        return body;
    }
}
