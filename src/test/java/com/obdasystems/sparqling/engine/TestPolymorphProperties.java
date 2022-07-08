package com.obdasystems.sparqling.engine;

import com.obdasystems.sparqling.model.Highlights;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class TestPolymorphProperties {
    String iri = "http://testTipizzazione.com#";
    @BeforeClass
    public static void init() throws FileNotFoundException {
        SWSOntologyManager.getOntologyManager().loadGrapholFile(new FileInputStream("src/test/resources/issue_11/testTipizzazione.graphol"));
    }

    @Test
    public void testA() {
        Highlights res = SWSOntologyManager.getOntologyManager().getOntologyProximityManager().getHighlights(iri + "A");
        assertEquals(iri + "B", res.getObjectProperties().get(0).getRelatedClasses().get(0));
    }

    @Test
    public void testA1() {
        Highlights res = SWSOntologyManager.getOntologyManager().getOntologyProximityManager().getHighlights(iri + "A1");
        assertEquals(iri + "B1", res.getObjectProperties().get(0).getRelatedClasses().get(0));
    }

    @Test
    public void testA2() {
        Highlights res = SWSOntologyManager.getOntologyManager().getOntologyProximityManager().getHighlights(iri + "A2");
        assertEquals(iri + "B2", res.getObjectProperties().get(0).getRelatedClasses().get(0));
    }
}
