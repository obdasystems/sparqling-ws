package com.obdasystems.sparqling.parsers.graphol.impl.v2;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class GraphOLOntology {
	
	private String iri;
	private String name;
	private String version;
	private String profile;
	private Map<String, String> prefixMap;
	private List<GraphOLDiagram> diagrams;
	
	private Set<String> classIRIs = new HashSet<>();
	private Set<String> roleIRIs = new HashSet<>();
	private Set<String> attrIRIs = new HashSet<>();
	private Set<String> indIRIs = new HashSet<>();
	
	OWLDataFactory df = OWLManager.getOWLDataFactory();
	
	public GraphOLOntology() {
		this.prefixMap = new HashMap<>();
		this.diagrams = new LinkedList<>();
	}
	
	public GraphOLOntology(String iri, String name, String version, String profile) {
		this.iri = iri;
		this.name = name;
		this.version = version;
		this.profile = profile;
		this.prefixMap = new HashMap<>();
		this.diagrams = new LinkedList<>();
	}
	
	public GraphOLOntology(String iri, String name, String version, String profile, Map<String, String> prefixMap, List<GraphOLDiagram> diagrams) {
		this.iri = iri;
		this.name = name;
		this.version = version;
		this.profile = profile;
		this.prefixMap = prefixMap;
		this.diagrams = diagrams;
		buildAlphabetSets();
	}
	
	public List<OWLDeclarationAxiom> getOWLDeclarationAxioms() {
		List<OWLDeclarationAxiom> result = new LinkedList<>();
		this.classIRIs.forEach(iri -> {
			OWLClass currEntity = df.getOWLClass(IRI.create(iri));
			OWLDeclarationAxiom ax = df.getOWLDeclarationAxiom(currEntity);
			result.add(ax);
		});
		this.roleIRIs.forEach(iri -> {
			OWLObjectProperty currEntity = df.getOWLObjectProperty(IRI.create(iri));
			OWLDeclarationAxiom ax = df.getOWLDeclarationAxiom(currEntity);
			result.add(ax);
		});
		this.attrIRIs.forEach(iri -> {
			OWLDataProperty currEntity = df.getOWLDataProperty(IRI.create(iri));
			OWLDeclarationAxiom ax = df.getOWLDeclarationAxiom(currEntity);
			result.add(ax);
		});
		this.indIRIs.forEach(iri -> {
			OWLNamedIndividual currEntity = df.getOWLNamedIndividual(IRI.create(iri));
			OWLDeclarationAxiom ax = df.getOWLDeclarationAxiom(currEntity);
			result.add(ax);
		});
		return result;
	}
	
	public void buildAlphabetSets() {
		this.diagrams.forEach(diagram -> {
			this.classIRIs.addAll(diagram.getClassIRIs());
			this.roleIRIs.addAll(diagram.getRoleIRIs());
			this.attrIRIs.addAll(diagram.getAttributeIRIs());
			this.indIRIs.addAll(diagram.getIndividualIRIs());
		});
	}
	
	public void addPrefix(String prefix, String nameSpace) {
		this.prefixMap.put(prefix, nameSpace);
	}
	
	public void addDiagram(GraphOLDiagram diagram) {
		this.diagrams.add(diagram);
	}
	
	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Map<String, String> getPrefixMap() {
		return prefixMap;
	}

	public void setPrefixMap(Map<String, String> prefixMap) {
		this.prefixMap = prefixMap;
	}

	public List<GraphOLDiagram> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<GraphOLDiagram> diagrams) {
		this.diagrams = diagrams;
	}
	
	@Override
	public String toString() {
		String result = "IRI=" + iri + "\n";
		result += "NAME=" + name + "\n";
		result += "VERSION=" + version + "\n";
		result += "PROFILE=" + profile + "\n";
		result += "#########PREFIXES#############\n";
		for(String prefix:prefixMap.keySet()) {
			result+= prefix + ":" + prefixMap.get(prefix) +"\n";
		}
		result += "#########CLASSES#############\n";
		for(String cl:classIRIs) {
			result+= cl +"\n";
		}
		result += "#########ROLES#############\n";
		for(String cl:roleIRIs) {
			result+= cl +"\n";
		}
		result += "#########ATTRIBUTES#############\n";
		for(String cl:attrIRIs) {
			result+= cl +"\n";
		}
		result += "#########INDIVIDUALS#############\n";
		for(String cl:indIRIs) {
			result+= cl +"\n";
		}
		for(GraphOLDiagram diag:diagrams) {
			result += "#########DIAGRAM#############\n";
			result += diag.toString()+"\n";
		}
		return result;
	}
}
