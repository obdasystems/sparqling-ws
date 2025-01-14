package com.obdasystems.sparqling.engine;

import com.obdasystems.sparqling.model.Branch;
import com.obdasystems.sparqling.model.Highlights;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import java.util.*;
import java.util.stream.Collectors;

public class OntologyProximityManager {

    private OWLOntology ontology;

    //CLASSES
    //private Map<OWLClass, Set<OWLClass>> classChildrenMap;
    private Map<OWLClass, Set<OWLClass>> classDescendantsMap;
    private Map<OWLClass, Set<OWLClass>> classFathersMap;
    private Map<OWLClass, Set<OWLClass>> classAncestorsMap;
    private Map<OWLClass, Set<OWLClass>> classNonDisjointSiblingsMap;
    private Map<OWLClass, Set<OWLClass>> classDisjointMap;
    private Map<OWLClass, Set<OWLObjectProperty>> classRolesMap;
    private Map<OWLClass, Set<OWLDataProperty>> classAttributesMap;

    //OBJ PROPS
    private Map<OWLObjectProperty, Set<OWLClass>> objPropDomainMap;
    private Map<OWLObjectProperty, Set<OWLClass>> objPropRangeMap;
    private Map<OWLObjectProperty, Set<OWLClass>> objPropMandPartDomainMap;
    private Map<OWLObjectProperty, Set<OWLClass>> objPropMandPartRangeMap;
    private Map<OWLObjectProperty, Set<OWLObjectProperty>> objPropChildrenMap;
    private Map<OWLObjectProperty, Set<OWLObjectProperty>> objPropAncestorsMap;

    //DATA PROPS
    private Map<OWLDataProperty, Set<OWLClass>> dataPropDomainMap;
    private Map<OWLDataProperty, Set<OWLDataProperty>> dataPropChildrenMap;
    private Map<OWLDataProperty, Set<OWLDataProperty>> dataPropAncestorsMap;

    private SimpleOwlOntologyDeductiveClosureProcesor dedProc;
    private Set<OWLAxiom> simpleDedClos;

    public OntologyProximityManager(OWLOntology ontology) {
        this.ontology = ontology;
        //this.classChildrenMap = new HashMap<>();
        this.classDescendantsMap = new HashMap<>();
        this.classFathersMap = new HashMap<>();
        this.classAncestorsMap = new HashMap<>();
        this.classNonDisjointSiblingsMap = new HashMap<>();
        this.classDisjointMap = new HashMap<>();
        this.classRolesMap = new HashMap<>();
        this.classAttributesMap = new HashMap<>();
        this.objPropDomainMap = new HashMap<>();
        this.objPropRangeMap = new HashMap<>();
        this.objPropMandPartDomainMap = new HashMap<>();
        this.objPropMandPartRangeMap = new HashMap<>();
        this.dataPropDomainMap = new HashMap<>();
        this.objPropChildrenMap = new HashMap<>();
        this.objPropAncestorsMap = new HashMap<>();
        this.dataPropChildrenMap = new HashMap<>();
        this.dataPropAncestorsMap = new HashMap<>();
        init();
    }

    public Set<OWLAxiom> getSimpleDeductiveClosure() {
        return simpleDedClos.stream().collect(Collectors.toSet());
    }

    //GETTERS
    public Set<OWLClass> getClassDescendants(OWLClass cl) {
        Set<OWLClass> ret = classDescendantsMap.get(cl);
        if (ret == null) throw new RuntimeException("Cannot find class " + cl.getIRI().toQuotedString());
        return ret;
    }

    public Set<OWLClass> getClassFathers(OWLClass cl) {
        return classFathersMap.get(cl);
    }

    public Set<OWLClass> getClassAncestors(OWLClass cl) {
        return classAncestorsMap.get(cl);
    }

    public Set<OWLClass> getClassNonDisjointSiblings(OWLClass cl) {
        return classNonDisjointSiblingsMap.get(cl);
    }

    public Set<OWLClass> getClassDisjoint(OWLClass cl) {
        return classDisjointMap.get(cl);
    }

    public Set<OWLObjectProperty> getClassRoles(OWLClass cl) {
        return classRolesMap.get(cl);
    }

    public Set<OWLDataProperty> getClassAttributes(OWLClass cl) {
        return classAttributesMap.get(cl);
    }

