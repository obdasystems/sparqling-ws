package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class TestQueryGraphBuilder {
    @BeforeClass
    public static void init() throws FileNotFoundException {
        SWSOntologyManager.getOntologyManager().loadGrapholFile(new FileInputStream("src/test/resources/books/books_ontology.graphol"));
    }
    @Test
    public void getQueryGraph() {
        QueryGraphBuilder qgb = new QueryGraphBuilder();
        System.out.println(qgb.getQueryGraph("http://www.obdasystems.com/books/Book"));
    }
    @Test
    public void testGuessNewVar() {
        SelectBuilder sb = new SelectBuilder();
        sb.addVar("*").addWhere("?Book0", "a", "<http://www.obdasystems.com/books/Book>");
        assertEquals(QueryGraphBuilder.getNewCountedVarFromQuery("Book", sb.build()), "Book1");
    }
}
