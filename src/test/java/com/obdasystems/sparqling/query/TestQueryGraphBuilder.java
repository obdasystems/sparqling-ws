package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.engine.SWSOntologyManager;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TestQueryGraphBuilder {
    @Before
    public void init() throws FileNotFoundException {
        SWSOntologyManager.getOntologyManager().loadGrapholFile(new FileInputStream("src/test/resources/books/books_ontology.graphol"));
    }
    @Test
    public void test() {
        QueryGraphBuilder qgb = new QueryGraphBuilder();
        System.out.println(qgb.addClassTriplePattern("http://www.obdasystems.com/books/Book"));
    }
}
