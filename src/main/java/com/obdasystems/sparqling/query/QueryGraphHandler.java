package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.*;
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
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.engine.Rename;
import org.apache.jena.sparql.expr.*;
import org.apache.jena.sparql.expr.aggregate.*;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.syntax.*;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.*;
import java.util.stream.Collectors;

public class QueryGraphHandler {
    private final OWLOntology ontology;
    private final Map<String, String> prefixes;
    private final PrefixDocumentFormat pdf;
    public static String varPrefix = "?";
    private final SPARQLParser parser;

    public QueryGraphHandler() {
        ontology = SWSOntologyManager.getOntologyManager().getOwlOntology();
        if(ontology == null) throw new RuntimeException("Please load an ontology before start building queries.");
        pdf = (PrefixDocumentFormat) ontology.getOWLOntologyManager().getOntologyFormat(ontology);
        if (pdf != null && pdf.isPrefixOWLOntologyFormat()) {
            prefixes = pdf.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap();
        } else {
            prefixes = new HashMap<>();
        }
        parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
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
        String var = QueryUtils.guessNewVarFromIRI(iri, null);
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
        Query q = parser.parse(new Query(), body.getSparql());
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        String var = QueryUtils.guessNewVarFromIRI(target, q);
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
        if (!isPredicateDirect) {
            ge.getEntities().get(0).setType(Entity.TypeEnum.INVERSEOBJECTPROPERTY);
        }
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
        Query q = parser.parse(new Query(), body.getSparql());
        AggregationHandler aggHandler = new AggregationHandler(q);
        SelectHandler sh = new SelectHandler(aggHandler);
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        String var = QueryUtils.guessNewVarFromIRI(iri, q);
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
        Query q = parser.parse(new Query(), body.getSparql());
        Op renamed = Rename.renameVar(Algebra.compile(q), AbstractQueryBuilder.makeVar(graphElementId2), AbstractQueryBuilder.makeVar(graphElementId1));
        Query newQ = OpAsQuery.asQuery(renamed);
        newQ.setPrefixMapping(q.getPrefixMapping());
        body.setSparql(newQ.serialize());
        // Modify graph
        if(ge1.getChildren() != null && ge2.getChildren() != null) {
            List<GraphElement> allChildren = new LinkedList<>();
            allChildren.addAll(ge1.getChildren());
            allChildren.addAll(ge2.getChildren());
            ge1.setChildren(allChildren);
            ge2.setChildren(allChildren);
        }
        if(ge1.getChildren() == null && ge2.getChildren() != null)
            ge1.setChildren(ge2.getChildren());
        if(ge1.getChildren() != null && ge2.getChildren() == null)
            ge2.setChildren(ge1.getChildren());
        ge2.setId(graphElementId1);
        new GraphElementCycleRemover().removeCycles(body.getGraph());
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
        if (varToBeDeleted.size() == 0) {
            throw new RuntimeException("Cannot find graph element" + graphElementId);
        }
        varToBeDeleted.add(graphElementId);
        //Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        DeleteElementVisitor deleteQueryGraphElementVisitor = new DeleteElementVisitor(varToBeDeleted);
        q.getQueryPattern().visit(deleteQueryGraphElementVisitor);
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

    public QueryGraph deleteQueryGraphElementClass(QueryGraph body, String graphElementId, String classIRI) {
        GraphElementFinder gef = new GraphElementFinder();
        GraphElement ge = gef.findElementById(graphElementId, body.getGraph());
        if (ge == null) {
            throw new RuntimeException("Cannot find graph element" + graphElementId);
        }
        Iterator<Entity> it = ge.getEntities().iterator();
        boolean found  = false;
        while (it.hasNext()) {
            Entity e = it.next();
            if (e.getIri().equals(classIRI)) {
                it.remove();
                found = true;
            }
        }
        if (!found) throw new RuntimeException("Cannot find class " + classIRI + " in GraphElement " + graphElementId);
        return body;
    }
    public QueryGraph newFilter(QueryGraph body, Integer filterId) {
        Filter f = body.getFilters().get(filterId);
        // Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        WhereHandler wh = new WhereHandler(q);
        wh.addFilter(QueryUtils.getExprForFilter(f, q.getPrefixMapping(), null));
        body.setSparql(q.serialize());
        return body;
    }


    public QueryGraph removeFilter(QueryGraph body, Integer filterId, boolean modifyGraph) {
        // Modify Graph
        if (modifyGraph) body.getFilters().remove((int)filterId);
        // Modify SPARQL
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
        Query q = parser.parse(new Query(), body.getSparql());
        q.getQueryPattern().visit(new ElementVisitorBase() {
            @Override
            public void visit(ElementGroup elementGroup) {
                elementGroup.getElements().removeIf(el -> el instanceof ElementFilter);
            }
        });
        body.setSparql(q.serialize());
        return body;
    }

    public QueryGraph addHeadTerm(QueryGraph body, String graphElementId) {
        GraphElementFinder gef = new GraphElementFinder();
        gef.findElementById(graphElementId, body.getGraph());
        //Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        AggregationHandler aggHandler = new AggregationHandler(q);
        SelectHandler sh = new SelectHandler(aggHandler);
        String var = varPrefix + graphElementId;
        Var jVar = AbstractQueryBuilder.makeVar(var);
        sh.addVar(jVar);
        if(!q.getGroupBy().isEmpty()) {
            q.getGroupBy().add(jVar);
        }
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
        Query q = parser.parse(new Query(), body.getSparql());
        Var jVar = AbstractQueryBuilder.makeVar(id);
        q.getProject().remove(jVar);
        if(!q.getGroupBy().isEmpty()) {
            q.getGroupBy().remove(jVar);
        }
        if(!q.getHavingExprs().isEmpty()) {
            Iterator<Expr> itH = q.getHavingExprs().iterator();
            while(itH.hasNext()) {
                if (itH.next().getVarsMentioned().contains(jVar)) {
                    it.remove();
                }
            }
        }
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
        Query q = parser.parse(new Query(), body.getSparql());
        Var newVar = AbstractQueryBuilder.makeVar(varPrefix + renamedHe.getAlias());
        Var jVar = AbstractQueryBuilder.makeVar(id);
        q.getProject().remove(jVar);
        q.getProject().getVars().add(index, newVar);
        q.getProject().getExprs().put(
            newVar,
            new ExprVar(AbstractQueryBuilder.makeVar(renamedHe.getVar()))
        );
        body.setSparql(q.serialize());
        return body;
    }

    public QueryGraph orderBy(QueryGraph body, String headTerm) {
        int ordering = body.getHead().stream().filter(i -> i.getId().equals(headTerm)).findFirst().
                orElseThrow(() -> new RuntimeException("Cannot find head term " + headTerm)).getOrdering();
        // Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        Var orderVar = AbstractQueryBuilder.makeVar(headTerm);
        if(q.getOrderBy() != null && !q.getOrderBy().isEmpty()) q.getOrderBy().clear();
        q.addOrderBy(orderVar, ordering);
        body.setSparql(q.serialize());
        return body;
    }

    public QueryGraph functionHeadTerm(QueryGraph body, String headTerm) {
        //Get function from head
        HeadElement he = null;
        int index = 0;
        for(HeadElement hei : body.getHead()) {
            if(hei.getId().equals(headTerm)) {
                he =  hei;
                break;
            }
            index++;
        }
        if (he == null) throw new RuntimeException("Cannot find head element " + headTerm);
        if (he.getFunction() == null) throw new RuntimeException("Cannot find function for head element " + headTerm);
        Function f = he.getFunction();
        Var newVar = AbstractQueryBuilder.makeVar(varPrefix + f.getName() + "_" + headTerm.substring(1));
        he.setAlias(newVar.getVarName());
        // Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        PrefixMapping p = q.getPrefixMapping();
        Expr expr = QueryUtils.getExprForFunction(f, p);
        q.getProject().remove(AbstractQueryBuilder.makeVar(headTerm));
        q.getProject().getVars().add(index, newVar);
        q.getProject().getExprs().put(
                newVar,
                expr
        );
        body.setSparql(q.serialize());
        return body;
    }

    public QueryGraph aggregationHeadTerm(QueryGraph body, String headTerm) {
        //Get function from head
        if (body.getGroupBy() == null) throw new RuntimeException("Cannot find aggregate/group by function for head element " + headTerm);
        GroupByElement gb = body.getGroupBy();
        HeadElement he = null;
        for(HeadElement hei : body.getHead()) {
            if(hei.getId().equals(headTerm)) {
                he =  hei;
                break;
            }
        }
        if (he == null) throw new RuntimeException("Cannot find head element " + headTerm);
        // Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        AggregationHandler ah = new AggregationHandler(q);
        Aggregator agg;
        Var var = AbstractQueryBuilder.makeVar(headTerm);
        ExprVar expVar = new ExprVar(var);
        ExprAggregator exprAgg;
        switch (gb.getAggregateFunction()) {
            case COUNT:
                if (gb.isDistinct()) {
                    agg = new AggCountVarDistinct(expVar);
                } else {
                    agg = new AggCountVar(expVar);
                }
                exprAgg = new ExprAggregator(var,agg);
                break;
            case SUM:
                if (gb.isDistinct()) {
                    agg = new AggSumDistinct(expVar);
                } else {
                    agg = new AggSum(expVar);
                }
                exprAgg = new ExprAggregator(var,agg);
                break;
            case MIN:
                if (gb.isDistinct()) {
                    agg = new AggMinDistinct(expVar);
                } else {
                    agg = new AggMin(expVar);
                }
                exprAgg = new ExprAggregator(var,agg);
                break;
            case MAX:
                if (gb.isDistinct()) {
                    agg = new AggMaxDistinct(expVar);
                } else {
                    agg = new AggMax(expVar);
                }
                exprAgg = new ExprAggregator(var,agg);
                break;
            case AVARAGE:
                if (gb.isDistinct()) {
                    agg = new AggAvgDistinct(expVar);
                } else {
                    agg = new AggAvg(expVar);
                }
                exprAgg = new ExprAggregator(var,agg);
                break;
            default: throw new RuntimeException("Cannot find aggregate function");
        }
        q.getProject().remove(var);
        q.getGroupBy().addAll(q.getProject());
        if (body.getHaving() != null) {
            List<Filter> havingGraph = body.getHaving();
            Expr having = QueryUtils.getExprForFilter(havingGraph.get(havingGraph.size() - 1), q.getPrefixMapping(), exprAgg);
            q.addHavingCondition(having);
        }
        Var newVar = AbstractQueryBuilder.makeVar(varPrefix + gb.getAggregateFunction() + "_" + headTerm.substring(1));
        SelectHandler sh = new SelectHandler(ah);
        sh.addVar(exprAgg, newVar);
        body.setSparql(q.serialize());
        return body;
    }

    public QueryGraph reorderHeadTerm(QueryGraph body) {
        Query q = parser.parse(new Query(), body.getSparql());
        VarExprList oldHead = q.getProject();
        VarExprList newHead = new VarExprList();
        for(HeadElement he:body.getHead()) {
            newHead.add(oldHead.getVars().stream()
                    .filter(i -> i.getVarName().equals(he.getId().substring(1))).findAny()
                    .orElseThrow(() -> new RuntimeException("Cannot find head element " + he.getId())));
        }
        newHead.getExprs().putAll(oldHead.getExprs());
        q.getProject().clear();
        q.getProject().addAll(newHead);
        body.setSparql(q.serialize());
        return body;
    }
}
