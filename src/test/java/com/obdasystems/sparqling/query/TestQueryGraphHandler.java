package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import com.obdasystems.sparqling.model.QueryGraph;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

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
        System.out.println(qg);
    }
}