    public Set<OWLClass> getObjPropDomain(OWLObjectProperty prop) {
        return objPropDomainMap.get(prop);
    }

    public Set<OWLClass> getObjPropRange(OWLObjectProperty prop) {
        return objPropRangeMap.get(prop);
    }

    public Set<OWLClass> getObjPropMandPartDomain(OWLObjectProperty prop) {
        return objPropMandPartDomainMap.get(prop);
    }

    public Set<OWLClass> getObjPropMandPartRange(OWLObjectProperty prop) {
        return objPropMandPartRangeMap.get(prop);
    }

    public Set<OWLObjectProperty> getObjPropChildren(OWLObjectProperty prop) {
        return objPropChildrenMap.get(prop);
    }

    public Set<OWLObjectProperty> getObjPropAncestors(OWLObjectProperty prop) {
        return objPropAncestorsMap.get(prop);
    }

    public Set<OWLClass> getDataPropDomainMap(OWLDataProperty prop) {
        return dataPropDomainMap.get(prop);
    }

    public Set<OWLDataProperty> getDataPropChildrenMap(OWLDataProperty prop) {
        return dataPropChildrenMap.get(prop);
    }

    public Set<OWLDataProperty> getDataPropAncestorsMap(OWLDataProperty prop) {
        return dataPropAncestorsMap.get(prop);
    }

    //PROCESSOR
    public void init() {
        processSignature();
        ontology.getTBoxAxioms(Imports.INCLUDED).forEach(axiom -> {
            processAxiom(axiom, true);
        });
        dedProc = new SimpleOwlOntologyDeductiveClosureProcesor(ontology);
        simpleDedClos = dedProc.computeSimpleDeductiveClosure();
        simpleDedClos.forEach(axiom -> {
            processAxiom(axiom, false);
        });
        computeDisjointSiblingsMap();
        closeClassRolesAndAttributesMaps();
    }

    private void processSignature() {
        ontology.getClassesInSignature(Imports.INCLUDED).forEach(cl -> {
            //classChildrenMap.put(cl, new HashSet<>());
            classDescendantsMap.put(cl, new HashSet<>());
            classFathersMap.put(cl, new HashSet<>());
            classAncestorsMap.put(cl, new HashSet<>());
            classDisjointMap.put(cl, new HashSet<>());
            classNonDisjointSiblingsMap.put(cl, new HashSet<>());
            classRolesMap.put(cl, new HashSet<>());
            classAttributesMap.put(cl, new HashSet<>());
        });
        ontology.getObjectPropertiesInSignature(Imports.INCLUDED).forEach(objProp -> {
            objPropChildrenMap.put(objProp, new HashSet<>());
            objPropAncestorsMap.put(objProp, new HashSet<>());
            objPropDomainMap.put(objProp, new HashSet<>());
            objPropRangeMap.put(objProp, new HashSet<>());
            objPropMandPartDomainMap.put(objProp, new HashSet<>());
            objPropMandPartRangeMap.put(objProp, new HashSet<>());
        });
        ontology.getDataPropertiesInSignature(Imports.INCLUDED).forEach(dataProp -> {
            dataPropChildrenMap.put(dataProp, new HashSet<>());
            dataPropAncestorsMap.put(dataProp, new HashSet<>());
            dataPropDomainMap.put(dataProp, new HashSet<>());
        });
    }

    private void processAxiom(OWLAxiom axiom, boolean isAsserted) {
        if (axiom instanceof OWLSubClassOfAxiom) {
            processSubClassAxiom((OWLSubClassOfAxiom) axiom, isAsserted);
        } else {
            if (axiom instanceof OWLDisjointClassesAxiom) {
                processDisjointClassesAxiom((OWLDisjointClassesAxiom) axiom);
            } else {
                if (axiom instanceof OWLObjectPropertyDomainAxiom) {
                    processObjPropDomainAxiom((OWLObjectPropertyDomainAxiom) axiom);
                } else {
                    if (axiom instanceof OWLObjectPropertyRangeAxiom) {
                        processObjPropRangeAxiom((OWLObjectPropertyRangeAxiom) axiom);
                    } else {
                        if (axiom instanceof OWLDataPropertyDomainAxiom) {
                            processDataPropDomainAxiom((OWLDataPropertyDomainAxiom) axiom);
                        } else {
                            if (axiom instanceof OWLDisjointUnionAxiom) {
                                processDisjointUnionAxiom((OWLDisjointUnionAxiom) axiom);
                            }
                        }
                    }
                }
            }
        }
    }

