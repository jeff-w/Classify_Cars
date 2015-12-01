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
    }

    /* Gets the index (0-20) of attribute "attr" and value "field"
     **/
    protected int indexOfAttribute(int attr, String field){
	if (field.equals("1")) 		{ return attr * 2; }
	else if (field.equals("0"))	{ return attr * 2 + 1; }
	else				{ return -1; }
    }
}
