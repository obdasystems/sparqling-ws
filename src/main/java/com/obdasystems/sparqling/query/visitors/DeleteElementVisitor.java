package com.obdasystems.sparqling.query.visitors;

import org.apache.jena.sparql.core.PathBlock;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.*;

import java.util.Iterator;

public class DeleteElementVisitor implements ElementVisitor {
    private final String varToDelete;

    public DeleteElementVisitor(String varToDelete) {
        this.varToDelete = varToDelete;
    }

    @Override
    public void visit(ElementTriplesBlock el) {

    }

    @Override
    public void visit(ElementPathBlock el) {
        Iterator<TriplePath> it = el.patternElts();
        while(it.hasNext()) {
            TriplePath tp = it.next();
            if(tp.getSubject().isVariable() && tp.getSubject().getName().equals(varToDelete)
                || tp.getObject().isVariable() && tp.getObject().getName().equals(varToDelete)) {
                it.remove();
            }
        }
    }

    @Override
    public void visit(ElementFilter el) {

    }

    @Override
    public void visit(ElementAssign el) {

    }

    @Override
    public void visit(ElementBind el) {

    }

    @Override
    public void visit(ElementData el) {

    }

    @Override
    public void visit(ElementUnion el) {

    }

    @Override
    public void visit(ElementOptional el) {

    }

    @Override
    public void visit(ElementGroup el) {

    }

    @Override
    public void visit(ElementDataset el) {

    }

    @Override
    public void visit(ElementNamedGraph el) {

    }

    @Override
    public void visit(ElementExists el) {

    }

    @Override
    public void visit(ElementNotExists el) {

    }

    @Override
    public void visit(ElementMinus el) {

    }

    @Override
    public void visit(ElementService el) {

    }

    @Override
    public void visit(ElementSubQuery el) {

    }
}