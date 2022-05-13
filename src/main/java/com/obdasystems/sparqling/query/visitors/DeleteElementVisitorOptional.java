package com.obdasystems.sparqling.query.visitors;

import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DeleteElementVisitorOptional extends ElementVisitorBase {
    private String graphElementId;
    private String classIRI;
    List<TriplePath> triplePaths;
    public DeleteElementVisitorOptional() {
        triplePaths = new LinkedList<>();
    }

    public DeleteElementVisitorOptional(String graphElementId, String classIRI) {
        this();
        this.graphElementId = graphElementId;
        this.classIRI = classIRI;
    }

    @Override
    public void visit(ElementGroup elementGroup) {
        Iterator<Element> it = elementGroup.getElements().iterator();
        while(it.hasNext()) {
            Element el = it.next();
            el.visit(this);
            if(el instanceof ElementOptional) {
                it.remove();
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
                        triplePaths.add(it.next());
                    }
                }
            }
        }
    }

    public List<TriplePath> getTriplePaths() {
        return triplePaths;
    }
}
