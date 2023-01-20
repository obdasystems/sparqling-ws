package com.obdasystems.sparqling.engine;

import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SimpleOwlOntologyDeductiveClosureProcesor {
    static Logger logger = LoggerFactory.getLogger(SimpleOwlOntologyDeductiveClosureProcesor.class);

    private OWLOntology inputOntology;
    private OWLOntologyManager ontologyManager;
    private OWLDataFactory dataFactory;

    private Set<OWLClassExpression> emptyConcepts = null;
    private Set<OWLObjectPropertyExpression> emptyRoles = null;
    private Set<OWLDataPropertyExpression> emptyConceptAttributes = null;

    private SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC = null;
    private SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge> GR = null;
    private SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge> GCA = null;

    private Map<OWLDataProperty, Set<OWLClass>> dataPropertyDomainAxiom;
    private Map<OWLObjectPropertyExpression, Set<OWLClass>> objectPropertyDomainAxiom;
    private Map<OWLObjectPropertyExpression, Set<OWLClass>> objectPropertyRangeAxiom;

    public SimpleOwlOntologyDeductiveClosureProcesor(OWLOntology ontology) {
        inputOntology = ontology;
        ontologyManager = OWLManager.createOWLOntologyManager();
        dataFactory = ontologyManager.getOWLDataFactory();
    }

    private void printGraph(SimpleDirectedGraph graph) {
        graph.edgeSet().forEach(edge->{
            DefaultEdge defEdge = (DefaultEdge)edge;
            System.out.println(graph.getEdgeSource(defEdge) + " ===> " + graph.getEdgeTarget(defEdge));
        });
    }

    public Map<OWLDataProperty, Set<OWLClass>> getDataPropertyDomainAxiom() {
        return dataPropertyDomainAxiom;
    }

    public Map<OWLObjectPropertyExpression, Set<OWLClass>> getObjectPropertyDomainAxiom() {
        return objectPropertyDomainAxiom;
    }

    public Map<OWLObjectPropertyExpression, Set<OWLClass>> getObjectPropertyRangeAxiom() {
        return objectPropertyRangeAxiom;
    }

    private boolean isUnionOrIntersection(OWLClassExpression exp) {
        if(exp instanceof OWLObjectUnionOf || exp instanceof OWLObjectIntersectionOf) {
            return true;
        }
        return false;
    }

    public Set<OWLAxiom> computeSimpleDeductiveClosure() {
        logger.debug("TBoxGraphDeductiveClosure started... ");
        long t = System.currentTimeMillis();
        emptyConcepts = new HashSet<OWLClassExpression>();
        emptyRoles = new HashSet<OWLObjectPropertyExpression>();
        emptyConceptAttributes = new HashSet<OWLDataPropertyExpression>();
        GC = new SimpleDirectedGraph<OWLClassExpression, DefaultEdge>(DefaultEdge.class);
        GR = new SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge>(DefaultEdge.class);
        GCA = new SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge>(DefaultEdge.class);
        initGraphVertex();
        //initQualifiedConceptAttributeDomainTrivialIAs();
        inputOntology.getLogicalAxioms(Imports.INCLUDED).forEach(axiom -> {
            if (axiom instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom castAxiom = (OWLSubClassOfAxiom) axiom;
                OWLClassExpression sub = castAxiom.getSubClass();
                OWLClassExpression sup = castAxiom.getSuperClass();
                if (sub instanceof OWLObjectUnionOf) {
                    OWLObjectUnionOf union = (OWLObjectUnionOf) sub;
                    if (sup instanceof OWLObjectIntersectionOf) {
                        processConceptInclusionWithUnionOnTheLeftAndIntersectionOnTheRight(union, (OWLObjectIntersectionOf) sup);
                    } else {
                        processConceptInclusionWithUnionOnTheLeft(union, sup);
                    }
                } else {
                    if (sup instanceof OWLObjectIntersectionOf) {
                        processConceptInclusionWithIntersectionOnTheRight(sub, (OWLObjectIntersectionOf) sup);
                    } else {
                        if(!(isUnionOrIntersection(sub) || isUnionOrIntersection(sup))) {
                            GC.addVertex(sub);
                            GC.addVertex(sup);
                            GC.addEdge(sub, sup);
                        }
                    }
                }
            } else {
                if (axiom instanceof OWLSubObjectPropertyOfAxiom) {
                    OWLSubObjectPropertyOfAxiom castAxiom = (OWLSubObjectPropertyOfAxiom) axiom;
                    OWLObjectPropertyExpression sub = castAxiom.getSubProperty();
                    OWLObjectPropertyExpression sup = castAxiom.getSuperProperty();
                    GR.addVertex(sub);
                    GR.addVertex(sup);
                    GR.addEdge(sub, sup);
                } else {
                    if (axiom instanceof OWLSubDataPropertyOfAxiom) {
                        OWLSubDataPropertyOfAxiom castAxiom = (OWLSubDataPropertyOfAxiom) axiom;
                        OWLDataPropertyExpression sub = castAxiom.getSubProperty();
                        OWLDataPropertyExpression sup = castAxiom.getSuperProperty();
                        GCA.addVertex(sub);
                        GCA.addVertex(sup);
                        GCA.addEdge(sub, sup);
                    } else {
                        if (axiom instanceof OWLEquivalentClassesAxiom) {
                            OWLEquivalentClassesAxiom castAxiom = (OWLEquivalentClassesAxiom) axiom;
                            List<OWLClassExpression> classExpressions = castAxiom.getClassExpressionsAsList();
                            for (int i = 0; i < classExpressions.size() - 1; i++) {
                                OWLClassExpression first = classExpressions.get(i);
                                for (int j = i + 1; j < classExpressions.size(); j++) {
                                    OWLClassExpression second = classExpressions.get(j);
                                    if (first instanceof OWLObjectUnionOf) {
                                        if (second instanceof OWLObjectIntersectionOf) {
                                            processConceptInclusionWithUnionOnTheLeftAndIntersectionOnTheRight((OWLObjectUnionOf) first, (OWLObjectIntersectionOf) second);
                                        } else {
                                            processConceptInclusionWithUnionOnTheLeft((OWLObjectUnionOf) first, second);
                                        }
                                    } else {
                                        if (second instanceof OWLObjectUnionOf) {
                                            if (first instanceof OWLObjectIntersectionOf) {
                                                processConceptInclusionWithUnionOnTheLeftAndIntersectionOnTheRight((OWLObjectUnionOf) second, (OWLObjectIntersectionOf) first);
                                            } else {
                                                processConceptInclusionWithUnionOnTheLeft((OWLObjectUnionOf) second, first);
                                            }
                                        } else {
                                            if(!(isUnionOrIntersection(first) || isUnionOrIntersection(second))) {
                                                GC.addVertex(first);
                                                GC.addVertex(second);
                                                GC.addEdge(first, second);
                                                GC.addEdge(second, first);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (axiom instanceof OWLDisjointClassesAxiom) {
                                OWLDisjointClassesAxiom castAxiom = (OWLDisjointClassesAxiom) axiom;
                                List<OWLClassExpression> classExpressions = castAxiom.getClassExpressionsAsList();
                                for (int i = 0; i < classExpressions.size() - 1; i++) {
                                    OWLClassExpression first = classExpressions.get(i);
                                    if (!(first instanceof OWLObjectComplementOf)) {
                                        for (int j = i + 1; j < classExpressions.size(); j++) {
                                            OWLClassExpression second = classExpressions.get(j);
                                            if (!(second instanceof OWLObjectComplementOf)) {
                                                if (first instanceof OWLObjectUnionOf) {
                                                    processConceptDisjointnessWithUnionOnTheLeft((OWLObjectUnionOf) first, second);
                                                } else {
                                                    if (second instanceof OWLObjectUnionOf) {
                                                        processConceptDisjointnessWithUnionOnTheLeft((OWLObjectUnionOf) second, first);
                                                    } else {
                                                        if(!(isUnionOrIntersection(first) || isUnionOrIntersection(second))) {
                                                            OWLObjectComplementOf negFirst = dataFactory.getOWLObjectComplementOf(first);
                                                            OWLObjectComplementOf negSecond = dataFactory.getOWLObjectComplementOf(second);
                                                            GC.addVertex(first);
                                                            GC.addVertex(second);
                                                            GC.addVertex(negFirst);
                                                            GC.addVertex(negSecond);
                                                            GC.addEdge(first, negSecond);
                                                            GC.addEdge(second, negFirst);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (axiom instanceof OWLDisjointUnionAxiom) {
                                    OWLDisjointUnionAxiom castAxiom = (OWLDisjointUnionAxiom) axiom;
                                    OWLClass sup = castAxiom.getOWLClass();
                                    GC.addVertex(sup);
                                    LinkedList<OWLClassExpression> operands = new LinkedList<>(castAxiom.getClassExpressions());
                                    for (int i = 0; i < operands.size() - 1; i++) {
                                        OWLClassExpression first = operands.get(i);
                                        if (first instanceof OWLObjectUnionOf) {
                                            processConceptInclusionWithUnionOnTheLeft((OWLObjectUnionOf) first, sup);
                                        } else {
                                            GC.addVertex(first);
                                            GC.addEdge(first, sup);
                                        }
                                        for (int j = i + 1; j < operands.size(); j++) {
                                            OWLClassExpression second = operands.get(j);
                                            if (j == operands.size() - 1) {
                                                if (second instanceof OWLObjectUnionOf) {
                                                    processConceptInclusionWithUnionOnTheLeft((OWLObjectUnionOf) second, sup);
                                                } else {
                                                    GC.addVertex(second);
                                                    GC.addEdge(second, sup);
                                                }
                                            }
                                            if (!(first instanceof OWLObjectComplementOf || second instanceof OWLObjectComplementOf)) {
                                                if (first instanceof OWLObjectUnionOf) {
                                                    processConceptDisjointnessWithUnionOnTheLeft((OWLObjectUnionOf) first, second);
                                                } else {
                                                    if (second instanceof OWLObjectUnionOf) {
                                                        processConceptDisjointnessWithUnionOnTheLeft((OWLObjectUnionOf) second, first);
                                                    } else {
                                                        OWLObjectComplementOf negFirst = dataFactory.getOWLObjectComplementOf(first);
                                                        OWLObjectComplementOf negSecond = dataFactory.getOWLObjectComplementOf(second);
                                                        GC.addVertex(first);
                                                        GC.addVertex(second);
                                                        GC.addVertex(negFirst);
                                                        GC.addVertex(negSecond);
                                                        GC.addEdge(first, negSecond);
                                                        GC.addEdge(second, negFirst);
                                                    }
                                                }
                                            }
                                        }

                                    }
                                } else {
                                    if (axiom instanceof OWLEquivalentObjectPropertiesAxiom) {
                                        OWLEquivalentObjectPropertiesAxiom castAxiom = (OWLEquivalentObjectPropertiesAxiom) axiom;
                                        castAxiom.asSubObjectPropertyOfAxioms().forEach(subAx -> {
                                            OWLObjectPropertyExpression sub = subAx.getSubProperty();
                                            OWLObjectPropertyExpression sup = subAx.getSuperProperty();
                                            GR.addVertex(sub);
                                            GR.addVertex(sup);
                                            GR.addEdge(sub, sup);
                                        });
                                    } else {
                                        if (axiom instanceof OWLDisjointObjectPropertiesAxiom) {

                                        } else {
                                            if (axiom instanceof OWLEquivalentDataPropertiesAxiom) {
                                                OWLEquivalentDataPropertiesAxiom castAxiom = (OWLEquivalentDataPropertiesAxiom) axiom;
                                                castAxiom.asSubDataPropertyOfAxioms().forEach(subAx -> {
                                                    OWLDataPropertyExpression sub = subAx.getSubProperty();
                                                    OWLDataPropertyExpression sup = subAx.getSuperProperty();
                                                    GCA.addVertex(sub);
                                                    GCA.addVertex(sup);
                                                    GCA.addEdge(sub, sup);
                                                });
                                            } else {
                                                if (axiom instanceof OWLDisjointDataPropertiesAxiom) {

                                                } else {
                                                    if (axiom instanceof OWLObjectPropertyDomainAxiom) {
                                                        //inserisci arco exist R --> C
                                                        OWLObjectPropertyDomainAxiom castAxiom = (OWLObjectPropertyDomainAxiom) axiom;
                                                        OWLObjectPropertyExpression prop = castAxiom.getProperty();
                                                        OWLClassExpression filler = castAxiom.getDomain();
                                                        OWLObjectSomeValuesFrom exProp = dataFactory.getOWLObjectSomeValuesFrom(prop, dataFactory.getOWLThing());
                                                        if (filler instanceof OWLObjectIntersectionOf) {
                                                            processConceptInclusionWithIntersectionOnTheRight(exProp, (OWLObjectIntersectionOf) filler);
                                                        } else {
                                                            GC.addVertex(exProp);
                                                            GC.addVertex(filler);
                                                            GC.addEdge(exProp, filler);
                                                        }
                                                    } else {
                                                        if (axiom instanceof OWLObjectPropertyRangeAxiom) {
                                                            OWLObjectPropertyRangeAxiom castAxiom = (OWLObjectPropertyRangeAxiom) axiom;
                                                            OWLObjectPropertyExpression prop = castAxiom.getProperty().getInverseProperty();
                                                            OWLClassExpression filler = castAxiom.getRange();
                                                            OWLObjectSomeValuesFrom exProp = dataFactory.getOWLObjectSomeValuesFrom(prop, dataFactory.getOWLThing());
                                                            if (filler instanceof OWLObjectIntersectionOf) {
                                                                processConceptInclusionWithIntersectionOnTheRight(exProp, (OWLObjectIntersectionOf) filler);
                                                            } else {
                                                                GC.addVertex(exProp);
                                                                GC.addVertex(filler);
                                                                GC.addEdge(exProp, filler);
                                                            }
                                                        } else {
                                                            if (axiom instanceof OWLDataPropertyDomainAxiom) {
                                                                OWLDataPropertyDomainAxiom castAxiom = (OWLDataPropertyDomainAxiom) axiom;
                                                                OWLDataPropertyExpression prop = castAxiom.getProperty();
                                                                OWLClassExpression filler = castAxiom.getDomain();
                                                                OWLDataSomeValuesFrom exProp = dataFactory.getOWLDataSomeValuesFrom(prop, dataFactory.getTopDatatype());
                                                                if (filler instanceof OWLObjectIntersectionOf) {
                                                                    processConceptInclusionWithIntersectionOnTheRight(exProp, (OWLObjectIntersectionOf) filler);
                                                                } else {
                                                                    GC.addVertex(exProp);
                                                                    GC.addVertex(filler);
                                                                    GC.addEdge(exProp, filler);
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
                        }
                    }
                }
            }
        });

        /*System.out.println("##### PRE #####");
        printGraph(GC);
        System.out.println();*/
        int initialTotalIAs;
        int finalTotalIAs;
        int round=0;
        do {
            initialTotalIAs = GC.edgeSet().size() +
                    GR.edgeSet().size() +
                    GCA.edgeSet().size();
            applyAllConceptIARules(GC, GR, GCA);
            applyAllRoleIARules(GC, GR);
            applyAllConceptAttributeIARules(GC, GCA);
            finalTotalIAs = GC.edgeSet().size() +
                    GR.edgeSet().size() +
                    GCA.edgeSet().size();

            logger.debug("iteration nr. " + round++ + " --> finalTotalIAs="+finalTotalIAs);

        } while (initialTotalIAs != finalTotalIAs);
        /*System.out.println("##### POST #####");
        printGraph(GC);*/

        Set<OWLAxiom> simpleDeductiveClosure = extractAllAssertionsFromGraphs(GC, GR, GCA);
        buildPropertiesDomainRangeAxiomSet(simpleDeductiveClosure);
        t = System.currentTimeMillis() - t;
        logger.debug("SimpleOwlOntologyDeductiveClosure completed (" + t + " ms).");
        return simpleDeductiveClosure;
    }

    private void buildPropertiesDomainRangeAxiomSet(Set<OWLAxiom> simpleDeductiveClosure) {
        this.dataPropertyDomainAxiom = new HashMap<>();
        this.objectPropertyDomainAxiom = new HashMap<>();
        this.objectPropertyRangeAxiom = new HashMap<>();
        simpleDeductiveClosure.stream().
                filter(owlAxiom -> owlAxiom instanceof OWLSubClassOfAxiom).
                forEach(ax->{
                    OWLClassExpression sup = ((OWLSubClassOfAxiom) ax).getSuperClass();
                    if(sup instanceof OWLClass) {
                        OWLClassExpression sub = ((OWLSubClassOfAxiom) ax).getSubClass();
                        if(sub instanceof OWLDataSomeValuesFrom) {
                            OWLDataPropertyExpression currProp = ((OWLDataSomeValuesFrom) sub).getProperty();
                            Set<OWLClass> currSet = this.dataPropertyDomainAxiom.get(currProp);
                            if(currSet == null) {
                                currSet = new HashSet<>();
                                this.dataPropertyDomainAxiom.put(currProp.asOWLDataProperty(), currSet);
                            }
                            currSet.add((OWLClass) sup);
                        }
                        else {
                            if(sub instanceof OWLObjectSomeValuesFrom) {
                                OWLObjectPropertyExpression currExpr = ((OWLObjectSomeValuesFrom) sub).getProperty();
                                if(currExpr instanceof OWLObjectProperty) {
                                    Set<OWLClass> currSet = this.objectPropertyDomainAxiom.get(currExpr);
                                    if(currSet == null) {
                                        currSet = new HashSet<>();
                                        this.objectPropertyDomainAxiom.put(currExpr.getNamedProperty(), currSet);
                                    }
                                    currSet.add((OWLClass) sup);
                                }
                                else {
                                    if(currExpr instanceof OWLObjectInverseOf) {
                                        Set<OWLClass> currSet = this.objectPropertyRangeAxiom.get(currExpr);
                                        if(currSet == null) {
                                            currSet = new HashSet<>();
                                            this.objectPropertyRangeAxiom.put(currExpr.getNamedProperty(), currSet);
                                        }
                                        currSet.add((OWLClass) sup);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private Set<OWLAxiom> extractAllAssertionsFromGraphs(SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC,
                                                         SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge> GR,
                                                         SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge> GCA) {

        Set<OWLAxiom> result = new HashSet<OWLAxiom>();
        Set<DefaultEdge> allConceptIAs = GC.edgeSet();
        Iterator<DefaultEdge> it = allConceptIAs.iterator();
        while (it.hasNext()) {
            DefaultEdge edgeIA = it.next();
            OWLSubClassOfAxiom newIA = dataFactory.getOWLSubClassOfAxiom(GC.getEdgeSource(edgeIA), GC.getEdgeTarget(edgeIA));
            result.add(newIA);
        }
        Set<DefaultEdge> allRoleIAs = GR.edgeSet();
        Iterator<DefaultEdge> it2 = allRoleIAs.iterator();
        while (it2.hasNext()) {
            DefaultEdge edgeIA = it2.next();
            OWLSubObjectPropertyOfAxiom newIA = dataFactory.getOWLSubObjectPropertyOfAxiom(GR.getEdgeSource(edgeIA), GR.getEdgeTarget(edgeIA));
            result.add(newIA);
        }
        Set<DefaultEdge> allCAIAs = GCA.edgeSet();
        Iterator<DefaultEdge> it3 = allCAIAs.iterator();
        while (it3.hasNext()) {
            DefaultEdge edgeIA = it3.next();
            OWLSubDataPropertyOfAxiom newIA = dataFactory.getOWLSubDataPropertyOfAxiom(GCA.getEdgeSource(edgeIA), GCA.getEdgeTarget(edgeIA));
            result.add(newIA);
        }
        return result;
    }

    private void initGraphVertex() {
        inputOntology.getClassesInSignature(Imports.INCLUDED).forEach(cl -> {
            GC.addVertex(cl);
        });
        inputOntology.getObjectPropertiesInSignature(Imports.INCLUDED).forEach(objProp -> {
            GR.addVertex(objProp);
        });
        inputOntology.getDataPropertiesInSignature(Imports.INCLUDED).forEach(dataProp -> {
            GCA.addVertex(dataProp);
        });
    }

    private void initQualifiedConceptAttributeDomainTrivialIAs() {
        inputOntology.getDataPropertiesInSignature(Imports.INCLUDED).forEach(dataProp -> {
            if (!dataProp.isOWLBottomDataProperty()) {
                OWLDataSomeValuesFrom cad = dataFactory.getOWLDataSomeValuesFrom(dataProp, dataFactory.getTopDatatype());
                GC.addVertex(cad);
                inputOntology.getDatatypesInSignature(Imports.INCLUDED).forEach(dataType -> {
                    if (!(dataType.isBottomEntity() || dataType.isTopDatatype())) {
                        OWLDataSomeValuesFrom qcad = dataFactory.getOWLDataSomeValuesFrom(dataProp, dataType);
                        GC.addVertex(qcad);
                        GC.addEdge(qcad, cad);
                    }
                });
            }
        });
    }

    private void processConceptInclusionWithUnionOnTheLeft(OWLObjectUnionOf sub, OWLClassExpression sup) {
        sub.getOperands().forEach(op -> {
            if (op instanceof OWLObjectUnionOf) {
                processConceptInclusionWithUnionOnTheLeft((OWLObjectUnionOf) op, sup);
            } else {
                GC.addVertex(op);
                GC.addVertex(sup);
                GC.addEdge(op, sup);
            }
        });
    }

    private void processConceptInclusionWithIntersectionOnTheRight(OWLClassExpression sub, OWLObjectIntersectionOf sup) {
        sup.getOperands().forEach(intOp -> {
            if (intOp instanceof OWLObjectIntersectionOf) {
                processConceptInclusionWithIntersectionOnTheRight(sub, (OWLObjectIntersectionOf) intOp);
            } else {
                GC.addVertex(sub);
                GC.addVertex(intOp);
                GC.addEdge(sub, intOp);
            }
        });
    }

    private void processConceptInclusionWithUnionOnTheLeftAndIntersectionOnTheRight(OWLObjectUnionOf sub, OWLObjectIntersectionOf sup) {
        sub.getOperands().forEach(op -> {
            sup.getOperands().forEach(intOp -> {
                if (op instanceof OWLObjectUnionOf) {
                    if (intOp instanceof OWLObjectIntersectionOf) {
                        processConceptInclusionWithUnionOnTheLeftAndIntersectionOnTheRight((OWLObjectUnionOf) op, (OWLObjectIntersectionOf) intOp);
                    } else {
                        processConceptInclusionWithUnionOnTheLeft((OWLObjectUnionOf) op, intOp);
                    }
                } else {
                    if (intOp instanceof OWLObjectIntersectionOf) {
                        processConceptInclusionWithIntersectionOnTheRight(op, (OWLObjectIntersectionOf) intOp);
                    } else {
                        GC.addVertex(op);
                        GC.addVertex(intOp);
                        GC.addEdge(op, intOp);
                    }
                }
            });
        });
    }

    private void processConceptDisjointnessWithUnionOnTheLeft(OWLObjectUnionOf sub, OWLClassExpression disj) {
        sub.getOperands().forEach(op -> {
            if (op instanceof OWLObjectUnionOf) {
                processConceptInclusionWithUnionOnTheLeft((OWLObjectUnionOf) op, disj);
            } else {
                OWLObjectComplementOf negDisj = dataFactory.getOWLObjectComplementOf(disj);
                GC.addVertex(op);
                GC.addVertex(negDisj);
                GC.addEdge(op, negDisj);
            }
        });
    }

    private void applyAllConceptIARules(SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC,
                                        SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge> GR,
                                        SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge> GCA) {

        TransitiveClosure TC = TransitiveClosure.INSTANCE;
        TC.closeSimpleDirectedGraph(GC);

        Object[] allEdges = (GC.edgeSet()).toArray();
        for (int i = 0; i < allEdges.length; i++) {
            DefaultEdge conceptIA = (DefaultEdge) allEdges[i];
            OWLClassExpression src = GC.getEdgeSource(conceptIA);
            OWLClassExpression tgt = GC.getEdgeTarget(conceptIA);

            if (src instanceof OWLObjectComplementOf) {
                continue;
            }

            if (tgt instanceof OWLObjectComplementOf) {
                OWLObjectComplementOf nbc = (OWLObjectComplementOf) tgt;
                OWLClassExpression op = (nbc).getOperand();
                if (op.isOWLThing()) {
                    GC.addEdge(src, dataFactory.getOWLObjectComplementOf(src));
                }
            }

            if (!(src instanceof OWLObjectComplementOf) &&
                    tgt instanceof OWLObjectComplementOf) {
                OWLClassExpression newSrc = ((OWLObjectComplementOf) tgt).getOperand();
                OWLObjectComplementOf newTgt = dataFactory.getOWLObjectComplementOf(src);
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }

            if (src instanceof OWLObjectSomeValuesFrom && tgt instanceof OWLObjectComplementOf) {
                OWLObjectComplementOf negTgt = (OWLObjectComplementOf) tgt;
                if (negTgt.getOperand() instanceof OWLObjectSomeValuesFrom) {
                    OWLObjectSomeValuesFrom srcExistential = (OWLObjectSomeValuesFrom) src;
                    OWLObjectSomeValuesFrom tgtExistential = (OWLObjectSomeValuesFrom) negTgt.getOperand();
                    if (srcExistential.equals(tgtExistential)) {
                        OWLObjectSomeValuesFrom newSrc = dataFactory.getOWLObjectSomeValuesFrom(srcExistential.getProperty().getInverseProperty(), dataFactory.getOWLThing());
                        OWLObjectComplementOf newTgt = dataFactory.getOWLObjectComplementOf(
                                dataFactory.getOWLObjectSomeValuesFrom(srcExistential.getProperty().getInverseProperty(), dataFactory.getOWLThing()));

                        GC.addVertex(newSrc);
                        GC.addVertex(newTgt);
                        GC.addEdge(newSrc, newTgt);
                        OWLObjectPropertyExpression newSrc2 = srcExistential.getProperty();
                        OWLObjectPropertyExpression newTgt2 = tgtExistential.getProperty();
                        OWLObjectPropertyExpression newSrc3 = srcExistential.getProperty().getInverseProperty();
                        OWLObjectPropertyExpression newTgt3 = tgtExistential.getProperty().getInverseProperty();
                    }
                }
            }

            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLObjectSomeValuesFrom) {
                if (!((OWLObjectSomeValuesFrom) tgt).getFiller().isOWLThing()) {
                    OWLObjectSomeValuesFrom er = dataFactory.getOWLObjectSomeValuesFrom(((OWLObjectSomeValuesFrom) tgt).getProperty(), dataFactory.getOWLThing());
                    if (!er.equals(src)) {
                        GC.addVertex(er);
                        GC.addEdge(src, er);
                    }
                }
            }

            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom qec = (OWLObjectSomeValuesFrom) tgt;
                if (!qec.getFiller().isOWLThing()) {
                    OWLObjectPropertyExpression br = qec.getProperty();
                    if (GR.containsVertex(br)) {
                        Set<DefaultEdge> outgoingEdgesForBr = GR.outgoingEdgesOf(br);
                        Iterator<DefaultEdge> it = outgoingEdgesForBr.iterator();
                        Set<OWLObjectSomeValuesFrom> toAdd = new HashSet<OWLObjectSomeValuesFrom>();
                        while (it.hasNext()) {
                            DefaultEdge outgoingEdgeForBr = it.next();
                            OWLObjectPropertyExpression target = GR.getEdgeTarget(outgoingEdgeForBr);
                            if (target instanceof OWLObjectPropertyExpression) {
                                OWLObjectPropertyExpression newRole = (OWLObjectPropertyExpression) target;
                                OWLObjectSomeValuesFrom newQec = dataFactory.getOWLObjectSomeValuesFrom(newRole, qec.getFiller());
                                toAdd.add(newQec);
                            }
                        }
                        for (OWLObjectSomeValuesFrom add : toAdd) {
                            GC.addVertex(add);
                            GC.addEdge(src, add);
                        }
                    }
                }
            }

            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom qec = (OWLObjectSomeValuesFrom) tgt;
                if (!qec.getFiller().isOWLThing()) {
                    OWLClassExpression bc = qec.getFiller();
                    if (GC.containsVertex(bc)) {
                        Set<DefaultEdge> outgoingEdgesForBc = GC.outgoingEdgesOf(bc);
                        Iterator<DefaultEdge> it = outgoingEdgesForBc.iterator();
                        Set<OWLObjectSomeValuesFrom> toAdd = new HashSet<OWLObjectSomeValuesFrom>();
                        while (it.hasNext()) {
                            DefaultEdge outgoingEdgeForBc = it.next();
                            OWLClassExpression target = GC.getEdgeTarget(outgoingEdgeForBc);
                            if (!(target instanceof OWLObjectComplementOf) && !(target instanceof OWLClass)) {
                                OWLClassExpression newBasicConcept = (OWLClassExpression) target;
                                OWLObjectSomeValuesFrom newQec = dataFactory.getOWLObjectSomeValuesFrom(qec.getProperty(), newBasicConcept);
                                toAdd.add(newQec);
                            }
                        }
                        for (OWLObjectSomeValuesFrom add : toAdd) {
                            GC.addVertex(add);
                            GC.addEdge(src, add);
                        }
                    }
                }
            }

            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLDataSomeValuesFrom) {
                OWLDataSomeValuesFrom qec = (OWLDataSomeValuesFrom) tgt;
                if (!qec.getFiller().isTopDatatype()) {
                    OWLDataSomeValuesFrom cad = dataFactory.getOWLDataSomeValuesFrom(qec.getProperty(), dataFactory.getTopDatatype());
                    if (!src.equals(cad)) {
                        GC.addVertex(cad);
                        GC.addEdge(src, cad);
                    }
                }
            }

            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLDataSomeValuesFrom) {
                OWLDataSomeValuesFrom qec = (OWLDataSomeValuesFrom) tgt;
                if (!qec.getFiller().isTopDatatype()) {
                    OWLDataPropertyExpression br = qec.getProperty();
                    if (GCA.containsVertex(br)) {
                        Set<DefaultEdge> outgoingEdgesForBr = GCA.outgoingEdgesOf(br);
                        Iterator<DefaultEdge> it = outgoingEdgesForBr.iterator();
                        Set<OWLDataSomeValuesFrom> toAdd = new HashSet<OWLDataSomeValuesFrom>();
                        while (it.hasNext()) {
                            DefaultEdge outgoingEdgeForBr = it.next();
                            OWLDataPropertyExpression target = GCA.getEdgeTarget(outgoingEdgeForBr);
                            OWLDataSomeValuesFrom newQec = dataFactory.getOWLDataSomeValuesFrom(target, qec.getFiller());
                            if (!src.equals(newQec)) {
                                toAdd.add(newQec);
                            }

                        }
                        for (OWLDataSomeValuesFrom add : toAdd) {
                            GC.addVertex(add);
                            GC.addEdge(src, add);
                        }
                    }
                }
            }
        }
    }

    private void applyAllRoleIARules(SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC,
                                     SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge> GR) {

        TransitiveClosure TC = TransitiveClosure.INSTANCE;
        TC.closeSimpleDirectedGraph(GR);

        Object[] allEdges = (GR.edgeSet()).toArray();
        for (int i = 0; i < allEdges.length; i++) {
            DefaultEdge roleIA = (DefaultEdge) allEdges[i];
            OWLObjectPropertyExpression src = GR.getEdgeSource(roleIA);
            OWLObjectPropertyExpression tgt = GR.getEdgeTarget(roleIA);
            if (src instanceof OWLObjectPropertyExpression && tgt instanceof OWLObjectPropertyExpression) {
                OWLObjectSomeValuesFrom newSrc = dataFactory.getOWLObjectSomeValuesFrom(src, dataFactory.getOWLThing());
                OWLObjectSomeValuesFrom newTgt = dataFactory.getOWLObjectSomeValuesFrom(tgt, dataFactory.getOWLThing());
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
                OWLObjectSomeValuesFrom newSrc2 = dataFactory.getOWLObjectSomeValuesFrom(src.getInverseProperty(), dataFactory.getOWLThing());
                OWLObjectSomeValuesFrom newTgt2 = dataFactory.getOWLObjectSomeValuesFrom(tgt.getInverseProperty(), dataFactory.getOWLThing());
                GC.addVertex(newSrc2);
                GC.addVertex(newTgt2);
                GC.addEdge(newSrc2, newTgt2);
            }

            /*if (src instanceof OWLObjectPropertyExpression && tgt instanceof OWLObjectPropertyExpression) {
                OWLObjectSomeValuesFrom newSrc2 = dataFactory.getOWLObjectSomeValuesFrom(src, dataFactory.getOWLThing());
                OWLObjectSomeValuesFrom negTgt2 = dataFactory.getOWLObjectSomeValuesFrom(tgt, dataFactory.getOWLThing());
                OWLObjectComplementOf newTgt2 = dataFactory.getOWLObjectComplementOf(negTgt2);
                GC.addVertex(newSrc2);
                GC.addVertex(newTgt2);
                GC.addEdge(newSrc2, newTgt2);
                OWLObjectSomeValuesFrom newSrc3 = dataFactory.getOWLObjectSomeValuesFrom(src.getInverseProperty(), dataFactory.getOWLThing());
                OWLObjectSomeValuesFrom negTgt3 = dataFactory.getOWLObjectSomeValuesFrom(tgt.getInverseProperty(), dataFactory.getOWLThing());
                OWLObjectComplementOf newTgt3 = dataFactory.getOWLObjectComplementOf(negTgt3);
                GC.addVertex(newSrc3);
                GC.addVertex(newTgt3);
                GC.addEdge(newSrc3, newTgt3);
            }*/
        }
    }

    private void applyAllConceptAttributeIARules(SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC,
                                                 SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge> GCA) {
        TransitiveClosure TC = TransitiveClosure.INSTANCE;
        TC.closeSimpleDirectedGraph(GCA);
        Object[] allEdges = (GCA.edgeSet()).toArray();
        for (int i = 0; i < allEdges.length; i++) {
            DefaultEdge conceptAttrIA = (DefaultEdge) allEdges[i];
            OWLDataPropertyExpression src = GCA.getEdgeSource(conceptAttrIA);
            OWLDataPropertyExpression tgt = GCA.getEdgeTarget(conceptAttrIA);
            if (src instanceof OWLDataPropertyExpression &&
                    tgt instanceof OWLDataPropertyExpression) {
                OWLDataSomeValuesFrom newSrc = dataFactory.getOWLDataSomeValuesFrom(src, dataFactory.getTopDatatype());
                OWLDataSomeValuesFrom newTgt = dataFactory.getOWLDataSomeValuesFrom(tgt, dataFactory.getTopDatatype());

                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }
            if (src instanceof OWLDataPropertyExpression &&
                    emptyConceptAttributes.contains(src)) {
                OWLDataSomeValuesFrom newSrc2 = dataFactory.getOWLDataSomeValuesFrom(src, dataFactory.getTopDatatype());
                OWLObjectComplementOf newTgt2 = dataFactory.getOWLObjectComplementOf(newSrc2);
                GC.addVertex(newSrc2);
                GC.addVertex(newTgt2);
                GC.addEdge(newSrc2, newTgt2);

            }
        }
    }
}
