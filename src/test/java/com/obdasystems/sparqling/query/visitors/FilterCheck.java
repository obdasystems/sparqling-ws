package com.obdasystems.sparqling.query.visitors;

import org.apache.jena.sparql.syntax.*;

public class FilterCheck implements ElementVisitor {
    private boolean found;

    @Override
    public void visit(ElementTriplesBlock elementTriplesBlock) {

    }

    @Override
    public void visit(ElementPathBlock elementPathBlock) {

    }

    @Override
    public void visit(ElementFilter elementFilter) {
        found = true;
    }

    @Override
    public void visit(ElementAssign elementAssign) {

    }

    @Override
    public void visit(ElementBind elementBind) {

    }

    @Override
    public void visit(ElementData elementData) {

    }

    @Override
    public void visit(ElementUnion elementUnion) {

    }

    @Override
    public void visit(ElementOptional elementOptional) {

    }

    @Override
    public void visit(ElementGroup elementGroup) {
        for(Element el:elementGroup.getElements()) {
            el.visit(this);
        }
    }

    @Override
    public void visit(ElementDataset elementDataset) {

    }

    @Override
    public void visit(ElementNamedGraph elementNamedGraph) {

    }

    @Override
    public void visit(ElementExists elementExists) {

    }

    @Override
    public void visit(ElementNotExists elementNotExists) {

    }

    @Override
    public void visit(ElementMinus elementMinus) {

    }

    @Override
    public void visit(ElementService elementService) {

    }

    @Override
    public void visit(ElementSubQuery elementSubQuery) {

    }

    public boolean isFound() {
        return found;
    }
}
