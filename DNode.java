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
	private Map<String, DNode> children = new HashMap<String, DNode>();

	private boolean isPureClassification = false;

	public DNode(String attribute, String result) {
		this.attribute = attribute;
		this.result = result;
	}

	public DNode(String attribute, String result, boolean isPureClassification) {
		this.attribute = attribute;
		this.result = result;
		this.isPureClassification = isPureClassification;
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

	public Map<String, DNode> getChildren() {
		return children;
	}

	private String stringRepeat(String s, int n) {
		String result = "";
		for (int i = 0; i < n; i++)
			result += s;
		return result;
	}

	private String getResultAndPurity() {
		return getResult() + (isPureClassification ? " (PURE)" : "");
	}

	public void print(String indent, int split_length) {
		// check if attribute exist, if not print out result and return because no more children 
		if (getAttribute() == null) {
			System.out.print( stringRepeat("-", 2)
					+ "> "
					+ stringRepeat(".", getResultAndPurity().length()+4)
					+ " \n"
					+ indent.substring(0, indent.length()-split_length)
					+ stringRepeat(" ", split_length)
					+ ". "
					+ getResultAndPurity()
					+ " . \n"
					+ indent.substring(0, indent.length()-split_length)
					+ stringRepeat(" ", split_length)
					+ stringRepeat("'",getResultAndPurity().length()+4)
					+ "\n"
					+ indent.substring(0, indent.length()-split_length)
					+ "\n");
			return;
		} else {
			if (split_length > 0){
				System.out.print(stringRepeat("-", 2));
				System.out.print("> ");
			}

			System.out.print(getAttribute() + "\n");

			//remove branches that have no values
			while(children.values().remove(null));
			
			int kids_left = children.size();
			for (Map.Entry<String, DNode> entry: children.entrySet()) {
				String attribute_splitter = entry.getKey();
				DNode node = entry.getValue();

				System.out.print(indent);
				System.out.print("(" + attribute_splitter + ")");
				if (kids_left < 2){
					node.print(indent + " " + stringRepeat(" ", (attribute_splitter.length() + 5)),
							attribute_splitter.length() + 5);
				} else {
					node.print(indent + "|" + stringRepeat(" ", (attribute_splitter.length() + 5)), attribute_splitter.length() + 5);
				}
				kids_left--;
			}
		}

	}
}
