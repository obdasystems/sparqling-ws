package com.obdasystems.sparqling.parsers.graphol.utils;

import com.obdasystems.sparqling.parsers.graphol.*;
import com.obdasystems.sparqling.parsers.graphol.edge.impl.*;
import com.obdasystems.sparqling.parsers.graphol.impl.v2.GraphOLDiagram;
import com.obdasystems.sparqling.parsers.graphol.impl.v2.GraphOLOntology;
import com.obdasystems.sparqling.parsers.graphol.node.impl.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GraphOLUtilities_v3 {

    static Logger logger = LoggerFactory.getLogger(GraphOLUtilities_v3.class);

    public static Set<OWLAxiom> getOWLLogicalAxioms(GraphOLOntology ontology, OWLDataFactory df) {
        Set<OWLAxiom> result = new HashSet<>();
        List<GraphOLDiagram> diagrams = ontology.getDiagrams();
        diagrams.forEach(diag -> {
            result.addAll(GraphOLUtilities_v3.getOWLLogicalAxioms(diag, df));
        });
        return result;
    }

    public static List<OWLAxiom> getOWLLogicalAxioms(GraphOLDiagram diagram, OWLDataFactory df) {
        List<OWLAxiom> result = new LinkedList<>();
        List<GraphOLOntologyAxiomEdge> logicalEdges = diagram.getAxiomEdges();
        logicalEdges.forEach(edge -> {
            if (edge instanceof GraphOLOntologyInstanceOfEdge) {
                result.add(GraphOLUtilities_v3.getAssertionAxiom((GraphOLOntologyInstanceOfEdge) edge, diagram, df));
            } else {
                if (edge instanceof GraphOLOntologyInclusionEdge) {
                    //result.add(GraphOLUtilities.getInclusionAxiom((GraphOLOntologyInclusionEdge) edge, diagram, df));
                    result.addAll(GraphOLUtilities_v3.getInclusionAxiomPlusDomainRangeAxioms((GraphOLOntologyInclusionEdge) edge, diagram, df));
                } else {
                    if (edge instanceof GraphOLOntologyEquivalenceEdge) {
                        //result.add(GraphOLUtilities.getEquivalenceAxiom((GraphOLOntologyEquivalenceEdge) edge, diagram, df));
                        result.addAll(GraphOLUtilities_v3.getEquivalenceAxiomPlusSubDomainRangeAxioms((GraphOLOntologyEquivalenceEdge) edge, diagram, df));
                    } else {
                        if (edge instanceof GraphOLOntologyDifferentEdge) {
                            result.add(GraphOLUtilities_v3.getDifferentFromAxiom((GraphOLOntologyDifferentEdge) edge, diagram, df));
                        } else {
                            if (edge instanceof GraphOLOntologySameAsEdge) {
                                result.add(GraphOLUtilities_v3.getSameAsAxiom((GraphOLOntologySameAsEdge) edge, diagram, df));
                            }
                        }
                    }
                }
            }
        });

        List<GraphOLDisjointUnionNode> disjUnionNodes = diagram.getDisjointUnionNodes();
        disjUnionNodes.forEach(node -> {
            OWLDisjointClassesAxiom ax = GraphOLUtilities_v3.getDisjointClasses(node, diagram, df);
            if (ax != null) {
                result.add(ax);
            }
        });

        List<GraphOLHasKeyNode> hasKeyNodes = diagram.getHasKeyNodes();
        hasKeyNodes.forEach(node -> {
            OWLHasKeyAxiom ax = GraphOLUtilities_v3.getOWLHasKeyAxiom(node, diagram, df);
            if (ax != null) {
                result.add(ax);
            }
        });

        return result;
    }

    /****************************
     AXIOMS
     *****************************/
    //different individuals
    private static OWLLogicalAxiom getDifferentFromAxiom(GraphOLOntologyDifferentEdge edge, GraphOLDiagram diagram, OWLDataFactory df) {
        String srcNodeId = edge.getSourceNodeId();
        String tgtNodeId = edge.getTargetNodeId();
        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
        GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
        if (srcNode instanceof GraphOLOntologyIRIElementNode) {
            if (tgtNode instanceof GraphOLOntologyIRIElementNode) {
                String srcIriStr = ((GraphOLOntologyIRIElementNode) srcNode).getIri();
                IRI scrIRI = IRI.create(srcIriStr);
                OWLIndividual srcInd = df.getOWLNamedIndividual(scrIRI);
                String tgtIriStr = ((GraphOLOntologyIRIElementNode) tgtNode).getIri();
                IRI tgtIRI = IRI.create(tgtIriStr);
                OWLIndividual tgtInd = df.getOWLNamedIndividual(tgtIRI);
                Set<OWLIndividual> indSet = new HashSet<OWLIndividual>();
                indSet.add(srcInd);
                indSet.add(tgtInd);
                return df.getOWLDifferentIndividualsAxiom(indSet, edge.getAnnotations());
            } else {
                logger.error("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
                throw new RuntimeException("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
            }
        } else {
            logger.error("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
            throw new RuntimeException("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
        }
    }

    //same individuals
    private static OWLLogicalAxiom getSameAsAxiom(GraphOLOntologySameAsEdge edge, GraphOLDiagram diagram, OWLDataFactory df) {
        String srcNodeId = edge.getSourceNodeId();
        String tgtNodeId = edge.getTargetNodeId();
        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
        GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
        if (srcNode instanceof GraphOLOntologyIRIElementNode) {
            if (tgtNode instanceof GraphOLOntologyIRIElementNode) {
                String srcIriStr = ((GraphOLOntologyIRIElementNode) srcNode).getIri();
                IRI scrIRI = IRI.create(srcIriStr);
                OWLIndividual srcInd = df.getOWLNamedIndividual(scrIRI);
                String tgtIriStr = ((GraphOLOntologyIRIElementNode) tgtNode).getIri();
                IRI tgtIRI = IRI.create(tgtIriStr);
                OWLIndividual tgtInd = df.getOWLNamedIndividual(tgtIRI);
                Set<OWLIndividual> indSet = new HashSet<OWLIndividual>();
                indSet.add(srcInd);
                indSet.add(tgtInd);
                return df.getOWLSameIndividualAxiom(indSet, edge.getAnnotations());
            } else {
                logger.error("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
                throw new RuntimeException("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
            }
        } else {
            logger.error("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
            throw new RuntimeException("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
        }
    }

    //ASSERTIONS
    private static OWLLogicalAxiom getAssertionAxiom(GraphOLOntologyInstanceOfEdge edge, GraphOLDiagram diagram, OWLDataFactory df) {
        String srcNodeId = edge.getSourceNodeId();
        String tgtNodeId = edge.getTargetNodeId();
        Set<OWLAnnotation> annotations = edge.getAnnotations();
        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
        GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
        if (srcNode instanceof GraphOLOntologyIndividualNode) {
            return GraphOLUtilities_v3.getClassAssertionAxiom((GraphOLOntologyIndividualNode) srcNode, tgtNode, diagram, df, annotations);
        } else {
            if (srcNode instanceof GraphOLOntologyClassNode) {
                return GraphOLUtilities_v3.getClassAssertionAxiom((GraphOLOntologyClassNode) srcNode, tgtNode, diagram, df, annotations);
            } else {

                if (srcNode instanceof GraphOLPropertyAssertionNode) {//INIZIO IF
                    String firstEdgeId = ((GraphOLPropertyAssertionNode) srcNode).getFirstEdgeID();
                    GraphOLEdge firstEdge = diagram.getEdgeById(firstEdgeId);
                    String secEdgeId = ((GraphOLPropertyAssertionNode) srcNode).getSecondEdgeID();
                    GraphOLEdge secEdge = diagram.getEdgeById(secEdgeId);
                    GraphOLNode firstSrcNode = diagram.getNodeById(firstEdge.getSourceNodeId());
                    GraphOLNode secSrcNode = diagram.getNodeById(secEdge.getSourceNodeId());
                    if (tgtNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                        if (firstSrcNode instanceof GraphOLOntologyIndividualNode && secSrcNode instanceof GraphOLOntologyIndividualNode) {
                            return GraphOLUtilities_v3.getObjPropAssertionAxiom((GraphOLOntologyIndividualNode) firstSrcNode, (GraphOLOntologyIndividualNode) secSrcNode, tgtNode, diagram, df, annotations);
                        } else {
                            if (firstSrcNode instanceof GraphOLOntologyClassNode && secSrcNode instanceof GraphOLOntologyClassNode) {
                                return GraphOLUtilities_v3.getObjPropAssertionAxiom((GraphOLOntologyClassNode) firstSrcNode, (GraphOLOntologyClassNode) secSrcNode, tgtNode, diagram, df, annotations);

                            } else {
                                if (firstSrcNode instanceof GraphOLOntologyClassNode && secSrcNode instanceof GraphOLOntologyIndividualNode) {
                                    return GraphOLUtilities_v3.getObjPropAssertionAxiom((GraphOLOntologyClassNode) firstSrcNode, (GraphOLOntologyIndividualNode) secSrcNode, tgtNode, diagram, df, annotations);

                                } else {
                                    if (firstSrcNode instanceof GraphOLOntologyIndividualNode && secSrcNode instanceof GraphOLOntologyClassNode) {
                                        return GraphOLUtilities_v3.getObjPropAssertionAxiom((GraphOLOntologyIndividualNode) firstSrcNode, (GraphOLOntologyClassNode) secSrcNode, tgtNode, diagram, df, annotations);

                                    }
                                }
                            }
                        }
                    } else {
                        if (firstSrcNode instanceof GraphOLOntologyIndividualNode) {
                            return GraphOLUtilities_v3.getDataPropAssertionAxiom((GraphOLOntologyIndividualNode) firstSrcNode, (GraphOLOntologyLiteralNode) secSrcNode, tgtNode, diagram, df, annotations);
                        } else {
                            if (firstSrcNode instanceof GraphOLOntologyClassNode) {
                                return GraphOLUtilities_v3.getDataPropAssertionAxiom((GraphOLOntologyClassNode) firstSrcNode, (GraphOLOntologyLiteralNode) secSrcNode, tgtNode, diagram, df, annotations);
                            }
                        }
                    }
                }
            }
        }
        logger.error("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
        throw new RuntimeException("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
    }

    private static OWLDataPropertyAssertionAxiom getDataPropAssertionAxiom(GraphOLOntologyIndividualNode firstSrcNode, GraphOLOntologyLiteralNode secSrcNode, GraphOLNode tgtNode,
                                                                           GraphOLDiagram diagram, OWLDataFactory df, Set<OWLAnnotation> annotations) {
        OWLNamedIndividual domInd = df.getOWLNamedIndividual(IRI.create(firstSrcNode.getIri()));
        OWLLiteral lit = null;
        String lang = secSrcNode.getLanguage();
        if (lang != null) {
            lit = df.getOWLLiteral(secSrcNode.getLexicalForm(), lang);
        } else {
            OWL2Datatype dt2 = GraphOLUtilities_v3.getOWLDatatype(secSrcNode.getDatatypeIRI());
            if (dt2 != null) {
                lit = df.getOWLLiteral(secSrcNode.getLexicalForm(), dt2);
            } else {
                lit = df.getOWLLiteral(secSrcNode.getLexicalForm(), df.getOWLDatatype(IRI.create(secSrcNode.getDatatypeIRI())));
            }
        }
        OWLDataPropertyExpression expr = GraphOLUtilities_v3.getDataPropertyExpression(tgtNode, diagram, df);
        return df.getOWLDataPropertyAssertionAxiom(expr, domInd, lit, annotations);
    }

    private static OWLDataPropertyAssertionAxiom getDataPropAssertionAxiom(GraphOLOntologyClassNode firstSrcNode, GraphOLOntologyLiteralNode secSrcNode, GraphOLNode tgtNode,
                                                                           GraphOLDiagram diagram, OWLDataFactory df, Set<OWLAnnotation> annotations) {
        OWLNamedIndividual domInd = df.getOWLNamedIndividual(IRI.create(firstSrcNode.getIri()));
        OWLLiteral lit = null;
        String lang = secSrcNode.getLanguage();
        if (lang != null) {
            lit = df.getOWLLiteral(secSrcNode.getLexicalForm(), lang);
        } else {
            OWL2Datatype dt2 = GraphOLUtilities_v3.getOWLDatatype(secSrcNode.getDatatypeIRI());
            if (dt2 != null) {
                lit = df.getOWLLiteral(secSrcNode.getLexicalForm(), dt2);
            } else {
                lit = df.getOWLLiteral(secSrcNode.getLexicalForm(), df.getOWLDatatype(IRI.create(secSrcNode.getDatatypeIRI())));
            }
        }
        OWLDataPropertyExpression expr = GraphOLUtilities_v3.getDataPropertyExpression(tgtNode, diagram, df);
        return df.getOWLDataPropertyAssertionAxiom(expr, domInd, lit, annotations);
    }

    private static OWL2Datatype getOWLDatatype(String dtIRI) {
        switch (dtIRI) {
            case "http://www.w3.org/2002/07/owl#real":
                return OWL2Datatype.OWL_REAL;
            case "http://www.w3.org/2002/07/owl#rational":
                return OWL2Datatype.OWL_RATIONAL;
            case "http://www.w3.org/2001/XMLSchema#decimal":
                return OWL2Datatype.XSD_DECIMAL;
            case "http://www.w3.org/2001/XMLSchema#integer":
                return OWL2Datatype.XSD_INTEGER;
            case "http://www.w3.org/2001/XMLSchema#nonNegativeInteger":
                return OWL2Datatype.XSD_NON_NEGATIVE_INTEGER;
            case "http://www.w3.org/2001/XMLSchema#nonPositiveInteger":
                return OWL2Datatype.XSD_NON_POSITIVE_INTEGER;
            case "http://www.w3.org/2001/XMLSchema#positiveInteger":
                return OWL2Datatype.XSD_POSITIVE_INTEGER;
            case "http://www.w3.org/2001/XMLSchema#negativeInteger":
                return OWL2Datatype.XSD_NEGATIVE_INTEGER;
            case "http://www.w3.org/2001/XMLSchema#long":
                return OWL2Datatype.XSD_LONG;
            case "http://www.w3.org/2001/XMLSchema#int":
                return OWL2Datatype.XSD_INT;
            case "http://www.w3.org/2001/XMLSchema#short":
                return OWL2Datatype.XSD_SHORT;
            case "http://www.w3.org/2001/XMLSchema#byte":
                return OWL2Datatype.XSD_BYTE;
            case "http://www.w3.org/2001/XMLSchema#unsignedLong":
                return OWL2Datatype.XSD_UNSIGNED_LONG;
            case "http://www.w3.org/2001/XMLSchema#unsignedInt":
                return OWL2Datatype.XSD_UNSIGNED_INT;
            case "http://www.w3.org/2001/XMLSchema#unsignedShort":
                return OWL2Datatype.XSD_UNSIGNED_SHORT;
            case "http://www.w3.org/2001/XMLSchema#unsignedByte":
                return OWL2Datatype.XSD_UNSIGNED_BYTE;
            case "http://www.w3.org/2001/XMLSchema#double":
                return OWL2Datatype.XSD_DOUBLE;
            case "http://www.w3.org/2001/XMLSchema#float":
                return OWL2Datatype.XSD_FLOAT;
            case "http://www.w3.org/2001/XMLSchema#string":
                return OWL2Datatype.XSD_STRING;
            case "http://www.w3.org/2001/XMLSchema#normalizedString":
                return OWL2Datatype.XSD_NORMALIZED_STRING;
            case "http://www.w3.org/2001/XMLSchema#token":
                return OWL2Datatype.XSD_TOKEN;
            case "http://www.w3.org/2001/XMLSchema#language":
                return OWL2Datatype.XSD_LANGUAGE;
            case "http://www.w3.org/2001/XMLSchema#Name":
                return OWL2Datatype.XSD_NAME;
            case "http://www.w3.org/2001/XMLSchema#NCName":
                return OWL2Datatype.XSD_NCNAME;
            case "http://www.w3.org/2001/XMLSchema#NMTOKEN":
                return OWL2Datatype.XSD_NMTOKEN;
            case "http://www.w3.org/2001/XMLSchema#boolean":
                return OWL2Datatype.XSD_BOOLEAN;
            case "http://www.w3.org/2001/XMLSchema#hexBinary":
                return OWL2Datatype.XSD_HEX_BINARY;
            case "http://www.w3.org/2001/XMLSchema#base64Binary":
                return OWL2Datatype.XSD_BASE_64_BINARY;
            case "http://www.w3.org/2001/XMLSchema#anyURI":
                return OWL2Datatype.XSD_ANY_URI;
            case "http://www.w3.org/2001/XMLSchema#dateTime":
                return OWL2Datatype.XSD_DATE_TIME;
            case "http://www.w3.org/2001/XMLSchema#dateTimeStamp":
                return OWL2Datatype.XSD_DATE_TIME_STAMP;
            case "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral":
                return OWL2Datatype.RDF_XML_LITERAL;
            default:
                return null;
        }
    }

    private static OWLObjectPropertyAssertionAxiom getObjPropAssertionAxiom(GraphOLOntologyIndividualNode firstSrcNode, GraphOLOntologyIndividualNode secSrcNode, GraphOLNode tgtNode,
                                                                            GraphOLDiagram diagram, OWLDataFactory df, Set<OWLAnnotation> annotations) {
        OWLNamedIndividual domInd = df.getOWLNamedIndividual(IRI.create(firstSrcNode.getIri()));
        OWLNamedIndividual ranInd = df.getOWLNamedIndividual(IRI.create(secSrcNode.getIri()));
        OWLObjectPropertyExpression expr = GraphOLUtilities_v3.getObjectPropertyExpression(tgtNode, diagram, df);
        return df.getOWLObjectPropertyAssertionAxiom(expr, domInd, ranInd, annotations);
    }

    private static OWLObjectPropertyAssertionAxiom getObjPropAssertionAxiom(GraphOLOntologyIndividualNode firstSrcNode, GraphOLOntologyClassNode secSrcNode, GraphOLNode tgtNode,
                                                                            GraphOLDiagram diagram, OWLDataFactory df, Set<OWLAnnotation> annotations) {
        OWLNamedIndividual domInd = df.getOWLNamedIndividual(IRI.create(firstSrcNode.getIri()));
        OWLNamedIndividual ranInd = df.getOWLNamedIndividual(IRI.create(secSrcNode.getIri()));
        OWLObjectPropertyExpression expr = GraphOLUtilities_v3.getObjectPropertyExpression(tgtNode, diagram, df);
        return df.getOWLObjectPropertyAssertionAxiom(expr, domInd, ranInd, annotations);
    }

    private static OWLObjectPropertyAssertionAxiom getObjPropAssertionAxiom(GraphOLOntologyClassNode firstSrcNode, GraphOLOntologyIndividualNode secSrcNode, GraphOLNode tgtNode,
                                                                            GraphOLDiagram diagram, OWLDataFactory df, Set<OWLAnnotation> annotations) {
        OWLNamedIndividual domInd = df.getOWLNamedIndividual(IRI.create(firstSrcNode.getIri()));
        OWLNamedIndividual ranInd = df.getOWLNamedIndividual(IRI.create(secSrcNode.getIri()));
        OWLObjectPropertyExpression expr = GraphOLUtilities_v3.getObjectPropertyExpression(tgtNode, diagram, df);
        return df.getOWLObjectPropertyAssertionAxiom(expr, domInd, ranInd, annotations);
    }

    private static OWLObjectPropertyAssertionAxiom getObjPropAssertionAxiom(GraphOLOntologyClassNode firstSrcNode, GraphOLOntologyClassNode secSrcNode, GraphOLNode tgtNode,
                                                                            GraphOLDiagram diagram, OWLDataFactory df, Set<OWLAnnotation> annotations) {
        OWLNamedIndividual domInd = df.getOWLNamedIndividual(IRI.create(firstSrcNode.getIri()));
        OWLNamedIndividual ranInd = df.getOWLNamedIndividual(IRI.create(secSrcNode.getIri()));
        OWLObjectPropertyExpression expr = GraphOLUtilities_v3.getObjectPropertyExpression(tgtNode, diagram, df);
        return df.getOWLObjectPropertyAssertionAxiom(expr, domInd, ranInd, annotations);
    }

    private static OWLClassAssertionAxiom getClassAssertionAxiom(GraphOLOntologyIndividualNode srcNode, GraphOLNode tgtNode,
                                                                 GraphOLDiagram diagram, OWLDataFactory df, Set<OWLAnnotation> annotations) {
        OWLNamedIndividual ind = df.getOWLNamedIndividual(IRI.create(srcNode.getIri()));
        OWLClassExpression expr = GraphOLUtilities_v3.getClassExpression(tgtNode, diagram, df);
        return df.getOWLClassAssertionAxiom(expr, ind, annotations);
    }

    private static OWLClassAssertionAxiom getClassAssertionAxiom(GraphOLOntologyClassNode srcNode, GraphOLNode tgtNode,
                                                                 GraphOLDiagram diagram, OWLDataFactory df, Set<OWLAnnotation> annotations) {
        OWLNamedIndividual ind = df.getOWLNamedIndividual(IRI.create(srcNode.getIri()));
        OWLClassExpression expr = GraphOLUtilities_v3.getClassExpression(tgtNode, diagram, df);
        return df.getOWLClassAssertionAxiom(expr, ind, annotations);
    }

    //INCLUSIONS
    private static OWLLogicalAxiom getInclusionAxiom(GraphOLOntologyInclusionEdge edge, GraphOLDiagram diagram, OWLDataFactory df) {
        String srcNodeId = edge.getSourceNodeId();
        String tgtNodeId = edge.getTargetNodeId();
        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
        if (srcNode instanceof GraphOLClassExpressionStartingNodeI) {
            OWLClassExpression subExpr = GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df);
            GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
            OWLClassExpression superExpr = GraphOLUtilities_v3.getClassExpression(tgtNode, diagram, df);
            return df.getOWLSubClassOfAxiom(subExpr, superExpr, edge.getAnnotations());
        } else {
            if (srcNode instanceof GraphOLInclusionObjectPropertyExpressionStartingNodeI) {
                GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
                if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                    OWLObjectPropertyExpression subExpr = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                    OWLObjectPropertyExpression superExpr = GraphOLUtilities_v3.getObjectPropertyExpression(tgtNode, diagram, df);
                    return df.getOWLSubObjectPropertyOfAxiom(subExpr, superExpr, edge.getAnnotations());
                } else {
                    if (srcNode instanceof GraphOLRoleChainNode) {
                        return GraphOLUtilities_v3.getChainInclusionAxiom(edge, (GraphOLRoleChainNode) srcNode, tgtNode, diagram, df);
                    }
                }
            } else {
                if (srcNode instanceof GraphOLDataPropertyExpressionStartingNodeI) {
                    OWLDataPropertyExpression subExpr = GraphOLUtilities_v3.getDataPropertyExpression(srcNode, diagram, df);
                    GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
                    OWLDataPropertyExpression superExpr = GraphOLUtilities_v3.getDataPropertyExpression(tgtNode, diagram, df);
                    return df.getOWLSubDataPropertyOfAxiom(subExpr, superExpr, edge.getAnnotations());
                }
            }
        }
        logger.error("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
        throw new RuntimeException("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
    }

    private static boolean isDisambiguatedToDataRangeExpression(String tgtNodeId, GraphOLDiagram diagram) {
        GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);

        if (tgtNode instanceof GraphOLOntologyValueDomainNode) {
            return true;
        } else {
            List<GraphOLConstructorInputEdge> incomingEdges = diagram.getIncomingOperatorEdgesByNodeId(tgtNode.getNodeId());
            if (tgtNode instanceof GraphOLComplementNode) {
                if (incomingEdges.size() == 1) {
                    GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                    String srcNodeId = opEdge.getSourceNodeId();
                    return isDisambiguatedToDataRangeExpression(srcNodeId, diagram);
                } else {
                    logger.error("Found complement node " + tgtNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                    throw new RuntimeException("Found complement node " + tgtNode.getNodeId() + " with incoming operator edges count different from 1");
                }
            } else {
                if (tgtNode instanceof GraphOLUnionNode) {
                    if (!incomingEdges.isEmpty()) {
                        GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                        String srcNodeId = opEdge.getSourceNodeId();
                        return isDisambiguatedToDataRangeExpression(srcNodeId, diagram);
                    } else {
                        logger.error("Found union node " + tgtNode.getNodeId() + " with no incoming operator edges");
                        throw new RuntimeException("Found union node " + tgtNode.getNodeId() + " with no incoming operator edges");
                    }
                } else {
                    if (tgtNode instanceof GraphOLIntersectionNode) {
                        if (!incomingEdges.isEmpty()) {
                            GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                            String srcNodeId = opEdge.getSourceNodeId();
                            return isDisambiguatedToDataRangeExpression(srcNodeId, diagram);
                        } else {
                            logger.error("Found intersection node " + tgtNode.getNodeId() + " with no incoming operator edges");
                            throw new RuntimeException("Found intersection node " + tgtNode.getNodeId() + " with no incoming operator edges");
                        }
                    } else {
                        if (tgtNode instanceof GraphOLEnumerationNode) {
                            Set<OWLLiteral> operands = new HashSet<OWLLiteral>();
                            for (GraphOLConstructorInputEdge opEdge : incomingEdges) {
                                String srcNodeId = opEdge.getSourceNodeId();
                                GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                if (srcNode instanceof GraphOLOntologyLiteralNode) {
                                    return true;
                                }
                            }
                        } else {
                            if (tgtNode instanceof GraphOLDatatypeRestrictionNode) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static List<OWLLogicalAxiom> getInclusionAxiomPlusDomainRangeAxioms(GraphOLOntologyInclusionEdge edge, GraphOLDiagram diagram, OWLDataFactory df) {
        String srcNodeId = edge.getSourceNodeId();
        String tgtNodeId = edge.getTargetNodeId();
        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
        List<OWLLogicalAxiom> result = new LinkedList<>();

        boolean disambiguatedToDataRangeExpression = false;

        if (srcNode instanceof GraphOLClassExpressionStartingNodeI && srcNode instanceof GraphOLDataRangeStartingNodeI) {
            disambiguatedToDataRangeExpression = isDisambiguatedToDataRangeExpression(tgtNodeId, diagram);
        }

        if (srcNode instanceof GraphOLClassExpressionStartingNodeI && !disambiguatedToDataRangeExpression) {
            OWLClassExpression subExpr = GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df);
            GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
            //TODO added for #768
            if (tgtNode instanceof GraphOLDomainCardinalityRestrictionNode) {
                OWLClassExpression translatedExpression = GraphOLUtilities_v3.getClassExpression(tgtNode, diagram, df);
                if (translatedExpression instanceof OWLObjectIntersectionOf) {
                    ((OWLObjectIntersectionOf) translatedExpression).getOperands().forEach(cardExpr->{
                        result.add(df.getOWLSubClassOfAxiom(subExpr, cardExpr, edge.getAnnotations()));
                    });
                    return result;
                }
            }
            //TODO END added for #768
            //TODO added for #153
            if (tgtNode instanceof GraphOLRangeCardinalityRestrictionNode) {
                OWLClassExpression translatedExpression = GraphOLUtilities_v3.getClassExpression(tgtNode, diagram, df);
                if (translatedExpression instanceof OWLObjectIntersectionOf) {
                    ((OWLObjectIntersectionOf) translatedExpression).getOperands().forEach(cardExpr->{
                        result.add(df.getOWLSubClassOfAxiom(subExpr, cardExpr, edge.getAnnotations()));
                    });
                    return result;
                }
            }
            //TODO END added for #153

            OWLClassExpression superExpr = GraphOLUtilities_v3.getClassExpression(tgtNode, diagram, df);
            result.add(df.getOWLSubClassOfAxiom(subExpr, superExpr, edge.getAnnotations()));
            if (subExpr instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom osvf = (OWLObjectSomeValuesFrom) subExpr;
                if (osvf.getFiller().isOWLThing()) {
                    OWLObjectPropertyExpression objExp = osvf.getProperty();
                    if (objExp instanceof OWLObjectInverseOf) {
                        result.add(df.getOWLObjectPropertyRangeAxiom(objExp.getNamedProperty(), superExpr, edge.getAnnotations()));
                    } else {
                        result.add(df.getOWLObjectPropertyDomainAxiom(objExp, superExpr, edge.getAnnotations()));
                    }
                }
            } else {
                if (subExpr instanceof OWLDataSomeValuesFrom) {
                    OWLDataSomeValuesFrom dsvf = (OWLDataSomeValuesFrom) subExpr;
                    if (dsvf.getFiller().isTopDatatype()) {
                        result.add(df.getOWLDataPropertyDomainAxiom(dsvf.getProperty(), superExpr, edge.getAnnotations()));
                    }
                }
            }
            return result;

        } else {
            if (srcNode instanceof GraphOLInclusionObjectPropertyExpressionStartingNodeI) {
                GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
                if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                    OWLObjectPropertyExpression subExpr = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                    OWLObjectPropertyExpression superExpr = GraphOLUtilities_v3.getObjectPropertyExpression(tgtNode, diagram, df);
                    result.add(df.getOWLSubObjectPropertyOfAxiom(subExpr, superExpr, edge.getAnnotations()));
                } else {
                    if (srcNode instanceof GraphOLRoleChainNode) {
                        result.add(GraphOLUtilities_v3.getChainInclusionAxiom(edge, (GraphOLRoleChainNode) srcNode, tgtNode, diagram, df));
                    }
                }
                return result;
            } else {
                if (srcNode instanceof GraphOLDataPropertyExpressionStartingNodeI) {
                    OWLDataPropertyExpression subExpr = GraphOLUtilities_v3.getDataPropertyExpression(srcNode, diagram, df);
                    GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
                    OWLDataPropertyExpression superExpr = GraphOLUtilities_v3.getDataPropertyExpression(tgtNode, diagram, df);
                    result.add(df.getOWLSubDataPropertyOfAxiom(subExpr, superExpr, edge.getAnnotations()));
                    return result;
                } else {
                    if (srcNode instanceof GraphOLDataRangeStartingNodeI && disambiguatedToDataRangeExpression) {
                        GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
                        OWLDataRange range = getDataRangeExpression(tgtNode, diagram, df);
                        List<GraphOLConstructorInputEdge> incomingEdges = diagram.getIncomingOperatorEdgesByNodeId(srcNode.getNodeId());

                        if (incomingEdges.size() == 1) {
                            GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                            String attrNodeId = opEdge.getSourceNodeId();
                            GraphOLNode attrNode = diagram.getNodeById(attrNodeId);
                            OWLDataPropertyExpression dataPropExp = GraphOLUtilities_v3.getDataPropertyExpression(attrNode, diagram, df);
                            result.add(df.getOWLDataPropertyRangeAxiom(dataPropExp, range, edge.getAnnotations()));
                            return result;
                        } else {
                            logger.error("Found data range operator node " + srcNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                            throw new RuntimeException("Found data range operator node " + srcNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                        }
                    }
                }
            }
        }
        logger.error("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
        throw new RuntimeException("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
    }

    private static OWLSubPropertyChainOfAxiom getChainInclusionAxiom(GraphOLOntologyInclusionEdge edge, GraphOLRoleChainNode srcNode, GraphOLNode tgtNode,
                                                                     GraphOLDiagram diagram, OWLDataFactory df) {
        OWLObjectPropertyExpression superExpr = GraphOLUtilities_v3.getObjectPropertyExpression(tgtNode, diagram, df);

        List<OWLObjectPropertyExpression> chainList = new LinkedList<>();
        List<String> inputEdgeList = srcNode.getInputEdgeList();
        for (String edgeId : inputEdgeList) {
            GraphOLEdge currEdge = diagram.getEdgeById(edgeId);
            String srcNodeId = currEdge.getSourceNodeId();
            GraphOLNode currNode = diagram.getNodeById(srcNodeId);
            OWLObjectPropertyExpression currExpr = GraphOLUtilities_v3.getObjectPropertyExpression(currNode, diagram, df);
            chainList.add(currExpr);
        }
        return df.getOWLSubPropertyChainOfAxiom(chainList, superExpr, edge.getAnnotations());
    }


    //EQUIVALENCE
    private static OWLLogicalAxiom getEquivalenceAxiom(GraphOLOntologyEquivalenceEdge edge, GraphOLDiagram diagram, OWLDataFactory df) {
        String srcNodeId = edge.getSourceNodeId();
        String tgtNodeId = edge.getTargetNodeId();
        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
        if (srcNode instanceof GraphOLClassExpressionStartingNodeI) {
            OWLClassExpression subExpr = GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df);
            GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
            OWLClassExpression superExpr = GraphOLUtilities_v3.getClassExpression(tgtNode, diagram, df);
            return df.getOWLEquivalentClassesAxiom(subExpr, superExpr, edge.getAnnotations());
        } else {
            if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                OWLObjectPropertyExpression subExpr = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
                OWLObjectPropertyExpression superExpr = GraphOLUtilities_v3.getObjectPropertyExpression(tgtNode, diagram, df);
                return df.getOWLEquivalentObjectPropertiesAxiom(subExpr, superExpr, edge.getAnnotations());
            } else {
                if (srcNode instanceof GraphOLDataPropertyExpressionStartingNodeI) {
                    OWLDataPropertyExpression subExpr = GraphOLUtilities_v3.getDataPropertyExpression(srcNode, diagram, df);
                    GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
                    OWLDataPropertyExpression superExpr = GraphOLUtilities_v3.getDataPropertyExpression(tgtNode, diagram, df);
                    return df.getOWLEquivalentDataPropertiesAxiom(subExpr, superExpr, edge.getAnnotations());
                }
            }
        }
        logger.error("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
        throw new RuntimeException("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
    }

    //EQUIVALENCE + subclasses +domain + range
    private static List<OWLLogicalAxiom> getEquivalenceAxiomPlusSubDomainRangeAxioms(GraphOLOntologyEquivalenceEdge edge, GraphOLDiagram diagram, OWLDataFactory df) {
        String srcNodeId = edge.getSourceNodeId();
        String tgtNodeId = edge.getTargetNodeId();
        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
        List<OWLLogicalAxiom> result = new LinkedList<>();
        if (srcNode instanceof GraphOLClassExpressionStartingNodeI) {
            OWLClassExpression subExpr = GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df);
            GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
            OWLClassExpression superExpr = GraphOLUtilities_v3.getClassExpression(tgtNode, diagram, df);
            result.add(df.getOWLEquivalentClassesAxiom(subExpr, superExpr, edge.getAnnotations()));
            result.add(df.getOWLSubClassOfAxiom(subExpr, superExpr, edge.getAnnotations()));
            //aggiungo domain(range) per sub
            if (subExpr instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom osvf = (OWLObjectSomeValuesFrom) subExpr;
                if (osvf.getFiller().isOWLThing()) {
                    OWLObjectPropertyExpression objExp = osvf.getProperty();
                    if (objExp instanceof OWLObjectInverseOf) {
                        result.add(df.getOWLObjectPropertyRangeAxiom(objExp.getNamedProperty(), superExpr, edge.getAnnotations()));
                    } else {
                        result.add(df.getOWLObjectPropertyDomainAxiom(objExp, superExpr, edge.getAnnotations()));
                    }
                }
            } else {
                if (subExpr instanceof OWLDataSomeValuesFrom) {
                    OWLDataSomeValuesFrom dsvf = (OWLDataSomeValuesFrom) subExpr;
                    if (dsvf.getFiller().isTopDatatype()) {
                        result.add(df.getOWLDataPropertyDomainAxiom(dsvf.getProperty(), superExpr, edge.getAnnotations()));
                    }
                }
            }
            result.add(df.getOWLSubClassOfAxiom(superExpr, subExpr, edge.getAnnotations()));
            //aggiungo domain(range) per super
            if (superExpr instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom osvf = (OWLObjectSomeValuesFrom) superExpr;
                if (osvf.getFiller().isOWLThing()) {
                    OWLObjectPropertyExpression objExp = osvf.getProperty();
                    if (objExp instanceof OWLObjectInverseOf) {
                        result.add(df.getOWLObjectPropertyRangeAxiom(objExp.getNamedProperty(), subExpr, edge.getAnnotations()));
                    } else {
                        result.add(df.getOWLObjectPropertyDomainAxiom(objExp, subExpr, edge.getAnnotations()));
                    }
                }
            } else {
                if (superExpr instanceof OWLDataSomeValuesFrom) {
                    OWLDataSomeValuesFrom dsvf = (OWLDataSomeValuesFrom) superExpr;
                    if (dsvf.getFiller().isTopDatatype()) {
                        result.add(df.getOWLDataPropertyDomainAxiom(dsvf.getProperty(), subExpr, edge.getAnnotations()));
                    }
                }
            }
            return result;
        } else {
            if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                OWLObjectPropertyExpression subExpr = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
                OWLObjectPropertyExpression superExpr = GraphOLUtilities_v3.getObjectPropertyExpression(tgtNode, diagram, df);
                result.add(df.getOWLEquivalentObjectPropertiesAxiom(subExpr, superExpr, edge.getAnnotations()));
                result.add(df.getOWLSubObjectPropertyOfAxiom(subExpr, superExpr, edge.getAnnotations()));
                result.add(df.getOWLSubObjectPropertyOfAxiom(superExpr, subExpr, edge.getAnnotations()));
                return result;
            } else {
                if (srcNode instanceof GraphOLDataPropertyExpressionStartingNodeI) {
                    OWLDataPropertyExpression subExpr = GraphOLUtilities_v3.getDataPropertyExpression(srcNode, diagram, df);
                    GraphOLNode tgtNode = diagram.getNodeById(tgtNodeId);
                    OWLDataPropertyExpression superExpr = GraphOLUtilities_v3.getDataPropertyExpression(tgtNode, diagram, df);
                    result.add(df.getOWLEquivalentDataPropertiesAxiom(subExpr, superExpr, edge.getAnnotations()));
                    result.add(df.getOWLSubDataPropertyOfAxiom(subExpr, superExpr, edge.getAnnotations()));
                    result.add(df.getOWLSubDataPropertyOfAxiom(superExpr, subExpr, edge.getAnnotations()));
                    return result;
                }
            }
        }
        logger.error("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
        throw new RuntimeException("Parsing of logical axiom not possile by edge with id=" + edge.getEdgeId() + " instance of class " + edge.getClass());
    }

    //DISJOINTNESS
    private static OWLDisjointClassesAxiom getDisjointClasses(GraphOLDisjointUnionNode disjUnionNode, GraphOLDiagram diagram, OWLDataFactory df) {
        Set<OWLClassExpression> operands = new HashSet<>();
        List<GraphOLConstructorInputEdge> incomingEdges = diagram.getIncomingOperatorEdgesByNodeId(disjUnionNode.getNodeId());
        if (incomingEdges.size() > 1) {
            incomingEdges.forEach(edge -> {
                String srcNodeId = edge.getSourceNodeId();
                GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                operands.add(GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df));
            });
            return df.getOWLDisjointClassesAxiom(operands);
        }
        return null;
    }

    //KEYS
    private static OWLHasKeyAxiom getOWLHasKeyAxiom(GraphOLHasKeyNode hasKeyNode, GraphOLDiagram diagram, OWLDataFactory df) {
        List<OWLClassExpression> classExpressions = new LinkedList<>();
        Set<OWLPropertyExpression> propExpressions = new HashSet<>();
        List<GraphOLConstructorInputEdge> incomingEdges = diagram.getIncomingOperatorEdgesByNodeId(hasKeyNode.getNodeId());
        if (incomingEdges.size() > 1) {
            incomingEdges.forEach(edge -> {
                String srcNodeId = edge.getSourceNodeId();
                GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                if (srcNode instanceof GraphOLOntologyAttributeNode) {
                    propExpressions.add(GraphOLUtilities_v3.getDataPropertyExpression(srcNode, diagram, df));
                } else {
                    if (srcNode instanceof GraphOLOntologyRoleNode || srcNode instanceof GraphOLRoleInverseNode) {
                        propExpressions.add(GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df));
                    } else {
                        classExpressions.add(GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df));
                    }
                }
            });
            return df.getOWLHasKeyAxiom(classExpressions.get(0), propExpressions);
        }
        return null;
    }

    /****************************
     EXPRESSIONS
     *****************************/

    private static OWLClassExpression getClassExpression(GraphOLNode startNode, GraphOLDiagram diagram, OWLDataFactory df) {
        if (startNode instanceof GraphOLOntologyClassNode) {
            GraphOLOntologyClassNode classnode = (GraphOLOntologyClassNode) startNode;
            return df.getOWLClass(IRI.create(classnode.getIri()));
        } else {
            List<GraphOLConstructorInputEdge> incomingEdges = diagram.getIncomingOperatorEdgesByNodeId(startNode.getNodeId());
            if (startNode instanceof GraphOLComplementNode) {
                if (incomingEdges.size() == 1) {
                    GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                    String srcNodeId = opEdge.getSourceNodeId();
                    GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                    OWLClassExpression operand = GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df);
                    return df.getOWLObjectComplementOf(operand);
                } else {
                    logger.error("Found complement node " + startNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                    throw new RuntimeException("Found complement node " + startNode.getNodeId() + " with incoming operator edges count different from 1");
                }
            } else {
                if (startNode instanceof GraphOLDisjointUnionNode) {
                    Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
                    incomingEdges.forEach(edge -> {
                        String srcNodeId = edge.getSourceNodeId();
                        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                        OWLClassExpression currExp = GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df);
                        operands.add(currExp);
                    });
                    return df.getOWLObjectUnionOf(operands);
                } else {
                    if (startNode instanceof GraphOLDomainCardinalityRestrictionNode) {
                        int min = ((GraphOLDomainCardinalityRestrictionNode) startNode).getMinCardinality();
                        int max = -1;
                        boolean hasMaxCard = ((GraphOLDomainCardinalityRestrictionNode) startNode).hasBoundedMaxCardinality();
                        if (hasMaxCard) {
                            max = ((GraphOLDomainCardinalityRestrictionNode) startNode).getMaxCardinality();
                        }
                        if (incomingEdges.size() > 0 && incomingEdges.size() <= 2) {
                            if (incomingEdges.size() == 1) {
                                GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                                String srcNodeId = opEdge.getSourceNodeId();
                                GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                                    OWLObjectPropertyExpression objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                                    OWLClassExpression clExp = df.getOWLThing();
                                    if (hasMaxCard) {
                                        if (min == max) {
                                            return df.getOWLObjectExactCardinality(min, objPropExp, clExp);
                                        } else {
                                            OWLObjectMinCardinality minExpr = df.getOWLObjectMinCardinality(min, objPropExp, clExp);
                                            OWLObjectMaxCardinality maxExpr = df.getOWLObjectMaxCardinality(max, objPropExp, clExp);
                                            return df.getOWLObjectIntersectionOf(minExpr, maxExpr);
                                        }
                                    } else {
                                        return df.getOWLObjectMinCardinality(min, objPropExp, clExp);
                                    }
                                } else {
                                    //DATAPROPERTY
                                    OWLDataPropertyExpression dataPropExp = GraphOLUtilities_v3.getDataPropertyExpression(srcNode, diagram, df);
                                    OWLDatatype dtRange = df.getTopDatatype();
                                    if (hasMaxCard) {
                                        if (min == max) {
                                            return df.getOWLDataExactCardinality(min, dataPropExp, dtRange);
                                        } else {
                                            OWLDataMinCardinality minExpr = df.getOWLDataMinCardinality(min, dataPropExp, dtRange);
                                            OWLDataMaxCardinality maxExpr = df.getOWLDataMaxCardinality(max, dataPropExp, dtRange);
                                            return df.getOWLObjectIntersectionOf(minExpr, maxExpr);
                                        }
                                    } else {
                                        return df.getOWLDataMinCardinality(min, dataPropExp, dtRange);
                                    }
                                }
                            } else {
                                GraphOLConstructorInputEdge firstOpEdge = incomingEdges.get(0);
                                String firstSrcNodeId = firstOpEdge.getSourceNodeId();
                                GraphOLNode firstSrcNode = diagram.getNodeById(firstSrcNodeId);

                                GraphOLConstructorInputEdge secondOpEdge = incomingEdges.get(1);
                                String secondSrcNodeId = secondOpEdge.getSourceNodeId();
                                GraphOLNode secondSrcNode = diagram.getNodeById(secondSrcNodeId);
                                if ((firstSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) || (secondSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI)) {
                                    OWLClassExpression clExp;
                                    OWLObjectPropertyExpression objPropExp;
                                    if (firstSrcNode instanceof GraphOLClassExpressionStartingNodeI) {
                                        clExp = GraphOLUtilities_v3.getClassExpression(firstSrcNode, diagram, df);
                                        objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(secondSrcNode, diagram, df);
                                    } else {
                                        clExp = GraphOLUtilities_v3.getClassExpression(secondSrcNode, diagram, df);
                                        objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(firstSrcNode, diagram, df);
                                    }
                                    if (hasMaxCard) {
                                        if (min == max) {
                                            return df.getOWLObjectExactCardinality(min, objPropExp, clExp);
                                        } else {
                                            OWLObjectMinCardinality minExpr = df.getOWLObjectMinCardinality(min, objPropExp, clExp);
                                            OWLObjectMaxCardinality maxExpr = df.getOWLObjectMaxCardinality(max, objPropExp, clExp);
                                            return df.getOWLObjectIntersectionOf(minExpr, maxExpr);
                                        }
                                    } else {
                                        return df.getOWLObjectMinCardinality(min, objPropExp, clExp);
                                    }
                                } else {
                                    //DATAPROPERTY
                                    OWLDataPropertyExpression dataPropExp;
                                    OWLDataRange dtRange;
                                    if (firstSrcNode instanceof GraphOLDataPropertyExpressionStartingNodeI) {
                                        dataPropExp = GraphOLUtilities_v3.getDataPropertyExpression(firstSrcNode, diagram, df);
                                        dtRange = GraphOLUtilities_v3.getDataRangeExpression(secondSrcNode, diagram, df);
                                    } else {
                                        dataPropExp = GraphOLUtilities_v3.getDataPropertyExpression(secondSrcNode, diagram, df);
                                        dtRange = GraphOLUtilities_v3.getDataRangeExpression(firstSrcNode, diagram, df);
                                    }
                                    if (hasMaxCard) {
                                        if (min == max) {
                                            return df.getOWLDataExactCardinality(min, dataPropExp, dtRange);
                                        } else {
                                            OWLDataMinCardinality minExpr = df.getOWLDataMinCardinality(min, dataPropExp, dtRange);
                                            OWLDataMaxCardinality maxExpr = df.getOWLDataMaxCardinality(max, dataPropExp, dtRange);
                                            return df.getOWLObjectIntersectionOf(minExpr, maxExpr);
                                        }
                                    } else {
                                        return df.getOWLDataMinCardinality(min, dataPropExp, dtRange);
                                    }
                                }
                            }
                        } else {
                            logger.error("Found cardinality restriction node " + startNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                            throw new RuntimeException("Found cardinality restriction node " + startNode.getNodeId() + " with incoming operator edges count different from 1");
                        }
                    } else {
                        if (startNode instanceof GraphOLDomainExistentialNode) {
                            if (incomingEdges.size() > 0 && incomingEdges.size() <= 2) {
                                if (incomingEdges.size() == 1) {
                                    GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                                    String srcNodeId = opEdge.getSourceNodeId();
                                    GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                    if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                                        OWLClassExpression clExp = df.getOWLThing();
                                        OWLObjectPropertyExpression objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                                        return df.getOWLObjectSomeValuesFrom(objPropExp, clExp);
                                    } else {
                                        //DATAPROPERTY
                                        OWLDataPropertyExpression dataPropExp = GraphOLUtilities_v3.getDataPropertyExpression(srcNode, diagram, df);
                                        OWLDatatype dtRange = df.getTopDatatype();
                                        ;
                                        return df.getOWLDataSomeValuesFrom(dataPropExp, dtRange);
                                    }
                                } else {
                                    GraphOLConstructorInputEdge firstOpEdge = incomingEdges.get(0);
                                    String firstSrcNodeId = firstOpEdge.getSourceNodeId();
                                    GraphOLNode firstSrcNode = diagram.getNodeById(firstSrcNodeId);

                                    GraphOLConstructorInputEdge secondOpEdge = incomingEdges.get(1);
                                    String secondSrcNodeId = secondOpEdge.getSourceNodeId();
                                    GraphOLNode secondSrcNode = diagram.getNodeById(secondSrcNodeId);
                                    if ((firstSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) || (secondSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI)) {
                                        OWLClassExpression clExp;
                                        OWLObjectPropertyExpression objPropExp;
                                        if (firstSrcNode instanceof GraphOLClassExpressionStartingNodeI) {
                                            clExp = GraphOLUtilities_v3.getClassExpression(firstSrcNode, diagram, df);
                                            objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(secondSrcNode, diagram, df);
                                        } else {
                                            clExp = GraphOLUtilities_v3.getClassExpression(secondSrcNode, diagram, df);
                                            objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(firstSrcNode, diagram, df);
                                        }
                                        return df.getOWLObjectSomeValuesFrom(objPropExp, clExp);
                                    } else {
                                        //DATAPROPERTY
                                        OWLDataPropertyExpression dtPropExpr;
                                        OWLDataRange dtRange;
                                        if (firstSrcNode instanceof GraphOLDataPropertyExpressionStartingNodeI) {
                                            dtPropExpr = GraphOLUtilities_v3.getDataPropertyExpression(firstSrcNode, diagram, df);
                                            dtRange = GraphOLUtilities_v3.getDataRangeExpression(secondSrcNode, diagram, df);
                                        } else {
                                            dtPropExpr = GraphOLUtilities_v3.getDataPropertyExpression(secondSrcNode, diagram, df);
                                            dtRange = GraphOLUtilities_v3.getDataRangeExpression(firstSrcNode, diagram, df);
                                        }
                                        return df.getOWLDataSomeValuesFrom(dtPropExpr, dtRange);
                                    }
                                }
                            } else {
                                logger.error("Found existential node " + startNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                                throw new RuntimeException("Found existential node " + startNode.getNodeId() + " with incoming operator edges count different from 1");
                            }
                        } else {
                            if (startNode instanceof GraphOLDomainForAllNode) {
                                if (incomingEdges.size() > 0 && incomingEdges.size() <= 2) {
                                    if (incomingEdges.size() == 1) {
                                        GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                                        String srcNodeId = opEdge.getSourceNodeId();
                                        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                        if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                                            OWLObjectPropertyExpression objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                                            OWLClassExpression clExp = df.getOWLThing();
                                            return df.getOWLObjectAllValuesFrom(objPropExp, clExp);
                                        } else {
                                            //DATAPROPERTY
                                            OWLDataPropertyExpression dataPropExp = GraphOLUtilities_v3.getDataPropertyExpression(srcNode, diagram, df);
                                            OWLDatatype dtRange = df.getTopDatatype();
                                            ;
                                            return df.getOWLDataAllValuesFrom(dataPropExp, dtRange);
                                        }
                                    } else {
                                        GraphOLConstructorInputEdge firstOpEdge = incomingEdges.get(0);
                                        String firstSrcNodeId = firstOpEdge.getSourceNodeId();
                                        GraphOLNode firstSrcNode = diagram.getNodeById(firstSrcNodeId);

                                        GraphOLConstructorInputEdge secondOpEdge = incomingEdges.get(1);
                                        String secondSrcNodeId = secondOpEdge.getSourceNodeId();
                                        GraphOLNode secondSrcNode = diagram.getNodeById(secondSrcNodeId);
                                        if ((firstSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) || (secondSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI)) {
                                            OWLClassExpression clExp;
                                            OWLObjectPropertyExpression objPropExp;
                                            if (firstSrcNode instanceof GraphOLClassExpressionStartingNodeI) {
                                                clExp = GraphOLUtilities_v3.getClassExpression(firstSrcNode, diagram, df);
                                                objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(secondSrcNode, diagram, df);
                                            } else {
                                                clExp = GraphOLUtilities_v3.getClassExpression(secondSrcNode, diagram, df);
                                                objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(firstSrcNode, diagram, df);
                                            }
                                            return df.getOWLObjectAllValuesFrom(objPropExp, clExp);
                                        } else {
                                            //DATAPROPERTY
                                            OWLDataPropertyExpression dtPropExpr;
                                            OWLDataRange dtRange;
                                            if (firstSrcNode instanceof GraphOLDataPropertyExpressionStartingNodeI) {
                                                dtPropExpr = GraphOLUtilities_v3.getDataPropertyExpression(firstSrcNode, diagram, df);
                                                dtRange = GraphOLUtilities_v3.getDataRangeExpression(secondSrcNode, diagram, df);
                                            } else {
                                                dtPropExpr = GraphOLUtilities_v3.getDataPropertyExpression(secondSrcNode, diagram, df);
                                                dtRange = GraphOLUtilities_v3.getDataRangeExpression(firstSrcNode, diagram, df);
                                            }
                                            return df.getOWLDataAllValuesFrom(dtPropExpr, dtRange);
                                        }
                                    }
                                } else {
                                    logger.error("Found forAll node " + startNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                                    throw new RuntimeException("Found forAll node " + startNode.getNodeId() + " with incoming operator edges count different from 1");
                                }
                            } else {
                                if (startNode instanceof GraphOLDomainSelfNode) {
                                    GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                                    String srcNodeId = opEdge.getSourceNodeId();
                                    GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                    OWLObjectPropertyExpression objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                                    return df.getOWLObjectHasSelf(objPropExp);
                                } else {
                                    if (startNode instanceof GraphOLEnumerationNode) {
                                        Set<OWLIndividual> operands = new HashSet<OWLIndividual>();
                                        incomingEdges.forEach(edge -> {
                                            String srcNodeId = edge.getSourceNodeId();
                                            GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                            if (srcNode instanceof GraphOLOntologyIndividualNode) {
                                                operands.add(df.getOWLNamedIndividual(IRI.create(((GraphOLOntologyIndividualNode) srcNode).getIri())));
                                            } else {
                                                logger.error("Found  node " + srcNode.getNodeId() + " of type " + srcNode.getNodeType() + " connected to a node of type oneOf");
                                                throw new RuntimeException("Found  node " + srcNode.getNodeId() + " of type " + srcNode.getNodeType() + " connected to a node of type oneOf");

                                            }
                                        });
                                        return df.getOWLObjectOneOf(operands);
                                    } else {
                                        if (startNode instanceof GraphOLIntersectionNode) {
                                            Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
                                            incomingEdges.forEach(edge -> {
                                                String srcNodeId = edge.getSourceNodeId();
                                                GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                                OWLClassExpression currExp = GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df);
                                                operands.add(currExp);
                                            });
                                            return df.getOWLObjectIntersectionOf(operands);
                                        } else {
                                            if (startNode instanceof GraphOLRangeCardinalityRestrictionNode) {
                                                int min = ((GraphOLRangeCardinalityRestrictionNode) startNode).getMinCardinality();
                                                int max = -1;
                                                boolean hasMaxCard = ((GraphOLRangeCardinalityRestrictionNode) startNode).hasBoundedMaxCardinality();
                                                if (hasMaxCard) {
                                                    max = ((GraphOLRangeCardinalityRestrictionNode) startNode).getMaxCardinality();
                                                }
                                                if (incomingEdges.size() > 0 && incomingEdges.size() <= 2) {
                                                    if (incomingEdges.size() == 1) {
                                                        GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                                                        String srcNodeId = opEdge.getSourceNodeId();
                                                        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                                        if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                                                            OWLObjectPropertyExpression objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                                                            OWLObjectPropertyExpression objPropExpInv;
                                                            if(objPropExp instanceof OWLObjectProperty) {
                                                                objPropExpInv = df.getOWLObjectInverseOf((OWLObjectProperty)objPropExp);
                                                            }
                                                            else{
                                                                objPropExpInv = objPropExp.getNamedProperty();
                                                            }
                                                            OWLClassExpression clExp = df.getOWLThing();
                                                            if (hasMaxCard) {
                                                                if (min == max) {
                                                                    return df.getOWLObjectExactCardinality(min, objPropExpInv, clExp);
                                                                } else {
                                                                    OWLObjectMinCardinality minExpr = df.getOWLObjectMinCardinality(min, objPropExpInv, clExp);
                                                                    OWLObjectMaxCardinality maxExpr = df.getOWLObjectMaxCardinality(max, objPropExpInv, clExp);
                                                                    return df.getOWLObjectIntersectionOf(minExpr, maxExpr);
                                                                }
                                                            } else {
                                                                return df.getOWLObjectMinCardinality(min, objPropExpInv, clExp);
                                                            }
                                                        } else {
                                                            logger.error("Found cardinality restriction inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                            throw new RuntimeException("Found cardinality restriction inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                        }
                                                    } else {
                                                        GraphOLConstructorInputEdge firstOpEdge = incomingEdges.get(0);
                                                        String firstSrcNodeId = firstOpEdge.getSourceNodeId();
                                                        GraphOLNode firstSrcNode = diagram.getNodeById(firstSrcNodeId);

                                                        GraphOLConstructorInputEdge secondOpEdge = incomingEdges.get(1);
                                                        String secondSrcNodeId = secondOpEdge.getSourceNodeId();
                                                        GraphOLNode secondSrcNode = diagram.getNodeById(secondSrcNodeId);
                                                        if ((firstSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) || (secondSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI)) {
                                                            OWLClassExpression clExp;
                                                            OWLObjectPropertyExpression objPropExp;
                                                            if (firstSrcNode instanceof GraphOLClassExpressionStartingNodeI) {
                                                                clExp = GraphOLUtilities_v3.getClassExpression(firstSrcNode, diagram, df);
                                                                objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(secondSrcNode, diagram, df);
                                                            } else {
                                                                clExp = GraphOLUtilities_v3.getClassExpression(secondSrcNode, diagram, df);
                                                                objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(firstSrcNode, diagram, df);
                                                            }
                                                            OWLObjectPropertyExpression objPropExpInv;
                                                            if(objPropExp instanceof OWLObjectProperty) {
                                                                objPropExpInv = df.getOWLObjectInverseOf((OWLObjectProperty)objPropExp);
                                                            }
                                                            else{
                                                                objPropExpInv = objPropExp.getNamedProperty();
                                                            }
                                                            if (hasMaxCard) {
                                                                if (min == max) {
                                                                    return df.getOWLObjectExactCardinality(min, objPropExpInv, clExp);
                                                                } else {
                                                                    OWLObjectMinCardinality minExpr = df.getOWLObjectMinCardinality(min, objPropExpInv, clExp);
                                                                    OWLObjectMaxCardinality maxExpr = df.getOWLObjectMaxCardinality(max, objPropExpInv, clExp);
                                                                    return df.getOWLObjectIntersectionOf(minExpr, maxExpr);
                                                                }
                                                            } else {
                                                                return df.getOWLObjectMinCardinality(min, objPropExpInv, clExp);
                                                            }
                                                        } else {
                                                            logger.error("Found cardinality restriction inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                            throw new RuntimeException("Found cardinality restriction inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                        }
                                                    }
                                                } else {
                                                    logger.error("Found cardinality restriction node " + startNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                                                    throw new RuntimeException("Found cardinality restriction node " + startNode.getNodeId() + " with incoming operator edges count different from 1");
                                                }
                                            } else {
                                                if (startNode instanceof GraphOLRangeExistentialNode) {
                                                    if (incomingEdges.size() > 0 && incomingEdges.size() <= 2) {
                                                        if (incomingEdges.size() == 1) {
                                                            GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                                                            String srcNodeId = opEdge.getSourceNodeId();
                                                            GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                                            if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                                                                OWLClassExpression clExp = df.getOWLThing();
                                                                OWLObjectPropertyExpression objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                                                                OWLObjectPropertyExpression objPropExpInv;
                                                                if(objPropExp instanceof OWLObjectProperty) {
                                                                    objPropExpInv = df.getOWLObjectInverseOf((OWLObjectProperty)objPropExp);
                                                                }
                                                                else{
                                                                    objPropExpInv = objPropExp.getNamedProperty();
                                                                }
                                                                return df.getOWLObjectSomeValuesFrom(objPropExpInv, clExp);
                                                            } else {
                                                                logger.error("Found existential inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                                throw new RuntimeException("Found existential inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                            }
                                                        } else {
                                                            GraphOLConstructorInputEdge firstOpEdge = incomingEdges.get(0);
                                                            String firstSrcNodeId = firstOpEdge.getSourceNodeId();
                                                            GraphOLNode firstSrcNode = diagram.getNodeById(firstSrcNodeId);

                                                            GraphOLConstructorInputEdge secondOpEdge = incomingEdges.get(1);
                                                            String secondSrcNodeId = secondOpEdge.getSourceNodeId();
                                                            GraphOLNode secondSrcNode = diagram.getNodeById(secondSrcNodeId);
                                                            if ((firstSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) || (secondSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI)) {
                                                                OWLClassExpression clExp;
                                                                OWLObjectPropertyExpression objPropExp;
                                                                if (firstSrcNode instanceof GraphOLClassExpressionStartingNodeI) {
                                                                    clExp = GraphOLUtilities_v3.getClassExpression(firstSrcNode, diagram, df);
                                                                    objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(secondSrcNode, diagram, df);
                                                                } else {
                                                                    clExp = GraphOLUtilities_v3.getClassExpression(secondSrcNode, diagram, df);
                                                                    objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(firstSrcNode, diagram, df);
                                                                }
                                                                OWLObjectPropertyExpression objPropExpInv;
                                                                if(objPropExp instanceof OWLObjectProperty) {
                                                                    objPropExpInv = df.getOWLObjectInverseOf((OWLObjectProperty)objPropExp);
                                                                }
                                                                else{
                                                                    objPropExpInv = objPropExp.getNamedProperty();
                                                                }
                                                                return df.getOWLObjectSomeValuesFrom(objPropExpInv, clExp);
                                                            } else {
                                                                logger.error("Found existential inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                                throw new RuntimeException("Found existential inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                            }
                                                        }
                                                    } else {
                                                        logger.error("Found existential node " + startNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                                                        throw new RuntimeException("Found existential node " + startNode.getNodeId() + " with incoming operator edges count different from 1");
                                                    }
                                                } else {
                                                    if (startNode instanceof GraphOLRangeForAllNode) {
                                                        if (incomingEdges.size() > 0 && incomingEdges.size() <= 2) {
                                                            if (incomingEdges.size() == 1) {
                                                                GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                                                                String srcNodeId = opEdge.getSourceNodeId();
                                                                GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                                                if (srcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) {
                                                                    OWLObjectPropertyExpression objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                                                                    OWLClassExpression clExp = df.getOWLThing();
                                                                    OWLObjectPropertyExpression objPropExpInv;
                                                                    if(objPropExp instanceof OWLObjectProperty) {
                                                                        objPropExpInv = df.getOWLObjectInverseOf((OWLObjectProperty)objPropExp);
                                                                    }
                                                                    else{
                                                                        objPropExpInv = objPropExp.getNamedProperty();
                                                                    }
                                                                    return df.getOWLObjectAllValuesFrom(objPropExpInv, clExp);
                                                                } else {
                                                                    logger.error("Found forAll inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                                    throw new RuntimeException("Found forAll inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                                }
                                                            } else {
                                                                GraphOLConstructorInputEdge firstOpEdge = incomingEdges.get(0);
                                                                String firstSrcNodeId = firstOpEdge.getSourceNodeId();
                                                                GraphOLNode firstSrcNode = diagram.getNodeById(firstSrcNodeId);
                                                                GraphOLConstructorInputEdge secondOpEdge = incomingEdges.get(1);
                                                                String secondSrcNodeId = secondOpEdge.getSourceNodeId();
                                                                GraphOLNode secondSrcNode = diagram.getNodeById(secondSrcNodeId);
                                                                if ((firstSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI) || (secondSrcNode instanceof GraphOLObjectPropertyExpressionStartingNodeI)) {
                                                                    OWLClassExpression clExp;
                                                                    OWLObjectPropertyExpression objPropExp;
                                                                    if (firstSrcNode instanceof GraphOLClassExpressionStartingNodeI) {
                                                                        clExp = GraphOLUtilities_v3.getClassExpression(firstSrcNode, diagram, df);
                                                                        objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(secondSrcNode, diagram, df);
                                                                    } else {
                                                                        clExp = GraphOLUtilities_v3.getClassExpression(secondSrcNode, diagram, df);
                                                                        objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(firstSrcNode, diagram, df);
                                                                    }
                                                                    OWLObjectPropertyExpression objPropExpInv;
                                                                    if(objPropExp instanceof OWLObjectProperty) {
                                                                        objPropExpInv = df.getOWLObjectInverseOf((OWLObjectProperty)objPropExp);
                                                                    }
                                                                    else{
                                                                        objPropExpInv = objPropExp.getNamedProperty();
                                                                    }
                                                                    return df.getOWLObjectAllValuesFrom(objPropExpInv, clExp);
                                                                } else {
                                                                    logger.error("Found forAll inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                                    throw new RuntimeException("Found forAll inverse node " + startNode.getNodeId() + " connected to a data property node ");
                                                                }
                                                            }
                                                        } else {
                                                            logger.error("Found forAll node " + startNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                                                            throw new RuntimeException("Found forAll node " + startNode.getNodeId() + " with incoming operator edges count different from 1");
                                                        }
                                                    } else {
                                                        if (startNode instanceof GraphOLRangeSelfNode) {
                                                            GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                                                            String srcNodeId = opEdge.getSourceNodeId();
                                                            GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                                            OWLObjectPropertyExpression objPropExp = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                                                            OWLObjectPropertyExpression objPropExpInv;
                                                            if(objPropExp instanceof OWLObjectProperty) {
                                                                objPropExpInv = df.getOWLObjectInverseOf((OWLObjectProperty)objPropExp);
                                                            }
                                                            else{
                                                                objPropExpInv = objPropExp.getNamedProperty();
                                                            }
                                                            return df.getOWLObjectHasSelf(objPropExpInv);
                                                        } else {
                                                            if (startNode instanceof GraphOLUnionNode) {
                                                                Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
                                                                incomingEdges.forEach(edge -> {
                                                                    String srcNodeId = edge.getSourceNodeId();
                                                                    GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                                                    OWLClassExpression currExp = GraphOLUtilities_v3.getClassExpression(srcNode, diagram, df);
                                                                    operands.add(currExp);
                                                                });
                                                                return df.getOWLObjectUnionOf(operands);
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
        logger.error("Parsing of classExpression not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass());
        throw new RuntimeException("Parsing of classExpression not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass());
    }


    private static OWLObjectPropertyExpression getObjectPropertyExpression(GraphOLNode startNode, GraphOLDiagram diagram, OWLDataFactory df) {
        if (startNode instanceof GraphOLOntologyRoleNode) {
            String iriStr = ((GraphOLOntologyRoleNode) startNode).getIri();
            IRI iri = IRI.create(iriStr);
            return df.getOWLObjectProperty(iri);
        } else {
            if (startNode instanceof GraphOLRoleInverseNode) {
                List<GraphOLConstructorInputEdge> incomingEdges = diagram.getIncomingOperatorEdgesByNodeId(startNode.getNodeId());
                if (incomingEdges.size() == 1) {
                    //if(startNode instanceof GraphOLOntologyRoleNode) {
                    if (incomingEdges.size() == 1) {
                        GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                        String srcNodeId = opEdge.getSourceNodeId();
                        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                        OWLObjectPropertyExpression dirExpr = GraphOLUtilities_v3.getObjectPropertyExpression(srcNode, diagram, df);
                        return dirExpr.getInverseProperty();
                    }
                } else {
                    logger.error("Found GraphOLRoleInverseNode " + startNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                    throw new RuntimeException("Found GraphOLRoleInverseNode " + startNode.getNodeId() + " with incoming operator edges count different from 1");
                }
            }
            logger.error("Parsing of roleExpression not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass());
            throw new RuntimeException("Parsing of roleExpression not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass());

        }
    }

    private static OWLDataPropertyExpression getDataPropertyExpression(GraphOLNode startNode, GraphOLDiagram diagram, OWLDataFactory df) {
        if (startNode instanceof GraphOLOntologyAttributeNode) {
            String iriStr = ((GraphOLOntologyAttributeNode) startNode).getIri();
            IRI iri = IRI.create(iriStr);
            return df.getOWLDataProperty(iri);
        }
        logger.error("Parsing of dataPropertyExpression not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass());
        throw new RuntimeException("Parsing of dataPropertyExpression not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass());
    }

    private static OWLDataRange getDataRangeExpression(GraphOLNode startNode, GraphOLDiagram diagram, OWLDataFactory df) {
        if (startNode instanceof GraphOLOntologyValueDomainNode) {
            OWLDatatype datatype = df.getOWLDatatype(IRI.create(((GraphOLOntologyValueDomainNode) startNode).getIri()));
            return datatype;
        } else {
            List<GraphOLConstructorInputEdge> incomingEdges = diagram.getIncomingOperatorEdgesByNodeId(startNode.getNodeId());
            if (startNode instanceof GraphOLComplementNode) {
                if (incomingEdges.size() == 1) {
                    GraphOLConstructorInputEdge opEdge = incomingEdges.get(0);
                    String srcNodeId = opEdge.getSourceNodeId();
                    GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                    OWLDataRange operand = GraphOLUtilities_v3.getDataRangeExpression(srcNode, diagram, df);
                    return df.getOWLDataComplementOf(operand);
                } else {
                    logger.error("Found complement node " + startNode.getNodeId() + " with incoming operator edges count different from 1 found (" + incomingEdges.size() + ")");
                    throw new RuntimeException("Found complement node " + startNode.getNodeId() + " with incoming operator edges count different from 1");
                }
            } else {
                if (startNode instanceof GraphOLUnionNode) {
                    Set<OWLDataRange> operands = new HashSet<OWLDataRange>();
                    incomingEdges.forEach(edge -> {
                        String srcNodeId = edge.getSourceNodeId();
                        GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                        OWLDataRange currExp = GraphOLUtilities_v3.getDataRangeExpression(srcNode, diagram, df);
                        operands.add(currExp);
                    });
                    return df.getOWLDataUnionOf(operands);
                } else {
                    if (startNode instanceof GraphOLIntersectionNode) {
                        Set<OWLDataRange> operands = new HashSet<OWLDataRange>();
                        incomingEdges.forEach(edge -> {
                            String srcNodeId = edge.getSourceNodeId();
                            GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                            OWLDataRange currExp = GraphOLUtilities_v3.getDataRangeExpression(srcNode, diagram, df);
                            operands.add(currExp);
                        });
                        return df.getOWLDataIntersectionOf(operands);
                    } else {
                        if (startNode instanceof GraphOLEnumerationNode) {
                            Set<OWLLiteral> operands = new HashSet<OWLLiteral>();
                            incomingEdges.forEach(edge -> {
                                String srcNodeId = edge.getSourceNodeId();
                                GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                if (srcNode instanceof GraphOLOntologyLiteralNode) {
                                    GraphOLOntologyLiteralNode litNode = (GraphOLOntologyLiteralNode) srcNode;
                                    OWLLiteral lit = null;
                                    String lang = litNode.getLanguage();
                                    if (lang != null) {
                                        lit = df.getOWLLiteral(litNode.getLexicalForm(), lang);
                                    } else {
                                        OWL2Datatype dt2 = GraphOLUtilities_v3.getOWLDatatype(litNode.getDatatypeIRI());
                                        if (dt2 != null) {
                                            lit = df.getOWLLiteral(litNode.getLexicalForm(), dt2);
                                        } else {
                                            lit = df.getOWLLiteral(litNode.getLexicalForm(), df.getOWLDatatype(IRI.create(litNode.getDatatypeIRI())));
                                        }
                                    }
                                    operands.add(lit);
                                } else {
                                    logger.error("Parsing of DataOneOf  not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass() +
                                            ". Found input node " + srcNodeId + " that is instance of class " + srcNodeId.getClass());
                                    throw new RuntimeException("Parsing of DataOneOf not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass() +
                                            ". Found input node " + srcNodeId + " that is instance of class " + srcNodeId.getClass());
                                }
                            });
                            return df.getOWLDataOneOf(operands);
                        } else {
                            if (startNode instanceof GraphOLDatatypeRestrictionNode) {
                                OWLDatatype datatype = null;
                                Set<OWLFacetRestriction> facetRestrictions = new HashSet<>();
                                for (GraphOLConstructorInputEdge edge : incomingEdges) {
                                    String srcNodeId = edge.getSourceNodeId();
                                    GraphOLNode srcNode = diagram.getNodeById(srcNodeId);
                                    if (srcNode instanceof GraphOLOntologyValueDomainNode) {
                                        OWL2Datatype owl2DT = GraphOLUtilities_v3.getOWLDatatype(((GraphOLOntologyValueDomainNode) srcNode).getIri());
                                        datatype = owl2DT.getDatatype(df);
                                    } else {
                                        if (srcNode instanceof GraphOLOntologyFacetNode) {
                                            GraphOLOntologyFacetNode facetNode = (GraphOLOntologyFacetNode) srcNode;
                                            String restrValue = facetNode.getRestrictionValue();
                                            OWLDatatype valueDatatype = df.getOWLDatatype(IRI.create(facetNode.getValueDatatype()));
                                            IRI facetIRI = IRI.create(facetNode.getConstrainingFacet());
                                            OWLFacet facet = OWLFacet.getFacet(facetIRI);
                                            OWLLiteral literal = df.getOWLLiteral(restrValue, valueDatatype);
                                            //TODO ORA FAI COSI PERCHè eddy non mette tipo di dato dopo constrainingValue, quando eddy sara più preciso, allora associa
                                            //tipo di dato riportato da eddy
											/*switch (facet) {
											case FRACTION_DIGITS:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#integer"));
												break;
											case LANG_RANGE:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#language"));
												break;
											case LENGTH:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#integer"));
												break;
											case MAX_EXCLUSIVE:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#integer"));
												break;
											case MAX_INCLUSIVE:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#integer"));
												break;
											case MAX_LENGTH:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#integer"));
												break;
											case MIN_EXCLUSIVE:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#integer"));
												break;
											case MIN_INCLUSIVE:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#integer"));
												break;
											case MIN_LENGTH:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#integer"));
												break;
											case PATTERN:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#string"));
												break;
											case TOTAL_DIGITS:
												literal = df.getOWLLiteral(restrValue, GraphOLUtilities_v3.getOWLDatatype("http://www.w3.org/2001/XMLSchema#integer"));
												break;	
											default:
												logger.error("Parsing of constraining facet not possile by facet node node with id="+facetNode.getNodeId()+" instance of class " + facetNode.getClass());
												throw new RuntimeException("Parsing of constraining facet not possile by facet node node with id="+facetNode.getNodeId()+" instance of class " + facetNode.getClass());
											}*/
                                            OWLFacetRestriction facetRestr = df.getOWLFacetRestriction(facet, literal);
                                            facetRestrictions.add(facetRestr);
                                        }
                                    }
                                }
                                return df.getOWLDatatypeRestriction(datatype, facetRestrictions);
                            } else {
                                logger.error("Parsing of dataRange not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass());
                                throw new RuntimeException("Parsing of dataRange not possile by start node with id=" + startNode.getNodeId() + " instance of class " + startNode.getClass());
                            }
                        }
                    }
                }
            }
        }
    }
}

