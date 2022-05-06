package com.obdasystems.sparqling.query.visitors;

import org.apache.jena.ext.com.google.common.base.Predicates;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.*;

import java.util.Iterator;
import java.util.Set;

public class DeleteElementVisitor extends ElementVisitorBase {
    private final Set<String> varToDelete;
    private String classIRI;
    private boolean shouldDeleteExpr = false;

    public DeleteElementVisitor(Set<String> varToDelete) {
        this.varToDelete = varToDelete;
    }

    public DeleteElementVisitor(Set<String> varToBeDeleted, String classIRI) {
        this.varToDelete = varToBeDeleted;
        this.classIRI = classIRI;
    }

    @Override
    public void visit(ElementPathBlock el) {
        Iterator<TriplePath> it = el.patternElts();
        while(it.hasNext()) {
            TriplePath tp = it.next();
            if (classIRI != null) {
                if (tp.getPredicate().getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
                    && tp.getObject().getURI().equals(classIRI)) {
                    it.remove();
                }
            }
            else if(tp.getSubject().isVariable() && varToDelete.contains(tp.getSubject().getName())
                || tp.getObject().isVariable() && varToDelete.contains(tp.getObject().getName())) {
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
            if(shouldDeleteExpr) {
                it.remove();
                shouldDeleteExpr = false;
            }
        }
    }

    @Override
    public void visit(ElementFilter elementFilter) {
        for(Var var :elementFilter.getExpr().getVarsMentioned()) {
            if(varToDelete.contains(var.getVarName())) {
                shouldDeleteExpr = true;
                break;
            }
        }
    }
}
