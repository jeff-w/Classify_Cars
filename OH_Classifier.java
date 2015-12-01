/**
 * @(#)Classifier.java
 *
 * @author 
 * @version 1.00 2015/11/19
 */

/*
Step 1      DONE
Step 2      DONE
Step 3      DONE
Step 4      DONE
Step 5      TODO    ***
Step 6      TODO    ***
Step 7      DONE
Step 8      TODO    ***
*/

/*IMPORTS*/
import java.util.*;
import java.io.*;

public class OH_Classifier {

    // Variables for one-hot encoded text files
    private static final String MANUAL_TRAINING      = "train_manual_onehot.txt";
    private static final String MANUAL_TESTING       = "test_manual_onehot.txt";
    private static final String MANUAL_RESULTS       = "results_manual_onehot.txt";
    private static final String RANDOM_TRAINING      = "train_random_onehot.txt";
    private static final String RANDOM_TESTING       = "test_random_onehot.txt";
    private static final String RANDOM_RESULTS       = "results_random_onehot.txt";

    // Indices into our arrays (for classification values)
    private static final int CLASS_UNACC_INDEX          = 0;
    private static final int CLASS_ACC_INDEX            = 1;
    private static final int CLASS_GOOD_INDEX           = 2;
    private static final int CLASS_VGOOD_INDEX          = 3;
    private static final int NUM_CLASSIFICATIONS        = 4;
    
    private static final int NUM_ATTRIBUTES             = 21;
    
    private static final int NUM_ATTRIBUTE_VALUES       = 42;

    private int numDataPoints                           = 0;
    private int[] classificationCounts                  = new int[NUM_CLASSIFICATIONS];
    private int[][] jointCounts                         = new int[NUM_ATTRIBUTE_VALUES][NUM_CLASSIFICATIONS];
    private double[] classificationPriorProbabilities   = new double[NUM_CLASSIFICATIONS];
    private double[][] likelihoods                      = new double[NUM_ATTRIBUTE_VALUES][NUM_CLASSIFICATIONS];

    public OH_Classifier() {
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
        for (int i = 0; i < NUM_CLASSIFICATIONS; i++) {
            classificationPriorProbabilities[i] = (double) classificationCounts[i] / numDataPoints;
        }
	
        for (int i = 0; i < NUM_ATTRIBUTE_VALUES; i++) {
            for (int j = 0; j < NUM_CLASSIFICATIONS; j++) {
                double jointProbability = ((double) jointCounts[i][j]) / ((double) numDataPoints);
		likelihoods[i][j] = jointProbability / classificationPriorProbabilities[j];
            }
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
    public void test(String type){
        Scanner in =  null;
        PrintWriter out = null;
        try{
            if(type.equals("MANUAL")){
                in = new Scanner(new FileReader(MANUAL_TESTING));
                out = new PrintWriter(MANUAL_RESULTS, "UTF-8");
            }
            else if(type.equals("RANDOM")){
                in = new Scanner(new FileReader(RANDOM_TESTING));
                out = new PrintWriter(RANDOM_RESULTS, "UTF-8");
            }
            else{
                System.out.println("[EXITING] test() function: unsupported run type: " + type);
                System.exit(1);
            }
        }
        catch(FileNotFoundException e){
            System.out.println("[EXITING] File not found (Scanner): " + e);
            System.exit(1);
        }
        catch(UnsupportedEncodingException e){
            System.out.println("[EXITING] File not found (PrintWriter): " + e);
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
            
            //classify it as the greatest probability
            String result = null;
            if(greatestDouble(pUnacc, pAcc, pGood, pVgood)){ result = "unacc";  }
            if(greatestDouble(pAcc, pUnacc, pGood, pVgood)){ result = "acc";    }
            if(greatestDouble(pGood, pUnacc, pAcc, pVgood)){ result = "good";   }
            if(greatestDouble(pVgood, pUnacc, pAcc, pGood)){ result = "vgood";  }

            String correct = result.equals(fields[NUM_ATTRIBUTES]) ? "yes" : "no";
                
            //write the new file with results
            out.print(line);
            out.print("," + result);
            out.println("," + correct);
            
            //write the results to terminal output
            System.out.print(line + "\t");
            System.out.print(result + "\t");
            System.out.println(correct);
        }

        //cleanup
        in.close();
        out.close();
    }

    /* Builds the Confusion Matrix generated from the test() step 
    */
    public int[][] buildConfusionMatrix(String type){
        Scanner in = null;
        try{
            if(type.equals("MANUAL")){
                in = new Scanner(new FileReader(MANUAL_RESULTS));
            }
            else if(type.equals("RANDOM")){
                in = new Scanner(new FileReader(RANDOM_RESULTS));
            }
            else{
                System.out.println("[EXITING] buildConfusionMatrix(): unsupported run type: " + type);
                System.exit(1);
            }
        }
        catch(FileNotFoundException e){
            System.out.println("[EXITING] File not found (Scanner): " + e);
            System.exit(1);
        }

        int[][] matrix = new int[4][4]; //default initialized to all 0

        while(in.hasNext()){
            String line = in.next();
            String[] fields = line.split(",");  //size 9

            /*original 6 attr | Actual | Measured | Correct*/

            matrix[confusionIndex(fields[NUM_ATTRIBUTES])][confusionIndex(fields[NUM_ATTRIBUTES+1])]++;
        }

        in.close();

        return matrix;
    }

    /* Checks if double d is greater than all three other doubles
    */
    private boolean greatestDouble(double d, double a, double b, double c){
        return (d > a && d > b && d > c);
    }

    /* Takes in array of size 7. 
       Will only use first 6 fields for multiplication of independent probabilities.
     */
    private double conditionalProb(String[] fields, String classification){
        double product = 1.0;
        
        for(int i = 0; i < NUM_ATTRIBUTES; i++){
            product *= likelihoods
                        [indexOfAttribute(i, fields[i])]
                        [indexOfClassification(classification)];
        }
        
        return product;
    }

    /* Returns index of the classification
    */
    private int confusionIndex(String type){
        if(type.equals("unacc"))    { return 0; }
        if(type.equals("acc"))      { return 1; }
        if(type.equals("good"))     { return 2; }
        if(type.equals("vgood"))    { return 3; }

        return -1;
    }

    /* Gets the index (0-20) of attribute "attr" and value "field"
     **/
    private int indexOfAttribute(int attr, String field){
	if (field.equals("1")) 		{ return attr * 2; }
	else if (field.equals("0"))	{ return attr * 2 + 1; }
	else				{ return -1; }
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

    public void printConfusionMatrix(int[][] matrix){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

}
