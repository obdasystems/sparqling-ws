package com.obdasystems.sparqling.parsers.graphol.impl.v2;

import com.obdasystems.sparqling.parsers.graphol.edge.impl.GraphOLConstructorInputEdge;
import com.obdasystems.sparqling.parsers.graphol.edge.impl.GraphOLEdge;
import com.obdasystems.sparqling.parsers.graphol.edge.impl.GraphOLOntologyAxiomEdge;
import com.obdasystems.sparqling.parsers.graphol.node.impl.*;

import java.util.*;

public class GraphOLDiagram {

	private Map<String, GraphOLNode> idToNodeMap;
	private List<GraphOLOntologyAxiomEdge> axiomEdges;
	private List<GraphOLConstructorInputEdge> operatorEdges;

	private Map<String, GraphOLEdge> idToEdgeMap;

	private Map<String, List<GraphOLOntologyAxiomEdge>> nodeIdToAxiomIncomingEdges;
	private Map<String, List<GraphOLOntologyAxiomEdge>> nodeIdToAxiomOutgoingEdges;
	private Map<String, List<GraphOLConstructorInputEdge>> nodeIdToOperatorIncomingEdges;
	private Map<String, List<GraphOLConstructorInputEdge>> nodeIdToOperatorOutgoingEdges;
	
	private List<GraphOLDisjointUnionNode> disjointUnionNodes;
	private List<GraphOLHasKeyNode> hasKeyNodes;

	private Set<String> classIRIs = new HashSet<>();
	private Set<String> roleIRIs = new HashSet<>();
	private Set<String> attrIRIs = new HashSet<>();
	private Set<String> indIRIs = new HashSet<>();
	
	private String name; 
	
	public GraphOLDiagram() {
		this.idToNodeMap = new HashMap<>();
		this.axiomEdges = new LinkedList<>();
		this.operatorEdges = new LinkedList<>();
		this.idToEdgeMap = new HashMap<>();
		this.nodeIdToAxiomIncomingEdges = new HashMap<>();
		this.nodeIdToAxiomOutgoingEdges = new HashMap<>();
		this.nodeIdToOperatorIncomingEdges = new HashMap<>();
		this.nodeIdToOperatorOutgoingEdges = new HashMap<>();
		this.disjointUnionNodes = new LinkedList<>();
		this.hasKeyNodes = new LinkedList<GraphOLHasKeyNode>();
	}

	public GraphOLDiagram(String name,Map<String, GraphOLNode> idToNodeMap, List<GraphOLOntologyAxiomEdge> axiomEdges, List<GraphOLConstructorInputEdge> operatorEdges, 
			Map<String, GraphOLEdge> idToEdgeMap, Map<String, List<GraphOLOntologyAxiomEdge>> nodeIdToAxiomIncomingEdges, 
			Map<String, List<GraphOLOntologyAxiomEdge>> nodeIdToAxiomOutgoingEdges, Map<String, List<GraphOLConstructorInputEdge>> nodeIdToOperatorIncomingEdges, 
			Map<String, List<GraphOLConstructorInputEdge>> nodeIdToOperatorOutgoingEdges, List<GraphOLDisjointUnionNode> disjointUnionNodes,
			List<GraphOLHasKeyNode> hasKeyNodes) {
		this.name = name;
		this.idToNodeMap = idToNodeMap;
		this.axiomEdges = axiomEdges;
		this.operatorEdges = operatorEdges;
		this.idToEdgeMap = idToEdgeMap;
		this.nodeIdToAxiomIncomingEdges = nodeIdToAxiomIncomingEdges;
		this.nodeIdToAxiomOutgoingEdges = nodeIdToAxiomOutgoingEdges;
		this.nodeIdToOperatorIncomingEdges = nodeIdToOperatorIncomingEdges;
		this.nodeIdToOperatorOutgoingEdges = nodeIdToOperatorOutgoingEdges;
		buildAlphabetSets();
		this.disjointUnionNodes = disjointUnionNodes;
		this.hasKeyNodes = hasKeyNodes;
	}

