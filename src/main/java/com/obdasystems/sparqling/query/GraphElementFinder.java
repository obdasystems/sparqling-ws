package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.model.Entity;
import com.obdasystems.sparqling.model.GraphElement;

import java.util.Iterator;

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

    public void deleteElementById(String id, GraphElement e) {
        if(e.getChildren() == null || e.getChildren().size() == 0) {
            return;
        }
        Iterator<GraphElement> it = e.getChildren().iterator();
        while(it.hasNext()) {
            GraphElement child = it.next();
            if(child.getId().equals(id)) {
                it.remove();
            } else {
                deleteElementById(id, child);
            }
        }
    }

    public void deleteObjectPropertiesWithNoChild(GraphElement e) {
        if(e.getChildren() == null || e.getChildren().size() == 0) {
            return;
        }
        Iterator<GraphElement> it = e.getChildren().iterator();
        while(it.hasNext()) {
            GraphElement child = it.next();
            if(e.getEntities().get(0).getType().equals(Entity.TypeEnum.OBJECTPROPERTY) && child == null) {
                it.remove();
            } else {
                deleteObjectPropertiesWithNoChild(child);
            }
        }
    }
}
