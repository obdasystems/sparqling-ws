package com.obdasystems.sparqling.engine;

import com.obdasystems.sparqling.model.Branch;
import com.obdasystems.sparqling.model.Highlights;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class TestHighlightsDuplicates {
    String iri = "http://www.obdasystems.com/books/";

    @BeforeClass
    public static void init() throws FileNotFoundException {
        SWSOntologyManager.getOntologyManager().loadGrapholFile(new FileInputStream("src/test/resources/books/books_1.0.0/books_ontology.graphol"));
    }
    @Test
    public void booksDuplocates() {
        Highlights res = SWSOntologyManager.getOntologyManager().getOntologyProximityManager().getHighlights(iri + "Author");
        for (Branch b:res.getObjectProperties()) {
            if(b.getRelatedClasses().stream().anyMatch(i -> Collections.frequency(b.getRelatedClasses(), i) > 1)) {
                assertTrue("Duplicates found!", false);
            }
        }
    }
}
