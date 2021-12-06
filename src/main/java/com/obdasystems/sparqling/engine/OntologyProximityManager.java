package com.obdasystems.sparqling.engine;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.*;

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
                    processDisjointClassesAxiom((OWLDisjointClassesAxiom) axiom);
                }
                else {
                    if(axiom instanceof OWLObjectPropertyDomainAxiom) {
                        processObjPropDomainAxiom((OWLObjectPropertyDomainAxiom) axiom);
                    }
                    else {
                        if(axiom instanceof OWLObjectPropertyRangeAxiom) {
                            processObjPropRangeAxiom((OWLObjectPropertyRangeAxiom) axiom);
                        }
                        else {
                            if(axiom instanceof OWLDataPropertyDomainAxiom) {
                                processDataPropDomainAxiom((OWLDataPropertyDomainAxiom) axiom);
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

    //CLASS INCLUSION
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
                            //Obj props domain-range
                            OWLObjectSomeValuesFrom objSomeVal = (OWLObjectSomeValuesFrom) sub;
                            if (objSomeVal.getFiller().isOWLThing()) {
                                if (sup instanceof OWLClass) {
                                    processObjPropDomainRangeFromContainment(sup.asOWLClass(), objSomeVal);
                                }else {
                                    if(sup instanceof OWLObjectUnionOf) {
                                        processObjPropDomainRangeFromContainment((OWLObjectUnionOf) sup, objSomeVal);
                                    }
                                }
                            }
                        } else {
                            if (sub instanceof OWLDataSomeValuesFrom) {
                                //Data prop domain
                                OWLDataSomeValuesFrom dataSomeVal = (OWLDataSomeValuesFrom) sub;
                                if ((dataSomeVal).getFiller().isTopDatatype()) {
                                    if (sup instanceof OWLClass) {
                                        processDataPropDomainRangeFromContainment(sup.asOWLClass(), dataSomeVal);
                                    }else {
                                        if(sup instanceof OWLObjectUnionOf) {
                                            processDataPropDomainRangeFromContainment((OWLObjectUnionOf) sup, dataSomeVal);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void processObjPropDomainRangeFromContainment(OWLObjectUnionOf sup, OWLObjectSomeValuesFrom objSomeVal) {
        sup.getOperands().forEach(operand-> {
            if(operand instanceof OWLClass) {
                processObjPropDomainRangeFromContainment(operand.asOWLClass(), objSomeVal);
            }
            else {
                if(operand instanceof OWLObjectUnionOf) {
                    processObjPropDomainRangeFromContainment((OWLObjectUnionOf) operand, objSomeVal);
                }
            }
        });
    }

    private void processObjPropDomainRangeFromContainment(OWLClass sup, OWLObjectSomeValuesFrom objSomeVal) {
        if ((objSomeVal).getFiller().isOWLThing()) {
            OWLObjectPropertyExpression prop = objSomeVal.getProperty();
            OWLObjectProperty named = prop.getNamedProperty();
            if (prop instanceof OWLObjectProperty) {
                objPropDomainMap.get(named).add((OWLClass) sup);
            } else {
                if (prop instanceof OWLObjectInverseOf) {
                    objPropRangeMap.get(named).add(sup);
                }
            }
        }
    }

    private void processDataPropDomainRangeFromContainment(OWLObjectUnionOf sup, OWLDataSomeValuesFrom dataSomeVal) {
        sup.getOperands().forEach(operand-> {
            if(operand instanceof OWLClass) {
                processDataPropDomainRangeFromContainment(operand.asOWLClass(), dataSomeVal);
            }
            else {
                if(operand instanceof OWLObjectUnionOf) {
                    processDataPropDomainRangeFromContainment((OWLObjectUnionOf) operand, dataSomeVal);
                }
            }
        });
    }

    private void processDataPropDomainRangeFromContainment(OWLClass sup, OWLDataSomeValuesFrom dataSomeVal) {
        if ((dataSomeVal).getFiller().isTopDatatype()) {
            OWLDataPropertyExpression prop = dataSomeVal.getProperty();
            if (sup instanceof OWLClass) {
                dataPropDomainMap.get(prop.asOWLDataProperty()).add(sup);
            }
        }
    }

    private void processClassComplementOf(OWLObjectComplementOf sup, OWLObjectUnionOf sub) {
        sub.getOperands().forEach(operand-> {
            if(operand instanceof OWLClass) {
                processClassComplementOf(sup, (OWLClass) operand);
            }
            else {
                if(operand instanceof OWLObjectUnionOf) {
                    processClassComplementOf(sup, (OWLObjectUnionOf) operand);
                }
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
            else {
                if(operand instanceof OWLObjectUnionOf) {
                    processClassContainment(sup, (OWLObjectUnionOf) operand);
                }
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

    //CLASS DISJOINTNESS
    private void processDisjointClassesAxiom(OWLDisjointClassesAxiom axiom) {
        List<OWLClassExpression> operandList = axiom.getClassExpressionsAsList();
        for(int i=0;i<operandList.size()-1;i++) {
            OWLClassExpression first = operandList.get(i);
            if(first instanceof OWLClass) {
                for(int j=i+1;j<operandList.size();j++) {
                    OWLClassExpression second = operandList.get(j);
                    if(second instanceof OWLClass) {
                        classDisjointMap.get(first.asOWLClass()).add(second.asOWLClass());
                        classDisjointMap.get(second.asOWLClass()).add(first.asOWLClass());
                    }
                }
            }
        }
    }

    //PROP DOMAIN-RANGE
    private void processObjPropDomainAxiom(OWLObjectPropertyDomainAxiom axiom) {
        OWLObjectPropertyExpression prop = axiom.getProperty();
        OWLClassExpression domainExpr = axiom.getDomain();
        if(domainExpr instanceof OWLClass) {
            if(prop instanceof OWLObjectInverseOf) {
                processObjPropRange(domainExpr.asOWLClass(), ((OWLObjectInverseOf) prop).getInverse().asOWLObjectProperty());
            }
            else {
                processObjPropDomain(domainExpr.asOWLClass(), prop.asOWLObjectProperty());
            }
        }
        else {
            if(domainExpr instanceof OWLObjectUnionOf) {
                if(prop instanceof OWLObjectInverseOf) {
                    processObjPropRange((OWLObjectUnionOf)domainExpr, ((OWLObjectInverseOf) prop).getInverse().asOWLObjectProperty());
                }
                else {
                    processObjPropDomain((OWLObjectUnionOf)domainExpr, prop.asOWLObjectProperty());
                }
            }
        }
    }

    private void processObjPropDomain(OWLObjectUnionOf union, OWLObjectProperty prop) {
        union.getOperands().forEach(clExpr->{
            if(clExpr instanceof OWLClass) {
                processObjPropDomain(clExpr.asOWLClass(), prop);
            }
            else {
                if(clExpr instanceof OWLObjectUnionOf) { {
                    processObjPropDomain((OWLObjectUnionOf) clExpr, prop);
                }}
            }
        });
    }

    private void processObjPropDomain(OWLClass cl, OWLObjectProperty prop) {
        objPropDomainMap.get(prop).add(cl);
    }

    private void processObjPropRangeAxiom(OWLObjectPropertyRangeAxiom axiom) {
        OWLObjectPropertyExpression prop = axiom.getProperty();
        OWLClassExpression rangeExpr = axiom.getRange();
        if(rangeExpr instanceof OWLClass) {
            if(prop instanceof OWLObjectInverseOf) {
                processObjPropDomain(rangeExpr.asOWLClass(), ((OWLObjectInverseOf) prop).getInverse().asOWLObjectProperty());
            }
            else {
                processObjPropRange(rangeExpr.asOWLClass(), prop.asOWLObjectProperty());
            }
        }
        else {
            if(rangeExpr instanceof OWLObjectUnionOf) {
                if(prop instanceof OWLObjectInverseOf) {
                    processObjPropDomain((OWLObjectUnionOf)rangeExpr, ((OWLObjectInverseOf) prop).getInverse().asOWLObjectProperty());
                }
                else {
                    processObjPropRange((OWLObjectUnionOf)rangeExpr, prop.asOWLObjectProperty());
                }
            }
        }
    }

    private void processObjPropRange(OWLObjectUnionOf union, OWLObjectProperty prop) {
        union.getOperands().forEach(clExpr->{
            if(clExpr instanceof OWLClass) {
                processObjPropRange(clExpr.asOWLClass(), prop);
            }
            else {
                if(clExpr instanceof OWLObjectUnionOf) { {
                    processObjPropRange((OWLObjectUnionOf) clExpr, prop);
                }}
            }
        });
    }

    private void processObjPropRange(OWLClass cl, OWLObjectProperty prop) {
        objPropRangeMap.get(prop).add(cl);
    }


    private void processDataPropDomainAxiom(OWLDataPropertyDomainAxiom axiom) {
        OWLDataPropertyExpression prop = axiom.getProperty();
        OWLClassExpression domainExpr = axiom.getDomain();
        if(domainExpr instanceof OWLClass) {
            processDataPropDomain(domainExpr.asOWLClass(), prop.asOWLDataProperty());

        }
        else {
            if(domainExpr instanceof OWLObjectUnionOf) {
                processDataPropDomain((OWLObjectUnionOf)domainExpr, prop.asOWLDataProperty());
            }
        }
    }

    private void processDataPropDomain(OWLObjectUnionOf union, OWLDataProperty prop) {
        union.getOperands().forEach(clExpr->{
            if(clExpr instanceof OWLClass) {
                processDataPropDomain(clExpr.asOWLClass(), prop);
            }
            else {
                if(clExpr instanceof OWLObjectUnionOf) { {
                    processDataPropDomain((OWLObjectUnionOf) clExpr, prop);
                }}
            }
        });
    }

    private void processDataPropDomain(OWLClass cl, OWLDataProperty prop) {
        dataPropDomainMap.get(prop).add(cl);
    }
}
