package com.obdasystems.sparqling.query.visitors;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.*;

import java.util.Iterator;
import java.util.Set;

public class DeleteElementVisitorByTriple extends ElementVisitorBase {
    private final Triple triple;

    public DeleteElementVisitorByTriple(Triple triple) {
        this.triple = triple;
    }

    @Override
    public void visit(ElementPathBlock el) {
        Iterator<TriplePath> it = el.patternElts();
        while(it.hasNext()) {
            TriplePath tp = it.next();
            if (tp.asTriple().equals(triple)) {
                it.remove();
            }
        }
    }

    @Override
    public void visit(ElementGroup elementGroup) {
        Iterator<Element> it = elementGroup.getElements().iterator();
        while(it.hasNext()) {
            Element el = it.next();
            el.visit(this);
        }
    }
}