    private void closeClassRolesAndAttributesMaps() {
        Set<OWLClass> tops = getClassesWithNoFather();
        tops.forEach(t->{
            Set<OWLObjectProperty> tRoles = classRolesMap.get(t);
            Set<OWLDataProperty> tAttrs = classAttributesMap.get(t);
            classDescendantsMap.get(t).forEach(desc->{
                Set<OWLClass> descDisj = classDisjointMap.get(desc);
                Set<OWLObjectProperty> descRole = classRolesMap.get(desc);
                Set<OWLDataProperty> descAttr = classAttributesMap.get(desc);

                tAttrs.forEach(tAttr->{
                    Set<OWLClass> tAttrDomSet = dataPropDomainMap.get(tAttr);
                    if(!intersect(tAttrDomSet, descDisj)) {
                        descAttr.add(tAttr);
                    }
                });

                tRoles.forEach(tRole->{
                    boolean goOn = true;
                    Set<OWLClass> tRoleDomSet = objPropDomainMap.get(tRole);
                    if(tRoleDomSet.contains(t)) {
                        if(!intersect(tRoleDomSet, descDisj)) {
                            descRole.add(tRole);
                            goOn = false;
                        }
                    }
                    if(goOn) {
                        Set<OWLClass> tRoleRanSet = objPropRangeMap.get(tRole);
                        if (tRoleRanSet.contains(t)) {
                            if(!intersect(tRoleRanSet, descDisj)) {
                                descRole.add(tRole);
                            }
                        }
                    }
                });
            });
        });
    }

    private boolean intersect(Set first, Set second) {
        return first.stream().anyMatch(second::contains);
    }



    private Set<OWLClass> getClassesWithNoFather() {
        Set<OWLClass> result = new HashSet<>();
        ontology.getClassesInSignature(Imports.INCLUDED).forEach(cl->{
            Set<OWLClass> clFathers = classFathersMap.get(cl);
            if(clFathers.isEmpty()) {
                result.add(cl);
            }
            else {
                boolean add = true;
                for(OWLClass other:clFathers) {
                    if(!(other.isOWLThing() || other.isOWLNothing())) {
                        Set<OWLClass> otherFathers = classFathersMap.get(other);
                        if(!otherFathers.contains(cl)) {
                            add = false;
                            break;
                        }
                    }
                }
                if(add) {
                    result.add(cl);
                }
            }
        });
        return result;
    }

    private void computeDisjointSiblingsMap() {
        List<OWLClass> classSigList = new LinkedList<>(ontology.getClassesInSignature(Imports.INCLUDED));
        for (int i = 0; i < classSigList.size() - 1; i++) {
            OWLClass first = classSigList.get(i);
            Set<OWLClass> firstDisjSet = classDisjointMap.get(first);
            Set<OWLClass> firstFatSet = classFathersMap.get(first);
            for (int j = i + 1; j < classSigList.size(); j++) {
                OWLClass second = classSigList.get(j);
                Set<OWLClass> secondDisjSet = classDisjointMap.get(second);
                if (!(firstDisjSet.contains(second) || secondDisjSet.contains(first))) {
                    classFathersMap.get(second).forEach(fatCl -> {
                        if (firstFatSet.contains(fatCl)) {
                            classNonDisjointSiblingsMap.get(first).add(second);
                            classNonDisjointSiblingsMap.get(second).add(first);
                        }
                    });
                }
            }
        }
    }

    //CLASS INCLUSION
    private boolean isAcceptedSuperClassExpression(OWLClassExpression expr) {
        if (expr instanceof OWLClass) {
            return true;
        }
        if (expr instanceof OWLObjectSomeValuesFrom) {
            return true;
        }
        if (expr instanceof OWLObjectAllValuesFrom) {
            return true;
        }
        if (expr instanceof OWLDataSomeValuesFrom) {
            return true;
        }
        if (expr instanceof OWLObjectComplementOf) {
            OWLClassExpression disjExpr = ((OWLObjectComplementOf) expr).getOperand();
            if (disjExpr instanceof OWLClass || disjExpr instanceof OWLObjectUnionOf) {
                return true;
            }
        }
        return false;
    }

