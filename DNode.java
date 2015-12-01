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
	private HashMap<String, DNode> children = new HashMap<String, DNode>();

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

	public void printIndent(Integer indent) {
		for (Integer i = 0; i < indent; i ++) {
			System.out.print(" ");
		}
	}

	public void print(Integer indent) {
		// check if attribute exist, if not print out result and return because no more children 
		if (!Boolean.parseBoolean(getAttribute())) {
			printIndent(4);
			System.out.print(getResult() + "\n");
			return;
		}
		else {
			printIndent(indent);

			System.out.print(getAttribute() + "\n");

			for (HashMap.Entry<String, DNode> entry: children.entrySet()) {
		      String value = entry.getKey();
		      DNode  node = entry.getValue();
		      printIndent(indent);
		      System.out.print(value);
		      node.print(value.length() + 4);
		    }
		}

		System.out.println();
	}
}