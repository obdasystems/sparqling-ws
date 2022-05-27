package com.obdasystems.sparqling.query;

import com.google.common.collect.Sets;
import com.obdasystems.sparqling.model.*;
import com.obdasystems.sparqling.query.visitors.DeleteElementVisitorByTriple;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.ExprFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.Syntax;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.algebra.walker.ElementWalker_New;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.*;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.vocabulary.RDF;
import org.semanticweb.owlapi.model.IRI;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.obdasystems.sparqling.query.QueryGraphHandler.varPrefix;

public class QueryUtils {
    static SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
    private static int VAR_MAX_LENGTH = 30;

    static void validate(String sparql) {
        parser.parse(new Query(), sparql);
    }

    static String guessNewVarFromIRI(IRI iri, Query q) {
        String res = iri.getFragment();
        if(res.isEmpty()) {
            res = "x" + System.currentTimeMillis();
        } else {
            if (res.length() > VAR_MAX_LENGTH - 2) res = res.substring(0, VAR_MAX_LENGTH - 3);
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
                    },
                    new ExprVisitorBase() {}
            );
        }
        return ret;
    }

    public static String getVarFromFunction(String funcName, QueryGraph qg) {
        int count = 0;
        Iterator<HeadElement> it = qg.getHead().iterator();
        while(it.hasNext()) {
            HeadElement v = it.next();
            if (v.getId().equals(funcName)) {
                it = qg.getHead().iterator();
                count++;
            }
        }
        return varPrefix + funcName + count;
    }

    static Expr getVarOrConstant(VarOrConstant varOrConstant, PrefixMapping p) {
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

    public static Expr getExprForFilter(Filter f, PrefixMapping p, Expr firstArg) {
        ExprFactory ef = new ExprFactory(p);
        if (firstArg == null) {
            firstArg = getVarOrConstant(f.getExpression().getParameters().get(0), p);
        }
        Expr filterExpr;
        switch (f.getExpression().getOperator()) {
            case EQUAL:
                filterExpr = ef.eq(
                        firstArg,
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case GREATER_THAN:
                filterExpr = ef.gt(
                        firstArg,
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case LESS_THAN:
                filterExpr = ef.lt(
                        firstArg,
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                filterExpr = ef.ge(
                        firstArg,
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case LESS_THAN_OR_EQUAL_TO:
                filterExpr = ef.le(
                        firstArg,
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case NOT_EQUAL:
                filterExpr = ef.ne(
                        firstArg,
                        getVarOrConstant(f.getExpression().getParameters().get(1), p));
                break;
            case IN:
                if (f.getExpression().getParameters().size() <= 2) {
                    throw new RuntimeException("IN function must have at least 2 parameters");
                }
                ExprList list = new ExprList();
                boolean first = true;
                for (VarOrConstant v:f.getExpression().getParameters()) {
                    if (!first) {
                        list.add(getVarOrConstant(v, p));
                    }
                    first = false;
                }
                filterExpr = ef.in(
                        firstArg,
                        list);
                break;
            case NOT_IN:
                if (f.getExpression().getParameters().size() <= 2) {
                    throw new RuntimeException("IN function must have at least 2 parameters");
                }
                ExprList not_list = new ExprList();
                first = true;
                for (VarOrConstant v:f.getExpression().getParameters()) {
                    if (!first) {
                        not_list.add(getVarOrConstant(v, p));
                    }
                    first = false;
                }
                filterExpr = ef.notin(
                        firstArg,
                        not_list);
                break;
            case REGEX:
                filterExpr = ef.regex(
                        firstArg,
                        f.getExpression().getParameters().get(1).getValue(),
                        f.getExpression().getParameters().get(2).getValue());
                break;
            default:
                throw new RuntimeException("Cannot recognize operator of filter. Found " + f.getExpression().getOperator());
        }
        return filterExpr;
    }

    public static Expr getExprForFunction(Function f, PrefixMapping p) {
        ExprFactory ef = new ExprFactory(p);
        Expr expr;
        switch (f.getName()) {
            case ADD:
                expr = ef.add(QueryUtils.getVarOrConstant(f.getParameters().get(0), p), QueryUtils.getVarOrConstant(f.getParameters().get(1), p));
                break;
            case SUBCTRACT:
                expr = ef.subtract(QueryUtils.getVarOrConstant(f.getParameters().get(0), p), QueryUtils.getVarOrConstant(f.getParameters().get(1), p));
                break;
            case MULTIPLY:
                expr = ef.multiply(QueryUtils.getVarOrConstant(f.getParameters().get(0), p), QueryUtils.getVarOrConstant(f.getParameters().get(1), p));
                break;
            case DIVIDE:
                expr = ef.divide(QueryUtils.getVarOrConstant(f.getParameters().get(0), p), QueryUtils.getVarOrConstant(f.getParameters().get(1), p));
                break;
            case SUBSTR:
                expr = ef.substr(QueryUtils.getVarOrConstant(f.getParameters().get(0), p), QueryUtils.getVarOrConstant(f.getParameters().get(1), p));
                break;
            case UCASE:
                expr = ef.ucase(QueryUtils.getVarOrConstant(f.getParameters().get(0), p));
                break;
            case LCASE:
                expr = ef.lcase(QueryUtils.getVarOrConstant(f.getParameters().get(0), p));
                break;
            case CONTAINS:
                expr = ef.contains(QueryUtils.getVarOrConstant(f.getParameters().get(0), p), QueryUtils.getVarOrConstant(f.getParameters().get(1), p));
                break;
            case CONCAT:
                expr = ef.concat(getVarOrConstant(f.getParameters().get(0), p), getVarOrConstant(f.getParameters().get(1), p));
                break;
            case ROUND:
                expr = ef.round(getVarOrConstant(f.getParameters().get(0), p));
                break;
            case CEIL:
                expr = ef.ceil(getVarOrConstant(f.getParameters().get(0), p));
                break;
            case FLOOR:
                expr = ef.floor(getVarOrConstant(f.getParameters().get(0), p));
                break;
            case YEAR:
                expr = ef.year(getVarOrConstant(f.getParameters().get(0), p));
                break;
            case MONTH:
                expr = ef.month(getVarOrConstant(f.getParameters().get(0), p));
                break;
            case DAY:
                expr = ef.day(getVarOrConstant(f.getParameters().get(0), p));
                break;
            case HOURS:
                expr = ef.hours(getVarOrConstant(f.getParameters().get(0), p));
                break;
            case MINUTES:
                expr = ef.minutes(getVarOrConstant(f.getParameters().get(0), p));
                break;
            case SECONDS:
                expr = ef.seconds(getVarOrConstant(f.getParameters().get(0), p));
                break;
            default: throw new RuntimeException("Cannot find function name.");
        }
        return expr;
    }

    public static void removeAggregations(Query q, Set<String> varToBeDeleted) {
        Iterator<Var> it = q.getProject().getExprs().keySet().iterator();
        //Remove aggregation functions in head
        while (it.hasNext()) {
            Var var = it.next();
            Expr expr = q.getProject().getExpr(var);
            if (expr instanceof ExprAggregator) {
                ExprAggregator agg = (ExprAggregator) expr;
                if (!Sets.intersection(
                        agg.getAggregator().getExprList().getVarsMentioned().stream().map(v -> v.getName()).collect(Collectors.toSet()),
                        varToBeDeleted)
                        .isEmpty()) {
                    it.remove();
                    q.getProject().remove(var);
                }
            }
        }
        //Remove group by if no more aggregations
        boolean foundAggr = false;
        for (Var k:q.getProject().getExprs().keySet()) {
            if (q.getProject().getExpr(k) instanceof ExprAggregator) {
               foundAggr = true;
               break;
            }
        }
        if (!foundAggr) q.getGroupBy().clear();
        //Remove having
        q.getHavingExprs().removeIf(expr -> {
              String serializedExpr = expr.toString();
              for (String v:varToBeDeleted) {
                if (serializedExpr.contains(v)) return true;
              }
              return false;
        });
        //Remove vars from group by
        for(String var:varToBeDeleted) {
            q.getGroupBy().remove(AbstractQueryBuilder.makeVar(var));
        }
    }

    public static void removeOrderBy(Query q, Set<String> varToBeDeleted) {
        if (q.getOrderBy() != null) {
            q.getOrderBy().removeIf(sortCondition -> !org.apache.jena.ext.com.google.common.collect.Sets.intersection(
                    sortCondition.getExpression().getVarsMentioned().stream().map(v -> v.getName()).collect(Collectors.toSet()),
                    varToBeDeleted)
                    .isEmpty());
        }
    }

    public static List<Triple> getOptionalTriplesToMove(GraphElement el, String classIRI, PrefixMapping p, List<String> list) {
        Entity.TypeEnum type = el.getEntities().get(0).getType();
        List<Triple> res = new LinkedList<>();
        if (type.equals(Entity.TypeEnum.CLASS)) {
            Node sub = AbstractQueryBuilder.makeNode(el.getVariables().get(0), p);
            Node pred = RDF.Nodes.type;
            Node obj = AbstractQueryBuilder.makeNode(IRI.create(classIRI).toQuotedString(), p);
            Triple triple = new Triple(sub, pred, obj);
            res.add(triple);
        } else if (type.equals(Entity.TypeEnum.OBJECTPROPERTY)) {
            Node sub2 = AbstractQueryBuilder.makeNode(el.getVariables().get(0), p);
            Node pred2 = (Node)AbstractQueryBuilder.makeNodeOrPath(IRI.create(el.getEntities().get(0).getIri()).toQuotedString(), p);
            Node obj2 = AbstractQueryBuilder.makeNode(el.getVariables().get(1), p);
            Triple triple2 = new Triple(sub2, pred2, obj2);
            res.add(triple2);
            list.add(el.getVariables().get(1).substring(1));
            Set<String> ids = new GraphElementFinder().findChildrenIds(el.getId(), el);
            for (String id : ids) {
                if (!id.equals(el.getId())) {
                    GraphElement child = new GraphElementFinder().findElementById(id, el);
                    res.addAll(getOptionalTriplesToMove(child, classIRI, p, list));
                }
            }
        } else if (type.equals(Entity.TypeEnum.INVERSEOBJECTPROPERTY)) {
            Node sub2 = AbstractQueryBuilder.makeNode(el.getVariables().get(1), p);
            Node pred2 = (Node)AbstractQueryBuilder.makeNodeOrPath(IRI.create(el.getEntities().get(0).getIri()).toQuotedString(), p);
            Node obj2 = AbstractQueryBuilder.makeNode(el.getVariables().get(0), p);
            Triple triple2 = new Triple(sub2, pred2, obj2);
            res.add(triple2);
            list.add(el.getVariables().get(0).substring(1));
            Set<String> ids = new GraphElementFinder().findChildrenIds(el.getId(), el);
            for (String id : ids) {
                if (!id.equals(el.getId())) {
                    GraphElement child = new GraphElementFinder().findElementById(id, el);
                    res.addAll(getOptionalTriplesToMove(child, classIRI, p, list));
                }
            }

        } else {
            Node sub = AbstractQueryBuilder.makeNode(el.getVariables().get(0), p);
            IRI predicate = IRI.create(el.getEntities().get(0).getIri());
            Node pred = (Node)AbstractQueryBuilder.makeNodeOrPath(predicate.toQuotedString(), p);
            Node obj = AbstractQueryBuilder.makeNode(el.getVariables().get(1), p);
            Triple triple = new Triple(sub, pred, obj);
            res.add(triple);
            list.add(el.getVariables().get(1).substring(1));
        }
        return res;
    }
}