	public GraphOLNode getNodeById(String nodeId) {
		return this.idToNodeMap.get(nodeId);
	}

	public GraphOLEdge getEdgeById(String edgeId) {
		return this.idToEdgeMap.get(edgeId);
	}

	public List<GraphOLOntologyAxiomEdge> getIncomingAxiomEdgesByNodeId(String nodeId) {
		List<GraphOLOntologyAxiomEdge> result = this.nodeIdToAxiomIncomingEdges.get(nodeId);
		if(result != null) {
			return result;
		}
		return new LinkedList<>();
	}

	public List<GraphOLOntologyAxiomEdge> getOutgoingAxiomEdgesByNodeId(String nodeId) {
		List<GraphOLOntologyAxiomEdge> result = this.nodeIdToAxiomOutgoingEdges.get(nodeId);
		if(result != null) {
			return result;
		}
		return new LinkedList<>();
	}

	public List<GraphOLConstructorInputEdge> getIncomingOperatorEdgesByNodeId(String nodeId) {
		List<GraphOLConstructorInputEdge> result = this.nodeIdToOperatorIncomingEdges.get(nodeId);
		if(result != null) {
			return result;
		}
		return new LinkedList<>();
	}

	public List<GraphOLConstructorInputEdge> getOutgoingOperatorEdgesByNodeId(String nodeId) {
		List<GraphOLConstructorInputEdge> result = this.nodeIdToOperatorOutgoingEdges.get(nodeId);
		if(result != null) {
			return result;
		}
		return new LinkedList<>();
	}

	public void buildAlphabetSets() {
		idToNodeMap.values().forEach(node -> {
			if(node instanceof GraphOLOntologyIRIElementNode) {
				if(node instanceof GraphOLOntologyClassNode) {
					this.classIRIs.add(((GraphOLOntologyIRIElementNode) node).getIri());
				}
				else {
					if(node instanceof GraphOLOntologyRoleNode) {
						this.roleIRIs.add(((GraphOLOntologyIRIElementNode) node).getIri());
					}
					else {
						if(node instanceof GraphOLOntologyAttributeNode) {
							this.attrIRIs.add(((GraphOLOntologyIRIElementNode) node).getIri());
						}
						else {
							if(node instanceof GraphOLOntologyIndividualNode) {
								if(!node.getNodeLabel().getLabel().contains("\"^^")) {
									this.indIRIs.add(((GraphOLOntologyIRIElementNode) node).getIri());
								}
							}
						}
					}
				}
			}
		});
	}

	public Set<String> getClassIRIs() {
		return this.classIRIs;
	}

	public Set<String> getRoleIRIs() {
		return this.roleIRIs;
	}

	public Set<String> getAttributeIRIs() {
		return this.attrIRIs;
	}

	public Set<String> getIndividualIRIs() {
		return this.indIRIs;
	}

	public void addNode(String id, GraphOLNode node) {
		this.idToNodeMap.put(id, node);
	}

	public boolean addAxiomEdge(GraphOLOntologyAxiomEdge edge) {
		return this.axiomEdges.add(edge);
	}

	public boolean addOperatorEdge(GraphOLConstructorInputEdge edge) {
		return this.operatorEdges.add(edge);
	}


	public void addIncomingAxiomEdgesToNode(String nodeId, GraphOLOntologyAxiomEdge... edges) {
		if(this.nodeIdToAxiomIncomingEdges.containsKey(nodeId)) {
			for(GraphOLOntologyAxiomEdge edge:edges) {
				this.nodeIdToAxiomIncomingEdges.get(nodeId).add(edge);
			}
		}
		else {
			List<GraphOLOntologyAxiomEdge> currList = new LinkedList<>();
			for(GraphOLOntologyAxiomEdge edge:edges) {
				currList.add(edge);
			}
			this.nodeIdToAxiomIncomingEdges.put(nodeId, currList);
		}
	}

