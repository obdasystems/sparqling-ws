package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.QueryGraph;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
        SWSOntologyManager.getOntologyManager().loadGrapholFile(new FileInputStream("src/test/resources/books/books_ontology.graphol"));
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
        assertTrue(exc.getMessage().equals("Graph element not found!"));
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
    public void sandbox() {
        SPARQLParser parser = SPARQLParser.createParser(Syntax.syntaxSPARQL_11);
        Query q = parser.parse(new Query(), "select (?x as ?pippo) { ?x a <IRI> }");
        q.getProject().forEachVarExpr((var, expr) -> {
            if(var.getVarName().equals("")) {
                System.out.println();
            }
        });
    }
}
