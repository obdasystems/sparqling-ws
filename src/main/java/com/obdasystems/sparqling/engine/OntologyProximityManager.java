package com.obdasystems.sparqling.engine;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OntologyProximityManager {

    private OWLOntology ontology;
    private OWLSignature signature;

    //CLASSES
    private Map<OWLClass, Set<OWLClass>> classChildrenMap;
    private Map<OWLClass, Set<OWLClass>> classDescendantsMap;
    private Map<OWLClass, Set<OWLClass>> classFathersMap;
    private Map<OWLClass, Set<OWLClass>> classAncestorsMap;
    private Map<OWLClass, Set<OWLClass>> classSiblingsMap;
    private Map<OWLClass, Set<OWLClass>> classDisjointMap;
    private Map<OWLClass, Set<OWLObjectProperty>> classRoles;
    private Map<OWLClass, Set<OWLDataProperty>> classAttributes;

    //OBJ PROPS
    private Map<OWLObjectProperty, Set<OWLClass>> objPropDomainMap;
    private Map<OWLObjectProperty, Set<OWLClass>> objPropRangeMap;
    private Map<OWLObjectProperty, Set<OWLObjectProperty>> objPropChildrenMap;
    private Map<OWLObjectProperty, Set<OWLObjectProperty>> objPropAncestorsMap;

    //DATA PROPS
    private Map<OWLDataProperty, Set<OWLClass>> dataPropDomainMap;
    private Map<OWLDataProperty, Set<OWLDataProperty>> dataPropChildrenMap;
    private Map<OWLDataProperty, Set<OWLDataProperty>> dataPropAncestorsMap;


    public OntologyProximityManager(OWLOntology ontology) {
        this.ontology=ontology;
        this.classChildrenMap = new HashMap<>();
        this.classDescendantsMap = new HashMap<>();
        this.classFathersMap = new HashMap<>();
        this.classAncestorsMap = new HashMap<>();
        this.classSiblingsMap = new HashMap<>();
        this.classDisjointMap =new HashMap<>();
        this.classRoles = new HashMap<>();
        this.classAttributes = new HashMap<>();
        this.objPropDomainMap = new HashMap<>();
        this.objPropRangeMap = new HashMap<>();
        this.dataPropDomainMap = new HashMap<>();
        this.objPropChildrenMap = new HashMap<>();
        this.objPropAncestorsMap = new HashMap<>();
        this.dataPropChildrenMap = new HashMap<>();
        this.dataPropAncestorsMap = new HashMap<>();
    }

    public void run(){
        processSignature();
        ontology.getTBoxAxioms(Imports.INCLUDED).forEach(axiom->{
            if(axiom instanceof OWLSubClassOfAxiom) {
                processSubClassAxiom((OWLSubClassOfAxiom) axiom);
            }
            else {
                if(axiom instanceof OWLDisjointClassesAxiom) {

                }
                else {
                    if(axiom instanceof OWLObjectPropertyDomainAxiom) {

                    }
                    else {
                        if(axiom instanceof OWLObjectPropertyRangeAxiom) {

                        }
                        else {
                            if(axiom instanceof OWLDataPropertyDomainAxiom) {

                            }
                        }
                    }
                }
            }
        });

    }

    private void processSignature() {
        ontology.getClassesInSignature(Imports.INCLUDED).forEach(cl->{
            classChildrenMap.put(cl, new HashSet<>());
            classDescendantsMap.put(cl, new HashSet<>());
            classFathersMap.put(cl, new HashSet<>());
            classAncestorsMap.put(cl, new HashSet<>());
            classDisjointMap.put(cl, new HashSet<>());
            classSiblingsMap.put(cl, new HashSet<>());
            classRoles.put(cl, new HashSet<>());
            classAttributes.put(cl, new HashSet<>());
        });
        ontology.getObjectPropertiesInSignature(Imports.INCLUDED).forEach(objProp->{
            objPropChildrenMap.put(objProp, new HashSet<>());
            objPropAncestorsMap.put(objProp, new HashSet<>());
        });
        ontology.getDataPropertiesInSignature(Imports.INCLUDED).forEach(dataProp->{
            dataPropChildrenMap.put(dataProp, new HashSet<>());
            dataPropAncestorsMap.put(dataProp, new HashSet<>());
        });
    }

    //CLASSES
    private void processSubClassAxiom(OWLSubClassOfAxiom axiom) {
        OWLClassExpression sub = axiom.getSubClass();
        OWLClassExpression sup = axiom.getSuperClass();
        if(isAcceptedSuperClassExpression(sup) && !(sup.isOWLThing()||sup.isOWLNothing())) {
            if(sup instanceof OWLObjectComplementOf) {
                //DISJOINTNESS
                if (sub instanceof OWLClass) {
                    processClassComplementOf((OWLObjectComplementOf) sup, (OWLClass) sub);
                } else {
                    if (sub instanceof OWLObjectUnionOf) {
                        processClassComplementOf((OWLObjectComplementOf) sup, (OWLObjectUnionOf) sub);
                    }
                }
            }
            else {
                //NORMAL SUBSUMPTION
                if (sub instanceof OWLClass) {
                    processClassContainment(sup, (OWLClass) sub);
                } else {
                    if (sub instanceof OWLObjectUnionOf) {
                        processClassContainment(sup, (OWLObjectUnionOf) sub);
                    } else {
                        if (sub instanceof OWLObjectSomeValuesFrom) {
                            OWLObjectSomeValuesFrom objSomeVal = (OWLObjectSomeValuesFrom) sub;
                            if ((objSomeVal).getFiller().isOWLThing()) {
                                if (sup instanceof OWLClass) {
                                    OWLObjectPropertyExpression prop = objSomeVal.getProperty();
                                    OWLObjectProperty named = prop.getNamedProperty();
                                    if (prop instanceof OWLObjectProperty) {
                                        objPropDomainMap.get(named).add((OWLClass) sup);
                                    } else {
                                        if (prop instanceof OWLObjectInverseOf) {
                                            objPropRangeMap.get(named).add((OWLClass) sup);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (sub instanceof OWLDataSomeValuesFrom) {
                                OWLDataSomeValuesFrom dataSomeVal = (OWLDataSomeValuesFrom) sub;
                                if ((dataSomeVal).getFiller().isTopDatatype()) {
                                    OWLDataPropertyExpression prop = dataSomeVal.getProperty();
                                    if (sup instanceof OWLClass) {
                                        dataPropDomainMap.get(prop.asOWLDataProperty()).add((OWLClass) sup);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void processClassComplementOf(OWLObjectComplementOf sup, OWLObjectUnionOf sub) {
        sub.getOperands().forEach(operand-> {
            if(operand instanceof OWLClass) {
                processClassComplementOf(sup, (OWLClass) operand);
            }
        });
    }

    private void processClassComplementOf(OWLObjectComplementOf sup, OWLClass sub) {
        OWLClassExpression disjExpr = ((OWLObjectComplementOf) sup).getOperand();
        if(disjExpr instanceof OWLClass) {
            classDisjointMap.get(sub).add((OWLClass) disjExpr);
        }
        else {
            if(disjExpr instanceof OWLObjectUnionOf) {
                ((OWLObjectUnionOf) disjExpr).getOperands().forEach(innDisj->{
                    if(innDisj instanceof OWLClass) {
                        classDisjointMap.get(sub).add((OWLClass) innDisj);
                    }
                });
            }
        }
    }

    private void processClassContainment(OWLClassExpression sup, OWLObjectUnionOf sub) {
        sub.getOperands().forEach(operand-> {
            if(operand instanceof OWLClass) {
                processClassContainment(sup, (OWLClass) operand);
            }
        });
    }

    private void processClassContainment(OWLClassExpression sup, OWLClass sub) {
        if(sup instanceof OWLClass) {
            classFathersMap.get(sub).add((OWLClass) sup);
            classAncestorsMap.get(sub).add((OWLClass) sup);
            classChildrenMap.get(sup).add(sub);
            classDescendantsMap.get(sup).add(sub);
        }
        else {
            if(sup instanceof OWLObjectSomeValuesFrom) {
                OWLObjectProperty prop = (OWLObjectProperty) ((OWLObjectSomeValuesFrom) sup).getProperty();
                classRoles.get(sub).add(prop);
            }
            else {
                if(sup instanceof OWLDataSomeValuesFrom) {
                    OWLDataProperty prop = (OWLDataProperty) ((OWLDataSomeValuesFrom) sup).getProperty();
                    classAttributes.get(sub).add(prop);
                }
            }
        }
    }

    private boolean isAcceptedSuperClassExpression(OWLClassExpression expr) {
        if(expr instanceof  OWLClass) {
            return true;
        }
        if(expr instanceof OWLObjectSomeValuesFrom) {
            return true;
        }
        if(expr instanceof OWLDataSomeValuesFrom) {
            return true;
        }
        if(expr instanceof OWLObjectComplementOf) {
            OWLClassExpression disjExpr = ((OWLObjectComplementOf) expr).getOperand();
            if(disjExpr instanceof OWLClass || disjExpr instanceof OWLObjectUnionOf) {
                return true;
            }
        }
        return false;
    }



}
