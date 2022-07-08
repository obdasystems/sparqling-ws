package com.obdasystems.sparqling.query.visitors;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteElementVisitorOptional extends ElementVisitorBase {
    private List<TriplePath> triplesToMove;
    private String graphElementId;
    private String classIRI;
    List<TriplePath> triplePaths;
    private boolean toRemove;

    public DeleteElementVisitorOptional(List<Triple> triplesToMove) {
        this();
        this.triplesToMove = triplesToMove.stream().map(t -> new TriplePath(t)).collect(Collectors.toList());
    }
    
    public DeleteElementVisitorOptional() {
        triplePaths = new LinkedList<>();
    }

    @Override
    public void visit(ElementGroup elementGroup) {
        Iterator<Element> it = elementGroup.getElements().iterator();
        while(it.hasNext()) {
            Element el = it.next();
            el.visit(this);
            if(el instanceof ElementOptional) {
                if (triplesToMove != null) {
                    if (toRemove) {
                        it.remove();
                        toRemove = false;
                    }
                } else {
                    it.remove();
                }
            }
        }
    }

    @Override
    public void visit(ElementOptional el) {
        super.visit(el);
        if (el.getOptionalElement() instanceof ElementGroup) {
            ElementGroup eg = (ElementGroup) el.getOptionalElement();
            for (Element e:eg.getElements()) {
                if (e instanceof ElementPathBlock) {
                    ElementPathBlock epb = (ElementPathBlock) e;
                    Iterator<TriplePath> it = epb.patternElts();
                    while(it.hasNext()) {
                        TriplePath tp = it.next();
                        if (triplesToMove != null) {
                            if(triplesToMove.contains(tp)) {
                                toRemove = true;
                                triplePaths.add(tp);
                            }
                        } else {
                            triplePaths.add(tp);
                        }
                    }
                }
            }
        }
    }

    public List<TriplePath> getTriplePaths() {
        return triplePaths;
    }
}
