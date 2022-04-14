package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.model.Filter;
import com.obdasystems.sparqling.model.Function;
import com.obdasystems.sparqling.model.VarOrConstant;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.ExprFactory;
import org.apache.jena.query.Query;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.algebra.walker.ElementWalker_New;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.*;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.semanticweb.owlapi.model.IRI;

import java.util.Iterator;

import static com.obdasystems.sparqling.query.QueryGraphHandler.varPrefix;

public class QueryUtils {
    static String guessNewVarFromIRI(IRI iri, Query q) {
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
                    },
                    new ExprVisitorBase() {}
            );
        }
        return ret;
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
                ExprList list = new ExprList();
                for (VarOrConstant v:f.getExpression().getParameters()) {
                    list.add(getVarOrConstant(v,p));
                }
                filterExpr = ef.in(
                        firstArg,
                        list);
                break;
            case NOT_IN:
                ExprList not_list = new ExprList();
                for (VarOrConstant v:f.getExpression().getParameters()) {
                    not_list.add(getVarOrConstant(v,p));
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
}
