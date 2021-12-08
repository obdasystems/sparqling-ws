package com.obdasystems.sparqling.engine;

import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimpleOwlOntologyDeductiveClosure {

    private OWLOntology inputOntology;

    private OWLOntology expandedOntology;
    private OWLOntologyManager ontologyManager;
    private OWLDataFactory dataFactory;

    private Set<OWLClassExpression> emptyConcepts = null;
    private Set<OWLObjectPropertyExpression> emptyRoles = null;
    private Set<OWLDataPropertyExpression> emptyConceptAttributes = null;

    private SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC = null;
    private SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge> GR = null;
    private SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge> GCA = null;

    public SimpleOwlOntologyDeductiveClosure(OWLOntology ontology) {
        inputOntology = ontology;
        ontologyManager = OWLManager.createOWLOntologyManager();
        dataFactory = ontologyManager.getOWLDataFactory();
    }

    public void computeSimpleDeductiveClosure() {
        emptyConcepts = new HashSet<OWLClassExpression>();
        emptyRoles = new HashSet<OWLObjectPropertyExpression>();
        emptyConceptAttributes = new HashSet<OWLDataPropertyExpression>();

        GC = new SimpleDirectedGraph<OWLClassExpression, DefaultEdge>(DefaultEdge.class);
        GR = new SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge>(DefaultEdge.class);
        GCA = new SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge>(DefaultEdge.class);

        initGraphVertex();
        initQualifiedConceptAttributeDomainTrivialIAs();

        inputOntology.getTBoxAxioms(Imports.INCLUDED).forEach(axiom->{
            if(axiom instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom castAxiom = (OWLSubClassOfAxiom) axiom;
                OWLClassExpression sub = castAxiom.getSubClass();
                OWLClassExpression sup = castAxiom.getSuperClass();
                if(isAcceptedSuperClassExpression(sup) && !(sup.isOWLThing()||sup.isOWLNothing())) {
                    GC.addVertex(sub);
                    GC.addVertex(sup);
                    GC.addEdge(sub, sup);
                }
            }
            else {
                if(axiom instanceof OWLSubObjectPropertyOfAxiom) {
                    OWLSubObjectPropertyOfAxiom castAxiom = (OWLSubObjectPropertyOfAxiom) axiom;
                    OWLObjectPropertyExpression sub = castAxiom.getSubProperty();
                    OWLObjectPropertyExpression sup = castAxiom.getSuperProperty();
                    if( !(sup.isOWLTopObjectProperty()||sup.isOWLBottomObjectProperty())) {
                        GR.addVertex(sub);
                        GR.addVertex(sup);
                        GR.addEdge(sub, sup);
                    }
                }
                else {
                    if(axiom instanceof OWLSubDataPropertyOfAxiom) {
                        OWLSubDataPropertyOfAxiom castAxiom = (OWLSubDataPropertyOfAxiom) axiom;
                        OWLDataPropertyExpression sub = castAxiom.getSubProperty();
                        OWLDataPropertyExpression sup = castAxiom.getSuperProperty();
                        if( !(sup.isOWLTopDataProperty()||sup.isOWLBottomDataProperty())) {
                            GCA.addVertex(sub);
                            GCA.addVertex(sup);
                            GCA.addEdge(sub, sup);
                        }
                    }
                }
            }
        });

        //we proceed until we exhaust our options
        int initialTotalIAs;
        int finalTotalIAs;
        do  {
            //to verify if any new assertion has been added, we confront
            //the number of edges of all graphs (i.e. all the IAs) BEFORE
            //and AFTER the "do" body
            initialTotalIAs = GC.edgeSet().size() +
                    GR.edgeSet().size() +
                    GCA.edgeSet().size() ;


            //Step 1 - Concept IA Graph
            applyAllConceptIARules(GC, GR, GCA);

            //Step 2 - Role IA Graph
            //applyAllRoleIARules(GC, GR);

            //Step 3 - Concept Attribute IA Graph
            //applyAllConceptAttributeIARules(GC, GCA);


            //"do" body termination check
            finalTotalIAs = GC.edgeSet().size() +
                    GR.edgeSet().size() +
                    GCA.edgeSet().size() ;

        } while (initialTotalIAs != finalTotalIAs);
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

    private void initGraphVertex() {
        inputOntology.getClassesInSignature(Imports.INCLUDED).forEach(cl->{
            GC.addVertex(cl);
        });
        inputOntology.getObjectPropertiesInSignature(Imports.INCLUDED).forEach(objProp->{
            GR.addVertex(objProp);
        });
        inputOntology.getDataPropertiesInSignature(Imports.INCLUDED).forEach(dataProp->{
            GCA.addVertex(dataProp);
        });
    }

    private void initQualifiedConceptAttributeDomainTrivialIAs() {
        inputOntology.getDataPropertiesInSignature(Imports.INCLUDED).forEach(dataProp->{
            if(!dataProp.isOWLBottomDataProperty()) {
                OWLDataSomeValuesFrom cad = dataFactory.getOWLDataSomeValuesFrom(dataProp, dataFactory.getTopDatatype());
                GC.addVertex(cad);
                inputOntology.getDatatypesInSignature(Imports.INCLUDED).forEach(dataType->{
                    if(!dataType.isBottomEntity()) {
                        OWLDataSomeValuesFrom qcad = dataFactory.getOWLDataSomeValuesFrom(dataProp, dataType);
                        GC.addVertex(qcad);
                        GC.addEdge(qcad, cad);
                    }
                });
            }
        });
    }

    private void applyAllConceptIARules(SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC,
                                        SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge> GR,
                                        SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge> GCA) {

        //RULE 2: "If (C1 in C2) && (C2 in C3), then (C1 in C3)."
        TransitiveClosure TC = TransitiveClosure.INSTANCE;
        TC.closeSimpleDirectedGraph(GC);

        //outer loop on all edges (concept IAs) of GC
        Object[] allEdges = (GC.edgeSet()).toArray();
        for (int i=0; i<allEdges.length; i++)  {
            DefaultEdge conceptIA = (DefaultEdge)allEdges[i];
            OWLClassExpression src = GC.getEdgeSource(conceptIA);
            OWLClassExpression tgt = GC.getEdgeTarget(conceptIA);

            //no further rule is appliable if conceptIA is totally negated
            if (src instanceof OWLObjectComplementOf)  {
                continue;
            }

            if (tgt instanceof OWLObjectComplementOf) {
                OWLObjectComplementOf nbc = (OWLObjectComplementOf)tgt;
                OWLClassExpression op = ((OWLObjectComplementOf) tgt).getOperand();
                if(op.isOWLThing()) {
                    GC.addEdge(src, dataFactory.getOWLObjectComplementOf(src));
                }
            }

            //pre-rule 21 operation: if this IA is "B in NOT B", mark
            //B as an empty concept
            if (!(src instanceof OWLObjectComplementOf) &&
                    tgt instanceof OWLObjectComplementOf)  {
                if(src.equals(((OWLObjectComplementOf) tgt).getOperand())) {
                    emptyConcepts.add(src);
                }
            }

            //RULE 3: "If (C1 in C2), then (!C2 in !C1)."
            //case 1 of 4: direct-direct
            //&& !(src instanceof QualifiedConceptAttributeDomain || tgt instanceof QualifiedConceptAttributeDomain) removed
            if (!(src instanceof OWLObjectComplementOf) &&
                    !(tgt instanceof OWLObjectComplementOf))  {
                OWLObjectComplementOf newSrc = dataFactory.getOWLObjectComplementOf(tgt);
                OWLObjectComplementOf newTgt = dataFactory.getOWLObjectComplementOf(src);
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }
            //case 2 of 4: direct-negated
            else if (!(src instanceof OWLObjectComplementOf) &&
                    tgt instanceof OWLObjectComplementOf)  {
                OWLClassExpression newSrc = ((OWLObjectComplementOf) tgt).getOperand();
                OWLObjectComplementOf newTgt = dataFactory.getOWLObjectComplementOf(src);
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }
            //case 3 of 4: negated-direct
            else if (src instanceof OWLObjectComplementOf &&
                    !(tgt instanceof OWLObjectComplementOf))  {
                OWLObjectComplementOf newSrc = dataFactory.getOWLObjectComplementOf(tgt);
                OWLClassExpression newTgt = ((OWLObjectComplementOf) src).getOperand();
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }
            //case 4 of 4: negated-negated
            else if (src instanceof OWLObjectComplementOf &&
                    tgt instanceof OWLObjectComplementOf)  {
                OWLClassExpression newSrc = ((OWLObjectComplementOf) tgt).getOperand();
                OWLClassExpression newTgt = ((OWLObjectComplementOf) src).getOperand();
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }
            //end of Rule 3 application


            //Rule 15: "If existsQ1 in NOT existsQ2, then Q1 in NOT Q2."
            if (src instanceof OWLObjectSomeValuesFrom &&
                    tgt instanceof OWLObjectComplementOf)  {
                OWLObjectComplementOf negTgt = (OWLObjectComplementOf)tgt;
                if (negTgt.getOperand() instanceof OWLObjectSomeValuesFrom)  {
                    //once our check is complete, add assertion to GR
                    OWLObjectPropertyExpression newSrc = ((OWLObjectSomeValuesFrom)src).getProperty();
                    OWLObjectPropertyExpression newTgt = ((OWLObjectSomeValuesFrom)negTgt.getOperand()).getProperty();
                    /*TODO come implemento negatedBasicRole (E' necessario?)
                    NegatedBasicRole newNegTgt = new NegatedBasicRole(df, newTgt);
                    GR.addVertex(newSrc);
                    GR.addVertex(newNegTgt);
                    GR.addEdge(newSrc, newNegTgt);
                     */
                }
            }
            //end of Rule 15 application


            //Rule 16-1: "If dom(CA1) in NOT dom(CA2), then CA1 in NOT CA2."
            if (src instanceof OWLDataSomeValuesFrom &&
                    tgt instanceof OWLObjectComplementOf)  {
                OWLObjectComplementOf negTgt = (OWLObjectComplementOf)tgt;
                if (negTgt.getOperand() instanceof OWLDataSomeValuesFrom)  {
                    //once our check is complete, add assertion to GCA
                    OWLDataPropertyExpression newSrc = ((OWLDataSomeValuesFrom)src).getProperty();
                    OWLDataPropertyExpression newTgt = ((OWLDataSomeValuesFrom)negTgt.getOperand()).getProperty();
                    /*TODO come implemento NegatedConceptAttribute (E' necessario?)
                    NegatedConceptAttribute newNegTgt = new NegatedConceptAttribute(df, newTgt);
                    GCA.addVertex(newSrc);
                    GCA.addVertex(newNegTgt);
                    GCA.addEdge(newSrc, newNegTgt);
                     */
                }
            }


            //Rule 18-1: "If existsQ in NOT existsQ OR existsQ- in NOT existsQ-,
            //            then all the following 4 assertions hold true:
            //            - the said 2 assertions;
            //            - Q in NOT Q;
            //            - Q- in NOT Q-."
            //(Since existential RA domains ARE existential basic roles, 18-1 takes care of part of 20-1 too)
            //Rule 20-1: "If existsDom(RA) in NOT existsDom(RA) OR existsDom(RA)- in NOT existsDom(RA)-,
            //            then all the following 6 assertions hold true:
            //            - the said 2 assertions; (taken care of by 18-1)
            //            - dom(RA) in NOT dom(RA); (taken care of by 18-1)
            //            - dom(RA)- in NOT dom(RA)-; (taken care of by 18-1)
            //            - RA in NOT RA;
            //            - rng(RA) in NOT rng(RA)."
            if (src instanceof OWLObjectSomeValuesFrom && tgt instanceof OWLObjectComplementOf)  {
                OWLObjectComplementOf negTgt = (OWLObjectComplementOf)tgt;
                if (negTgt.getOperand() instanceof OWLObjectSomeValuesFrom)  {
                    OWLObjectSomeValuesFrom srcExistential = (OWLObjectSomeValuesFrom)src;
                    OWLObjectSomeValuesFrom tgtExistential = (OWLObjectSomeValuesFrom)negTgt.getOperand();
                    if (srcExistential.equals(tgtExistential))  {
                        //clever passage: if they are equals, one of the two
                        //is already contained in the graphs, thus we only insert the inverted one
                        OWLObjectSomeValuesFrom newSrc = dataFactory.getOWLObjectSomeValuesFrom(srcExistential.getProperty().getInverseProperty(), dataFactory.getOWLThing());
                        OWLObjectComplementOf newTgt = dataFactory.getOWLObjectComplementOf(
                                dataFactory.getOWLObjectSomeValuesFrom(srcExistential.getProperty().getInverseProperty(), dataFactory.getOWLThing()));

                        GC.addVertex(newSrc);
                        GC.addVertex(newTgt);
                        GC.addEdge(newSrc, newTgt);

                        //and we must also insert the other two
                        //first, same sign
                        OWLObjectPropertyExpression newSrc2 = srcExistential.getProperty();
                        OWLObjectPropertyExpression newTgt2 = tgtExistential.getProperty();
                        /*TODO come implemento negatedBasicRole (E' necessario?)
                        NegatedBasicRole newNegTgt2 = new NegatedBasicRole(df, tgtExistential.getBasicRole());
                        GR.addVertex(newSrc2);
                        GR.addVertex(newNegTgt2);
                        GR.addEdge(newSrc2, newNegTgt2);
                         */

                        //secondly, opposite sign
                        OWLObjectPropertyExpression newSrc3 = srcExistential.getProperty().getInverseProperty();
                        OWLObjectPropertyExpression newTgt3 = tgtExistential.getProperty().getInverseProperty();
                        /*TODO come implemento negatedBasicRole (E' necessario?)
                        NegatedBasicRole newNegTgt3 = new NegatedBasicRole(df, tgtExistential.getBasicRole());
                        GR.addVertex(newSrc3);
                        GR.addVertex(newNegTgt3);
                        GR.addEdge(newSrc2, newNegTgt3);
                         */
                    }
                }
            }

            //Rule 19-1: "If dom(CA) in NOT dom(CA),
            //            then all the following 3 assertions hold true:
            //            - the said assertion;
            //            - rng(CA) in NOT rng(CA);
            //            - CA in NOT CA."
            if (src instanceof OWLDataSomeValuesFrom &&
                    tgt instanceof OWLObjectComplementOf)  {
                OWLObjectComplementOf negTgt = (OWLObjectComplementOf)tgt;

                if (negTgt.getOperand() instanceof OWLDataSomeValuesFrom)  {
                    OWLDataSomeValuesFrom srcDom = (OWLDataSomeValuesFrom)src;
                    OWLDataSomeValuesFrom tgtDom = (OWLDataSomeValuesFrom)negTgt.getOperand();
                    if (srcDom.equals(tgtDom))  {
                        //the first assertion is present
                        OWLDataPropertyExpression ca = srcDom.getProperty();
                        //the second
                        /*TODO come implemento NegatedConceptAttribute (E' necessario?)
                        NegatedConceptAttribute newTgt2 = new NegatedConceptAttribute(df, ca);
                        GCA.addVertex(ca);
                        GCA.addVertex(newTgt2);
                        GCA.addEdge(ca, newTgt2);
                         */
                    }
                }
            }

            //ok, up until now it's been mostly dependent on the
            //current GC edge only; for Rule 21 we need something cleverer;
            //hence, the data field emptyConcepts.

            //Rule 21: "If B1 in NOT B1 (empty concept) && B2 in B1, then
            //          B2 in NOT B2 (empty concept)."
            if (src instanceof OWLClassExpression &&
                    tgt instanceof OWLClassExpression)  {
                OWLClassExpression basicTgt = (OWLClassExpression)tgt;
                //we must check if tgt is an empty
                //if it is, it "infects" the src
                if (emptyConcepts.contains(basicTgt))  {
                    OWLClassExpression basicSrc = (OWLClassExpression)src;
                    //mark as empty
                    emptyConcepts.add(basicSrc);
                    OWLObjectComplementOf newTgt = dataFactory.getOWLObjectComplementOf(basicSrc);
                    //insert empty assertion in GC
                    GC.addVertex(basicSrc);
                    GC.addVertex(newTgt);
                    GC.addEdge(basicSrc, newTgt);

                }
            }

            //Rule 28 (Claudio Corona): "If B1 \isa \exists Q.B, then B1 \isa \exists Q"
            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLObjectSomeValuesFrom) {
                if(!((OWLObjectSomeValuesFrom) tgt).getFiller().isOWLThing()) {
                    OWLObjectSomeValuesFrom er = dataFactory.getOWLObjectSomeValuesFrom(((OWLObjectSomeValuesFrom) tgt).getProperty(), dataFactory.getOWLThing());
                    if (!er.equals(src)) {
                        GC.addVertex(er);
                        GC.addEdge(src, er);
                    }
                }
            }

            //Rule 29 (Claudio Corona): "If B1 \isa \exists Q.B2 and Q \isa Q1, then B1 \isa \exists Q1.B2"
            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom qec = (OWLObjectSomeValuesFrom) tgt;
                if(!qec.getFiller().isOWLThing()) {
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

            //Rule 30 (Claudio Corona): "If B1 \isa \exists Q.B2 and B2 \isa B3, then B1 \isa \exists Q1.B3"
            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLObjectSomeValuesFrom)  {
                OWLObjectSomeValuesFrom qec = (OWLObjectSomeValuesFrom) tgt;
                if(!qec.getFiller().isOWLThing()) {
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
/*
            //Rule 31 (Claudio Corona): "If B1 \isa \exists Q.B2 and B2 \isa \not B2, then B1 \isa \not B1"
            if (src instanceof IBasicConcept && tgt instanceof QualifiedExistentialConcept) {
                IBasicConcept bcSrc = (IBasicConcept)src;
                QualifiedExistentialConcept qec = (QualifiedExistentialConcept)tgt;
                IBasicConcept bc = qec.getBasicConcept();
                if (GC.containsVertex(bc)) {
                    Set<DefaultEdge> outgoingEdgesForBc = GC.outgoingEdgesOf(bc);
                    Iterator<DefaultEdge> it = outgoingEdgesForBc.iterator();
                    boolean hasToBeEmpty = false;
                    while(it.hasNext()) {
                        DefaultEdge outgoingEdgeForBc = it.next();
                        IConcept target = GC.getEdgeTarget(outgoingEdgeForBc);
                        if (target instanceof NegatedBasicConcept) {
                            NegatedBasicConcept nBasicConcept = (NegatedBasicConcept)target;
                            IBasicConcept nbc = nBasicConcept.getNegatedConcept();
                            if (bc.equals(nbc)) {
                                hasToBeEmpty = true;
                                break;
                            }
                        }
                    }
                    if (hasToBeEmpty) {
                        IConcept negatedBasicConcept = df.getNegatedBasicConcept(bcSrc);
                        GC.addVertex(negatedBasicConcept);
                        GC.addEdge(bcSrc, negatedBasicConcept);
                    }
                }
            }

            if (fragment instanceof Fragments_OWL2QLContext) {

                //Rule 32 (Claudio Corona): "If B1 \isa \domain CA.datatype, then B1 \isa \domain CA"
                if (src instanceof IBasicConcept && tgt instanceof QualifiedConceptAttributeDomain) {
                    QualifiedConceptAttributeDomain qec = (QualifiedConceptAttributeDomain)tgt;
                    ConceptAttributeDomain cad = qec.getAloneImpliedConceptAttributeDomain();
                    if (!src.equals(cad)) {
                        GC.addVertex(cad);
                        GC.addEdge(src, cad);
                    }
                }

                //Rule 33 (Claudio Corona): "If B1 \isa \domain CA.datatype1 and CA \isa CA1, then B1 \isa \domain CA1.datatype1"
                if (src instanceof IBasicConcept && tgt instanceof QualifiedConceptAttributeDomain) {
                    QualifiedConceptAttributeDomain qec = (QualifiedConceptAttributeDomain)tgt;
                    ConceptAttribute br = qec.getQualifiedConceptAttribute();
                    if (GCA.containsVertex(br)) {
                        Set<DefaultEdge> outgoingEdgesForBr = GCA.outgoingEdgesOf(br);
                        Iterator<DefaultEdge> it = outgoingEdgesForBr.iterator();
                        Set<QualifiedConceptAttributeDomain> toAdd = new HashSet<QualifiedConceptAttributeDomain>();
                        while(it.hasNext()) {
                            DefaultEdge outgoingEdgeForBr = it.next();
                            IConceptAttribute target = GCA.getEdgeTarget(outgoingEdgeForBr);
                            if (target instanceof ConceptAttribute) {
                                ConceptAttribute newRole = (ConceptAttribute)target;
                                QualifiedConceptAttributeDomain newQec = df.getNewQualifiedConceptAttributeDomain(newRole, qec.getQualifyingDataType());
                                if(!src.equals(newQec)) toAdd.add(newQec);
                            }
                        }
                        for (QualifiedConceptAttributeDomain add : toAdd) {
                            GC.addVertex(add);
                            GC.addEdge(src, add);
                        }
                    }
                }

                //Rule 34 (Claudio Corona): "If B1 \isa \domain CA.D1 and D1 \isa D2, then B1 \isa \domain CA.D2"
                if (src instanceof IBasicConcept && tgt instanceof QualifiedConceptAttributeDomain) {
                    QualifiedConceptAttributeDomain qec = (QualifiedConceptAttributeDomain)tgt;
                    AtomicValueSet avs = qec.getQualifyingDataType();
                    if (GVS.containsVertex(avs)) {
                        Set<DefaultEdge> outgoingEdgesForBc = GVS.outgoingEdgesOf(avs);
                        Iterator<DefaultEdge> it = outgoingEdgesForBc.iterator();
                        Set<QualifiedConceptAttributeDomain> toAdd = new HashSet<QualifiedConceptAttributeDomain>();
                        while(it.hasNext()) {
                            DefaultEdge outgoingEdgeForBc = it.next();
                            IValueSet target = GVS.getEdgeTarget(outgoingEdgeForBc);
                            if (target instanceof AtomicValueSet) {
                                AtomicValueSet newAvs = (AtomicValueSet)target;
                                QualifiedConceptAttributeDomain newQec = df.getNewQualifiedConceptAttributeDomain(qec.getQualifiedConceptAttribute(), newAvs);
                                if(!src.equals(newQec)) toAdd.add(newQec);
                            }
                        }
                        for (QualifiedConceptAttributeDomain add : toAdd) {
                            GC.addVertex(add);
                            GC.addEdge(src, add);
                        }
                    }
                }

            }

//			//Rule 35 (Claudio Corona): "If A \isa \exists R and \exists R^- ISA B, A \isa \exists R.B //it can be disabled for optimization
//			if (src instanceof IBasicConcept && tgt instanceof ExistentialRole) {
//				ExistentialRole er = (ExistentialRole)tgt;
//				IBasicRole br = er.getBasicRole();
//				IBasicRole inverseBr = br.getInverted();
//				ExistentialRole inverseEr =er.getDomainFactory().getExistentialRole(inverseBr);
//				if (GC.containsVertex(inverseEr)) {
//					Set<DefaultEdge> outgoingEdgesForBc = GC.outgoingEdgesOf(inverseEr);
//					Iterator<DefaultEdge> it = outgoingEdgesForBc.iterator();
//					Set<QualifiedExistentialConcept> toAdd = new HashSet<QualifiedExistentialConcept>();
//					while(it.hasNext()) {
//						DefaultEdge outgoingEdgeForBc = it.next();
//						IConcept target = GC.getEdgeTarget(outgoingEdgeForBc);
//						if (target instanceof IBasicConcept) {
//							IBasicConcept newBC = (IBasicConcept)target;
//							QualifiedExistentialConcept newQec = df.getNewQualifiedExistentialConcept(br, newBC);
//							toAdd.add(newQec);
//						}
//					}
//					for(QualifiedExistentialConcept qec : toAdd) {
//						GC.addVertex(qec);
//						GC.addEdge(src, qec);
//					}
//				}
//			}

            //Rule 35 (Claudio Corona): "If \exists R^- ISA B, then \exists R \isa \exists R.B"
            if (src instanceof ExistentialRole && tgt instanceof IBasicConcept) {
                IBasicConcept ibc = null;
                if (fragment instanceof Fragments_OWL2QLContext && !(tgt instanceof AtomicConcept)) {

                }
                else {
                    ibc = (IBasicConcept)tgt;
                }
                if (ibc != null) {
                    ExistentialRole er = (ExistentialRole)src;
                    IBasicRole ibr = er.getBasicRole();
                    IBasicRole inverseIbr = ibr.getInverted();
                    ExistentialRole inverseEr = er.getDomainFactory().getExistentialRole(inverseIbr);
                    QualifiedExistentialConcept qec = er.getDomainFactory().getNewQualifiedExistentialConcept(inverseIbr, ibc);
                    GC.addVertex(inverseEr);
                    GC.addVertex(qec);
                    GC.addEdge(inverseEr, qec);
                }
            }

//			//Rule 36 (Claudio Corona): "If A \isa \domain CA and \range CA ISA Datatype, A \isa \domain CA.Datatype //it can be disabled for optimization
//			if (src instanceof IBasicConcept && tgt instanceof ConceptAttributeDomain) {
//				ConceptAttributeDomain cad = (ConceptAttributeDomain)tgt;
//				ConceptAttribute ca = cad.getConceptAttribute();
//				ConceptAttributeRange car = ca.getDomainFactory().getConceptAttributeRange(ca);
//				if (GVS.containsVertex(car)) {
//					Set<DefaultEdge> outgoingEdgesForBc = GVS.outgoingEdgesOf(car);
//					Iterator<DefaultEdge> it = outgoingEdgesForBc.iterator();
//					Set<QualifiedConceptAttributeDomain> toAdd = new HashSet<QualifiedConceptAttributeDomain>();
//					while(it.hasNext()) {
//						DefaultEdge outgoingEdgeForBc = it.next();
//						IValueSet target = GVS.getEdgeTarget(outgoingEdgeForBc);
//						if (target instanceof AtomicValueSet) {
//							AtomicValueSet newAVS = (AtomicValueSet)target;
//							QualifiedConceptAttributeDomain newQec = df.getNewQualifiedConceptAttributeDomain(ca, newAVS);
//							toAdd.add(newQec);
//						}
//					}
//					for(QualifiedConceptAttributeDomain qec : toAdd) {
//						if (!src.equals(qec)) {
//							GC.addVertex(qec);
//							GC.addEdge(src, qec);
//						}
//					}
//				}
//			}

//			//Rule 37 (Claudio Corona): "If B1 \isa \domain CA.D1 and \range CA \isa \not D1, then \domain CA.D1 \isa \not \domain CA.D1"
//			if (src instanceof IBasicConcept && tgt instanceof QualifiedConceptAttributeDomain) {
//				QualifiedConceptAttributeDomain qec = (QualifiedConceptAttributeDomain)tgt;
//				AtomicValueSet avs = qec.getQualifyingDataType();
//				ConceptAttributeRange car = qec.getDomainFactory().getConceptAttributeRange(qec.getQualifiedConceptAttribute());
//				if (GVS.containsVertex(car)) {
//					Set<DefaultEdge> outgoingEdgesForBc = GVS.outgoingEdgesOf(car);
//					Iterator<DefaultEdge> it = outgoingEdgesForBc.iterator();
//					boolean isEmpty = false;
//					while(it.hasNext()) {
//						DefaultEdge outgoingEdgeForBc = it.next();
//						IValueSet target = GVS.getEdgeTarget(outgoingEdgeForBc);
//						if (target instanceof NegatedBasicValueSet) {
//							NegatedBasicValueSet newNAvs = (NegatedBasicValueSet)target;
//							if (newNAvs.getNegatedValueSet().equals(avs)) {
//								isEmpty = true;
//								break;
//							}
//						}
//					}
//					if (isEmpty) {
//						IConcept nqcad = avs.getDomainFactory().getNegatedBasicConcept((IBasicConcept)qec);
//						GC.addVertex(nqcad);
//						GC.addEdge(qec, nqcad);
//					}
//				}
//
//			}

            if (fragment instanceof Fragments_OWL2QLContext) {

                //Rule 37.1 "If \domain CA.E \isa \not \domain CA.E, then \range CA \isa \not E
                if (src instanceof QualifiedConceptAttributeDomain && tgt instanceof NegatedBasicConcept) {
                    QualifiedConceptAttributeDomain qcad = (QualifiedConceptAttributeDomain)src;
                    NegatedBasicConcept nbc = (NegatedBasicConcept)tgt;
                    if (nbc.getNegatedConcept().equals(qcad)) {
                        ConceptAttributeRange car = qcad.getDomainFactory().getConceptAttributeRange(qcad.getQualifiedConceptAttribute());
                        AtomicValueSet avs = qcad.getQualifyingDataType();
                        IValueSet nbvs = qcad.getDomainFactory().getNegatedBasicValueSet(avs);
                        GVS.addVertex(car);
                        GVS.addVertex(nbvs);
                        GVS.addEdge(car, nbvs);
                    }
                }

            }


            //Rule 38 (Claudio Corona): "If B1 \isa \exists R.B and \exists R^- \isa \not B, then B1 \isa \not B1"
            if (src instanceof IBasicConcept && tgt instanceof QualifiedExistentialConcept) {
                QualifiedExistentialConcept qec = (QualifiedExistentialConcept)tgt;
                IBasicConcept bc = qec.getBasicConcept();
                IBasicRole br = qec.getBasicRole();
                ExistentialRole inverseEr = qec.getDomainFactory().getExistentialRole(br.getInverted());
                if (GC.containsVertex(inverseEr)) {
                    Set<DefaultEdge> outgoingEdgesForBc = GC.outgoingEdgesOf(inverseEr);
                    Iterator<DefaultEdge> it = outgoingEdgesForBc.iterator();
                    boolean isEmpty = false;
                    while(it.hasNext()) {
                        DefaultEdge outgoingEdgeForBc = it.next();
                        IConcept target = GC.getEdgeTarget(outgoingEdgeForBc);
                        if (target instanceof NegatedBasicConcept) {
                            NegatedBasicConcept newNAvs = (NegatedBasicConcept)target;
                            if (newNAvs.getNegatedConcept().equals(bc)) {
                                isEmpty = true;
                                break;
                            }
                        }
                    }
                    if (isEmpty) {
                        IConcept nbc = qec.getDomainFactory().getNegatedBasicConcept((IBasicConcept)src);
                        GC.addVertex(nbc);
                        GC.addEdge(src, nbc);
                    }
                }

            }
*/
            //end of all rules involving checks on Concept IAs
        }

    }
}
