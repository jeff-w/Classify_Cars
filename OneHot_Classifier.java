/**
 * @(#)Classifier.java
 *
 * @author 
 * @version 1.00 2015/11/19
 */

/*IMPORTS*/
import java.util.*;
import java.io.*;

public class OneHot_Classifier extends Classifier {

    // Variables for one-hot encoded text files

    public OneHot_Classifier() {
    	MANUAL_TESTING = "test_manual_onehot.txt";
    	MANUAL_RESULTS = "results_manual_onehot.txt";
    	RANDOM_TESTING = "test_random_onehot.txt";
    	RANDOM_RESULTS = "results_random_onehot.txt";

    	NUM_ATTRIBUTES = 21;
    	NUM_ATTRIBUTE_VALUES = 42;

        jointCounts = new int[NUM_ATTRIBUTE_VALUES][NUM_CLASSIFICATIONS];
        likelihoods = new double[NUM_ATTRIBUTE_VALUES][NUM_CLASSIFICATIONS];

    	for (int i = 0; i < NUM_ATTRIBUTE_VALUES; i++) {
    		for (int j = 0; j < NUM_CLASSIFICATIONS; j++) {
    			jointCounts[i][j] = 1; // Initialize with add-one smoothing
    		}
    	}

        attributes = new String[]{  "Attr0",
                                    "Attr1", "Attr2", 
                                    "Attr3", "Attr4", 
                                    "Attr5", "Attr6", 
                                    "Attr7", "Attr8", 
                                    "Attr9", "Attr10", 
                                    "Attr11", "Attr12", 
                                    "Attr13", "Attr14", 
                                    "Attr15", "Attr16", 
                                    "Attr17", "Attr18", 
                                    "Attr19", "Attr20" };
    }

    /* Gets the index (0-20) of attribute "attr" and value "field"
     **/
    protected int indexOfAttribute(int attr, String field){
	   if (field.equals("1")) 		{ return attr * 2; }
	   else if (field.equals("0"))	{ return attr * 2 + 1; }
	   else				            { return -1; }
    }

    /*
    Override
    lines are like:     1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,1,0,0,unacc
    attribute is like:  Attr2
    */
    protected double attributeSplit(String[] lines, String attribute, AttributeSelectionMeasurement measurement){
        String[] zeroLines = subLines(lines, attribute, "0");
        String[] oneLines = subLines(lines, attribute, "1");
        return (zeroLines.length/(double)lines.length)*measurement.measure(zeroLines)
                + (oneLines.length/(double)lines.length)*measurement.measure(oneLines);
    }

    /*
    Override
    lines are like:     1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,1,0,0,unacc
    attribute is like:  Attr14
    attributeValue is:  0 or 1
    */
    protected String[] subLines(String[] lines, String attribute, String attributeValue){
        ArrayList<String> list = new ArrayList<String>();

        for(String line : lines){
            if(line.split(",")[Integer.parseInt(attribute.substring(4))].equals(attributeValue)){
                list.add(line);
            }
        }

        //make it back to String[]
        return list.toArray(new String[list.size()]);
    }

    protected DNode newNode(DNode node, String[] lines, String[] subtractedArray, String bestAttribute, boolean useGini){
        return node
            .addChild("0", createDecisionNode(subLines(lines, bestAttribute, "0"), subtractedArray, useGini))
            .addChild("1", createDecisionNode(subLines(lines, bestAttribute, "1"), subtractedArray, useGini));
    }
}
