package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.*;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.expr.E_StrSubstring;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.syntax.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class TestQueryGraphHandler {

    private static String bookIRI;
    private static String audioBookIRI;
    private static String writtenByIRI;
    private static String authorIRI;
    private static String nameIRI;
    private static SPARQLParser parser;
    private static String titleIRI;

    @BeforeClass
    public static void init() throws FileNotFoundException {
        bookIRI = "http://www.obdasystems.com/books/Book";
        audioBookIRI = "http://www.obdasystems.com/books/AudioBook";
        writtenByIRI = "http://www.obdasystems.com/books/writtenBy";
        authorIRI = "http://www.obdasystems.com/books/Author";
        nameIRI = "http://www.obdasystems.com/books/name";
        titleIRI = "http://www.obdasystems.com/books/title";
        parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
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
        assertEquals(QueryUtils.getNewCountedVarFromQuery("Book", sb.build()), "Book1");
    }
    @Test
    public void testGuessNewLongVar() {
        SelectBuilder sb = new SelectBuilder();
        IRI iri = IRI.create("http://www.example.com/ClassLoooooooooooooooooooooooooooooooooooooooooooong");
        sb.addVar("*").addWhere("?x", "a", iri);
        assertEquals(29, QueryUtils.guessNewVarFromIRI(iri, sb.build()).length());
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
        RuntimeException exc = assertThrows(RuntimeException.class, () -> gef.findElementById("Author0", finalQg.getGraph()));
        assertEquals("Graph element Author0 not found!", exc.getMessage());
        Query q = parser.parse(new Query(), qg.getSparql());
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementPathBlock el) {
                        Iterator<TriplePath> it = el.patternElts();
                        while(it.hasNext()) {
                            TriplePath tp = it.next();
                            if(tp.getSubject().isVariable() && tp.getSubject().getName().equals("Author0")
                                    || tp.getObject().isVariable() && tp.getObject().getName().equals("Author0")) {
                                throw new RuntimeException("Test delete graph element failed.");
                            }
                        }
                    }
                });
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
                Objects.requireNonNull(qg.getHead().stream().filter(headElement -> headElement.getGraphElementId().equals("Author0")).findAny().orElse(null)).getAlias());
    }

    @Test
    public void testRenameHeadElementDuplicate() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.addHeadTerm(qg, "Book0");
        qg = qgb.addHeadTerm(qg, "Author0");
        qg.getHead().get(1).setAlias("AUTORE");
        qg = qgb.renameHeadTerm(qg, "?Author0");
        qg.getHead().get(0).setAlias("AUTORE");
        QueryGraph finalQg = qg;
        RuntimeException exc = assertThrows(RuntimeException.class, () -> qgb.renameHeadTerm(finalQg, "?Book0"));
        assertEquals("Duplicate variable in result projection '?AUTORE'", exc.getMessage());
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
        Query q = parser.parse(new Query(), qg.getSparql());
        final int[] filters = {0};
        ElementWalker.walk(q.getQueryPattern(), new ElementVisitorBase() {
            @Override
            public void visit(ElementFilter el) {
                filters[0]++;
            }
        });
        assertEquals(2, filters[0]);
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
        assertEquals(1, filters[0]);
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
        assertFalse(qg.getSparql().contains("?Book0"));
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
        assertEquals(1, gef.findChildrenIds("name0", qg.getGraph()).size());
        assertEquals(5, gef.findChildrenIds("Author0", qg.getGraph()).size());
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
        qg = qgb.addHeadTerm(qg, "Book0");
        qg.getHead().get(1).setOrdering(-1);
        qg = qgb.orderBy(qg, "?Book0");
        Query q = parser.parse(new Query(), qg.getSparql());
        assertEquals(2,q.getOrderBy().size());
        qg.getHead().get(0).setOrdering(0);
        qg = qgb.orderBy(qg, "?name0");
        q = parser.parse(new Query(), qg.getSparql());
        assertEquals(1,q.getOrderBy().size());
    }

    @Test
    public void testOrderByFunction() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        Function f = new Function();
        f.setName(Function.NameEnum.SUBSTR);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant p1 = new VarOrConstant();
        p1.setType(VarOrConstant.TypeEnum.VAR);
        p1.setValue("?name0");
        VarOrConstant p2 = new VarOrConstant();
        p2.setValue("apapapap");
        p2.setType(VarOrConstant.TypeEnum.CONSTANT);
        p2.setConstantType(VarOrConstant.ConstantTypeEnum.STRING);
        params.add(p1);
        params.add(p2);
        f.setParameters(params);
        qg.getHead().get(0).setFunction(f);
        qg = qgb.functionHeadTerm(qg, "?name0");
        qg.getHead().get(0).setOrdering(1);
        qg = qgb.orderBy(qg, "?SUBSTR0");
        Query q = parser.parse(new Query(), qg.getSparql());
        assertTrue(q.getOrderBy().get(0).getExpression() instanceof E_StrSubstring);
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
        Query q = parser.parse(new Query(), qg.getSparql());
        System.out.println(q);
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
        gb.distinct(true);
        qg.getHead().get(0).setGroupBy(gb);
        Filter having = new Filter();
        FilterExpression havingExpr = new FilterExpression();
        havingExpr.setOperator(FilterExpression.OperatorEnum.GREATER_THAN);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setType(VarOrConstant.TypeEnum.VAR);
        v1.setValue("?name0");
        VarOrConstant v2 = new VarOrConstant();
        v2.setType(VarOrConstant.TypeEnum.CONSTANT);
        v2.setConstantType(VarOrConstant.ConstantTypeEnum.DECIMAL);
        v2.setValue("12");
        params.add(v1);
        params.add(v2);
        havingExpr.setParameters(params);
        having.setExpression(havingExpr);
        List<Filter> havings = new LinkedList<>();
        havings.add(having);
        qg.getHead().get(0).setHaving(havings);
        qg = qgb.aggregationHeadTerm(qg, "?name0");
        Query q = parser.parse(new Query(), qg.getSparql());
        Expr e = q.getProject().getExprs().values().iterator().next();
        assertTrue(e instanceof ExprAggregator);
        assertEquals("COUNT", ((ExprAggregator)e).getAggregator().getName());
        assertEquals(1, q.getGroupBy().size());
        assertEquals(1, q.getHavingExprs().size());
    }

    @Test
    public void testAggregate2GroupBy() {
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
        gb.distinct(true);
        qg.getHead().get(0).setGroupBy(gb);
        Filter having = new Filter();
        FilterExpression havingExpr = new FilterExpression();
        havingExpr.setOperator(FilterExpression.OperatorEnum.GREATER_THAN);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setType(VarOrConstant.TypeEnum.VAR);
        v1.setValue("?name0");
        VarOrConstant v2 = new VarOrConstant();
        v2.setType(VarOrConstant.TypeEnum.CONSTANT);
        v2.setConstantType(VarOrConstant.ConstantTypeEnum.DECIMAL);
        v2.setValue("12");
        params.add(v1);
        params.add(v2);
        havingExpr.setParameters(params);
        having.setExpression(havingExpr);
        List<Filter> havings = new LinkedList<>();
        havings.add(having);
        qg.getHead().get(0).setHaving(havings);
        qg = qgb.aggregationHeadTerm(qg, "?name0");
        GroupByElement gb2 = new GroupByElement();
        gb2.setAggregateFunction(GroupByElement.AggregateFunctionEnum.MAX);
        gb2.distinct(true);
        qg.getHead().get(1).setGroupBy(gb2);
        qg = qgb.aggregationHeadTerm(qg, "?name1");
        Query q = parser.parse(new Query(), qg.getSparql());
        Iterator<Expr> it = q.getProject().getExprs().values().iterator();
        Expr e = it.next();
        Expr e1 = it.next();
        assertTrue(e instanceof ExprAggregator);
        assertTrue(e1 instanceof ExprAggregator);
        assertEquals("COUNT", ((ExprAggregator)e).getAggregator().getName());
        assertEquals("MAX", ((ExprAggregator)e1).getAggregator().getName());
        assertEquals(0, q.getGroupBy().size());
        assertEquals(1, q.getHavingExprs().size());
    }

    @Test
    public void testReorderHead() {
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
        List<HeadElement> newHead = new LinkedList<>();
        List<HeadElement> oldHead = qg.getHead();
        for(int i=oldHead.size()-1;i>=0;i--){
            newHead.add(oldHead.get(i));
        }
        qg.setHead(newHead);
        qg = qgb.reorderHeadTerm(qg);
        Query q = parser.parse(new Query(), qg.getSparql());
        AtomicInteger index = new AtomicInteger();
        q.getProject().forEachVar(i -> assertEquals(i.getVarName(),newHead.get(index.getAndIncrement()).getId().substring(1)));
    }

    @Test
    public void testIssue15() {
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
        gb.setAggregateFunction(GroupByElement.AggregateFunctionEnum.SUM);
        gb.distinct(false);
        qg.getHead().get(0).setGroupBy(gb);
        Filter having = new Filter();
        FilterExpression havingExpr = new FilterExpression();
        havingExpr.setOperator(FilterExpression.OperatorEnum.GREATER_THAN);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setType(VarOrConstant.TypeEnum.VAR);
        v1.setValue("?name0");
        VarOrConstant v2 = new VarOrConstant();
        v2.setType(VarOrConstant.TypeEnum.CONSTANT);
        v2.setConstantType(VarOrConstant.ConstantTypeEnum.DECIMAL);
        v2.setValue("12");
        params.add(v1);
        params.add(v2);
        havingExpr.setParameters(params);
        having.setExpression(havingExpr);
        List<Filter> havings = new LinkedList<>();
        havings.add(having);
        qg.getHead().get(0).setHaving(havings);
        qg = qgb.aggregationHeadTerm(qg, "?name0");
        qg = qgb.deleteQueryGraphElement(qg, "name0");
        Query q = parser.parse(new Query(), qg.getSparql());
        assertTrue(q.getProject().getExprs().isEmpty());
        assertTrue(q.getGroupBy().isEmpty());
        assertFalse(q.hasHaving());
    }

    @Test
    public void testIssue16() {
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
        v1.setValue("?name0");
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
        qg = qgb.deleteQueryGraphElement(qg, "name0");
        assertTrue(qg.getFilters().isEmpty());
    }

    @Test
    public void testIssue17Body() {
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
        qg = qgb.deleteQueryGraphElement(qg, "name0");
        Query q = parser.parse(new Query(), qg.getSparql());
        assertTrue(q.getOrderBy() == null);
    }

    @Test
    public void testIssue17Head() {
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
        qg = qgb.deleteHeadTerm(qg, "name0");
        Query q = parser.parse(new Query(), qg.getSparql());
        assertTrue(q.getOrderBy() == null);
    }

    @Test
    public void testCountStar() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.countStar(qg, true);
        Query q = parser.parse(new Query(), qg.getSparql());
        Iterator<Expr> it = q.getProject().getExprs().values().iterator();
        Expr e = it.next();
        assertTrue(e instanceof ExprAggregator);
        assertEquals("COUNT", ((ExprAggregator)e).getAggregator().getName());
        qg = qgb.deleteHeadTerm(qg, "?COUNT_STAR");
        q = parser.parse(new Query(), qg.getSparql());
        assertTrue(q.isQueryResultStar());
    }

    @Test
    public void testExtras() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.setDistinct(qg, true);
        int l = 100000000;
        int o = 200000;
        qg = qgb.setLimit(qg, l);
        qg = qgb.setOffset(qg, o);
        Query q = parser.parse(new Query(), qg.getSparql());
        assertTrue(q.isDistinct());
        assertTrue(q.getLimit() == l);
        assertTrue(q.getOffset() == o);

        qg = qgb.setDistinct(qg, false);
        qg = qgb.setLimit(qg, -1);
        qg = qgb.setOffset(qg, -1);
        q = parser.parse(new Query(), qg.getSparql());
        assertFalse(q.isDistinct());
        assertFalse(q.hasLimit());
        assertFalse(q.hasOffset());
    }

    @Test
    public void testIssue19_3() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        GroupByElement gb = new GroupByElement();
        gb.setAggregateFunction(GroupByElement.AggregateFunctionEnum.COUNT);
        gb.distinct(true);
        qg.getHead().get(0).setGroupBy(gb);
        Filter having = new Filter();
        FilterExpression havingExpr = new FilterExpression();
        havingExpr.setOperator(FilterExpression.OperatorEnum.GREATER_THAN);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setType(VarOrConstant.TypeEnum.VAR);
        v1.setValue("?name0");
        VarOrConstant v2 = new VarOrConstant();
        v2.setType(VarOrConstant.TypeEnum.CONSTANT);
        v2.setConstantType(VarOrConstant.ConstantTypeEnum.DECIMAL);
        v2.setValue("12");
        params.add(v1);
        params.add(v2);
        havingExpr.setParameters(params);
        having.setExpression(havingExpr);
        List<Filter> havings = new LinkedList<>();
        havings.add(having);
        qg.getHead().get(0).setHaving(havings);
        qg = qgb.aggregationHeadTerm(qg, "?name0");
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
    }

    @Test
    public void testIssue19_1() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        GroupByElement gb = new GroupByElement();
        gb.setAggregateFunction(GroupByElement.AggregateFunctionEnum.COUNT);
        gb.distinct(true);
        qg.getHead().get(0).setGroupBy(gb);
        Filter having = new Filter();
        FilterExpression havingExpr = new FilterExpression();
        havingExpr.setOperator(FilterExpression.OperatorEnum.GREATER_THAN);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setType(VarOrConstant.TypeEnum.VAR);
        v1.setValue("?name0");
        VarOrConstant v2 = new VarOrConstant();
        v2.setType(VarOrConstant.TypeEnum.CONSTANT);
        v2.setConstantType(VarOrConstant.ConstantTypeEnum.DECIMAL);
        v2.setValue("12");
        params.add(v1);
        params.add(v2);
        havingExpr.setParameters(params);
        having.setExpression(havingExpr);
        List<Filter> havings = new LinkedList<>();
        havings.add(having);
        qg.getHead().get(0).setHaving(havings);
        qg = qgb.aggregationHeadTerm(qg, "?name0");
        qg = qgb.deleteHeadTerm(qg, "count_name0");
    }

    @Test
    public void testIssue19_2() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        GroupByElement gb = new GroupByElement();
        gb.setAggregateFunction(GroupByElement.AggregateFunctionEnum.COUNT);
        gb.distinct(true);
        qg.getHead().get(0).setGroupBy(gb);
        Filter having = new Filter();
        FilterExpression havingExpr = new FilterExpression();
        havingExpr.setOperator(FilterExpression.OperatorEnum.GREATER_THAN);
        List<VarOrConstant> params = new LinkedList<>();
        VarOrConstant v1 = new VarOrConstant();
        v1.setType(VarOrConstant.TypeEnum.VAR);
        v1.setValue("?name0");
        VarOrConstant v2 = new VarOrConstant();
        v2.setType(VarOrConstant.TypeEnum.CONSTANT);
        v2.setConstantType(VarOrConstant.ConstantTypeEnum.DECIMAL);
        v2.setValue("12");
        params.add(v1);
        params.add(v2);
        havingExpr.setParameters(params);
        having.setExpression(havingExpr);
        List<Filter> havings = new LinkedList<>();
        havings.add(having);
        qg.getHead().get(0).setHaving(havings);
        qg = qgb.aggregationHeadTerm(qg, "?name0");
        qg = qgb.deleteQueryGraphElement(qg, "name0");
    }

    @Test
    public void testIssue21() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.deleteQueryGraphElement(qg, "Author0");
        Query q = parser.parse(new Query(), qg.getSparql());
        assertTrue(q.isQueryResultStar());
    }

    @Test
    public void testDeleteSubClass() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.deleteQueryGraphElementClass(qg, "Book0", bookIRI);
        Query q = parser.parse(new Query(), qg.getSparql());
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementPathBlock el) {
                        Iterator<TriplePath> it = el.patternElts();
                        while(it.hasNext()) {
                            TriplePath e = it.next();
                            if (e.getObject().isURI() && e.getObject().getURI().equals(bookIRI)) {
                                throw new RuntimeException("Delete sub class test failed!");
                            }
                        }
                    }
                });
    }

    @Test
    public void testNewOptional() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.newOptional(qg, qg.getGraph().getChildren().get(0).getId(), authorIRI);
        Query q = parser.parse(new Query(), qg.getSparql());
        final boolean[] found = {false};
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementOptional el) {
                        found[0] = true;
                    }
                });
        assertTrue(found[0]);
        qg = qgb.removeAllOptionals(qg);
        q = parser.parse(new Query(), qg.getSparql());
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementOptional el) {
                        throw new RuntimeException("Delete optional failed");
                    }
                });
    }

    @Test
    public void testNewOptionalInverseOP() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(authorIRI);
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, bookIRI, false, "Author0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.putQueryGraphDataProperty(qg, "", titleIRI, "Book0");
        qg = qgb.newOptional(qg, qg.getGraph().getChildren().get(0).getId(), bookIRI);
        Query q = parser.parse(new Query(), qg.getSparql());
        final boolean[] found = {false};
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementOptional el) {
                        found[0] = true;
                    }
                });
        System.out.println(qg);
        assertTrue(found[0]);
        qg = qgb.removeAllOptionals(qg);
        q = parser.parse(new Query(), qg.getSparql());
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementOptional el) {
                        throw new RuntimeException("Delete optional failed");
                    }
                });
    }

    @Test
    public void testNewOptionalData() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.newOptional(qg, "name0", authorIRI);
        Query q = parser.parse(new Query(), qg.getSparql());
        final boolean[] found = {false};
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementOptional el) {
                        found[0] = true;
                    }
                });
        assertTrue(found[0]);
        qg = qgb.removeAllOptionals(qg);
        q = parser.parse(new Query(), qg.getSparql());
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementOptional el) {
                        throw new RuntimeException("Delete optional failed");
                    }
                });
    }

    @Test
    public void testNewOptionalClass() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.newOptional(qg, "Book0", bookIRI);
        Query q = parser.parse(new Query(), qg.getSparql());
        final boolean[] found = {false};
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementOptional el) {
                        found[0] = true;
                    }
                });
        assertTrue(found[0]);
        qg = qgb.removeAllOptionals(qg);
        q = parser.parse(new Query(), qg.getSparql());
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementOptional el) {
                        throw new RuntimeException("Delete optional failed");
                    }
                });
    }

    @Test
    public void testTwoOptionals() {
        QueryGraphHandler qgb = new QueryGraphHandler();
        QueryGraph qg = qgb.getQueryGraph(bookIRI);
        qg = qgb.putQueryGraphClass(
                qg,"",audioBookIRI,"Book0");
        qg = qgb.putQueryGraphObjectProperty(
                qg, "", writtenByIRI, authorIRI, true, "Book0"
        );
        qg = qgb.putQueryGraphDataProperty(qg, "", nameIRI, "Author0");
        qg = qgb.putQueryGraphDataProperty(qg, "", titleIRI, "Book0");
        //qg = qgb.newOptional(qg, qg.getGraph().getChildren().get(0).getId(), authorIRI);
        qg = qgb.newOptional(qg, "name0", null);
        qg = qgb.newOptional(qg, "title0", null);
        qg = qgb.removeOptional(qg, "name0", null);
        Query q = parser.parse(new Query(), qg.getSparql());
        final int[] count = {0};
        ElementWalker.walk(
                q.getQueryPattern(),
                new ElementVisitorBase() {
                    @Override
                    public void visit(ElementOptional el) {
                        count[0]++;
                    }
                });
        assertEquals(1, count[0]);

    }

    @Test
    public void sandbox() {
        String sparql = "SELECT distinct (sum(distinct ?y) as ?sum) " +
                "{ " +
                "?x <op> ?y." +
                "?x <op> ?z." +
                "?x <op> ?z1." +
                "?x <op> ?z2." +
                "}" +
                "GROUP BY ?z ?z1 ?z2";
        Query q = parser.parse(new Query(), sparql);
        q.setLimit(Query.NOLIMIT);
        System.out.println(q);
    }
}
