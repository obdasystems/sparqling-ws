package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.model.GraphElement;

public class GraphElementFinder {
    private GraphElement found;

    public void findElementById(String id, GraphElement e) {
        if(e.getId().equals(id)) {
            found = e;
            return;
        }
        if(e.getChildren() == null || e.getChildren().size() == 0) {
            return;
        }
        for(GraphElement child:e.getChildren()) {
            findElementById(id, child);
        }
    }

    public GraphElement getFound() {
        if (found == null) {
            throw new RuntimeException("Graph element not found!");
        }
        return found;
    }
}
