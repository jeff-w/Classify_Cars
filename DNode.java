import java.util.*;
import java.io.*;

/*
 * DNode - a node of a decision tree
 */
public class DNode {
	
	// Attribute type. Equal to null if this DNode is a leaf node.
	private String attribute;
	
	// Classification. If attribute is not null, ignore value of 'result'.
	private String result;
	
	// Maps an attribute value to a child DNode.
	private HashMap<String, DNode> children;

	public DNode(String attribute, String result) {
		this.attribute = attribute;
		this.result = result;
	}

	public DNode addChild(String value, DNode node) {
		children.put(value, node);
		return this;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getResult() {
		return result;
	}

	public HashMap<String, DNode> getChildren() {
		return children;
	}

	public void print() {
		// TODO: implement
	}
}