package com.obdasystems.sparqling.parsers.graphol.impl.v2;

public class GraphOLRoleInformation extends GraphOLOntologyEntityInformation {
	
	private boolean functional;
	private boolean inverseFunctional;
	private boolean asymmetric;
	private boolean symmetric;
	private boolean irreflexive;
	private boolean reflexive;
	private boolean transitive;
	
	public GraphOLRoleInformation() {
		
	}
	
	public GraphOLRoleInformation(String iri, String type, GraphOLDescription descr, boolean funct, boolean invFunct, 
			boolean asymm, boolean symm, boolean irrefl, boolean reflex, boolean trans) {
		super(iri, type, descr);
		this.setFunctional(funct);
		this.inverseFunctional = invFunct;
		this.asymmetric = asymm;
		this.irreflexive = irrefl;
		this.reflexive = reflex;
		this.transitive = trans;
	}

	public boolean isFunctional() {
		return functional;
	}

	public void setFunctional(boolean functional) {
		this.functional = functional;
	}

	public boolean isInverseFunctional() {
		return inverseFunctional;
	}

	public void setInverseFunctional(boolean inverseFunctional) {
		this.inverseFunctional = inverseFunctional;
	}

	public boolean isAsymmetric() {
		return asymmetric;
	}

	public void setAsymmetric(boolean asymmetric) {
		this.asymmetric = asymmetric;
	}

	public boolean isSymmetric() {
		return symmetric;
	}

	public void setSymmetric(boolean symmetric) {
		this.symmetric = symmetric;
	}

	public boolean isIrreflexive() {
		return irreflexive;
	}

	public void setIrreflexive(boolean irreflexive) {
		this.irreflexive = irreflexive;
	}

	public boolean isReflexive() {
		return reflexive;
	}

	public void setReflexive(boolean reflexive) {
		this.reflexive = reflexive;
	}

	public boolean isTransitive() {
		return transitive;
	}

	public void setTransitive(boolean transitive) {
		this.transitive = transitive;
	}
	
	@Override
	public String toString() {
		String result = "#### Information about attribute " + this.getIri() + "\n";
		result += "Is functional? " + this.functional;
		result += "Is inverse functional? " + this.inverseFunctional;
		result += "Is symmetric? " + this.symmetric;
		result += "Is asymmetric? " + this.asymmetric;
		result += "Is reflexive? " + this.reflexive;
		result += "Is irreflexive? " + this.irreflexive;
		result += "Is transitive? " + this.transitive;
		result += "Description: " + this.getDescription().getDescriptionFormattedText();
		return result;
	}

}
