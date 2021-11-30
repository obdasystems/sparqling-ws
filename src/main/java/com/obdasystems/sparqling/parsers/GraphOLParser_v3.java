package com.obdasystems.sparqling.parsers;

import com.google.common.base.Optional;
import com.obdasystems.sparqling.parsers.graphol.GraphOLEdgePoint;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;
import com.obdasystems.sparqling.parsers.graphol.edge.impl.*;
import com.obdasystems.sparqling.parsers.graphol.exception.GraphOLParserException;
import com.obdasystems.sparqling.parsers.graphol.exception.GraphOLUnknownPrefixException;
import com.obdasystems.sparqling.parsers.graphol.impl.v2.GraphOLDiagram;
import com.obdasystems.sparqling.parsers.graphol.impl.v2.GraphOLOntology;
import com.obdasystems.sparqling.parsers.graphol.node.impl.*;
import com.obdasystems.sparqling.parsers.graphol.utils.GraphOLUtilities_v3;
import org.apache.xerces.parsers.DOMParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class GraphOLParser_v3 {

	static Logger logger = LoggerFactory.getLogger(GraphOLParser_v3.class);

	//TAG NAMES
	public static final String GRAPHOL_ELEMENT_TAG_NAME = "graphol";
	//project
	private static final String PROJECT_ELEMENT_TAG_NAME = "project";
	//ontology
	private static final String ONTOLOGY_ELEMENT_TAG_NAME = "ontology";
	private static final String ONTOLOGY_PREFIXES_ELEMENT_TAG_NAME = "prefixes";
	private static final String ONTOLOGY_PREFIXES_PREFIX_ELEMENT_TAG_NAME = "prefix";
	private static final String ONTOLOGY_PREFIXES_PREFIX_VALUE_ELEMENT_TAG_NAME = "value";
	private static final String ONTOLOGY_PREFIXES_PREFIX_NAMESPACE_ELEMENT_TAG_NAME = "namespace";
	private static final String ONTOLOGY_DATATYPES_ELEMENT_TAG_NAME = "datatypes";
	private static final String ONTOLOGY_DATATYPES_DATATYPE_ELEMENT_TAG_NAME = "datatype";
	private static final String ONTOLOGY_LANGUAGES_ELEMENT_TAG_NAME = "languages";
	private static final String ONTOLOGY_LANGUAGES_LANGUAGE_ELEMENT_TAG_NAME = "language";
	private static final String ONTOLOGY_FACETS_ELEMENT_TAG_NAME = "facets";
	private static final String ONTOLOGY_FACETS_FACET_DATATYPE_ELEMENT_TAG_NAME = "facet";
	private static final String ONTOLOGY_ANNOTATIONPROPERTIES_ELEMENT_TAG_NAME = "annotationProperties";
	private static final String ONTOLOGY_ANNOTATIONPROPERTIES_ANNOTATIONPROPERTY_ELEMENT_TAG_NAME = "annotationProperty";
	private static final String ONTOLOGY_IRIS_ELEMENT_TAG_NAME = "iris";
	private static final String ONTOLOGY_IRIS_IRI_ELEMENT_TAG_NAME = "iri";
	private static final String ONTOLOGY_IRIS_IRI_VALUE_ELEMENT_TAG_NAME = "value";
	private static final String ONTOLOGY_IRIS_IRI_FUNCT_ELEMENT_TAG_NAME = "functional";
	private static final String ONTOLOGY_IRIS_IRI_INVFUNCT_ELEMENT_TAG_NAME = "inverseFunctional";
	private static final String ONTOLOGY_IRIS_IRI_ASYMM_ELEMENT_TAG_NAME = "asymmetric";
	private static final String ONTOLOGY_IRIS_IRI_IRREFL_ELEMENT_TAG_NAME = "irreflexive";
	private static final String ONTOLOGY_IRIS_IRI_REFL_ELEMENT_TAG_NAME = "reflexive";
	private static final String ONTOLOGY_IRIS_IRI_SYMM_ELEMENT_TAG_NAME = "symmetric";
	private static final String ONTOLOGY_IRIS_IRI_TRANS_ELEMENT_TAG_NAME = "transitive";
	private static final String ONTOLOGY_IRIS_IRI_ANNOTATIONS_ELEMENT_TAG_NAME = "annotations";
	private static final String ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_ELEMENT_TAG_NAME = "annotation";
	private static final String ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_SUBJECT_ELEMENT_TAG_NAME = "subject";
	private static final String ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_PROPERTY_ELEMENT_TAG_NAME = "property";
	private static final String ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_ELEMENT_TAG_NAME = "object";
	private static final String ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_LEXICALFORM_ELEMENT_TAG_NAME = "lexicalForm";
	private static final String ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_DATATYPE_ELEMENT_TAG_NAME = "datatype";
	private static final String ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_LANGUAGE_ELEMENT_TAG_NAME = "language";
	//diagram
	private static final String DIAGRAMS_ELEMENT_TAG_NAME = "diagrams";
	private static final String DIAGRAMS_DIAGRAM_ELEMENT_TAG_NAME = "diagram";
	private static final String DIAGRAMS_DIAGRAM_NODE_ELEMENT_TAG_NAME = "node";
	private static final String DIAGRAMS_DIAGRAM_NODE_IRI_ELEMENT_TAG_NAME = "iri";
	private static final String DIAGRAMS_DIAGRAM_NODE_LITERAL_ELEMENT_TAG_NAME = "literal";
	private static final String DIAGRAMS_DIAGRAM_NODE_LITERAL_LEXICALFORM_ELEMENT_TAG_NAME = "lexicalForm";
	private static final String DIAGRAMS_DIAGRAM_NODE_LITERAL_DATATYPE_ELEMENT_TAG_NAME = "datatype";
	private static final String DIAGRAMS_DIAGRAM_NODE_LITERAL_LANGUAGE_ELEMENT_TAG_NAME = "language";
	private static final String DIAGRAMS_DIAGRAM_NODE_FACET_ELEMENT_TAG_NAME = "facet";
	private static final String DIAGRAMS_DIAGRAM_NODE_FACET_CONSTRFACET_ELEMENT_TAG_NAME = "constrainingFacet";
	private static final String DIAGRAMS_DIAGRAM_NODE_LABEL_ELEMENT_TAG_NAME = "label";
	private static final String DIAGRAMS_DIAGRAM_NODE_GEOMETRY_ELEMENT_TAG_NAME = "geometry";
	private static final String DIAGRAMS_DIAGRAM_EDGE_ELEMENT_TAG_NAME = "edge";
	private static final String DIAGRAMS_DIAGRAM_EDGE_POINT_ELEMENT_TAG_NAME = "point";

	//ATTRIBUTE NAMES
	//graphol
	public static final String GRAPHOL_VERSION_ATTR_NAME = "version";
	//project
	private static final String PROJECT_NAME_ATTR_NAME = "name";
	private static final String PROJECT_VERSION_ATTR_NAME = "version";
	//ontology
	private static final String ONTOLOGY_IRI_ATTR_NAME = "iri";
	//diagram
	private static final String DIAGRAMS_DIAGRAM_NAME_ATTR_NAME = "name";
	private static final String DIAGRAMS_DIAGRAM_HEIGHT_ATTR_NAME = "height";
	private static final String DIAGRAMS_DIAGRAM_WIDTH_ATTR_NAME = "width";
	//diagram nodes
	private static final String DIAGRAMS_DIAGRAM_NODE_ID_ATTR_NAME = "id";
	private static final String DIAGRAMS_DIAGRAM_NODE_TYPE_ATTR_NAME = "type";
	private static final String DIAGRAMS_DIAGRAM_NODE_COLOR_ATTR_NAME = "color";
	private static final String DIAGRAMS_DIAGRAM_NODE_INPUTS_ATTR_NAME = "inputs";
	private static final String DIAGRAMS_DIAGRAM_NODE_GEOMETRY_X_ATTR_NAME = "x";
	private static final String DIAGRAMS_DIAGRAM_NODE_GEOMETRY_Y_ATTR_NAME = "y";
	private static final String DIAGRAMS_DIAGRAM_NODE_GEOMETRY_HEIGHT_ATTR_NAME = "height";
	private static final String DIAGRAMS_DIAGRAM_NODE_GEOMETRY_WIDTH_ATTR_NAME = "width";
	private static final String DIAGRAMS_DIAGRAM_NODE_LABEL_X_ATTR_NAME = "x";
	private static final String DIAGRAMS_DIAGRAM_NODE_LABEL_Y_ATTR_NAME = "y";
	private static final String DIAGRAMS_DIAGRAM_NODE_LABEL_HEIGHT_ATTR_NAME = "height";
	private static final String DIAGRAMS_DIAGRAM_NODE_LABEL_WIDTH_ATTR_NAME = "width";
	//diagram edges
	private static final String DIAGRAMS_DIAGRAM_EDGE_ID_ATTR_NAME = "id";
	private static final String DIAGRAMS_DIAGRAM_EDGE_TYPE_ATTR_NAME = "type";
	private static final String DIAGRAMS_DIAGRAM_EDGE_SOURCE_ATTR_NAME = "source";
	private static final String DIAGRAMS_DIAGRAM_EDGE_TARGET_ATTR_NAME = "target";
	private static final String DIAGRAMS_DIAGRAM_EDGE_POINT_X_ATTR_NAME = "x";
	private static final String DIAGRAMS_DIAGRAM_EDGE_POINT_Y_ATTR_NAME = "y";

	//ATTRIBUTE DEFAULT VALUES
	//diagram nodes
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_CONCEPT_VALUE = "concept";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_ROLE_VALUE = "role";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_VALUEDOMAIN_VALUE = "value-domain";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_ATTRIBUTE_VALUE = "attribute";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_INDIVIDUAL_VALUE = "individual";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_LITERAL_VALUE = "literal";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_DATATYPERESTR_VALUE = "datatype-restriction";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_FACET_VALUE = "facet";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_ROLEINVERSE_VALUE = "role-inverse";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_ROLECHAIN_VALUE = "role-chain";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_UNION_VALUE = "union";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_INTERSECTION_VALUE = "intersection";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_DISJUNION_VALUE = "disjoint-union";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_DOMRESTR_VALUE = "domain-restriction";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_RANGERESTR_VALUE = "range-restriction";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_COMPLEMENT_VALUE = "complement";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_ENUMERATION_VALUE = "enumeration";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_PROPASS_VALUE = "property-assertion";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_HASKEY_VALUE = "has-key";
	//diagram edges
	public static final String DIAGRAMS_DIAGRAM_EDGE_TYPE_EQUIVALENT_VALUE = "equivalence";
	public static final String DIAGRAMS_DIAGRAM_EDGE_TYPE_INCLUSION_VALUE = "inclusion";
	public static final String DIAGRAMS_DIAGRAM_EDGE_TYPE_INSTANCEOF_VALUE = "membership";
	public static final String DIAGRAMS_DIAGRAM_EDGE_TYPE_SAMEIND_VALUE = "same";
	public static final String DIAGRAMS_DIAGRAM_EDGE_TYPE_DIFFIND_VALUE = "different";
	public static final String DIAGRAMS_DIAGRAM_EDGE_TYPE_INPUTOPERATOR_VALUE = "input";

	//ELEMENT (INNER TEXT) DEFAULT VALUES
	//diagram nodes
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_EXISTS_INNER_TEXT	 = "exists";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_FORALL_INNER_TEXT	 = "forall";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_SELF_INNER_TEXT	 = "self";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_CARDINALITY_INNER_FIRST_CHAR = "(";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_CARDINALITY_INNER_LAST_CHAR = ")";
	public static final String DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_CARDINALITY_INNER_SPLIT_CHAR = ",";
	
	/**
	 * Parses the XML GraphOL specification provided by the file at filePath
	 * 
	 * @param graphol the string serialization of the graphol
	 * @param o externally provided instance of OWLOntologyManager
	 * @return the parsed OWL ontology
	 * @throws GraphOLParserException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 */
	public OWLOntology parseOWLOntology(String graphol, OWLOntologyManager o) {
		DOMParser p = new DOMParser();
		try {
			OWLDataFactory df = OWLManager.getOWLDataFactory();
//			OWLOntologyManager o = OWLManager.createOWLOntologyManager();
			OWLOntology ont = null;
			Optional<IRI> optOntIri = null;
			Optional<IRI> optVersIri = null;
			OWLOntologyID ontID = null;

			String ontologyVersion = "";
			String ontologyIri = "";
			Map<String, String> prefixes = new HashMap<>();
			List<String> functionalIris = new LinkedList<String>();
			Set<OWLAnnotationAssertionAxiom> annAssAxioms = new HashSet<OWLAnnotationAssertionAxiom>();

			p.parse(new InputSource(new StringReader(graphol)));
			Document d = p.getDocument();

			Element grapholRootElement =  d.getDocumentElement();

			if(grapholRootElement.hasAttribute(GRAPHOL_VERSION_ATTR_NAME)) {
				String vStr = grapholRootElement.getAttribute(GRAPHOL_VERSION_ATTR_NAME);
				int v;
				try {
					v = Integer.parseInt(vStr);	
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				if(v!=3) {
					throw new RuntimeException("Found Graphol version="+v+" (was expecting 3)");
				}

			}
			NodeList projectNodes = grapholRootElement.getElementsByTagName(PROJECT_ELEMENT_TAG_NAME);
			if(projectNodes.getLength()>0) {
				Node projectNode = projectNodes.item(0);
				if(projectNode instanceof Element) {
					Element projectElement = (Element)projectNode;
					if(projectElement.hasAttribute(PROJECT_VERSION_ATTR_NAME)) {
						ontologyVersion = projectElement.getAttribute(PROJECT_VERSION_ATTR_NAME);
					}
				}
			}
			NodeList ontologyNodes = grapholRootElement.getElementsByTagName(ONTOLOGY_ELEMENT_TAG_NAME);
			if(ontologyNodes.getLength()>0) {
				Node ontologyNode = ontologyNodes.item(0);
				if(ontologyNode instanceof Element) {
					Element ontologyElement = (Element) ontologyNode;
					if(ontologyElement.hasAttribute(ONTOLOGY_IRI_ATTR_NAME)) {
						ontologyIri = ontologyElement.getAttribute(ONTOLOGY_IRI_ATTR_NAME);
					}
					NodeList prefixesNodes = ontologyElement.getElementsByTagName(ONTOLOGY_PREFIXES_ELEMENT_TAG_NAME);
					if(prefixesNodes.getLength()>0) {
						Node prefixesNode = prefixesNodes.item(0);
						if(prefixesNode instanceof Element) {
							Element prefixesElement = (Element)prefixesNode;
							NodeList prefixNodes = prefixesElement.getElementsByTagName(ONTOLOGY_PREFIXES_PREFIX_ELEMENT_TAG_NAME);
							for(int i=0;i<prefixNodes.getLength();i++) {
								Node prefixNode = prefixNodes.item(i);
								if(prefixNode instanceof Element) {
									Element prefixElement = (Element)prefixNode;
									String value = null;
									String namespace = null;
									NodeList valueNodes = prefixElement.getElementsByTagName(ONTOLOGY_PREFIXES_PREFIX_VALUE_ELEMENT_TAG_NAME);
									if(valueNodes.getLength()>0) {
										Node valueNode = valueNodes.item(0);
										if(valueNode instanceof Element) {
											value = ((Element)valueNode).getTextContent();
										}
									}
									NodeList namespaceNodes = prefixElement.getElementsByTagName(ONTOLOGY_PREFIXES_PREFIX_NAMESPACE_ELEMENT_TAG_NAME);
									if(namespaceNodes.getLength()>0) {
										Node namespaceNode = namespaceNodes.item(0);
										if(namespaceNode instanceof Element) {
											namespace = ((Element)namespaceNode).getTextContent();
										}
									}
									if(!(value==null||namespace==null)) {
										prefixes.put(value, namespace);
									}
								}
							}
						}
					}

					optOntIri = Optional.of(IRI.create(ontologyIri));
					optVersIri = Optional.of(IRI.create(ontologyVersion));
					ontID = new OWLOntologyID(optOntIri, optVersIri);
					ont =  o.createOntology(ontID);
					FunctionalSyntaxDocumentFormat format = new FunctionalSyntaxDocumentFormat();
					format.copyPrefixesFrom(prefixes);
					o.setOntologyFormat(ont, format);

					NodeList datatypesNodes = ontologyElement.getElementsByTagName(ONTOLOGY_DATATYPES_ELEMENT_TAG_NAME);
					if(datatypesNodes.getLength()>0) {
						Node datatypesNode = datatypesNodes.item(0);
						if(datatypesNode instanceof Element) {
							NodeList datatypeNodes = ((Element) datatypesNode).getElementsByTagName(ONTOLOGY_DATATYPES_DATATYPE_ELEMENT_TAG_NAME);
							for(int i=0;i<datatypeNodes.getLength();i++) {
								try {
									String iriStr = ((Element)datatypeNodes.item(i)).getTextContent();
									OWLDatatype dt = df.getOWLDatatype(IRI.create(iriStr));
									OWLDeclarationAxiom ax = df.getOWLDeclarationAxiom(dt);
									o.addAxiom(ont, ax);
								} catch (Exception e) {
									logger.error("["+datatypeNodes.item(i).toString()+"]"+e.getMessage());
									logger.error(e.getStackTrace().toString());
								}
							}
						}
					}

					NodeList annotationPropertiesNodes = ontologyElement.getElementsByTagName(ONTOLOGY_ANNOTATIONPROPERTIES_ELEMENT_TAG_NAME);
					if(annotationPropertiesNodes.getLength()>0) {
						Node annotationPropertiesNode = annotationPropertiesNodes.item(0);
						if(annotationPropertiesNode instanceof Element) {
							NodeList annotationPropertyNodes = ((Element) annotationPropertiesNode).getElementsByTagName(ONTOLOGY_ANNOTATIONPROPERTIES_ANNOTATIONPROPERTY_ELEMENT_TAG_NAME);
							for(int i=0;i<annotationPropertyNodes.getLength();i++) {
								try {
									String iriStr = ((Element)annotationPropertyNodes.item(i)).getTextContent();
									OWLAnnotationProperty dt = df.getOWLAnnotationProperty(IRI.create(iriStr));
									OWLDeclarationAxiom ax = df.getOWLDeclarationAxiom(dt);
									o.addAxiom(ont, ax);
								} catch (Exception e) {
									logger.error("["+annotationPropertyNodes.item(i).toString()+"]"+e.getMessage());
									logger.error(e.getStackTrace().toString());
								}
							}
						}
					}

					NodeList irisNodes = ontologyElement.getElementsByTagName(ONTOLOGY_IRIS_ELEMENT_TAG_NAME);
					if(irisNodes.getLength()>0) {
						Node irisNode = irisNodes.item(0);
						if(irisNode instanceof Element) {
							NodeList iriNodes = ((Element) irisNode).getElementsByTagName(ONTOLOGY_IRIS_IRI_ELEMENT_TAG_NAME);
							for(int i=0;i<iriNodes.getLength();i++) {
								try {
									if(iriNodes.item(i) instanceof Element) {
										Element iriElement = (Element)iriNodes.item(i);
										NodeList iriValueNodes = iriElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_VALUE_ELEMENT_TAG_NAME);
										if(iriValueNodes.getLength()>0) {
											if(iriValueNodes.item(0) instanceof Element) {
												String iriStr = ((Element)iriValueNodes.item(0)).getTextContent();
												IRI iri = IRI.create(iriStr);
												NodeList iriFunctNodes = iriElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_FUNCT_ELEMENT_TAG_NAME);
												if(iriFunctNodes.getLength()>0) {
													functionalIris.add(iriStr);
												}
												NodeList iriInvFunctNodes = iriElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_INVFUNCT_ELEMENT_TAG_NAME);
												if(iriInvFunctNodes.getLength()>0) {
													OWLObjectProperty prop = df.getOWLObjectProperty(iri);
													OWLAxiom ax = df.getOWLInverseFunctionalObjectPropertyAxiom(prop);
													o.addAxiom(ont, ax);
												}
												NodeList iriSymmNodes = iriElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_SYMM_ELEMENT_TAG_NAME);
												if(iriSymmNodes.getLength()>0) {
													OWLObjectProperty prop = df.getOWLObjectProperty(iri);
													OWLAxiom ax = df.getOWLSymmetricObjectPropertyAxiom(prop);
													o.addAxiom(ont, ax);
												}
												NodeList iriAsymmNodes = iriElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ASYMM_ELEMENT_TAG_NAME);
												if(iriAsymmNodes.getLength()>0) {
													OWLObjectProperty prop = df.getOWLObjectProperty(iri);
													OWLAxiom ax = df.getOWLAsymmetricObjectPropertyAxiom(prop);
													o.addAxiom(ont, ax);
												}
												NodeList iriReflNodes = iriElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_REFL_ELEMENT_TAG_NAME);
												if(iriReflNodes.getLength()>0) {
													OWLObjectProperty prop = df.getOWLObjectProperty(iri);
													OWLAxiom ax = df.getOWLReflexiveObjectPropertyAxiom(prop);
													o.addAxiom(ont, ax);
												}
												NodeList iriIrrNodes = iriElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_IRREFL_ELEMENT_TAG_NAME);
												if(iriIrrNodes.getLength()>0) {
													OWLObjectProperty prop = df.getOWLObjectProperty(iri);
													OWLAxiom ax = df.getOWLIrreflexiveObjectPropertyAxiom(prop);
													o.addAxiom(ont, ax);
												}
												NodeList iriTransNodes = iriElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_TRANS_ELEMENT_TAG_NAME);
												if(iriTransNodes.getLength()>0) {
													OWLObjectProperty prop = df.getOWLObjectProperty(iri);
													OWLAxiom ax = df.getOWLTransitiveObjectPropertyAxiom(prop);
													o.addAxiom(ont, ax);
												}
												NodeList iriAnnotationsNodes = iriElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ELEMENT_TAG_NAME);
												if(iriAnnotationsNodes.getLength()>0 ) {
													Element annotationsEl = (Element)iriAnnotationsNodes.item(0);
													NodeList iriAnnotationNodes = (annotationsEl).getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_ELEMENT_TAG_NAME);
													for(int j=0;j<iriAnnotationNodes.getLength();j++) {
														Node annotationNode = iriAnnotationNodes.item(j);
														if(annotationNode instanceof Element) {
															Element annotationElement = (Element)annotationNode;
															NodeList propertyNodes = annotationElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_PROPERTY_ELEMENT_TAG_NAME);
															if(propertyNodes.getLength()>0) {
																Element propElement = (Element)propertyNodes.item(0);
																OWLAnnotationProperty annProp = df.getOWLAnnotationProperty(IRI.create(propElement.getTextContent()));
																NodeList objectNodes = annotationElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_ELEMENT_TAG_NAME);
																if(objectNodes.getLength()>0) {
																	Element objElement = (Element)objectNodes.item(0);
																	String lexForm = null;
																	NodeList lexNodes = objElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_LEXICALFORM_ELEMENT_TAG_NAME);
																	if(lexNodes.getLength()>0) {
																		Node lexNode = lexNodes.item(0);
																		if(lexNode instanceof Element) {
																			lexForm = lexNode.getTextContent();
																		}
																	}
																	if(lexForm!=null) {
																		String lang = null;
																		NodeList languageNodes = objElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_LANGUAGE_ELEMENT_TAG_NAME);
																		if(languageNodes.getLength()>0) {
																			Node langNode = languageNodes.item(0);
																			if(langNode instanceof Element) {
																				lang = langNode.getTextContent();
																			}
																		}
																		if(lang!=null) {
																			OWLLiteral lit = df.getOWLLiteral(lexForm, lang);
																			OWLAnnotationAssertionAxiom ax = df.getOWLAnnotationAssertionAxiom(annProp, iri, lit);
																			annAssAxioms.add(ax);
																		}
																		else {
																			NodeList datatypeNodes = objElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_DATATYPE_ELEMENT_TAG_NAME);
																			if(datatypeNodes.getLength()>0) {
																				Node dtNode = datatypeNodes.item(0);
																				if(dtNode instanceof Element) {
																					OWLDatatype dt = df.getOWLDatatype(IRI.create(dtNode.getTextContent()));
																					OWLLiteral lit = df.getOWLLiteral(lexForm, dt);
																					OWLAnnotationAssertionAxiom ax = df.getOWLAnnotationAssertionAxiom(annProp, iri, lit);
																					annAssAxioms.add(ax);
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
								} catch (Exception e) {
									logger.error("["+iriNodes.item(i).toString()+"]"+e.getMessage());
									logger.error(e.getStackTrace().toString());
								}
							}
						}
					}
				}
			}

			List<GraphOLDiagram> diagrams = new LinkedList<>();
			//Extracting diagrams nodes and edges
			NodeList diagramsNodes = grapholRootElement.getElementsByTagName(DIAGRAMS_ELEMENT_TAG_NAME);
			if(diagramsNodes.getLength()>0) {
				Node diagramsNode = diagramsNodes.item(0);
				if(diagramsNode instanceof Element) {
					Element diagramsElem = (Element)diagramsNode;
					NodeList diagramNodes = diagramsElem.getElementsByTagName(DIAGRAMS_DIAGRAM_ELEMENT_TAG_NAME);
					for(int i=0;i<diagramNodes.getLength();i++) {
						Node diagramNode = diagramNodes.item(i);
						if(diagramNode instanceof Element) {
							Element diagramElem = (Element)diagramNode;
							GraphOLDiagram currDiagram = getGraphOLDiagram(diagramElem, prefixes, df);
							if(currDiagram!=null) {
								diagrams.add(currDiagram);
							}
						}
					}
				}
			}
			GraphOLOntology graphOLOnt = new GraphOLOntology(ontologyIri, null, ontologyVersion, null, prefixes, diagrams);

			FunctionalSyntaxDocumentFormat format = new FunctionalSyntaxDocumentFormat();
			format.copyPrefixesFrom(prefixes);
			o.setOntologyFormat(ont, format);
			o.addAxioms(ont, new HashSet<>(graphOLOnt.getOWLDeclarationAxioms()));
			o.addAxioms(ont, GraphOLUtilities_v3.getOWLLogicalAxioms(graphOLOnt, df));
			o.addAxioms(ont, annAssAxioms);
			for(String functIriStr:functionalIris) {
				for(GraphOLDiagram diagram:diagrams) {
					if(diagram.getAttributeIRIs().contains(functIriStr)) {
						OWLDataProperty prop = df.getOWLDataProperty(IRI.create(functIriStr));
						OWLAxiom ax = df.getOWLFunctionalDataPropertyAxiom(prop);
						o.addAxiom(ont, ax);
					}
					if(diagram.getRoleIRIs().contains(functIriStr)) {
						OWLObjectProperty prop = df.getOWLObjectProperty(IRI.create(functIriStr));
						OWLAxiom ax = df.getOWLFunctionalObjectPropertyAxiom(prop);
						o.addAxiom(ont, ax);
					}
				}
			}

			return ont;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace().toString());
			throw new RuntimeException(e);
		} catch (SAXException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace().toString());
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace().toString());
			throw new RuntimeException(e);
		} catch (OWLOntologyCreationException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace().toString());
			throw new RuntimeException(e);
		} catch (GraphOLParserException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace().toString());
			throw new RuntimeException(e);
		}

	}


	private GraphOLDiagram getGraphOLDiagram(Element diagramElement, Map<String, String> prefixMap, OWLDataFactory df) throws GraphOLParserException {
		GraphOLDiagram result = null;
		try {
			String diagName = "";
			Map<String, GraphOLNode> idToNodeMap = new HashMap<>();
			Map<String, List<GraphOLOntologyAxiomEdge>> nodeIdToAxiomIncomingEdges = new HashMap<>();
			Map<String, List<GraphOLOntologyAxiomEdge>> nodeIdToAxiomOutGoingEdges = new HashMap<>();
			Map<String, List<GraphOLConstructorInputEdge>> nodeIdToOperatorIncomingEdges = new HashMap<>();
			Map<String, List<GraphOLConstructorInputEdge>> nodeIdToOperatorOutGoingEdges = new HashMap<>();
			List<GraphOLOntologyAxiomEdge> axiomEdges = new LinkedList<>();
			List<GraphOLConstructorInputEdge> operatorEdges = new LinkedList<>();
			Map<String, GraphOLEdge> idToEdgeMap = new HashMap<>();
			List<GraphOLDisjointUnionNode> disjointUnionList = new LinkedList<>();
			List<GraphOLHasKeyNode> hasKeyList = new LinkedList<GraphOLHasKeyNode>();

			if(diagramElement.hasAttribute(DIAGRAMS_DIAGRAM_NAME_ATTR_NAME)) {
				diagName = diagramElement.getAttribute(DIAGRAMS_DIAGRAM_NAME_ATTR_NAME);
			}

			NodeList nodeList = diagramElement.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_ELEMENT_TAG_NAME);
			for(int i=0;i<nodeList.getLength();i++) {
				Node node = nodeList.item(i);
				if(node instanceof Element) {
					Element nodeElem = (Element)node;
					try {
						GraphOLNode grapholNode = getGraphOLNode(nodeElem, prefixMap);
						if(grapholNode!=null) {
							idToNodeMap.put(grapholNode.getNodeId(), grapholNode);
							if(grapholNode instanceof GraphOLDisjointUnionNode) {
								disjointUnionList.add((GraphOLDisjointUnionNode) grapholNode);
							}
							if(grapholNode instanceof GraphOLHasKeyNode) {
								hasKeyList.add((GraphOLHasKeyNode) grapholNode);
							}
						}
						else {
							throw new RuntimeException("Problems encountered while parsing XML node with text content '"+node.getTextContent()+"'");
						}
					} catch (Exception e) {
						if(nodeElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_ID_ATTR_NAME)) {
							logger.error("[id="+nodeElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_ID_ATTR_NAME).toString()+"]"+e.getMessage());
						}
						logger.error(e.getStackTrace().toString());
					}

				}
			}

			NodeList edgeList = diagramElement.getElementsByTagName(DIAGRAMS_DIAGRAM_EDGE_ELEMENT_TAG_NAME);
			for(int i=0;i<edgeList.getLength();i++) {
				Node edge = edgeList.item(i);
				if(edge instanceof Element) {
					Element edgeElem = (Element)edge;
					GraphOLEdge grapholEdge = getGraphOLEdge(edgeElem, df);
					if(grapholEdge!=null) {
						String edgeId = grapholEdge.getEdgeId();
						idToEdgeMap.put(edgeId, grapholEdge);
						String sourceNodeId = grapholEdge.getSourceNodeId();
						String targetNodeId = grapholEdge.getTargetNodeId();
						if(grapholEdge instanceof GraphOLOntologyAxiomEdge) {
							GraphOLOntologyAxiomEdge axEdge = (GraphOLOntologyAxiomEdge) grapholEdge;
							axiomEdges.add(axEdge);
							if(nodeIdToAxiomIncomingEdges.containsKey(targetNodeId)) {
								nodeIdToAxiomIncomingEdges.get(targetNodeId).add(axEdge);
							}
							else {
								List<GraphOLOntologyAxiomEdge> currList = new LinkedList<>();
								currList.add(axEdge);
								nodeIdToAxiomIncomingEdges.put(targetNodeId, currList);
							}

							if(nodeIdToAxiomOutGoingEdges.containsKey(sourceNodeId)) {
								nodeIdToAxiomOutGoingEdges.get(sourceNodeId).add(axEdge);
							}
							else {
								List<GraphOLOntologyAxiomEdge> currList = new LinkedList<>();
								currList.add(axEdge);
								nodeIdToAxiomOutGoingEdges.put(sourceNodeId, currList);
							}
						}
						else {
							if(grapholEdge instanceof GraphOLConstructorInputEdge) {
								GraphOLConstructorInputEdge opEdge = (GraphOLConstructorInputEdge)grapholEdge;
								operatorEdges.add(opEdge);
								if(nodeIdToOperatorIncomingEdges.containsKey(targetNodeId)) {
									nodeIdToOperatorIncomingEdges.get(targetNodeId).add(opEdge);
								}
								else {
									List<GraphOLConstructorInputEdge> currList = new LinkedList<>();
									currList.add(opEdge);
									nodeIdToOperatorIncomingEdges.put(targetNodeId, currList);
								}
								if(nodeIdToOperatorOutGoingEdges.containsKey(sourceNodeId)) {
									nodeIdToOperatorOutGoingEdges.get(sourceNodeId).add(opEdge);
								}
								else {
									List<GraphOLConstructorInputEdge> currList = new LinkedList<>();
									currList.add(opEdge);
									nodeIdToOperatorOutGoingEdges.put(sourceNodeId, currList);
								}
							}
							else {
								logger.error("Found edge instance of unknown class "+ grapholEdge.getClass());
								throw new RuntimeException("Found edge instance of unknown class "+ grapholEdge.getClass());
							}
						}
					}

				}
			}
			result = new GraphOLDiagram(diagName, idToNodeMap, axiomEdges, operatorEdges, idToEdgeMap, nodeIdToAxiomIncomingEdges, nodeIdToAxiomOutGoingEdges,
					nodeIdToOperatorIncomingEdges, nodeIdToOperatorOutGoingEdges, disjointUnionList, hasKeyList);
		} catch (Exception e) {
			throw new GraphOLParserException(e);
		}
		return result;
	}

	private GraphOLNode getGraphOLNode(Element nodeElem, Map<String, String> prefixMap) throws GraphOLParserException, GraphOLUnknownPrefixException {
		GraphOLNode result = null;
		GraphOLNodeLabel nodeLabel = null;
		GraphOLNodeGeometry nodeGeometry = null;
		String type = null;
		String iriStr = null;
		String color = null;
		String id = null;
		String label = null;
		List<String> orderedInputEdgeList = new LinkedList<>();//solo per role chain a property assertions
		if(nodeElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_ID_ATTR_NAME)) {
			id = nodeElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_ID_ATTR_NAME);
			if(nodeElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_TYPE_ATTR_NAME)) {
				type = nodeElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_TYPE_ATTR_NAME);
			}
			if(nodeElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_INPUTS_ATTR_NAME)) {
				String inputEdges = nodeElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_INPUTS_ATTR_NAME);
				orderedInputEdgeList = splitOrderedEdgeList(inputEdges);
			}
			if(nodeElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_COLOR_ATTR_NAME)) {
				color = nodeElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_COLOR_ATTR_NAME);
			}

			NodeList iriList = nodeElem.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_IRI_ELEMENT_TAG_NAME);
			if(iriList.getLength()>0) {
				Node iriNode = iriList.item(0);
				iriStr = iriNode.getTextContent();
			}
			NodeList labelList = nodeElem.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_LABEL_ELEMENT_TAG_NAME);
			if(labelList.getLength()>0) {
				Node labelNode = labelList.item(0);
				int x = -1;
				int y = -1;
				int width = -1;
				int height = -1;
				if(labelNode instanceof Element) {
					Element labelElem = (Element) labelNode;
					if(labelElem.getTextContent()!=null) {
						label = labelElem.getTextContent();
					}
					if(labelElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_LABEL_X_ATTR_NAME)) {
						String xStr = labelElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_LABEL_X_ATTR_NAME);
						try {
							x= Integer.parseInt(xStr);
						} catch (NumberFormatException e) {
							logger.error("Parsing non parsable Integer string " + xStr);
						}
					}
					if(labelElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_LABEL_Y_ATTR_NAME)) {
						String yStr = labelElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_LABEL_Y_ATTR_NAME);
						try {
							y= Integer.parseInt(yStr);
						} catch (NumberFormatException e) {
							logger.error("Parsing non parsable Integer string " + yStr);
						}
					}
					if(labelElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_LABEL_WIDTH_ATTR_NAME)) {
						String widthStr = labelElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_LABEL_WIDTH_ATTR_NAME);
						try {
							width = Integer.parseInt(widthStr);
						} catch (NumberFormatException e) {
							logger.error("Parsing non parsable Integer string " + widthStr);
						}
					}
					if(labelElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_LABEL_HEIGHT_ATTR_NAME)) {
						String heightStr = labelElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_LABEL_HEIGHT_ATTR_NAME);
						try {
							height = Integer.parseInt(heightStr);
						} catch (NumberFormatException e) {
							logger.error("Parsing non parsable Integer string " + heightStr);
						}
					}
				}
				nodeLabel = new GraphOLNodeLabel(label, x, y, height, width);
			}
			NodeList geometryList = nodeElem.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_GEOMETRY_ELEMENT_TAG_NAME);
			if(geometryList.getLength()>0) {
				Node geometryNode = geometryList.item(0);
				int x = -1;
				int y = -1;
				int width = -1;
				int height = -1;
				if(geometryNode instanceof Element) {
					Element geometryElem = (Element)geometryNode;
					if(geometryElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_GEOMETRY_X_ATTR_NAME)) {
						String xStr = geometryElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_GEOMETRY_X_ATTR_NAME);
						try {
							x= Integer.parseInt(xStr);
						} catch (NumberFormatException e) {
							logger.error("Parsing non parsable Integer string " + xStr);
						}
					}
					if(geometryElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_GEOMETRY_Y_ATTR_NAME)) {
						String yStr = geometryElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_GEOMETRY_Y_ATTR_NAME);
						try {
							y= Integer.parseInt(yStr);
						} catch (NumberFormatException e) {
							logger.error("Parsing non parsable Integer string " + yStr);
						}
					}
					if(geometryElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_GEOMETRY_WIDTH_ATTR_NAME)) {
						String widthStr = geometryElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_GEOMETRY_WIDTH_ATTR_NAME);
						try {
							width = Integer.parseInt(widthStr);
						} catch (NumberFormatException e) {
							logger.error("Parsing non parsable Integer string " + widthStr);
						}
					}
					if(geometryElem.hasAttribute(DIAGRAMS_DIAGRAM_NODE_GEOMETRY_HEIGHT_ATTR_NAME)) {
						String heightStr = geometryElem.getAttribute(DIAGRAMS_DIAGRAM_NODE_GEOMETRY_HEIGHT_ATTR_NAME);
						try {
							height = Integer.parseInt(heightStr);
						} catch (NumberFormatException e) {
							logger.error("Parsing non parsable Integer string " + heightStr);
						}
					}
				}
			}
		}
		else {
			logger.error("Found node element without id attribute");
			throw new GraphOLParserException("Found node element without id attribute");

		}

		if(type!=null) {
			switch (type) {
			case DIAGRAMS_DIAGRAM_NODE_TYPE_CONCEPT_VALUE:
				result = new GraphOLOntologyClassNode(nodeLabel, nodeGeometry, type, color, id, iriStr, null, null);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_ROLE_VALUE:
				result = new GraphOLOntologyRoleNode(nodeLabel, nodeGeometry, type, color, id, iriStr, null, null);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_VALUEDOMAIN_VALUE:
				result = new GraphOLOntologyValueDomainNode(nodeLabel, nodeGeometry, type, color, id, iriStr, null, null);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_ATTRIBUTE_VALUE:
				result = new GraphOLOntologyAttributeNode(nodeLabel, nodeGeometry, type, color, id, iriStr, null, null);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_INDIVIDUAL_VALUE:
				result = new GraphOLOntologyIndividualNode(nodeLabel, nodeGeometry, type, color, id, iriStr, null, null);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_LITERAL_VALUE:
				NodeList literalList = nodeElem.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_LITERAL_ELEMENT_TAG_NAME);
				if(literalList.getLength()>0) {
					Node literalNode = literalList.item(0);
					if(literalNode instanceof Element) {
						Element literalEl = (Element)literalNode;
						String lexForm = null;
						NodeList lexNodes = literalEl.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_LITERAL_LEXICALFORM_ELEMENT_TAG_NAME);
						if(lexNodes.getLength()>0) {
							Node lexNode = lexNodes.item(0);
							if(lexNode instanceof Element) {
								lexForm = lexNode.getTextContent();
							}
						}
						if(lexForm!=null) {
							String lang = null;
							NodeList languageNodes = literalEl.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_LITERAL_LANGUAGE_ELEMENT_TAG_NAME);
							if(languageNodes.getLength()>0) {
								Node langNode = languageNodes.item(0);
								if(langNode instanceof Element) {
									lang = langNode.getTextContent();
								}
							}
							if(!(lang==null||lang.trim().equals("") )) {
								result = new GraphOLOntologyLiteralNode(nodeLabel, nodeGeometry, type, color, id, lexForm, null, lang);
							}
							else {
								NodeList datatypeNodes = literalEl.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_LITERAL_DATATYPE_ELEMENT_TAG_NAME);
								if(datatypeNodes.getLength()>0) {
									Node dtNode = datatypeNodes.item(0);
									if(dtNode instanceof Element) {
										result = new GraphOLOntologyLiteralNode(nodeLabel, nodeGeometry, type, color, id, lexForm, dtNode.getTextContent(), null);
									}
								}
							}
						}
					}
				}
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_DATATYPERESTR_VALUE:
				result = new GraphOLDatatypeRestrictionNode(nodeLabel, nodeGeometry, type, color, id);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_FACET_VALUE:
				NodeList facetList = nodeElem.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_FACET_ELEMENT_TAG_NAME);
				if(facetList.getLength()>0) {
					Node facetNode = facetList.item(0);
					if(facetNode instanceof Element) {
						NodeList constrFacetList = ((Element)facetNode).getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_FACET_CONSTRFACET_ELEMENT_TAG_NAME);
						NodeList facetLiteralList = ((Element)facetNode).getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_LITERAL_ELEMENT_TAG_NAME);
						if(constrFacetList.getLength()>0 && facetLiteralList.getLength()>0) {
							Node constrFacetNode = constrFacetList.item(0);
							Node facetLiteralNode = facetLiteralList.item(0);
							if(facetLiteralNode instanceof Element) {
								Element literalEl = (Element)facetLiteralNode;
								String lexForm = null;
								NodeList lexNodes = literalEl.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_LITERAL_LEXICALFORM_ELEMENT_TAG_NAME);
								if(lexNodes.getLength()>0) {
									Node lexNode = lexNodes.item(0);
									if(lexNode instanceof Element) {
										lexForm = lexNode.getTextContent();
									}
								}
								if(lexForm!=null) {
									NodeList datatypeNodes = literalEl.getElementsByTagName(DIAGRAMS_DIAGRAM_NODE_LITERAL_DATATYPE_ELEMENT_TAG_NAME);
									if(datatypeNodes.getLength()>0) {
										Node dtNode = datatypeNodes.item(0);
										if(dtNode instanceof Element) {
											//result = new GraphOLOntologyLiteralNode(nodeLabel, nodeGeometry, type, color, id, lexForm, dtNode.getTextContent(), null);
											result = new GraphOLOntologyFacetNode(nodeLabel, nodeGeometry, type, color, id, constrFacetNode.getTextContent(), lexForm, dtNode.getTextContent());
										}
									}

								}
							}

						}
					}
				}
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_ROLEINVERSE_VALUE:
				result = new GraphOLRoleInverseNode(nodeLabel, nodeGeometry, type, color, id);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_ROLECHAIN_VALUE:
				result = new GraphOLRoleChainNode(nodeLabel, nodeGeometry, type, color, id, orderedInputEdgeList);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_UNION_VALUE:
				result = new GraphOLUnionNode(nodeLabel, nodeGeometry, type, color, id);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_INTERSECTION_VALUE:
				result = new GraphOLIntersectionNode(nodeLabel, nodeGeometry, type, color, id);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_DISJUNION_VALUE:
				result = new GraphOLDisjointUnionNode(nodeLabel, nodeGeometry, type, color, id);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_DOMRESTR_VALUE:
				switch (label) {
				case DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_EXISTS_INNER_TEXT:
					result = new GraphOLDomainExistentialNode(nodeLabel, nodeGeometry, type, color, id);
					break;
				case DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_FORALL_INNER_TEXT:
					result = new GraphOLDomainForAllNode(nodeLabel, nodeGeometry, type, color, id);
					break;
				case DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_SELF_INNER_TEXT:
					result = new GraphOLDomainSelfNode(nodeLabel, nodeGeometry, type, color, id);
					break;
				default:
					//try parse cardinality restr
					String[] restrValues = splitCardinalityRestriction(label);
					if(restrValues.length==2) {
						String minStr = restrValues[0];
						int min;
						if(minStr.trim().equals("-")) {
							min = 0;
						}
						else {
							min = Integer.parseInt(minStr);
						}
						String maxStr = restrValues[1];
						if(maxStr.trim().equals("-")) {
							result = new GraphOLDomainCardinalityRestrictionNode(nodeLabel, nodeGeometry, type, color, id, min);
						}
						else {
							int max = Integer.parseInt(maxStr);
							result = new GraphOLDomainCardinalityRestrictionNode(nodeLabel, nodeGeometry, type, color, id, min, max);
						}
					}
					break;
				}
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_RANGERESTR_VALUE:
				switch (label) {
				case DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_EXISTS_INNER_TEXT:
					result = new GraphOLRangeExistentialNode(nodeLabel, nodeGeometry, type, color, id);
					break;
				case DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_FORALL_INNER_TEXT:
					result = new GraphOLRangeForAllNode(nodeLabel, nodeGeometry, type, color, id);
					break;
				case DIAGRAMS_DIAGRAM_NODE_TYPE_RESTR_SELF_INNER_TEXT:
					result = new GraphOLRangeSelfNode(nodeLabel, nodeGeometry, type, color, id);
					break;
				default:
					//try parse cardinality restr
					String[] restrValues = splitCardinalityRestriction(label);
					if(restrValues.length==2) {
						String minStr = restrValues[0];
						int min;
						if(minStr.trim().equals("-")) {
							min = 0;
						}
						else {
							min = Integer.parseInt(minStr);
						}
						String maxStr = restrValues[1];
						if(maxStr.trim().equals("-")) {
							result = new GraphOLRangeCardinalityRestrictionNode(nodeLabel, nodeGeometry, type, color, id, min);
						}
						else {
							int max = Integer.parseInt(maxStr);
							result = new GraphOLRangeCardinalityRestrictionNode(nodeLabel, nodeGeometry, type, color, id, min, max);
						}
					}
					break;
				}
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_COMPLEMENT_VALUE:
				result = new GraphOLComplementNode(nodeLabel, nodeGeometry, type, color, id);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_ENUMERATION_VALUE:
				result = new GraphOLEnumerationNode(nodeLabel, nodeGeometry, type, color, id);
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_PROPASS_VALUE:
				result = new GraphOLPropertyAssertionNode(nodeLabel, nodeGeometry, type, color, id, orderedInputEdgeList.get(0), orderedInputEdgeList.get(1));
				break;
			case DIAGRAMS_DIAGRAM_NODE_TYPE_HASKEY_VALUE:
				result = new GraphOLHasKeyNode(nodeLabel, nodeGeometry, type, color, id);
			default:
				break;
			}
		}
		return result;
	}

	private GraphOLEdge getGraphOLEdge(Element edgeElem, OWLDataFactory df) {
		GraphOLEdge result = null;
		List<GraphOLEdgePoint> pointList = new LinkedList<>();
		String type = "";
		String id = null;
		String sourceNodeId = "";
		String targetNodeId = "";
		if(edgeElem.hasAttribute(DIAGRAMS_DIAGRAM_EDGE_ID_ATTR_NAME)) {
			id = edgeElem.getAttribute(DIAGRAMS_DIAGRAM_EDGE_ID_ATTR_NAME);
			if(edgeElem.hasAttribute(DIAGRAMS_DIAGRAM_EDGE_TYPE_ATTR_NAME)) {
				type = edgeElem.getAttribute(DIAGRAMS_DIAGRAM_EDGE_TYPE_ATTR_NAME);
			}
			if(edgeElem.hasAttribute(DIAGRAMS_DIAGRAM_EDGE_SOURCE_ATTR_NAME)) {
				sourceNodeId = edgeElem.getAttribute(DIAGRAMS_DIAGRAM_EDGE_SOURCE_ATTR_NAME);
			}
			if(edgeElem.hasAttribute(DIAGRAMS_DIAGRAM_EDGE_TARGET_ATTR_NAME)) {
				targetNodeId = edgeElem.getAttribute(DIAGRAMS_DIAGRAM_EDGE_TARGET_ATTR_NAME);
			}
			NodeList pointNodeList = edgeElem.getElementsByTagName(DIAGRAMS_DIAGRAM_EDGE_POINT_ELEMENT_TAG_NAME);
			for(int i=0;i<pointNodeList.getLength();i++) {
				Node pointNode = pointNodeList.item(i);
				if(pointNode instanceof Element) {
					Element pointElem = (Element)pointNode;
					if(pointElem.hasAttribute(DIAGRAMS_DIAGRAM_EDGE_POINT_X_ATTR_NAME) && pointElem.hasAttribute(DIAGRAMS_DIAGRAM_EDGE_POINT_Y_ATTR_NAME)) {
						String xStr = pointElem.getAttribute(DIAGRAMS_DIAGRAM_EDGE_POINT_X_ATTR_NAME);
						String yStr = pointElem.getAttribute(DIAGRAMS_DIAGRAM_EDGE_POINT_X_ATTR_NAME);
						try {
							int x = Integer.parseInt(xStr);
							int y = Integer.parseInt(yStr);
							GraphOLEdgePoint point = new GraphOLEdgePoint(x, y);
							pointList.add(point);
						} catch (Exception e) {
							logger.error("Parsing non parsable Integer string " + xStr + " or " + yStr);
						}
					}
				}
			}
			Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
			NodeList iriAnnotationsNodes = edgeElem.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ELEMENT_TAG_NAME);
			if(iriAnnotationsNodes.getLength()>0 ) {
				Element annotationsEl = (Element)iriAnnotationsNodes.item(0);
				NodeList iriAnnotationNodes = (annotationsEl).getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_ELEMENT_TAG_NAME);
				for(int j=0;j<iriAnnotationNodes.getLength();j++) {
					Node annotationNode = iriAnnotationNodes.item(j);
					if(annotationNode instanceof Element) {
						Element annotationElement = (Element)annotationNode;
						NodeList propertyNodes = annotationElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_PROPERTY_ELEMENT_TAG_NAME);
						if(propertyNodes.getLength()>0) {
							Element propElement = (Element)propertyNodes.item(0);
							OWLAnnotationProperty annProp = df.getOWLAnnotationProperty(IRI.create(propElement.getTextContent()));
							NodeList objectNodes = annotationElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_ELEMENT_TAG_NAME);
							if(objectNodes.getLength()>0) {
								Element objElement = (Element)objectNodes.item(0);
								String lexForm = null;
								NodeList lexNodes = objElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_LEXICALFORM_ELEMENT_TAG_NAME);
								if(lexNodes.getLength()>0) {
									Node lexNode = lexNodes.item(0);
									if(lexNode instanceof Element) {
										lexForm = lexNode.getTextContent();
									}
								}
								if(lexForm!=null) {
									String lang = null;
									NodeList languageNodes = objElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_LANGUAGE_ELEMENT_TAG_NAME);
									if(languageNodes.getLength()>0) {
										Node langNode = languageNodes.item(0);
										if(langNode instanceof Element) {
											lang = langNode.getTextContent();
										}
									}
									if(lang!=null) {
										OWLLiteral lit = df.getOWLLiteral(lexForm, lang);
										OWLAnnotation ann = df.getOWLAnnotation(annProp, lit);
										annotations.add(ann);
									}
									else {
										NodeList datatypeNodes = objElement.getElementsByTagName(ONTOLOGY_IRIS_IRI_ANNOTATIONS_ANNOTATION_OBJECT_DATATYPE_ELEMENT_TAG_NAME);
										if(datatypeNodes.getLength()>0) {
											Node dtNode = datatypeNodes.item(0);
											if(dtNode instanceof Element) {
												OWLDatatype dt = df.getOWLDatatype(IRI.create(dtNode.getTextContent()));
												OWLLiteral lit = df.getOWLLiteral(lexForm, dt);
												OWLAnnotation ann = df.getOWLAnnotation(annProp, lit);
												annotations.add(ann);
											}
										}
									}
								}
							}
						}
					}
				}
			}

			switch (type) {
			case DIAGRAMS_DIAGRAM_EDGE_TYPE_EQUIVALENT_VALUE:
				result = new GraphOLOntologyEquivalenceEdge(pointList, type, id, sourceNodeId, targetNodeId, annotations);
				break;
			case DIAGRAMS_DIAGRAM_EDGE_TYPE_INCLUSION_VALUE:
				result = new GraphOLOntologyInclusionEdge(pointList, type, id, sourceNodeId, targetNodeId, annotations);
				break;
			case DIAGRAMS_DIAGRAM_EDGE_TYPE_INSTANCEOF_VALUE:
				result = new GraphOLOntologyInstanceOfEdge(pointList, type, id, sourceNodeId, targetNodeId, annotations);
				break;
			case DIAGRAMS_DIAGRAM_EDGE_TYPE_SAMEIND_VALUE:
				result = new GraphOLOntologySameAsEdge(pointList, type, id, sourceNodeId, targetNodeId, annotations);
				break;
			case DIAGRAMS_DIAGRAM_EDGE_TYPE_DIFFIND_VALUE:
				result = new GraphOLOntologyDifferentEdge(pointList, type, id, sourceNodeId, targetNodeId, annotations);
				break;
			case DIAGRAMS_DIAGRAM_EDGE_TYPE_INPUTOPERATOR_VALUE:
				result = new GraphOLConstructorInputEdge(pointList, type, id, sourceNodeId, targetNodeId);
				break;
			default:
				break;
			}
		}
		return result;
	}

	private List<String> splitOrderedEdgeList(String inputEdges) {
		try {
			String[] strRes = inputEdges.split(",");
			List<String> res = new LinkedList<>();
			for(int i=0;i<strRes.length;i++) {
				res.add(strRes[i]);
			}
			return res;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String[] splitCardinalityRestriction(String cardinalityRestriction) {
		String[] strRes;
		try {
			String toBeSplitted = cardinalityRestriction.substring(1, cardinalityRestriction.length()-1);
			strRes = toBeSplitted.split(",");
		} catch (Exception e) {
			strRes = new String[1];
		}
		return strRes;
	}

	private String[] splitFacetValue(String facetValue) {
		String[] result;
		try {
			result = facetValue.split("\\^\\^");
		} catch (Exception e) {
			result = new String[3];
		}
		return result;
	}


}