	public void addOutgoingAxiomEdgesToNode(String nodeId, GraphOLOntologyAxiomEdge... edges) {
		if(this.nodeIdToAxiomOutgoingEdges.containsKey(nodeId)) {
			for(GraphOLOntologyAxiomEdge edge:edges) {
				this.nodeIdToAxiomOutgoingEdges.get(nodeId).add(edge);
			}
		}
		else {
			List<GraphOLOntologyAxiomEdge> currList = new LinkedList<>();
			for(GraphOLOntologyAxiomEdge edge:edges) {
				currList.add(edge);
			}
			this.nodeIdToAxiomOutgoingEdges.put(nodeId, currList);
		}
	}

	public void addIncomingOperatorEdgesToNode(String nodeId, GraphOLConstructorInputEdge... edges) {
		if(this.nodeIdToOperatorIncomingEdges.containsKey(nodeId)) {
			for(GraphOLConstructorInputEdge edge:edges) {
				this.nodeIdToOperatorIncomingEdges.get(nodeId).add(edge);
			}
		}
		else {
			List<GraphOLConstructorInputEdge> currList = new LinkedList<>();
			for(GraphOLConstructorInputEdge edge:edges) {
				currList.add(edge);
			}
			this.nodeIdToOperatorIncomingEdges.put(nodeId, currList);
		}
	}

	public void addOutgoingOperatorEdgesToNode(String nodeId, GraphOLConstructorInputEdge... edges) {
		if(this.nodeIdToOperatorOutgoingEdges.containsKey(nodeId)) {
			for(GraphOLConstructorInputEdge edge:edges) {
				this.nodeIdToOperatorOutgoingEdges.get(nodeId).add(edge);
			}
		}
		else {
			List<GraphOLConstructorInputEdge> currList = new LinkedList<>();
			for(GraphOLConstructorInputEdge edge:edges) {
				currList.add(edge);
			}
			this.nodeIdToOperatorOutgoingEdges.put(nodeId, currList);
		}
	}

	public Map<String, GraphOLNode> getNodesMap() {
		return idToNodeMap;
	}

	public void setNodesMap(Map<String, GraphOLNode> nodes) {
		this.idToNodeMap = nodes;
	}

	public List<GraphOLOntologyAxiomEdge> getAxiomEdges() {
		return axiomEdges;
	}

	public void setAxiomEdges(List<GraphOLOntologyAxiomEdge> edges) {
		this.axiomEdges = edges;
	}

	public List<GraphOLConstructorInputEdge> getOperatorEdges() {
		return operatorEdges;
	}

	public void setOperatorEdges(List<GraphOLConstructorInputEdge> edges) {
		this.operatorEdges = edges;
	}

	public Map<String, List<GraphOLOntologyAxiomEdge>> getnodeIdToAxiomIncomingEdges() {
		return nodeIdToAxiomIncomingEdges;
	}

	public void setnodeIdToAxiomIncomingEdges(Map<String, List<GraphOLOntologyAxiomEdge>> nodeIdToAxiomIncomingEdges) {
		this.nodeIdToAxiomIncomingEdges = nodeIdToAxiomIncomingEdges;
	}

	public Map<String, List<GraphOLOntologyAxiomEdge>> getnodeIdToAxiomOutgoingEdges() {
		return nodeIdToAxiomOutgoingEdges;
	}

	public void setnodeIdToAxiomOutgoingEdges(Map<String, List<GraphOLOntologyAxiomEdge>> nodeIdToAxiomOutgoingEdges) {
		this.nodeIdToAxiomOutgoingEdges = nodeIdToAxiomOutgoingEdges;
	}

	public Map<String, List<GraphOLConstructorInputEdge>> getNodeIdToOperatorIncomingEdges() {
		return nodeIdToOperatorIncomingEdges;
	}

	public void setNodeIdToOperatorIncomingEdges(Map<String, List<GraphOLConstructorInputEdge>> nodeIdToOperatorIncomingEdges) {
		this.nodeIdToOperatorIncomingEdges = nodeIdToOperatorIncomingEdges;
	}

