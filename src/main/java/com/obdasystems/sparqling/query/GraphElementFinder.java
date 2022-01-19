package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.model.Entity;
import com.obdasystems.sparqling.model.GraphElement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GraphElementFinder {
    private GraphElement found;
    private Set<String> childrenIds;

    public GraphElementFinder() {
        childrenIds = new HashSet<>();
    }

    public void findElementById(String id, GraphElement e) {
        if(e.getId().equals(id)) {
            // If this node is a result of join find the element with children
            if(found != null && e.getChildren() != null && e.getChildren().size() != 0)
                found = e;
            else if(found == null) {
                found = e;
            }
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

    public void findChildrenIds(String id, GraphElement e) {
        if(e.getId().equals(id)) {
            found = e;
        }
        if(e.getChildren() == null || e.getChildren().size() == 0) {
            return;
        }
        for(GraphElement child:e.getChildren()) {
            if(found != null) {
                childrenIds.add(child.getId());
            }
            findChildrenIds(id, child);
        }
        found = null;
    }

    public Set<String> getChildrenIds() {
        return childrenIds;
    }
}
