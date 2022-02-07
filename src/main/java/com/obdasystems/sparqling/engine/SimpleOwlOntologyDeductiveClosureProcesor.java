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

    private OWLOntology expandedOntology;
    private OWLOntologyManager ontologyManager;
    private OWLDataFactory dataFactory;

    private Set<OWLClassExpression> emptyConcepts = null;
    private Set<OWLObjectPropertyExpression> emptyRoles = null;
    private Set<OWLDataPropertyExpression> emptyConceptAttributes = null;

    private SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC = null;
    private SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge> GR = null;
    private SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge> GCA = null;

    //tiene traccia di tutti gli assiomi nella chiusura che indichino che una classe C è il dominio di un attributo a_1
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
        initQualifiedConceptAttributeDomainTrivialIAs();

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
                        GC.addVertex(sub);
                        GC.addVertex(sup);
                        GC.addEdge(sub, sup);
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
                                            GC.addVertex(first);
                                            GC.addVertex(second);
                                            GC.addEdge(first, second);
                                            GC.addEdge(second, first);
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
                                            //TODO Come rappresento ObjectProperty negate? (E' necessario?)
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
                                                    //TODO Come rappresento DataProperty negate? (E' necessario?)
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
                                                            //inserisci arco exist R^- --> C
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
                                                                //inserisci arco exist U --> C
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

        //we proceed until we exhaust our options
        int initialTotalIAs;
        int finalTotalIAs;
        do {
            //to verify if any new assertion has been added, we confront
            //the number of edges of all graphs (i.e. all the IAs) BEFORE
            //and AFTER the "do" body
            initialTotalIAs = GC.edgeSet().size() +
                    GR.edgeSet().size() +
                    GCA.edgeSet().size();


            //Step 1 - Concept IA Graph
            applyAllConceptIARules(GC, GR, GCA);

            //Step 2 - Role IA Graph
            applyAllRoleIARules(GC, GR);

            //Step 3 - Concept Attribute IA Graph
            applyAllConceptAttributeIARules(GC, GCA);


            //"do" body termination check
            finalTotalIAs = GC.edgeSet().size() +
                    GR.edgeSet().size() +
                    GCA.edgeSet().size();

        } while (initialTotalIAs != finalTotalIAs);

        /*System.out.println("##### POST #####");
        printGraph(GC);*/

        //checkForTBoxInconsistentcies
        //addEventualInconsistency();

        //as a finishing touch, we must infer all trivials
        //Set<OWLAxiom> trivials = inferAllTrivials();

        //And finally, we must extract all assertions from the five graphs
        Set<OWLAxiom> simpleDeductiveClosure = extractAllAssertionsFromGraphs(GC, GR, GCA);

        //TODO ora costruisci insieme dataPropertyDomainAxiom (e futuri su ObjectProperties che però sono complicati visto che devi capire ogni volta
        // se ruolo R è importante per una classe C perchè C è dominio o range di R)
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
        //our result
        Set<OWLAxiom> result = new HashSet<OWLAxiom>();
        //Inspection of GC
        Set<DefaultEdge> allConceptIAs = GC.edgeSet();
        Iterator<DefaultEdge> it = allConceptIAs.iterator();
        while (it.hasNext()) {
            DefaultEdge edgeIA = it.next();
            OWLSubClassOfAxiom newIA = dataFactory.getOWLSubClassOfAxiom(GC.getEdgeSource(edgeIA), GC.getEdgeTarget(edgeIA));
            result.add(newIA);
        }//end of GC

        //Inspection of GR
        Set<DefaultEdge> allRoleIAs = GR.edgeSet();
        Iterator<DefaultEdge> it2 = allRoleIAs.iterator();
        while (it2.hasNext()) {
            DefaultEdge edgeIA = it2.next();
            OWLSubObjectPropertyOfAxiom newIA = dataFactory.getOWLSubObjectPropertyOfAxiom(GR.getEdgeSource(edgeIA), GR.getEdgeTarget(edgeIA));
            result.add(newIA);
        }

        //Inspection of GCA
        Set<DefaultEdge> allCAIAs = GCA.edgeSet();
        Iterator<DefaultEdge> it3 = allCAIAs.iterator();
        while (it3.hasNext()) {
            DefaultEdge edgeIA = it3.next();
            OWLSubDataPropertyOfAxiom newIA = dataFactory.getOWLSubDataPropertyOfAxiom(GCA.getEdgeSource(edgeIA), GCA.getEdgeTarget(edgeIA));
            result.add(newIA);
        }//end of GCA

        return result;

    }

    private Set<OWLAxiom> inferAllTrivials() {
        Set<OWLAxiom> result = new HashSet<OWLAxiom>();

        Set<OWLClass> allConcepts = inputOntology.getClassesInSignature();
        Set<OWLObjectProperty> allRoles = inputOntology.getObjectPropertiesInSignature();
        Set<OWLDataProperty> allConceptAttributes = inputOntology.getDataPropertiesInSignature();

        /*TODO utile?
        if (fragment instanceof Fragments_OWL2QLContext) {
            for(QualifiedConceptAttributeDomain qcad : ((Fragments_OWL2QLContext)fragment).getAllPossibleQualifiedConceptAttributeDomain(alphabet)) {
                //qcad in qcad
                InclusionAssertion trivialAssertion = null;
                try {
                    trivialAssertion = new InclusionAssertion(df, qcad, qcad);
                } catch (InclusionAssertionException e) {
                    throw new RuntimeException("It's impossible to build a wrong inclusion assertion if the TBox is correctly build!");
                }
                result.add(trivialAssertion);

                //			//!qcad in !qcad
                //			TotalNegatedInclusionAssertion tnAssertion = new TotalNegatedInclusionAssertion(df,
                //					        new NegatedBasicConcept(df, qcad), new NegatedBasicConcept(df, qcad));
                //			result.add(tnAssertion);
            }
        }
         */

        //Concepts
        Iterator<OWLClass> it = allConcepts.iterator();
        while (it.hasNext()) {
            OWLClass c = it.next();
            //A in A
            OWLSubClassOfAxiom trivialAssertion = dataFactory.getOWLSubClassOfAxiom(c, c);
            result.add(trivialAssertion);

            //!A in !A
            OWLObjectComplementOf notC = dataFactory.getOWLObjectComplementOf(c);
            OWLSubClassOfAxiom tnAssertion = dataFactory.getOWLSubClassOfAxiom(notC, notC);
            result.add(tnAssertion);

            //A in TopC
//			InclusionAssertion trivialAssertion2 = new InclusionAssertion(df, c, df.getAtomicConcept("TopC"));
//			result.add(trivialAssertion2);
        }

        //Roles
        Iterator<OWLObjectProperty> it2 = allRoles.iterator();
        while (it2.hasNext()) {
            OWLObjectProperty r = it2.next();

            //P in P
            OWLSubObjectPropertyOfAxiom trivialAssertion = dataFactory.getOWLSubObjectPropertyOfAxiom(r, r);
            result.add(trivialAssertion);

            //P- in P-
            OWLSubObjectPropertyOfAxiom trivialAssertion2 = dataFactory.getOWLSubObjectPropertyOfAxiom(r.getInverseProperty(), r.getInverseProperty());
            result.add(trivialAssertion2);

            //existsP in existsP
            OWLObjectSomeValuesFrom exR = dataFactory.getOWLObjectSomeValuesFrom(r, dataFactory.getOWLThing());
            OWLSubClassOfAxiom trivialAssertion3 = dataFactory.getOWLSubClassOfAxiom(exR, exR);
            result.add(trivialAssertion3);

            //!existsP in !existsP
            OWLObjectComplementOf notexR = dataFactory.getOWLObjectComplementOf(exR);
            OWLSubClassOfAxiom tnAssertion = dataFactory.getOWLSubClassOfAxiom(notexR, notexR);
            result.add(tnAssertion);

            //existsP- in existsP-
            OWLObjectSomeValuesFrom exRInv = dataFactory.getOWLObjectSomeValuesFrom(r.getInverseProperty(), dataFactory.getOWLThing());
            OWLSubClassOfAxiom trivialAssertion4 = dataFactory.getOWLSubClassOfAxiom(exRInv, exRInv);
            result.add(trivialAssertion3);
            result.add(trivialAssertion4);

            //!existsP- in !existsP-
            OWLObjectComplementOf notexRInv = dataFactory.getOWLObjectComplementOf(exRInv);
            OWLSubClassOfAxiom tnAssertion4 = dataFactory.getOWLSubClassOfAxiom(notexRInv, notexRInv);
            result.add(tnAssertion4);
        }

        //Concept Attributes
        Iterator<OWLDataProperty> it3 = allConceptAttributes.iterator();
        while (it3.hasNext()) {
            OWLDataProperty ca = it3.next();

            //CA in CA
            OWLSubDataPropertyOfAxiom trivialAssertion = dataFactory.getOWLSubDataPropertyOfAxiom(ca, ca);
            result.add(trivialAssertion);

            //dom(CA) in dom(CA)
            OWLDataSomeValuesFrom exR = dataFactory.getOWLDataSomeValuesFrom(ca, dataFactory.getTopDatatype());
            OWLSubClassOfAxiom trivialAssertion2 = dataFactory.getOWLSubClassOfAxiom(exR, exR);
            result.add(trivialAssertion2);
            result.add(trivialAssertion2);

            //!dom(CA) in !dom(CA)
            OWLObjectComplementOf notexR = dataFactory.getOWLObjectComplementOf(exR);
            OWLSubClassOfAxiom tnAssertion = dataFactory.getOWLSubClassOfAxiom(notexR, notexR);
            result.add(tnAssertion);
        }
        return result;
    }

    private void addEventualInconsistency() {
        //inconsistencies can derive from: 1) empty Top 2) empty reflexive roles
        if (this.emptyConcepts.contains(dataFactory.getOWLThing())) {
            //it's already ok
        } else if (this.emptyConceptAttributes.contains(dataFactory.getOWLTopDataProperty())) {
            addEmptyTopC();
        } else if (this.emptyRoles.contains(dataFactory.getOWLTopObjectProperty())) {
            addEmptyTopC();
        }
    }

    private void addEmptyTopC() {
        OWLClass topConcept = dataFactory.getOWLThing();
        OWLClassExpression negatedBasicConcept = dataFactory.getOWLObjectComplementOf(topConcept);
        GC.addVertex(topConcept);
        GC.addVertex(negatedBasicConcept);
        GC.addEdge(topConcept, negatedBasicConcept);
        this.emptyConcepts.add(topConcept);
    }

    private boolean isAcceptedSuperClassExpression(OWLClassExpression expr) {
        if (expr instanceof OWLClass) {
            return true;
        }
        if (expr instanceof OWLObjectSomeValuesFrom) {
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

        //RULE 2: "If (C1 in C2) && (C2 in C3), then (C1 in C3)."
        TransitiveClosure TC = TransitiveClosure.INSTANCE;
        TC.closeSimpleDirectedGraph(GC);

        //outer loop on all edges (concept IAs) of GC
        Object[] allEdges = (GC.edgeSet()).toArray();
        for (int i = 0; i < allEdges.length; i++) {
            DefaultEdge conceptIA = (DefaultEdge) allEdges[i];
            OWLClassExpression src = GC.getEdgeSource(conceptIA);
            OWLClassExpression tgt = GC.getEdgeTarget(conceptIA);

            //no further rule is appliable if conceptIA is totally negated
            if (src instanceof OWLObjectComplementOf) {
                continue;
            }

            if (tgt instanceof OWLObjectComplementOf) {
                OWLObjectComplementOf nbc = (OWLObjectComplementOf) tgt;
                OWLClassExpression op = ((OWLObjectComplementOf) tgt).getOperand();
                if (op.isOWLThing()) {
                    GC.addEdge(src, dataFactory.getOWLObjectComplementOf(src));
                }
            }

            //pre-rule 21 operation: if this IA is "B in NOT B", mark
            //B as an empty concept
            /*TODO SKIP
            if (!(src instanceof OWLObjectComplementOf) &&
                    tgt instanceof OWLObjectComplementOf) {
                if (src.equals(((OWLObjectComplementOf) tgt).getOperand())) {
                    emptyConcepts.add(src);
                }
            }*/

            //RULE 3: "If (C1 in C2), then (!C2 in !C1)."
            //case 1 of 4: direct-direct
            //&& !(src instanceof QualifiedConceptAttributeDomain || tgt instanceof QualifiedConceptAttributeDomain) removed
            if (!(src instanceof OWLObjectComplementOf) &&
                    !(tgt instanceof OWLObjectComplementOf)) {
                OWLObjectComplementOf newSrc = dataFactory.getOWLObjectComplementOf(tgt);
                OWLObjectComplementOf newTgt = dataFactory.getOWLObjectComplementOf(src);
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }
            //case 2 of 4: direct-negated
            else if (!(src instanceof OWLObjectComplementOf) &&
                    tgt instanceof OWLObjectComplementOf) {
                OWLClassExpression newSrc = ((OWLObjectComplementOf) tgt).getOperand();
                OWLObjectComplementOf newTgt = dataFactory.getOWLObjectComplementOf(src);
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }
            //case 3 of 4: negated-direct
            else if (src instanceof OWLObjectComplementOf &&
                    !(tgt instanceof OWLObjectComplementOf)) {
                OWLObjectComplementOf newSrc = dataFactory.getOWLObjectComplementOf(tgt);
                OWLClassExpression newTgt = ((OWLObjectComplementOf) src).getOperand();
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }
            //case 4 of 4: negated-negated
            else if (src instanceof OWLObjectComplementOf &&
                    tgt instanceof OWLObjectComplementOf) {
                OWLClassExpression newSrc = ((OWLObjectComplementOf) tgt).getOperand();
                OWLClassExpression newTgt = ((OWLObjectComplementOf) src).getOperand();
                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
            }
            //end of Rule 3 application


            //Rule 15: "If existsQ1 in NOT existsQ2, then Q1 in NOT Q2."
            /*TODO SKIP
            if (src instanceof OWLObjectSomeValuesFrom &&
                    tgt instanceof OWLObjectComplementOf) {
                OWLObjectComplementOf negTgt = (OWLObjectComplementOf) tgt;
                if (negTgt.getOperand() instanceof OWLObjectSomeValuesFrom) {
                    //once our check is complete, add assertion to GR
                    OWLObjectPropertyExpression newSrc = ((OWLObjectSomeValuesFrom) src).getProperty();
                    OWLObjectPropertyExpression newTgt = ((OWLObjectSomeValuesFrom) negTgt.getOperand()).getProperty();

                    NegatedBasicRole newNegTgt = new NegatedBasicRole(df, newTgt);
                    GR.addVertex(newSrc);
                    GR.addVertex(newNegTgt);
                    GR.addEdge(newSrc, newNegTgt);

                }
            }
            */
            //end of Rule 15 application


            //Rule 16-1: "If dom(CA1) in NOT dom(CA2), then CA1 in NOT CA2."
            /*TODO SKIP
            if (src instanceof OWLDataSomeValuesFrom &&
                    tgt instanceof OWLObjectComplementOf) {
                OWLObjectComplementOf negTgt = (OWLObjectComplementOf) tgt;
                if (negTgt.getOperand() instanceof OWLDataSomeValuesFrom) {
                    //once our check is complete, add assertion to GCA
                    OWLDataPropertyExpression newSrc = ((OWLDataSomeValuesFrom) src).getProperty();
                    OWLDataPropertyExpression newTgt = ((OWLDataSomeValuesFrom) negTgt.getOperand()).getProperty();
                    NegatedConceptAttribute newNegTgt = new NegatedConceptAttribute(df, newTgt);
                    GCA.addVertex(newSrc);
                    GCA.addVertex(newNegTgt);
                    GCA.addEdge(newSrc, newNegTgt);

                }
            }
            */

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
            if (src instanceof OWLObjectSomeValuesFrom && tgt instanceof OWLObjectComplementOf) {
                OWLObjectComplementOf negTgt = (OWLObjectComplementOf) tgt;
                if (negTgt.getOperand() instanceof OWLObjectSomeValuesFrom) {
                    OWLObjectSomeValuesFrom srcExistential = (OWLObjectSomeValuesFrom) src;
                    OWLObjectSomeValuesFrom tgtExistential = (OWLObjectSomeValuesFrom) negTgt.getOperand();
                    if (srcExistential.equals(tgtExistential)) {
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
            /*TODO SKIP
            if (src instanceof OWLDataSomeValuesFrom &&
                    tgt instanceof OWLObjectComplementOf) {
                OWLObjectComplementOf negTgt = (OWLObjectComplementOf) tgt;

                if (negTgt.getOperand() instanceof OWLDataSomeValuesFrom) {
                    OWLDataSomeValuesFrom srcDom = (OWLDataSomeValuesFrom) src;
                    OWLDataSomeValuesFrom tgtDom = (OWLDataSomeValuesFrom) negTgt.getOperand();
                    if (srcDom.equals(tgtDom)) {
                        //the first assertion is present
                        OWLDataPropertyExpression ca = srcDom.getProperty();
                        //the second
                        NegatedConceptAttribute newTgt2 = new NegatedConceptAttribute(df, ca);
                        GCA.addVertex(ca);
                        GCA.addVertex(newTgt2);
                        GCA.addEdge(ca, newTgt2);

                    }
                }
            }*/

            //ok, up until now it's been mostly dependent on the
            //current GC edge only; for Rule 21 we need something cleverer;
            //hence, the data field emptyConcepts.

            //Rule 21: "If B1 in NOT B1 (empty concept) && B2 in B1, then
            //          B2 in NOT B2 (empty concept)."
            /*TODO SKIP
            if (src instanceof OWLClassExpression &&
                    tgt instanceof OWLClassExpression) {
                OWLClassExpression basicTgt = (OWLClassExpression) tgt;
                //we must check if tgt is an empty
                //if it is, it "infects" the src
                if (emptyConcepts.contains(basicTgt)) {
                    OWLClassExpression basicSrc = (OWLClassExpression) src;
                    //mark as empty
                    emptyConcepts.add(basicSrc);
                    OWLObjectComplementOf newTgt = dataFactory.getOWLObjectComplementOf(basicSrc);
                    //insert empty assertion in GC
                    GC.addVertex(basicSrc);
                    GC.addVertex(newTgt);
                    GC.addEdge(basicSrc, newTgt);

                }
            }*/

            //Rule 28 (Claudio Corona): "If B1 \isa \exists Q.B, then B1 \isa \exists Q"
            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLObjectSomeValuesFrom) {
                if (!((OWLObjectSomeValuesFrom) tgt).getFiller().isOWLThing()) {
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

            //Rule 30 (Claudio Corona): "If B1 \isa \exists Q.B2 and B2 \isa B3, then B1 \isa \exists Q1.B3"
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

            //Rule 31 (Claudio Corona): "If B1 \isa \exists Q.B2 and B2 \isa \not B2, then B1 \isa \not B1"
            /*TODO SKIP
            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom qec = (OWLObjectSomeValuesFrom) tgt;
                if (!qec.getFiller().isOWLThing()) {
                    OWLClassExpression bc = qec.getFiller();
                    if (GC.containsVertex(bc)) {
                        Set<DefaultEdge> outgoingEdgesForBc = GC.outgoingEdgesOf(bc);
                        Iterator<DefaultEdge> it = outgoingEdgesForBc.iterator();
                        boolean hasToBeEmpty = false;
                        while (it.hasNext()) {
                            DefaultEdge outgoingEdgeForBc = it.next();
                            OWLClassExpression target = GC.getEdgeTarget(outgoingEdgeForBc);
                            if (target instanceof OWLObjectComplementOf) {
                                OWLObjectComplementOf nBasicConcept = (OWLObjectComplementOf) target;
                                OWLClassExpression nbc = nBasicConcept.getOperand();
                                if (bc.equals(nbc)) {
                                    hasToBeEmpty = true;
                                    break;
                                }
                            }
                        }
                        if (hasToBeEmpty) {
                            OWLObjectComplementOf negatedBasicConcept = dataFactory.getOWLObjectComplementOf(src);
                            GC.addVertex(negatedBasicConcept);
                            GC.addEdge(src, negatedBasicConcept);
                        }
                    }
                }
            }*/

            //Rule 32 (Claudio Corona): "If B1 \isa \domain CA.datatype, then B1 \isa \domain CA"
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

            //Rule 33 (Claudio Corona): "If B1 \isa \domain CA.datatype1 and CA \isa CA1, then B1 \isa \domain CA1.datatype1"
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

            //Rule 35 (Claudio Corona): "If \exists R^- ISA B, then \exists R \isa \exists R.B"
            /*TODO SKIP
            if (src instanceof OWLObjectSomeValuesFrom && tgt instanceof OWLClassExpression) {
                OWLObjectSomeValuesFrom er = (OWLObjectSomeValuesFrom) src;
                if (er.getFiller().isOWLThing()) {
                    OWLObjectPropertyExpression ibr = er.getProperty();
                    OWLObjectPropertyExpression inverseIbr = ibr.getInverseProperty();
                    OWLObjectSomeValuesFrom inverseEr = dataFactory.getOWLObjectSomeValuesFrom(inverseIbr, dataFactory.getOWLThing());
                    OWLObjectSomeValuesFrom qec = dataFactory.getOWLObjectSomeValuesFrom(inverseIbr, tgt);
                    GC.addVertex(inverseEr);
                    GC.addVertex(qec);
                    GC.addEdge(inverseEr, qec);

                }
            }*/

            //Rule 38 (Claudio Corona): "If B1 \isa \exists R.B and \exists R^- \isa \not B, then B1 \isa \not B1"
            /*TODO SKIP
            if (!(src instanceof OWLObjectComplementOf) && tgt instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom qec = (OWLObjectSomeValuesFrom) tgt;
                if (!qec.getFiller().isOWLThing()) {
                    OWLClassExpression bc = qec.getFiller();
                    OWLObjectPropertyExpression br = qec.getProperty();
                    OWLObjectSomeValuesFrom inverseEr = dataFactory.getOWLObjectSomeValuesFrom(br.getInverseProperty(), dataFactory.getOWLThing());
                    if (GC.containsVertex(inverseEr)) {
                        Set<DefaultEdge> outgoingEdgesForBc = GC.outgoingEdgesOf(inverseEr);
                        Iterator<DefaultEdge> it = outgoingEdgesForBc.iterator();
                        boolean isEmpty = false;
                        while (it.hasNext()) {
                            DefaultEdge outgoingEdgeForBc = it.next();
                            OWLClassExpression target = GC.getEdgeTarget(outgoingEdgeForBc);
                            if (target instanceof OWLObjectComplementOf) {
                                OWLObjectComplementOf newNAvs = (OWLObjectComplementOf) target;
                                if (newNAvs.getOperand().equals(bc)) {
                                    isEmpty = true;
                                    break;
                                }
                            }
                        }
                        if (isEmpty) {
                            OWLClassExpression nbc = dataFactory.getOWLObjectComplementOf(src);
                            GC.addVertex(nbc);
                            GC.addEdge(src, nbc);
                        }
                    }
                }
            }
            */
        }
    }

    private void applyAllRoleIARules(SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC,
                                     SimpleDirectedGraph<OWLObjectPropertyExpression, DefaultEdge> GR) {

        //RULE 4: "If (R1 in R2) && (R2 in R3), then (R1 in R3)."
        TransitiveClosure TC = TransitiveClosure.INSTANCE;
        TC.closeSimpleDirectedGraph(GR);

        //outer loop on all edges (role IAs) of GR
        Object[] allEdges = (GR.edgeSet()).toArray();
        for (int i = 0; i < allEdges.length; i++) {
            DefaultEdge roleIA = (DefaultEdge) allEdges[i];
            OWLObjectPropertyExpression src = GR.getEdgeSource(roleIA);
            OWLObjectPropertyExpression tgt = GR.getEdgeTarget(roleIA);

            //no further rule is appliable if roleIA is totally negated
            /*TODO Non esiste controparte di NegatedBasicRole
            if (src instanceof NegatedBasicRole)  {
                continue;
            }

            if (tgt instanceof NegatedBasicRole) {
                NegatedBasicRole nbc = (NegatedBasicRole)tgt;
                IBasicRole bc = nbc.getNegatedRole();
                if (bc instanceof AtomicRole) {
                    AtomicRole ac = (AtomicRole)bc;
                    if (ac.equals(this.alphabet.getTopRole())) {
                        GR.addEdge(src, df.getNegatedBasicRole((IBasicRole) src));
                    }
                }
            }
            */

            //reflexivity assertions
            /*TODO Inutile
            if (src instanceof AtomicRole && tgt instanceof AtomicRole) {
                AtomicRole bSrc = (AtomicRole) src;
                AtomicRole bTgt = (AtomicRole) tgt;
                ReflexivityAssertion reflAss = df.getReflexivityAssertion(bSrc);
                // if (R1 \ISA R2) AND (R1 is reflexive) then (R2 is a reflexive role)
                if (this.reflexivityAssertion.contains(reflAss) && !(this.reflexivityAssertion.contains(df.getReflexivityAssertion(bTgt)))) {
                    reflexivityRulesOnAddition(bTgt, df, this.reflexivityAssertion, this.GC);
                }
            }
            */

            //pre-rule 22 operation: if this IA is "Q in NOT Q", mark
            //Q as an empty role
            /*TODO SKIP
            if (src instanceof IBasicRole &&
                    tgt instanceof NegatedBasicRole)  {
                IBasicRole basicSrc = (IBasicRole)src;
                IBasicRole negTgt = ((NegatedBasicRole)tgt).getNegatedRole();

                if (basicSrc.equals(negTgt))  {
                    treatmentForEmptyBasicRole(basicSrc);
                }
            }
             */

            //RULE 5: "If (G1 in G2), then (!G2 in !G1)."
            //case 1 of 4: positive-positive
            /*TODO SKIP
            if (src instanceof IBasicRole &&
                    tgt instanceof IBasicRole)  {
                NegatedBasicRole newSrc = new NegatedBasicRole(df, (IBasicRole)tgt);
                NegatedBasicRole newTgt = new NegatedBasicRole(df, (IBasicRole)src);

                GR.addVertex(newSrc);
                GR.addVertex(newTgt);
                GR.addEdge(newSrc, newTgt);
            }
            //case 2 of 4: positive-negated
            else if (src instanceof IBasicRole &&
                    tgt instanceof NegatedBasicRole)  {
                IBasicRole newSrc = ((NegatedBasicRole)tgt).getNegatedRole();
                NegatedBasicRole newTgt = new NegatedBasicRole(df, (IBasicRole)src);

                GR.addVertex(newSrc);
                GR.addVertex(newTgt);
                GR.addEdge(newSrc, newTgt);
            }
            //case 3 of 4: negated-positive
            else if (src instanceof NegatedBasicRole &&
                    tgt instanceof IBasicRole)  {
                NegatedBasicRole newSrc = new NegatedBasicRole(df, (IBasicRole)tgt);
                IBasicRole newTgt = ((NegatedBasicRole)src).getNegatedRole();

                GR.addVertex(newSrc);
                GR.addVertex(newTgt);
                GR.addEdge(newSrc, newTgt);
            }
            //case 4 of 4: negated-negated
            else if (src instanceof NegatedBasicRole &&
                    tgt instanceof NegatedBasicRole)  {
                IBasicRole newSrc = ((NegatedBasicRole)tgt).getNegatedRole();
                IBasicRole newTgt = ((NegatedBasicRole)src).getNegatedRole();

                GR.addVertex(newSrc);
                GR.addVertex(newTgt);
                GR.addEdge(newSrc, newTgt);
            }
            //end of Rule 5 application
             */


            //RULE 12: "If (Q1 in Q2), then (existsQ1 in existsQ2) &&
            //          (existsQ1- in existsQ2-)."
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
            //end rule


            //Rule 17-1: "If dom(RA1) in NOT dom(RA2), then RA1 in NOT RA2."
            /*TODO SKIP
            if (src instanceof RoleAttributeDomain &&
                    tgt instanceof NegatedBasicRole) {

                NegatedBasicRole negTgt = (NegatedBasicRole) tgt;

                if (negTgt.getNegatedRole() instanceof RoleAttributeDomain) {

                    //once our check is complete, add assertion to GRA
                    RoleAttribute newSrc = ((RoleAttributeDomain) src).getRoleAttribute();
                    RoleAttribute newTgt = ((RoleAttributeDomain) negTgt.getNegatedRole()).getRoleAttribute();
                    NegatedRoleAttribute newNegTgt = new NegatedRoleAttribute(df, newTgt);

                    GRA.addVertex(newSrc);
                    GRA.addVertex(newNegTgt);
                    GRA.addEdge(newSrc, newNegTgt);

                }
            }
             */


            //Rule 18-2: "If (Q in NOT Q) OR (Q- in NOT Q-),
            //            then all the following 4 assertions hold true:
            //            - the said 2 assertions;
            //            - existsQ in NOT existsQ;
            //            - existsQ- in NOT existsQ-."
            //(Since RA domains ARE basic roles, 18-2 takes care of part of 20-2 too)
            //Rule 20-2: "If Dom(RA) in NOT Dom(RA) OR Dom(RA)- in NOT Dom(RA)-,
            //            then all the following 6 assertions hold true:
            //            - the said 2 assertions; (taken care of by 18-2)
            //            - existsDom(RA) in NOT existsDom(RA); (taken care of by 18-2)
            //            - existsDom(RA)- in NOT existsDom(RA)-; (taken care of by 18-2)
            //            - RA in NOT RA;
            //            - rng(RA) in NOT rng(RA)."

            //(Additionally, we can also re-use the check on empty roles we have done before)

            //clever passage: if they are equals, one of the two
            //is already contained in the graphs, thus we only insert the inverted one
            /*TODO SKIP
            IBasicRole newSrc = basicSrc.getInverted();
            NegatedBasicRole newTgt = new NegatedBasicRole(df, basicSrc.getInverted());

            GR.addVertex(newSrc);
            GR.addVertex(newTgt);
            GR.addEdge(newSrc, newTgt);
             */

            //and we must also insert the other two
            //first, same sign

            if (src instanceof OWLObjectPropertyExpression && tgt instanceof OWLObjectPropertyExpression) {
                OWLObjectSomeValuesFrom newSrc2 = dataFactory.getOWLObjectSomeValuesFrom(src, dataFactory.getOWLThing());
                OWLObjectSomeValuesFrom negTgt2 = dataFactory.getOWLObjectSomeValuesFrom(tgt, dataFactory.getOWLThing());
                OWLObjectComplementOf newTgt2 = dataFactory.getOWLObjectComplementOf(negTgt2);
                GC.addVertex(newSrc2);
                GC.addVertex(newTgt2);
                GC.addEdge(newSrc2, newTgt2);

                //secondly, opposite sign
                OWLObjectSomeValuesFrom newSrc3 = dataFactory.getOWLObjectSomeValuesFrom(src.getInverseProperty(), dataFactory.getOWLThing());
                OWLObjectSomeValuesFrom negTgt3 = dataFactory.getOWLObjectSomeValuesFrom(tgt.getInverseProperty(), dataFactory.getOWLThing());
                OWLObjectComplementOf newTgt3 = dataFactory.getOWLObjectComplementOf(negTgt3);
                GC.addVertex(newSrc3);
                GC.addVertex(newTgt3);
                GC.addEdge(newSrc3, newTgt3);

                //now we complete 20-2 by examining the cases for RA and rng(RA)
                /*TODO inutile
                if (basicSrc instanceof RoleAttributeDomain) {

                    RoleAttribute ra = ((RoleAttributeDomain) basicSrc).getRoleAttribute();

                    NegatedRoleAttribute newTgt4 = new NegatedRoleAttribute(df, ra);

                    //RA in NOT RA
                    GRA.addVertex(ra);
                    GRA.addVertex(newTgt4);
                    GRA.addEdge(ra, newTgt4);

                    RoleAttributeRange newSrc5 = new RoleAttributeRange(df, ra);
                    NegatedBasicValueSet newTgt5 = new NegatedBasicValueSet(df, new RoleAttributeRange(df, ra));

                    //rng(RA) in NOT rng(RA)
                    GVS.addVertex(newSrc5);
                    GVS.addVertex(newTgt5);
                    GVS.addEdge(newSrc5, newTgt5);

                }
                 */
            }
            //end of rules 18-2 and 20-2


            //Rule 22: "If Q1 in NOT Q1 (empty role) && Q2 in Q1, then
            //          Q2 in NOT Q2 (empty role)."
            /*TODO SKIP
            if (src instanceof IBasicRole &&
                    tgt instanceof IBasicRole) {
                IBasicRole basicTgt = (IBasicRole) tgt;

                //we must check if tgt is an empty
                //if it is, it "infects" the src
                if (emptyRoles.contains(basicTgt)) {
                    IBasicRole basicSrc = (IBasicRole) src;

                    //mark as empty
                    emptyRoles.add(basicSrc);

                    NegatedBasicRole newTgt = new NegatedBasicRole(df, basicSrc);

                    //insert empty assertion in GR
                    GR.addVertex(basicSrc);
                    GR.addVertex(newTgt);
                    GR.addEdge(basicSrc, newTgt);
                }
            }
             */


            //Rule 26: "If (Q1 in Q2), then (Q1- in Q2-)."
            //(it should really be: "If (Q1 in R2), then (Q1- in R2-)."
            /*TODO SKIP
            if (src instanceof OWLObjectPropertyExpression && tgt instanceof OWLObjectPropertyExpression) {
                OWLObjectPropertyExpression newSrc = src.getInverseProperty();
                OWLObjectPropertyExpression newTgt = tgt.getInverseProperty();
                GR.addVertex(newSrc);
                GR.addVertex(newTgt);
                GR.addEdge(newSrc, newTgt);
            }
            //Modded rule 26: if Q1 in R2 -> Q1- in R2-
            else if (src instanceof IBasicRole &&
                    tgt instanceof NegatedBasicRole) {
                IBasicRole newSrc = ((IBasicRole) src).getInverted();
                IBasicRole newTgt = (((NegatedBasicRole) tgt).getNegatedRole()).getInverted();
                NegatedBasicRole negTgt = new NegatedBasicRole(df, newTgt);
                GR.addVertex(newSrc);
                GR.addVertex(negTgt);
                GR.addEdge(newSrc, negTgt);
            }
             */
            //end of all rules involving checks on Role IAs
        }
    }
    //end method

    private void applyAllConceptAttributeIARules(SimpleDirectedGraph<OWLClassExpression, DefaultEdge> GC,
                                                 SimpleDirectedGraph<OWLDataPropertyExpression, DefaultEdge> GCA) {
        //RULE 8: "If (genCA1 in genCA2) && (genCA2 in genCA3), then (genCA1 in genCA3)."
        TransitiveClosure TC = TransitiveClosure.INSTANCE;
        TC.closeSimpleDirectedGraph(GCA);

        //outer loop on all edges (concept attribute IAs) of GCA
        Object[] allEdges = (GCA.edgeSet()).toArray();
        for (int i = 0; i < allEdges.length; i++) {
            DefaultEdge conceptAttrIA = (DefaultEdge) allEdges[i];
            OWLDataPropertyExpression src = GCA.getEdgeSource(conceptAttrIA);
            OWLDataPropertyExpression tgt = GCA.getEdgeTarget(conceptAttrIA);

            //no further rule is appliable if conceptAttrIA is totally negated
                /*TODO SKIP
                if (src instanceof NegatedConceptAttribute)  {
                    continue;
                }

                if (tgt instanceof NegatedConceptAttribute) {
                    NegatedConceptAttribute nbc = (NegatedConceptAttribute)tgt;
                    ConceptAttribute bc = nbc.getNegatedConceptAttribute();
                    if (bc.equals(this.alphabet.getTopConceptAttribute())) {
                        GCA.addEdge(src, df.getNegatedConceptAttribute((ConceptAttribute) src));
                    }
                }
                 */


            //pre-rule 24 operation: if this IA is "CA in NOT CA", mark
            //CA as an empty concept attribute

                /*TODO SKIP
                if (src instanceof ConceptAttribute &&
                        tgt instanceof NegatedConceptAttribute)  {
                    ConceptAttribute caSrc = (ConceptAttribute)src;
                    ConceptAttribute negTgt = ((NegatedConceptAttribute)tgt).getNegatedConceptAttribute();

                    if (caSrc.equals(negTgt))  {
                        treatmentForEmptyConceptAttribute(caSrc);
                    }
                }
                 */


            //RULE 9: "If (genCA1 in genCA2), then (!genCA2 in !genCA1)."
            //case 1 of 4: direct-direct
                /*TODO SKIP
                if (src instanceof ConceptAttribute &&
                        tgt instanceof ConceptAttribute)  {
                    NegatedConceptAttribute newSrc = new NegatedConceptAttribute(df, (ConceptAttribute)tgt);
                    NegatedConceptAttribute newTgt = new NegatedConceptAttribute(df, (ConceptAttribute)src);

                    GCA.addVertex(newSrc);
                    GCA.addVertex(newTgt);
                    GCA.addEdge(newSrc, newTgt);
                }
                //case 2 of 4: direct-negated
                else if (src instanceof ConceptAttribute &&
                        tgt instanceof NegatedConceptAttribute)  {
                    ConceptAttribute newSrc = ((NegatedConceptAttribute)tgt).getNegatedConceptAttribute();
                    NegatedConceptAttribute newTgt = new NegatedConceptAttribute(df, (ConceptAttribute)src);

                    GCA.addVertex(newSrc);
                    GCA.addVertex(newTgt);
                    GCA.addEdge(newSrc, newTgt);
                }
                //case 3 of 4: negated-direct
                else if (src instanceof NegatedConceptAttribute &&
                        tgt instanceof ConceptAttribute)  {
                    NegatedConceptAttribute newSrc = new NegatedConceptAttribute(df, (ConceptAttribute)tgt);
                    ConceptAttribute newTgt = ((NegatedConceptAttribute)src).getNegatedConceptAttribute();

                    GCA.addVertex(newSrc);
                    GCA.addVertex(newTgt);
                    GCA.addEdge(newSrc, newTgt);
                }
                //case 4 of 4: negated-negated
                else if (src instanceof NegatedConceptAttribute &&
                        tgt instanceof NegatedConceptAttribute)  {
                    ConceptAttribute newSrc = ((NegatedConceptAttribute)tgt).getNegatedConceptAttribute();
                    ConceptAttribute newTgt = ((NegatedConceptAttribute)src).getNegatedConceptAttribute();

                    GCA.addVertex(newSrc);
                    GCA.addVertex(newTgt);
                    GCA.addEdge(newSrc, newTgt);
                }
                //end of Rule 9 application
                 */


            //RULE 13: "If (CA1 in CA2), then (dom(CA1) in dom(CA2)) &&
            //          (rng(CA1) in rng(CA2))."
            if (src instanceof OWLDataPropertyExpression &&
                    tgt instanceof OWLDataPropertyExpression) {
                OWLDataSomeValuesFrom newSrc = dataFactory.getOWLDataSomeValuesFrom(src, dataFactory.getTopDatatype());
                OWLDataSomeValuesFrom newTgt = dataFactory.getOWLDataSomeValuesFrom(tgt, dataFactory.getTopDatatype());

                GC.addVertex(newSrc);
                GC.addVertex(newTgt);
                GC.addEdge(newSrc, newTgt);
                    /*TODO inutile
                    ConceptAttributeRange newSrc2 = new ConceptAttributeRange(df, caSrc);
                    ConceptAttributeRange newTgt2 = new ConceptAttributeRange(df, caTgt);
                    GVS.addVertex(newSrc2);
                    GVS.addVertex(newTgt2);
                    GVS.addEdge(newSrc2, newTgt2);
                     */
            }

            //Rule 19-2: "If (CA in NOT CA) (empty concept attrib),
            //            then all the following 3 assertions hold true:
            //            - the said assertion;
            //            - rng(CA) in NOT rng(CA);
            //            - dom(CA) in NOT dom(CA)."

            //(we can reuse the check on empty CAs)
            if (src instanceof OWLDataPropertyExpression &&
                    emptyConceptAttributes.contains(src)) {

                //the first assertion is present

                //the second
                    /*TODO SKIP
                    ConceptAttribute ca = (ConceptAttribute)src;
                    ConceptAttributeRange newSrc = new ConceptAttributeRange(df, ca);
                    NegatedBasicValueSet newTgt = new NegatedBasicValueSet(df, new ConceptAttributeRange(df, ca));

                    GVS.addVertex(newSrc);
                    GVS.addVertex(newTgt);
                    GVS.addEdge(newSrc, newTgt);

                     */

                //the third
                OWLDataSomeValuesFrom newSrc2 = dataFactory.getOWLDataSomeValuesFrom(src, dataFactory.getTopDatatype());
                OWLObjectComplementOf newTgt2 = dataFactory.getOWLObjectComplementOf(newSrc2);
                GC.addVertex(newSrc2);
                GC.addVertex(newTgt2);
                GC.addEdge(newSrc2, newTgt2);

            }
            //thank God at least this class of IAs doesn't apply rule 20!

            //Rule 24: "If CA1 in NOT CA1 (empty concept attr) && CA2 in CA1, then
            //          CA2 in NOT CA2 (empty concept attribute)."
                /*TODO SKIP
                if (src instanceof ConceptAttribute &&
                        tgt instanceof ConceptAttribute)  {
                    ConceptAttribute caTgt = (ConceptAttribute)tgt;

                    //we must check if tgt is an empty
                    //if it is, it "infects" the src
                    if (emptyConceptAttributes.contains(caTgt))  {
                        ConceptAttribute caSrc = (ConceptAttribute)src;

                        //mark as empty
                        emptyConceptAttributes.add(caSrc);

                        NegatedConceptAttribute newTgt = new NegatedConceptAttribute(df, caSrc);

                        //insert empty assertion in GC
                        GCA.addVertex(caSrc);
                        GCA.addVertex(newTgt);
                        GCA.addEdge(caSrc, newTgt);

                    }
                }
                 */
            //end of all rules involving checks on Concept Attribute IAs
        }


    }//end method
}
