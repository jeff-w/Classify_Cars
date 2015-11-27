/**
 * @(#)Classifier.java
 *
 *
 * @author 
 * @version 1.00 2015/11/19
 */

import java.util.Scanner;
import java.io.*;

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
        for (int i = 0; i < NUM_ATTRIBUTE_VALUES; i++) {
            attributePriorProbabilities[i] = ((double) attributeCounts[i]) / ((double) numDataPoints);
            for (int j = 0; j < NUM_CLASSIFICATIONS; j++) {
                double jointProbability = ((double) jointCounts[i][j]) / ((double) numDataPoints);
                likelihoods[i][j] = jointProbability / attributePriorProbabilities[i];
            }
        }
        for (int i = 0; i < NUM_CLASSIFICATIONS; i++) {
            classificationPriorProbabilities[i] = (double) classificationCounts[i] / numDataPoints;
        }
    }

    public void train(String filename) {
    	loadCountsFromFile(filename);
        calculateProbabilities();
        System.out.println("Total data points: " + numDataPoints);
        for (int i = 0; i < NUM_ATTRIBUTE_VALUES; i++) {
            System.out.format("%f, %f, %f, %f\n", likelihoods[i][0], likelihoods[i][1], likelihoods[i][2], likelihoods[i][3]);
        }
    }

    /* 1. read INFILE line by line
     * 2. for each line, compute the 4 probabilities
     *      predicted class = max of the 4
     * 3. original 6 attr | Actual | Measured | Correct?
     *      (print these to terminal AND a new file)
     */
    public void test(){
        Scanner in =  null;
        try{
            in = new Scanner(new FileReader("test_manual_split.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("[EXITING] File not found: " + e);
            System.exit(1);
        }
        
        while(in.hasNext()){
            //read line and tokenize
            String line = in.next();
            String[] fields = line.split(",");  //size 7
            
            //compute 4 probabilities
            double pUnacc = classificationPriorProbabilities[indexOfClassification("unacc")] 
                            * conditionalProb(fields, "unacc");
            double pAcc   = classificationPriorProbabilities[indexOfClassification("acc")]
                            * conditionalProb(fields, "acc");
            double pGood  = classificationPriorProbabilities[indexOfClassification("good")]
                            * conditionalProb(fields, "good");
            double pVgood = classificationPriorProbabilities[indexOfClassification("vgood")]
                            * conditionalProb(fields, "vgood");
            
            //TODO - jeffrey
            
            
            
        }




    }

    /* Takes in array of size 7. 
       Will only use first 6 fields for multiplication of independent probabilities.
     */
    private double conditionalProb(String[] fields, String classification){
        double product = 1.0;
        
        for(int i = 0; i < 6; i++){
            product *= likelihoods
                        [indexOfAttribute(i, fields[i])]
                        [indexOfClassification(classification)];
        }
        
        return product;
    }

    /* Gets the index (0-20) of attribute "attr" and value "field"
     **/
    private int indexOfAttribute(int attr, String field){
        switch(attr){
            case 0: //buying
                if (field.equals("vhigh")) { return ATTR_BUYING_VHIGH_INDEX; }
                else if (field.equals("high")) { return ATTR_BUYING_HIGH_INDEX; }
                else if (field.equals("med")) { return ATTR_BUYING_MED_INDEX; }
                else if (field.equals("low")) { return ATTR_BUYING_LOW_INDEX; }
                else { return -1; }
            case 1: //maint
                if (field.equals("vhigh")) { return ATTR_MAINT_VHIGH_INDEX; }
                else if (field.equals("high")) { return ATTR_MAINT_HIGH_INDEX; }
                else if (field.equals("med")) { return ATTR_MAINT_MED_INDEX; }
                else if (field.equals("low")) { return ATTR_MAINT_LOW_INDEX; }
                else { return -1; }
            case 2: //doors
                if (field.equals("2")) { return ATTR_DOORS_2_INDEX; }
                else if (field.equals("3")) { return ATTR_DOORS_3_INDEX; }
                else if (field.equals("4")) { return ATTR_DOORS_4_INDEX; }
                else if (field.equals("5more")) { return ATTR_DOORS_5MORE_INDEX; }
                else { return -1; }
            case 3: //persons
                if (field.equals("2")) { return ATTR_PERSONS_2_INDEX; }
                else if (field.equals("4")) { return ATTR_PERSONS_4_INDEX; }
                else if (field.equals("more")) { return ATTR_PERSONS_MORE_INDEX; }
                else { return -1; }
            case 4: //lug_boot
                if (field.equals("small")) { return ATTR_LUG_BOOT_SMALL_INDEX; }
                else if (field.equals("med")) { return ATTR_LUG_BOOT_MED_INDEX; }
                else if (field.equals("big")) { return ATTR_LUG_BOOT_BIG_INDEX; }
                else { return -1; }
            case 5: //safety
                if (field.equals("low")) { return ATTR_SAFETY_LOW_INDEX; }
                else if (field.equals("med")) { return ATTR_SAFETY_MED_INDEX; }
                else if (field.equals("high")) { return ATTR_SAFETY_HIGH_INDEX; }
                else { return -1; }
            default:
                return -1;     
        }
    }

    /* Gets the index (0-3) of the classification
     **/
    private int indexOfClassification(String classification){
        if (classification.equals("unacc")) {
            return CLASS_UNACC_INDEX;
        
        } else if (classification.equals("acc")) {
            return CLASS_ACC_INDEX;
        
        } else if(classification.equals("good")) {
            return CLASS_GOOD_INDEX;
        
        }  else if(classification.equals("vgood")) {
            return CLASS_VGOOD_INDEX;
        
        } else {
            return -1;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if (args.length < 1) {
            System.err.println("No file specified.");
        } else {
            Classifier classifier = new Classifier();
            classifier.train(args[0]);
            System.err.println("Training complete.");
        }
    }
}
