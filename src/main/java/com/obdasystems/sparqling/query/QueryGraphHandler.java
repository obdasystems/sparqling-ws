package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.model.Optional;
import com.obdasystems.sparqling.query.visitors.DeleteElementVisitor;
import com.obdasystems.sparqling.query.visitors.DeleteElementVisitorByTriple;
import com.obdasystems.sparqling.query.visitors.DeleteElementVisitorOptional;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.arq.querybuilder.handlers.AggregationHandler;
import org.apache.jena.arq.querybuilder.handlers.SelectHandler;
import org.apache.jena.arq.querybuilder.handlers.WhereHandler;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.SortCondition;
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
import org.apache.jena.vocabulary.RDF;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.*;
import java.util.stream.Collectors;

import static com.obdasystems.sparqling.query.QueryUtils.getVarFromFunction;
import static com.obdasystems.sparqling.query.QueryUtils.validate;

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
        root.addVariablesItem(var);
        qg.setGraph(root);
        return qg;
    }

    public QueryGraph putQueryGraphClass(QueryGraph body, String sourceClassIRI, String targetClassIRI, String graphElementId) {
        IRI iri = IRI.create(targetClassIRI);
        if(!ontology.containsClassInSignature(iri)) {
            throw new RuntimeException("Iri " + targetClassIRI + " not found in ontology " + ontology.getOntologyID());
        }
        if (body.getOptionals() != null && body.getOptionals().stream().anyMatch(optional -> optional.getGraphIds().contains(graphElementId))) {
            throw new RuntimeException("Cannot add a class to an optional node");
        }
        //Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        wh.addWhere(new TriplePath(new Triple(
                AbstractQueryBuilder.makeNode(varPrefix + graphElementId, p),
                RDF.Nodes.type,
                AbstractQueryBuilder.makeNode(iri.toQuotedString(), p)
        )));
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
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
        if (body.getOptionals() != null && body.getOptionals().stream().anyMatch(optional -> optional.getGraphIds().contains(graphElementId))) {
            throw new RuntimeException("Cannot add an object property to an optional node");
        }
        //Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        String var = QueryUtils.guessNewVarFromIRI(target, q);
        String var2 = varPrefix + graphElementId;
        Node sub;
        Node pred = (Node)AbstractQueryBuilder.makeNodeOrPath(predicate.toQuotedString(), p);
        Node obj;
        if(isPredicateDirect) {
            sub = AbstractQueryBuilder.makeNode(var2, p);
            obj = AbstractQueryBuilder.makeNode(var, p);
        } else {
            sub = AbstractQueryBuilder.makeNode(var, p);
            obj = AbstractQueryBuilder.makeNode(var2, p);
        }
        wh.addWhere(new TriplePath(new Triple(sub,pred,obj)));
        wh.addWhere(new TriplePath(new Triple(
                AbstractQueryBuilder.makeNode(var, p),
                RDF.Nodes.type,
                AbstractQueryBuilder.makeNode(target.toQuotedString(), p)
        )));
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        //Modify graph
        GraphElementFinder gef = new GraphElementFinder();
        GraphElement found = gef.findElementById(graphElementId, body.getGraph());
        GraphElement ge = new GraphElement();
        ge.setId("x" + System.currentTimeMillis());
        ge.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(predicate, pdf));
        ge.addVariablesItem(var2);
        ge.addVariablesItem(var);
        if (!isPredicateDirect) {
            ge.getEntities().get(0).setType(Entity.TypeEnum.INVERSEOBJECTPROPERTY);
        }
        GraphElement ge1 = new GraphElement();
        ge1.setId(var.substring(1));
        ge1.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(target, pdf));
        ge1.addVariablesItem(var);
        ge.addChildrenItem(ge1);
        found.addChildrenItem(ge);
        return body;
    }

    public QueryGraph putQueryGraphDataProperty(QueryGraph body, String sourceClassIRI, String predicateIRI, String graphElementId) {
        IRI iri = IRI.create(predicateIRI);
        if(!ontology.containsDataPropertyInSignature(iri)) {
            throw new RuntimeException("Predicate " + predicateIRI + " not found in ontology " + ontology.getOntologyID());
        }
        if (body.getOptionals() != null && body.getOptionals().stream().anyMatch(optional -> optional.getGraphIds().contains(graphElementId))) {
            throw new RuntimeException("Cannot add a data property to an optional node");
        }
        //Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        AggregationHandler aggHandler = new AggregationHandler(q);
        SelectHandler sh = new SelectHandler(aggHandler);
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        String var = QueryUtils.guessNewVarFromIRI(iri, q);
        String var2 = varPrefix + graphElementId;
        Var newVar = AbstractQueryBuilder.makeVar(var);
        // count star handling => do not add elements to the head
        boolean isCountStarActive = body.getHead().size() == 1 && body.getHead().get(0).getGraphElementId() == null;
        if (!isCountStarActive)
            sh.addVar(newVar);
        wh.addWhere(new TriplePath(new Triple(
                AbstractQueryBuilder.makeNode(var2, p),
                (Node)AbstractQueryBuilder.makeNodeOrPath(iri.toQuotedString(), p),
                AbstractQueryBuilder.makeNode(var, p)
        )));
        if (!q.getAggregators().isEmpty()) {
            q.getGroupBy().add(newVar);
        }
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        //Modify graph
        GraphElementFinder gef = new GraphElementFinder();
        GraphElement found = gef.findElementById(graphElementId, body.getGraph());
        GraphElement ge = new GraphElement();
        ge.setId(var.substring(1));
        ge.addEntitiesItem(SWSOntologyManager.getOntologyManager().extractEntity(iri, pdf));
        ge.addVariablesItem(var2);
        ge.addVariablesItem(var);
        found.addChildrenItem(ge);
        HeadElement headItem = new HeadElement();
        headItem.setId(var);
        headItem.setVar(var);
        headItem.setGraphElementId(var.substring(1));
        if (!isCountStarActive)
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
        QueryUtils.removeAggregations(q, varToBeDeleted);
        QueryUtils.removeOrderBy(q, varToBeDeleted);
        if(q.getProject().isEmpty()) {
            q.setQueryResultStar(true);
        }
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        // Modify graph
        gef.deleteElementById(graphElementId, body.getGraph());
        gef.deleteObjectPropertiesWithNoChild(body.getGraph());
        body.setHead(body.getHead().stream().filter(
                headElement -> !varToBeDeleted.contains(headElement.getGraphElementId()))
                .collect(Collectors.toList()));
        if (body.getFilters() != null) {
            body.getFilters().removeIf(filter -> {
                List<String> filterVars = filter.getExpression().getParameters().stream()
                        .filter(params -> params.getType().equals(VarOrConstant.TypeEnum.VAR))
                        .map(params -> params.getValue().substring(1)).collect(Collectors.toList());
                for (String v : filterVars) {
                    if (varToBeDeleted.contains(v)) {
                        return true;
                    }
                }
                return false;
            });
        }
        if (body.getOptionals() != null) {
            for (Optional o:body.getOptionals()) {
                o.getGraphIds().removeIf(id -> varToBeDeleted.contains(id));
            }
            body.getOptionals().removeIf(optional -> optional.getGraphIds().isEmpty());
        }
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
        // Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        Set<String> varToBeDeleted = new HashSet<>();
        varToBeDeleted.add(graphElementId);
        DeleteElementVisitor deleteQueryGraphElementVisitor = new DeleteElementVisitor(varToBeDeleted, classIRI);
        q.getQueryPattern().visit(deleteQueryGraphElementVisitor);
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        return body;
    }
    public QueryGraph newFilter(QueryGraph body, Integer filterId) {
        Filter f = body.getFilters().get(filterId);
        // Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        WhereHandler wh = new WhereHandler(q);
        wh.addFilter(QueryUtils.getExprForFilter(f, q.getPrefixMapping(), null));
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
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
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
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
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
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
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
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
            q.getGroupBy().clear();
            q.getHavingExprs().clear();
        }
        Set<String> toRemove = new HashSet<>();
        toRemove.add(id);
        QueryUtils.removeOrderBy(q, toRemove);
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
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
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        return body;
    }

    public QueryGraph orderBy(QueryGraph body, String headTerm) {
        int ordering = body.getHead().stream().filter(i -> i.getId().equals(headTerm)).findFirst().
                orElseThrow(() -> new RuntimeException("Cannot find head term " + headTerm)).getOrdering();
        // Modify SPARQL
        Query q = parser.parse(new Query(), body.getSparql());
        Var orderVar = AbstractQueryBuilder.makeVar(headTerm);
        Expr expr = q.getProject().getExpr(orderVar);
        //remove precedent order for that var
        if (q.getOrderBy() != null) {
            Iterator<SortCondition> it = q.getOrderBy().iterator();
            while (it.hasNext()) {
                SortCondition sc = it.next();
                if (expr != null) {
                    if (expr.equals(sc.getExpression())) {
                        it.remove();
                    }
                } else if (sc.getExpression().getVarsMentioned().contains(orderVar)) {
                    it.remove();
                }
            }
        }

        if (ordering != 0) {
            if (expr != null) {
                q.addOrderBy(expr, ordering);
            } else {
                q.addOrderBy(orderVar, ordering);
            }
        }
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
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
        String var = getVarFromFunction(f.getName().toString(), body);
        Var newVar = AbstractQueryBuilder.makeVar(var);
        he.setId(var);
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
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        return body;
    }

    public QueryGraph aggregationHeadTerm(QueryGraph body, String headTerm) {
        //Get function from head
        HeadElement he = null;
        for(HeadElement hei : body.getHead()) {
            if(hei.getId().equals(headTerm)) {
                he =  hei;
                break;
            }
        }
        if (he == null) throw new RuntimeException("Cannot find head element " + headTerm);
        if (he.getGroupBy() == null) throw new RuntimeException("Cannot find aggregate/group by function for head element " + headTerm);
        GroupByElement gb = he.getGroupBy();
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
        if(!q.getGroupBy().isEmpty()) {
            q.getGroupBy().remove(var);
        }
        VarExprList gbVars = new VarExprList();
        for(Var v:q.getProject().getVars()) {
            Expr e = q.getProject().getExprs().get(v);
            if(!(e instanceof ExprAggregator)) {
                gbVars.add(v,e);
            }
        }
        q.getGroupBy().addAll(gbVars);
        if (he.getHaving() != null) {
            List<Filter> havingGraph = he.getHaving();
            Expr having = QueryUtils.getExprForFilter(havingGraph.get(havingGraph.size() - 1), q.getPrefixMapping(), exprAgg);
            q.addHavingCondition(having);
        }
        String varName = getVarFromFunction(gb.getAggregateFunction().toString(), body);
        Var newVar = AbstractQueryBuilder.makeVar(varName);
        he.setId(varName);
        he.setAlias(newVar.getVarName());
        SelectHandler sh = new SelectHandler(ah);
        sh.addVar(exprAgg, newVar);
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
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
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        return body;
    }

    public QueryGraph countStar(QueryGraph body, Boolean distinct) {
        body.getHead().clear();
        String var = varPrefix + "COUNT_STAR";
        HeadElement he = new HeadElement();
        he.setId(var);
        he.setAlias(var);
        body.addHeadItem(he);
        Query q = parser.parse(new Query(), body.getSparql());
        q.getProject().clear();
        AggregationHandler ah = new AggregationHandler(q);
        Aggregator agg;
            if(distinct) agg = new AggCountDistinct();
            else agg = new AggCount();
        Var newVar = AbstractQueryBuilder.makeVar(var);
        ExprAggregator exprAgg = new ExprAggregator(newVar, agg);
        SelectHandler sh = new SelectHandler(ah);
        sh.addVar(exprAgg, newVar);
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        return body;
    }

    public QueryGraph setDistinct(QueryGraph body, Boolean distinct) {
        body.setDistinct(distinct);
        Query q = parser.parse(new Query(), body.getSparql());
        q.setDistinct(distinct);
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        body.setDistinct(distinct);
        return body;
    }

    public QueryGraph setLimit(QueryGraph body, Integer limit) {
        body.setLimit(limit);
        Query q = parser.parse(new Query(), body.getSparql());
        if (limit < 0) {
            q.setLimit(Query.NOLIMIT);
        } else {
            q.setLimit(limit);
        }
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        body.setLimit(limit);
        return body;
    }

    public QueryGraph setOffset(QueryGraph body, Integer offset) {
        body.setOffset(offset);
        Query q = parser.parse(new Query(), body.getSparql());
        if (offset < 0) {
            q.setOffset(Query.NOLIMIT);
        } else {
            q.setOffset(offset);
        }
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        body.setOffset(offset);
        return body;
    }

    public QueryGraph newOptional(QueryGraph body, String graphElementId) {
        GraphElementFinder gef = new GraphElementFinder();
        GraphElement el = gef.findElementById(graphElementId, body.getGraph());
        Optional op = new Optional();
        Integer opId = 0;
        if (body.getOptionals() != null) opId = body.getOptionals().size();
        op.setId(opId);
        List<String> list = new LinkedList<>();
        list.add(graphElementId);
        op.setGraphIds(list);
        body.addOptionalsItem(op);
        Query q = parser.parse(new Query(), body.getSparql());
        WhereHandler wh = new WhereHandler(q);
        PrefixMapping p = q.getPrefixMapping();
        List<Triple> triplesToMove = QueryUtils.getOptionalTriplesToMove(el, p, list);
        WhereHandler wh2 = new WhereHandler();
        for (Triple triple : triplesToMove) {
            wh2.addWhere(new TriplePath(triple));
            DeleteElementVisitorByTriple deleteQueryGraphElementVisitor = new DeleteElementVisitorByTriple(triple);
            q.getQueryPattern().visit(deleteQueryGraphElementVisitor);
        }
        wh.addOptional(wh2);
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        return body;
    }

    public QueryGraph removeAllOptionals(QueryGraph body) {
        body.getOptionals().clear();
        Query q = parser.parse(new Query(), body.getSparql());
        DeleteElementVisitorOptional visitor = new DeleteElementVisitorOptional();
        q.getQueryPattern().visit(visitor);
        WhereHandler wh = new WhereHandler(q);
        for (TriplePath tp:visitor.getTriplePaths()) {
            wh.addWhere(tp);
        }
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        return body;
    }

    public QueryGraph removeOptional(QueryGraph body, String graphElementId) {
        body.getOptionals().removeIf(o -> o.getGraphIds().contains(graphElementId));
        Query q = parser.parse(new Query(), body.getSparql());
        GraphElementFinder gef = new GraphElementFinder();
        GraphElement el = gef.findElementById(graphElementId, body.getGraph());
        PrefixMapping p = q.getPrefixMapping();
        List<String> list = new LinkedList<>();
        List<Triple> triplesToMove = QueryUtils.getOptionalTriplesToMove(el, p, list);
        DeleteElementVisitorOptional visitor = new DeleteElementVisitorOptional(triplesToMove);
        q.getQueryPattern().visit(visitor);
        WhereHandler wh = new WhereHandler(q);
        for (TriplePath tp:visitor.getTriplePaths()) {
            wh.addWhere(tp);
        }
        String sparql = q.serialize();
        validate(sparql);
        body.setSparql(sparql);
        return body;
    }
}