    private void processSubClassAxiom(OWLSubClassOfAxiom axiom, boolean isAsserted) {
        OWLClassExpression sub = axiom.getSubClass();
        OWLClassExpression sup = axiom.getSuperClass();
        if (isAcceptedSuperClassExpression(sup) && !(sup.isOWLThing() || sup.isOWLNothing())) {
            if (sup instanceof OWLObjectComplementOf) {
                //DISJOINTNESS
                if (sub instanceof OWLClass) {
                    processClassComplementOf((OWLObjectComplementOf) sup, (OWLClass) sub);
                } else {
                    if (sub instanceof OWLObjectUnionOf) {
                        processClassComplementOf((OWLObjectComplementOf) sup, (OWLObjectUnionOf) sub);
                    }
                }
            } else {
                //STANDARD SUBSUMPTION
                if (sub instanceof OWLClass) {
                    processClassContainment(sup, (OWLClass) sub, isAsserted);
                } else {
                    if (sub instanceof OWLObjectUnionOf) {
                        processClassContainment(sup, (OWLObjectUnionOf) sub, isAsserted);
                    } else {
                        if (sub instanceof OWLObjectSomeValuesFrom) {
                            //Obj props domain-range
                            OWLObjectSomeValuesFrom objSomeVal = (OWLObjectSomeValuesFrom) sub;
                            if (objSomeVal.getFiller().isOWLThing()) {
                                if (sup instanceof OWLClass) {
                                    processObjPropDomainRangeFromContainment(sup.asOWLClass(), objSomeVal);
                                } else {
                                    if (sup instanceof OWLObjectUnionOf) {
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
                                    } else {
                                        if (sup instanceof OWLObjectUnionOf) {
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
        sup.getOperands().forEach(operand -> {
            if (operand instanceof OWLClass) {
                processObjPropDomainRangeFromContainment(operand.asOWLClass(), objSomeVal);
            } else {
                if (operand instanceof OWLObjectUnionOf) {
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
                objPropDomainMap.get(named).add(sup);
                classRolesMap.get(sup).add(named);
            } else {
                if (prop instanceof OWLObjectInverseOf) {
                    objPropRangeMap.get(named).add(sup);
                    classRolesMap.get(sup).add(named);
                }
            }
        }
    }

    private void processDataPropDomainRangeFromContainment(OWLObjectUnionOf sup, OWLDataSomeValuesFrom dataSomeVal) {
        sup.getOperands().forEach(operand -> {
            if (operand instanceof OWLClass) {
                processDataPropDomainRangeFromContainment(operand.asOWLClass(), dataSomeVal);
            } else {
                if (operand instanceof OWLObjectUnionOf) {
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
                classAttributesMap.get(sup).add(prop.asOWLDataProperty());
            }
        }
    }

    private void processClassComplementOf(OWLObjectComplementOf sup, OWLObjectUnionOf sub) {
        sub.getOperands().forEach(operand -> {
            if (operand instanceof OWLClass) {
                processClassComplementOf(sup, (OWLClass) operand);
            } else {
                if (operand instanceof OWLObjectUnionOf) {
                    processClassComplementOf(sup, (OWLObjectUnionOf) operand);
                }
            }
        });
    }

    private void processClassComplementOf(OWLObjectComplementOf sup, OWLClass sub) {
        OWLClassExpression disjExpr =  sup.getOperand();
        if (disjExpr instanceof OWLClass) {
            classDisjointMap.get(sub).add((OWLClass) disjExpr);
        } else {
            if (disjExpr instanceof OWLObjectUnionOf) {
                ((OWLObjectUnionOf) disjExpr).getOperands().forEach(innDisj -> {
                    if (innDisj instanceof OWLClass) {
                        classDisjointMap.get(sub).add((OWLClass) innDisj);
                    }
                });
            }
        }
    }

    private void processClassContainment(OWLClassExpression sup, OWLObjectUnionOf sub, boolean isAsserted) {
        sub.getOperands().forEach(operand -> {
            if (operand instanceof OWLClass) {
                processClassContainment(sup, (OWLClass) operand, isAsserted);
            } else {
                if (operand instanceof OWLObjectUnionOf) {
                    processClassContainment(sup, (OWLObjectUnionOf) operand, isAsserted);
                }
            }
        });
    }

    private void processClassContainment(OWLClassExpression sup, OWLClass sub, boolean isAsserted) {
        if (sup instanceof OWLClass) {
            if (isAsserted) {
                classFathersMap.get(sub).add((OWLClass) sup);
                //classChildrenMap.get(sup).add(sub);
            }
            classAncestorsMap.get(sub).add((OWLClass) sup);
            classDescendantsMap.get(sup).add(sub);
        } else {
            if (sup instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom objSomeVal = (OWLObjectSomeValuesFrom) sup;
                OWLObjectPropertyExpression p = objSomeVal.getProperty();
                classRolesMap.get(sub).add(p.getNamedProperty());
                if (p instanceof OWLObjectProperty) {
                    objPropMandPartDomainMap.get(p.getNamedProperty()).add(sub);
                } else if (p instanceof OWLObjectInverseOf) {
                    objPropMandPartRangeMap.get(p.getNamedProperty()).add(sub);
                }
            } else if (sup instanceof OWLObjectAllValuesFrom) {
                OWLObjectPropertyExpression p = ((OWLObjectAllValuesFrom) sup).getProperty();
                classRolesMap.get(sub).add(p.getNamedProperty());
                if (p instanceof OWLObjectProperty) {
                    objPropMandPartDomainMap.get(p.getNamedProperty()).add(sub);
                } else if (p instanceof OWLObjectInverseOf) {
                    objPropMandPartRangeMap.get(p.getNamedProperty()).add(sub);
                }
            } else {
                if (sup instanceof OWLDataSomeValuesFrom) {
                    OWLDataProperty prop = (OWLDataProperty) ((OWLDataSomeValuesFrom) sup).getProperty();
                    classAttributesMap.get(sub).add(prop);
                }
            }
        }
    }

    //CLASS DISJOINTNESS
    private void processDisjointClassesAxiom(OWLDisjointClassesAxiom axiom) {
        List<OWLClassExpression> operandList = new LinkedList<>(axiom.getClassExpressions());
        for (int i = 0; i < operandList.size() - 1; i++) {
            OWLClassExpression first = operandList.get(i);
            if (first instanceof OWLClass) {
                for (int j = i + 1; j < operandList.size(); j++) {
                    OWLClassExpression second = operandList.get(j);
                    if (second instanceof OWLClass) {
                        classDisjointMap.get(first.asOWLClass()).add(second.asOWLClass());
                        classDisjointMap.get(second.asOWLClass()).add(first.asOWLClass());
                    }
                }
            }
        }
    }

    //CLASS DISJOINTNESS
    private void processDisjointUnionAxiom(OWLDisjointUnionAxiom axiom) {
        OWLClass sup = axiom.getOWLClass();
        LinkedList<OWLClassExpression> operands = new LinkedList<>(axiom.getClassExpressions());
        for (int i = 0; i < operands.size() - 1; i++) {
            OWLClassExpression first = operands.get(i);
            if (first instanceof OWLClass) {
                classFathersMap.get(first).add(sup);
                classAncestorsMap.get(first).add((OWLClass) sup);
                classDescendantsMap.get(sup).add(first.asOWLClass());
                for (int j = i + 1; j < operands.size(); j++) {
                    OWLClassExpression second = operands.get(j);
                    if (second instanceof OWLClass) {
                        classDisjointMap.get(first.asOWLClass()).add(second.asOWLClass());
                        classDisjointMap.get(second.asOWLClass()).add(first.asOWLClass());
                        if (j == operands.size() - 1) {
                            classFathersMap.get(second).add(sup);
                            classAncestorsMap.get(second).add((OWLClass) sup);
                            classDescendantsMap.get(sup).add(second.asOWLClass());
                        }
                    }
                }
            }
        }
    }

    //PROP DOMAIN-RANGE
    private void processObjPropDomainAxiom(OWLObjectPropertyDomainAxiom axiom) {
        OWLObjectPropertyExpression prop = axiom.getProperty();
        OWLClassExpression domainExpr = axiom.getDomain();
        if (domainExpr instanceof OWLClass) {
            if (prop instanceof OWLObjectInverseOf) {
                processObjPropRange(domainExpr.asOWLClass(), ((OWLObjectInverseOf) prop).getInverse().asOWLObjectProperty());
            } else {
                processObjPropDomain(domainExpr.asOWLClass(), prop.asOWLObjectProperty());
            }
        } else {
            if (domainExpr instanceof OWLObjectUnionOf) {
                if (prop instanceof OWLObjectInverseOf) {
                    processObjPropRange((OWLObjectUnionOf) domainExpr, ((OWLObjectInverseOf) prop).getInverse().asOWLObjectProperty());
                } else {
                    processObjPropDomain((OWLObjectUnionOf) domainExpr, prop.asOWLObjectProperty());
                }
            }
        }
    }

    private void processObjPropDomain(OWLObjectUnionOf union, OWLObjectProperty prop) {
        union.getOperands().forEach(clExpr -> {
            if (clExpr instanceof OWLClass) {
                processObjPropDomain(clExpr.asOWLClass(), prop);
            } else {
                if (clExpr instanceof OWLObjectUnionOf) {
                    {
                        processObjPropDomain((OWLObjectUnionOf) clExpr, prop);
                    }
                }
            }
        });
    }

    private void processObjPropDomain(OWLClass cl, OWLObjectProperty prop) {
        objPropDomainMap.get(prop).add(cl);
        classRolesMap.get(cl).add(prop);
    }

    private void processObjPropRangeAxiom(OWLObjectPropertyRangeAxiom axiom) {
        OWLObjectPropertyExpression prop = axiom.getProperty();
        OWLClassExpression rangeExpr = axiom.getRange();
        if (rangeExpr instanceof OWLClass) {
            if (prop instanceof OWLObjectInverseOf) {
                processObjPropDomain(rangeExpr.asOWLClass(), ((OWLObjectInverseOf) prop).getInverse().asOWLObjectProperty());
            } else {
                processObjPropRange(rangeExpr.asOWLClass(), prop.asOWLObjectProperty());
            }
        } else {
            if (rangeExpr instanceof OWLObjectUnionOf) {
                if (prop instanceof OWLObjectInverseOf) {
                    processObjPropDomain((OWLObjectUnionOf) rangeExpr, ((OWLObjectInverseOf) prop).getInverse().asOWLObjectProperty());
                } else {
                    processObjPropRange((OWLObjectUnionOf) rangeExpr, prop.asOWLObjectProperty());
                }
            }
        }
    }

    private void processObjPropRange(OWLObjectUnionOf union, OWLObjectProperty prop) {
        union.getOperands().forEach(clExpr -> {
            if (clExpr instanceof OWLClass) {
                processObjPropRange(clExpr.asOWLClass(), prop);
            } else {
                if (clExpr instanceof OWLObjectUnionOf) {
                    {
                        processObjPropRange((OWLObjectUnionOf) clExpr, prop);
                    }
                }
            }
        });
    }

    private void processObjPropRange(OWLClass cl, OWLObjectProperty prop) {
        objPropRangeMap.get(prop).add(cl);
        classRolesMap.get(cl).add(prop);
    }


    private void processDataPropDomainAxiom(OWLDataPropertyDomainAxiom axiom) {
        OWLDataPropertyExpression prop = axiom.getProperty();
        OWLClassExpression domainExpr = axiom.getDomain();
        if (domainExpr instanceof OWLClass) {
            processDataPropDomain(domainExpr.asOWLClass(), prop.asOWLDataProperty());

        } else {
            if (domainExpr instanceof OWLObjectUnionOf) {
                processDataPropDomain((OWLObjectUnionOf) domainExpr, prop.asOWLDataProperty());
            }
        }
    }

    private void processDataPropDomain(OWLObjectUnionOf union, OWLDataProperty prop) {
        union.getOperands().forEach(clExpr -> {
            if (clExpr instanceof OWLClass) {
                processDataPropDomain(clExpr.asOWLClass(), prop);
            } else {
                if (clExpr instanceof OWLObjectUnionOf) {
                    {
                        processDataPropDomain((OWLObjectUnionOf) clExpr, prop);
                    }
                }
            }
        });
    }

    private void processDataPropDomain(OWLClass cl, OWLDataProperty prop) {
        dataPropDomainMap.get(prop).add(cl);
        classAttributesMap.get(cl).add(prop);
    }

    private boolean isDomainRelated(OWLClass cl, OWLObjectProperty objProp) {
        Set<OWLClass> domain = getObjPropDomain(objProp);
        Set<OWLClass> mandPart = getObjPropMandPartDomain(objProp);
        Set<OWLClass> ancestors = getClassAncestors(cl);
        return domain.contains(cl) || domain.stream().anyMatch(ancestors::contains) || mandPart.contains(cl) || mandPart.stream().anyMatch(ancestors::contains);
    }
    private boolean isRangeRelated(OWLClass cl, OWLObjectProperty objProp) {
        Set<OWLClass> range = getObjPropRange(objProp);
        Set<OWLClass> mandPart = getObjPropMandPartRange(objProp);
        Set<OWLClass> ancestors = getClassAncestors(cl);
        return range.contains(cl) || range.stream().anyMatch(ancestors::contains) || mandPart.contains(cl) || mandPart.stream().anyMatch(ancestors::contains);
    }

    public Highlights getHighlights(String clickedClassIRI) {
        Highlights ret = new Highlights();
        OWLClass cl = new OWLClassImpl(IRI.create(clickedClassIRI));

        Set<OWLClass> classes = new HashSet<>();
        classes.addAll(getClassDescendants(cl));
        Set<OWLClass> ancestors = getClassAncestors(cl);
        classes.addAll(ancestors);
        classes.addAll(getClassNonDisjointSiblings(cl));
        ret.setClasses(classes.stream().map(i -> i.getIRI().toString()).collect(Collectors.toList()));

        for(OWLObjectProperty objProp : getClassRoles(cl)) {
            Branch b = new Branch();
            b.setObjectPropertyIRI(objProp.getIRI().toString());
            Set<String> relatedClasses = new HashSet<>();
            /**
             * the variable "toAdd" is used to remove from the highlights the children properties inferred by the reasoner
             * as described here: https://github.com/obdasystems/sparqling-ws/issues/28
             **/
            boolean notAdd = false;
            if(isDomainRelated(cl, objProp)) {
                for (OWLObjectPropertyDomainAxiom a : ontology.getObjectPropertyDomainAxioms(objProp)) {
                    if (a.getDomain() instanceof OWLClass) {
                        OWLClass domainClass = (OWLClass) a.getDomain();
                        if(getClassDescendants(cl).contains(domainClass)) {
                            notAdd = true;
                            break;
                        }
                    }
                }
                if (notAdd) {
                    continue;
                }
                if(isRangeRelated(cl, objProp)) {
                    b.setCyclic(true);
                }
                getObjPropRange(objProp).forEach(c->relatedClasses.add(c.getIRI().toString()));
                getObjPropMandPartRange(objProp).forEach(c->relatedClasses.add(c.getIRI().toString()));

                b.setDirect(true);
            } else if(isRangeRelated(cl, objProp)) {
                for (OWLObjectPropertyRangeAxiom a : ontology.getObjectPropertyRangeAxioms(objProp)) {
                    if (a.getRange() instanceof OWLClass) {
                        OWLClass rangeClass = (OWLClass) a.getRange();
                        if(getClassDescendants(cl).contains(rangeClass)) {
                            notAdd = true;
                            break;
                        }
                    }
                }
                if (notAdd) {
                    continue;
                }
                if(isDomainRelated(cl, objProp)) {
                    b.setCyclic(true);
                }
                getObjPropDomain(objProp).forEach(c->relatedClasses.add(c.getIRI().toString()));
                getObjPropMandPartDomain(objProp).forEach(c->relatedClasses.add(c.getIRI().toString()));
                b.setDirect(false);
            }
            b.setRelatedClasses(relatedClasses.stream().collect(Collectors.toList()));
            ret.addObjectPropertiesItem(b);
        }

        for(OWLDataProperty i : getClassAttributes(cl)) {
            /**
             * the variable "toAdd" is used to remove from the highlights the children properties inferred by the reasoner
             * as described here: https://github.com/obdasystems/sparqling-ws/issues/28
             **/
            boolean notAdd = false;
            for (OWLDataPropertyDomainAxiom a : ontology.getDataPropertyDomainAxioms(i)) {
                if (a.getDomain() instanceof OWLClass) {
                    OWLClass domainClass = (OWLClass) a.getDomain();
                    if(getClassDescendants(cl).contains(domainClass)) {
                        notAdd = true;
                        break;
                    }
                }
            }
            if (notAdd) {
                continue;
            }
            ret.addDataPropertiesItem(i.getIRI().toString());
        }
        return ret;
    }
}
