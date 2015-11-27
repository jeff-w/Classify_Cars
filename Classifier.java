/**
 * @(#)Classifier.java
 *
 *
 * @author 
 * @version 1.00 2015/11/19
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Classifier {

    // Indices into our arrays (for classification values)
    private static final int CLASS_UNACC_INDEX = 0;
    private static final int CLASS_ACC_INDEX = 1;
    private static final int CLASS_GOOD_INDEX = 2;
    private static final int CLASS_VGOOD_INDEX = 3;
    private static final int NUM_CLASSIFICATIONS = 4;

    // Indices into our arrays (for attribute names)
    private static final int ATTR_BUYING_INDEX = 0;
    private static final int ATTR_MAINT_INDEX = 1;
    private static final int ATTR_DOORS_INDEX = 2;
    private static final int ATTR_PERSONS_INDEX = 3;
	private static final int ATTR_LUG_BOOT_INDEX = 4;
	private static final int ATTR_SAFETY_INDEX = 5;
	private static final int NUM_ATTRIBUTES = 6;

    // Indices into our arrays (for attribute values)
    private static final int ATTR_BUYING_VHIGH_INDEX = 0;
    private static final int ATTR_BUYING_HIGH_INDEX = 1;
    private static final int ATTR_BUYING_MED_INDEX = 2;
    private static final int ATTR_BUYING_LOW_INDEX = 3;
    private static final int ATTR_MAINT_VHIGH_INDEX = 4;
    private static final int ATTR_MAINT_HIGH_INDEX = 5;
    private static final int ATTR_MAINT_MED_INDEX = 6;
    private static final int ATTR_MAINT_LOW_INDEX = 7;
    private static final int ATTR_DOORS_2_INDEX = 8;
    private static final int ATTR_DOORS_3_INDEX = 9;
    private static final int ATTR_DOORS_4_INDEX = 10;
    private static final int ATTR_DOORS_5MORE_INDEX = 11;
    private static final int ATTR_PERSONS_2_INDEX = 12;
    private static final int ATTR_PERSONS_4_INDEX = 13;
    private static final int ATTR_PERSONS_MORE_INDEX = 14;
    private static final int ATTR_LUG_BOOT_SMALL_INDEX = 15;
    private static final int ATTR_LUG_BOOT_MED_INDEX = 16;
    private static final int ATTR_LUG_BOOT_BIG_INDEX = 17;
    private static final int ATTR_SAFETY_LOW_INDEX = 18;
    private static final int ATTR_SAFETY_MED_INDEX = 19;
    private static final int ATTR_SAFETY_HIGH_INDEX = 20;
    private static final int NUM_ATTRIBUTE_VALUES = 21;

    private int numDataPoints = 0;
    private int[] attributeCounts = new int[21];
    private int[] classificationCounts = new int[4];
    private int[][] jointCounts = new int[21][4];
    private double[] attributePriorProbabilities = new double[21];
    private double[] classificationPriorProbabilities = new double[4];
    private double[][] likelihoods = new double[21][4];

    private int indexOfAttribute(int attribute, String value) {
    	// TODO
    	return -1;
    }

    private int indexOfClassification(String value) {
    	// TODO
    	return -1;
    }

    public Classifier() {
    	for (int i = 0; i < NUM_ATTRIBUTE_VALUES; i++) {
    		for (int j = 0; j < NUM_CLASSIFICATIONS; j++) {
    			jointCounts[i][j] = 1; // Initialize with add-one smoothing
    		}
    	}
    }

    private void loadCountsFromFile(String filename) {
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			int attributeIndex;
			int classificationIndex;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",", -1);
                classificationIndex = indexOfClassification(values[NUM_ATTRIBUTES]);
                classificationCounts[classificationIndex]++;
				for (int i = 0; i < NUM_ATTRIBUTES; i++) {
					attributeIndex = indexOfAttribute(i, values[i]);
                    attributeCounts[attributeIndex]++;
					jointCounts[attributeIndex][classificationIndex]++;
				}
                numDataPoints++;
			}
			reader.close();

    	} catch (FileNotFoundException e) {
    		System.err.println("File not found");
    	
    	} catch (IOException e) {
    		System.err.println("Caught IO exception");
    	}
    }

    private void calculateProbabilities() {
        for (int i = 0; i < NUM_ATTRIBUTES; i++) {
            attributePriorProbabilities[i] = (double) attributeCounts[i] / numDataPoints;
            for (int j = 0; i < NUM_CLASSIFICATIONS; i++) {
                double jointProbability = (double) jointCounts[i][j] / numDataPoints;
                likelihoods[i][j] = attributePriorProbabilities[i] / jointProbability;
            }
        }
        for (int i = 0; i < NUM_CLASSIFICATIONS; i++) {
            classificationPriorProbabilities[i] = (double) classificationCounts[i] / numDataPoints;
        }
    }

    public void train(String filename) {
    	loadCountsFromFile(filename);
        calculateProbabilities();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("yes");
    }
}
