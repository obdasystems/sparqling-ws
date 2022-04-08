package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.*;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.ext.com.google.common.graph.ElementOrder;
import org.apache.jena.query.Query;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.expr.E_StrSubstring;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.syntax.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TestQueryGraphHandler {

    private static String bookIRI;
    private static String audioBookIRI;
    private static String writtenByIRI;
    private static String authorIRI;
    private static String nameIRI;

    @BeforeClass
    public static void init() throws FileNotFoundException {
        bookIRI = "http://www.obdasystems.com/books/Book";
        audioBookIRI = "http://www.obdasystems.com/books/AudioBook";
        writtenByIRI = "http://www.obdasystems.com/books/writtenBy";
        authorIRI = "http://www.obdasystems.com/books/Author";
        nameIRI = "http://www.obdasystems.com/books/name";
        SWSOntologyManager.getOntologyManager().loadGrapholFile(new FileInputStream("src/test/resources/books/books_1.0.0/books_ontology.graphol"));
    }
    @Test
    public void getQueryGraph() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        System.out.println(qg);
    }
    @Test
    public void testGuessNewVar() {
        SelectBuilder sb = new SelectBuilder();
        sb.addVar("*").addWhere("?Book0", "a", "<"+bookIRI+">");
        assertEquals(QueryGraphHandler.getNewCountedVarFromQuery("Book", sb.build()), "Book1");
    }
    @Test
    public void testDeleteGraphElement() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.deleteQueryGraphElement(
                qg, "Author0"
        );
        GraphElementFinder gef = new GraphElementFinder();
        QueryGraph finalQg = qg;
        RuntimeException exc = assertThrows(RuntimeException.class, () -> {
            gef.findElementById("Author0", finalQg.getGraph());
        });
        assertEquals("Graph element Author0 not found!", exc.getMessage());
    }
    @Test
    public void testAddHeadElement() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.addHeadTerm(qg, "Author0");
        assertNotNull(qg.getHead().stream().filter(headElement -> headElement.getGraphElementId().equals("Author0")).findAny().orElse(null));
    }

    @Test
    public void testDeleteHeadElement() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.addHeadTerm(qg, "Author0");
        qg = qgb.deleteHeadTerm(qg, "?Author0");
        assertNull(qg.getHead().stream().filter(headElement -> headElement.getGraphElementId().equals("Author0")).findAny().orElse(null));
    }

    @Test
    public void testRenameHeadElement() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.addHeadTerm(qg, "Book0");
        qg = qgb.addHeadTerm(qg, "Author0");
        qg.getHead().get(1).setAlias("AUTORE");
        qg = qgb.renameHeadTerm(qg, "?Author0");
        assertEquals(
                "AUTORE",
                qg.getHead().stream().filter(headElement -> headElement.getGraphElementId().equals("Author0")).findAny().orElse(null).getAlias());
    }

    @Test
    public void testRenameAndDeleteHeadElement() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.addHeadTerm(qg, "Book0");
        qg = qgb.addHeadTerm(qg, "Author0");
        qg.getHead().get(1).setAlias("AUTORE");
        qg = qgb.renameHeadTerm(qg, "?Author0");
        qg = qgb.deleteHeadTerm(qg, "?AUTORE");
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        assertFalse(q.getProject().contains(AbstractQueryBuilder.makeVar("?AUTORE")));
    }

    @Test
    public void testRenameAndRenameHeadElement() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.addHeadTerm(qg, "Book0");
        qg = qgb.addHeadTerm(qg, "Author0");
        qg.getHead().get(1).setAlias("AUTORE");
        qg = qgb.renameHeadTerm(qg, "?Author0");
        qg.getHead().get(1).setAlias("AUTORONE");
        qg = qgb.renameHeadTerm(qg, "?AUTORE");
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        assertFalse(q.getProject().contains(AbstractQueryBuilder.makeVar("?AUTORE")));
    }

    @Test
    public void testDeleteLastHeadElement() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.deleteHeadTerm(qg, "?name0");
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        assertTrue(q.isQueryResultStar());
    }

    @Test
    public void newFilter() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        Filter f = new Filter();
        FilterExpression fe = new FilterExpression();
        fe.setOperator(FilterExpression.OperatorEnum.NOT_IN);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setValue("?Book0");
        v1.setType(VarOrConstant.TypeEnum.VAR);
        params.add(v1);
        VarOrConstant v2 = new VarOrConstant();
        v2.setValue("http://www.obdasystems.com/books/book-1");
        v2.setType(VarOrConstant.TypeEnum.IRI);
        VarOrConstant v3 = new VarOrConstant();
        v3.setValue("1");
        v3.setType(VarOrConstant.TypeEnum.CONSTANT);
        v3.setConstantType(VarOrConstant.ConstantTypeEnum.DECIMAL);
        VarOrConstant v4 = new VarOrConstant();
        v4.setValue("11-11-2020");
        v4.setType(VarOrConstant.TypeEnum.CONSTANT);
        v4.setConstantType(VarOrConstant.ConstantTypeEnum.DATETIME);
        params.add(v2);
        params.add(v3);
        params.add(v4);
        fe.setParameters(params);
        f.setExpression(fe);
        qg.addFiltersItem(f);
        qg = qgb.newFilter(qg, 0);
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        final boolean[] passed = {false};
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementFilter el) {
                        if(el.getExpr().getFunction().getFunctionSymbol().getSymbol().equals("notin")
                            && el.getExpr().getVarsMentioned().contains(
                                    AbstractQueryBuilder.makeVar("?Book0")))
                            passed[0] = true;
                    }
                });
        assertTrue(passed[0]);
    }

    @Test
    public void twoFilters() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        Filter f = new Filter();
        FilterExpression fe = new FilterExpression();
        fe.setOperator(FilterExpression.OperatorEnum.NOT_IN);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setValue("?Book0");
        v1.setType(VarOrConstant.TypeEnum.VAR);
        params.add(v1);
        VarOrConstant v2 = new VarOrConstant();
        v2.setValue("http://www.obdasystems.com/books/book-1");
        v2.setType(VarOrConstant.TypeEnum.IRI);
        VarOrConstant v3 = new VarOrConstant();
        v3.setValue("1");
        v3.setType(VarOrConstant.TypeEnum.CONSTANT);
        v3.setConstantType(VarOrConstant.ConstantTypeEnum.DECIMAL);
        VarOrConstant v4 = new VarOrConstant();
        v4.setValue("11-11-2020");
        v4.setType(VarOrConstant.TypeEnum.CONSTANT);
        v4.setConstantType(VarOrConstant.ConstantTypeEnum.DATETIME);
        params.add(v2);
        params.add(v3);
        params.add(v4);
        fe.setParameters(params);
        f.setExpression(fe);
        qg.addFiltersItem(f);
        qg = qgb.newFilter(qg, 0);

        Filter f2 = new Filter();
        FilterExpression fe2 = new FilterExpression();
        fe2.setOperator(FilterExpression.OperatorEnum.NOT_EQUAL);
        List<VarOrConstant> params2 = new LinkedList<>();
        VarOrConstant v12 = new VarOrConstant();
        v12.setValue("?Author0");
        v12.setType(VarOrConstant.TypeEnum.VAR);
        VarOrConstant v22 = new VarOrConstant();
        v22.setValue("http://www.obdasystems.com/books/book-1");
        v22.setType(VarOrConstant.TypeEnum.IRI);
        params2.add(v12);
        params2.add(v22);
        fe2.setParameters(params2);
        f2.setExpression(fe2);
        qg.addFiltersItem(f2);
        qg = qgb.newFilter(qg, 1);
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        final int[] filters = {0};
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementFilter el) {
                        filters[0]++;
                    }
                });
        assertTrue(filters[0] == 2);
    }

    @Test
    public void testRegex() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        Filter f = new Filter();
        FilterExpression fe = new FilterExpression();
        fe.setOperator(FilterExpression.OperatorEnum.REGEX);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setValue("?Book0");
        v1.setType(VarOrConstant.TypeEnum.VAR);
        params.add(v1);
        VarOrConstant v2 = new VarOrConstant();
        v2.setValue("Pippo");
        v2.setType(VarOrConstant.TypeEnum.CONSTANT);
        v2.setConstantType(VarOrConstant.ConstantTypeEnum.STRING);
        VarOrConstant v3 = new VarOrConstant();
        v3.setValue("i");
        v3.setType(VarOrConstant.TypeEnum.CONSTANT);
        v3.setConstantType(VarOrConstant.ConstantTypeEnum.STRING);
        params.add(v2);
        params.add(v3);
        fe.setParameters(params);
        f.setExpression(fe);
        qg.addFiltersItem(f);
        qg = qgb.newFilter(qg, 0);
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementFilter el) {
                        assertEquals("regex", el.getExpr().getFunction().getFunctionSymbol().getSymbol());
                    }
                });
    }

    @Test
    public void removeFilter() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        Filter f = new Filter();
        FilterExpression fe = new FilterExpression();
        fe.setOperator(FilterExpression.OperatorEnum.NOT_IN);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setValue("?Book0");
        v1.setType(VarOrConstant.TypeEnum.VAR);
        params.add(v1);
        VarOrConstant v2 = new VarOrConstant();
        v2.setValue("http://www.obdasystems.com/books/book-1");
        v2.setType(VarOrConstant.TypeEnum.IRI);
        VarOrConstant v3 = new VarOrConstant();
        v3.setValue("1");
        v3.setType(VarOrConstant.TypeEnum.CONSTANT);
        v3.setConstantType(VarOrConstant.ConstantTypeEnum.DECIMAL);
        VarOrConstant v4 = new VarOrConstant();
        v4.setValue("11-11-2020");
        v4.setType(VarOrConstant.TypeEnum.CONSTANT);
        v4.setConstantType(VarOrConstant.ConstantTypeEnum.DATETIME);
        params.add(v2);
        params.add(v3);
        params.add(v4);
        fe.setParameters(params);
        f.setExpression(fe);
        qg.addFiltersItem(f);
        qg = qgb.newFilter(qg, 0);

        Filter f2 = new Filter();
        FilterExpression fe2 = new FilterExpression();
        fe2.setOperator(FilterExpression.OperatorEnum.NOT_EQUAL);
        List<VarOrConstant> params2 = new LinkedList<>();
        VarOrConstant v12 = new VarOrConstant();
        v12.setValue("?Author0");
        v12.setType(VarOrConstant.TypeEnum.VAR);
        VarOrConstant v22 = new VarOrConstant();
        v22.setValue("http://www.obdasystems.com/books/book-1");
        v22.setType(VarOrConstant.TypeEnum.IRI);
        params2.add(v12);
        params2.add(v22);
        fe2.setParameters(params2);
        f2.setExpression(fe2);
        qg.addFiltersItem(f2);
        qg = qgb.newFilter(qg, 1);
        qg = qgb.removeFilter(qg, 0, true);
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        final int[] filters = {0};
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementFilter el) {
                        filters[0]++;
                    }
                });
        assertTrue(filters[0] == 1);
    }

    @Test
    public void joinWithCycle() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, bookIRI, false, "Author0"
        );
        qg = qgb.putQueryGraphJoin(qg,"Book1", "Book0");
        qg.toString();
    }

    @Test
    public void testFindChildrenIds() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        GraphElementFinder gef = new GraphElementFinder();
        assertEquals(gef.findChildrenIds("name0", qg.getGraph()).size(), 1);
    }

    @Test
    public void testOrderBy() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg.getHead().get(0).setOrdering(1);
        qg = qgb.orderBy(qg, "?name0");
        qg.getHead().get(0).setOrdering(0);
        qg = qgb.orderBy(qg, "?name0");
        System.out.println(qg);
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        assertEquals(1,q.getOrderBy().size());
    }

    @Test
    public void testHeadFunction() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        HeadElement he = qg.getHead().get(0);
        Function f = new Function();
        f.setName(Function.NameEnum.SUBSTR);
        VarOrConstant v1 = new VarOrConstant();
        v1.setValue("?name0");
        v1.setType(VarOrConstant.TypeEnum.VAR);
        VarOrConstant v2 = new VarOrConstant();
        v2.setValue("pippo");
        v2.setType(VarOrConstant.TypeEnum.CONSTANT);
        v2.setConstantType(VarOrConstant.ConstantTypeEnum.STRING);
        f.addParametersItem(v1);
        f.addParametersItem(v2);
        he.setFunction(f);
        qg = qgb.functionHeadTerm(qg, "?name0");
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        assertTrue(q.getProject().getExprs().values().iterator().next() instanceof E_StrSubstring);
    }

    @Test
    public void testAggregateGroupBy() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        GroupByElement gb = new GroupByElement();
        gb.setAggregateFunction(GroupByElement.AggregateFunctionEnum.COUNT);
        qg.setGroupBy(gb);
        qg = qgb.aggregationHeadTerm(qg, "?name0");
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), qg.getSparql());
        Expr e = q.getProject().getExprs().values().iterator().next();
        assertTrue(e instanceof ExprAggregator);
        assertEquals("COUNT", ((ExprAggregator)e).getAggregator().getName());
        assertEquals(1, q.getGroupBy().size());
    }

    @Test
    public void sandbox() {
        String sparql = "SELECT (sum(?y) as ?sum) " +
                "{ " +
                "?x <op> ?y." +
                "?x <op> ?z." +
                "?x <op> ?z1." +
                "?x <op> ?z2." +
                "}" +
                "GROUP BY ?z ?z1 ?z2";
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), sparql);
        System.out.println(q);
    }
}