	public Map<String, List<GraphOLConstructorInputEdge>> getnodeIdToOperatorOutgoingEdges() {
		return nodeIdToOperatorOutgoingEdges;
	}

	public void setnodeIdToOperatorOutgoingEdges(Map<String, List<GraphOLConstructorInputEdge>> nodeIdToOperatorOutgoingEdges) {
		this.nodeIdToOperatorOutgoingEdges = nodeIdToOperatorOutgoingEdges;
	}

	public Map<String, GraphOLEdge> getIdToEdgeMap() {
		return idToEdgeMap;
	}

	public void setIdToEdgeMap(Map<String, GraphOLEdge> idToEdgeMap) {
		this.idToEdgeMap = idToEdgeMap;
	}

	public List<GraphOLDisjointUnionNode> getDisjointUnionNodes() {
		return disjointUnionNodes;
	}

	public void setDisjointUnionNodes(List<GraphOLDisjointUnionNode> disjointUnionNodes) {
		this.disjointUnionNodes = disjointUnionNodes;
	}
	
	public List<GraphOLHasKeyNode> getHasKeyNodes() {
		return hasKeyNodes;
	}

	public void setHasKeyNodes(List<GraphOLHasKeyNode> hasKeyNodes) {
		this.hasKeyNodes = hasKeyNodes;
	}

	@Override
	public String toString() {
		String result = "#########NODES#############\n";
		for(GraphOLNode node:idToNodeMap.values()) {
			result+=node.toString()+ " (instanceOf " + node.getClass() +  ")\n";
		}
		result += "#########disjointUnionNodes#############\n";
		for(GraphOLDisjointUnionNode node:disjointUnionNodes) {
			result+=node.toString()+"\n";
		}
		result += "#########AXIOM EDGES#############\n";
		for(GraphOLOntologyAxiomEdge edge:axiomEdges) {
			result+=edge.toString()+"\n";
		}
		result += "#########CONSTRUCTOR EDGES#############\n";
		for(GraphOLConstructorInputEdge edge:operatorEdges) {
			result+=edge.toString()+"\n";
		}
		result += "#########nodeIdToAxiomIncomingEdges#############\n";
		for(String nodeId:nodeIdToAxiomIncomingEdges.keySet()) {
			List<GraphOLOntologyAxiomEdge> currList = nodeIdToAxiomIncomingEdges.get(nodeId);
			result+="---- " +nodeId+":\n";
			for(GraphOLOntologyAxiomEdge edge:currList) {
				result+= edge.toString()+"\n";
			}
		}
		result += "#########nodeIdToAxiomOutgoingEdges#############\n";
		for(String nodeId:nodeIdToAxiomOutgoingEdges.keySet()) {
			List<GraphOLOntologyAxiomEdge> currList = nodeIdToAxiomOutgoingEdges.get(nodeId);
			result+="---- " +nodeId+":\n";
			for(GraphOLOntologyAxiomEdge edge:currList) {
				result+= edge.toString()+"\n";
			}
		}
		result += "#########nodeIdToOperatorIncomingEdges#############\n";
		for(String nodeId:nodeIdToOperatorIncomingEdges.keySet()) {
			List<GraphOLConstructorInputEdge> currList = nodeIdToOperatorIncomingEdges.get(nodeId);
			result+="---- " +nodeId+":\n";
			for(GraphOLConstructorInputEdge edge:currList) {
				result+= edge.toString()+"\n";
			}
		}
		result += "#########nodeIdToOperatorOutgoingEdges#############\n";
		for(String nodeId:nodeIdToOperatorOutgoingEdges.keySet()) {
			List<GraphOLConstructorInputEdge> currList = nodeIdToOperatorOutgoingEdges.get(nodeId);
			result+="---- " +nodeId+":\n";
			for(GraphOLConstructorInputEdge edge:currList) {
				result+= edge.toString()+"\n";
			}
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
