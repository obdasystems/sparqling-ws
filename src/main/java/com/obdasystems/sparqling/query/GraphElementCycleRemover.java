package com.obdasystems.sparqling.query;

import com.obdasystems.sparqling.model.Entity;
import com.obdasystems.sparqling.model.GraphElement;

import java.util.*;

public class GraphElementCycleRemover {
    private Set<String> visitedGraphElementIds;

    public GraphElementCycleRemover() {
        visitedGraphElementIds = new HashSet<>();
    }

    public void removeCycles(GraphElement ge) {
        visitedGraphElementIds.add(ge.getId());
        visit(ge);
    }

    private void visit(GraphElement ge) {
        if (ge.getChildren() != null) {
            Iterator<GraphElement> it = ge.getChildren().iterator();
            List<GraphElement> toAdd = new LinkedList<>();
            while (it.hasNext()) {
                GraphElement child = it.next();
                if (child.getEntities() != null && child.getEntities().get(0).getType() == Entity.TypeEnum.OBJECTPROPERTY) {
                    visit(child);
                } else if (visitedGraphElementIds.add(child.getId())) {
                    visit(child);
                } else {
                    it.remove();
                    GraphElement newChild = new GraphElement();
                    newChild.setId(child.getId());
                    newChild.setEntities(child.getEntities());
                    toAdd.add(newChild);
                }
            }
            ge.getChildren().addAll(toAdd);
        }

    }
}
