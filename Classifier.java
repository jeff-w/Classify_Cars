/**
 * @(#)Classifier.java
 *
 * @author 
 * @version 1.00 2015/11/19
 */

/*
Step 1      DONE    TESTED
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

public class Classifier {

    // Variables for manual text files
    private static final String MANUAL_TRAINING         = "train_manual_split.txt";     //356-1728
    private static final String MANUAL_TESTING          = "test_manual_split.txt";      //1-355
    private static final String MANUAL_RESULTS          = "results_manual_split.txt";

    // Variables for random text files
    private static final String DATA_FILE               = "car.data";
    private static final String RANDOM_TRAINING         = "train_random_split.txt";
    private static final String RANDOM_TESTING          = "test_random_split.txt";
    private static final String RANDOM_RESULTS          = "results_random_split.txt";

    // Variables for one-hot encoded text files
    private static final String MANUAL_OH_TRAINING      = "train_manual_onehot.txt";
    private static final String MANUAL_OH_TESTING       = "test_manual_onehot.txt";
    private static final String RANDOM_OH_TRAINING      = "train_random_onehot.txt";
    private static final String RANDOM_OH_TESTING       = "test_random_onehot.txt";

    // Indices into our arrays (for classification values)
    private static final int CLASS_UNACC_INDEX          = 0;
    private static final int CLASS_ACC_INDEX            = 1;
    private static final int CLASS_GOOD_INDEX           = 2;
    private static final int CLASS_VGOOD_INDEX          = 3;
    private static final int NUM_CLASSIFICATIONS        = 4;
    
    // Indices into our arrays (for attribute names)
    private static final int ATTR_BUYING_INDEX          = 0;
    private static final int ATTR_MAINT_INDEX           = 1;
    private static final int ATTR_DOORS_INDEX           = 2;
    private static final int ATTR_PERSONS_INDEX         = 3;
    private static final int ATTR_SAFETY_INDEX          = 5;
    private static final int NUM_ATTRIBUTES             = 6;
    
    // Indices into our arrays (for attribute values)
    private static final int ATTR_BUYING_VHIGH_INDEX    = 0;
    private static final int ATTR_BUYING_HIGH_INDEX     = 1;
    private static final int ATTR_BUYING_MED_INDEX      = 2;
    private static final int ATTR_BUYING_LOW_INDEX      = 3;
    private static final int ATTR_MAINT_VHIGH_INDEX     = 4;
    private static final int ATTR_MAINT_HIGH_INDEX      = 5;
    private static final int ATTR_MAINT_MED_INDEX       = 6;
    private static final int ATTR_MAINT_LOW_INDEX       = 7;
    private static final int ATTR_DOORS_2_INDEX         = 8;
    private static final int ATTR_DOORS_3_INDEX         = 9;
    private static final int ATTR_DOORS_4_INDEX         = 10;
    private static final int ATTR_DOORS_5MORE_INDEX     = 11;
    private static final int ATTR_PERSONS_2_INDEX       = 12;
    private static final int ATTR_PERSONS_4_INDEX       = 13;
    private static final int ATTR_PERSONS_MORE_INDEX    = 14;
    private static final int ATTR_LUG_BOOT_SMALL_INDEX  = 15;
    private static final int ATTR_LUG_BOOT_MED_INDEX    = 16;
    private static final int ATTR_LUG_BOOT_BIG_INDEX    = 17;
    private static final int ATTR_SAFETY_LOW_INDEX      = 18;
    private static final int ATTR_SAFETY_MED_INDEX      = 19;
    private static final int ATTR_SAFETY_HIGH_INDEX     = 20; 
    private static final int NUM_ATTRIBUTE_VALUES       = 21;

    private int numDataPoints                           = 0;
    private int[] attributeCounts                       = new int[21];
    private int[] classificationCounts                  = new int[4];
    private int[][] jointCounts                         = new int[21][4];
    private double[] attributePriorProbabilities        = new double[21];
    private double[] classificationPriorProbabilities   = new double[4];
    private double[][] likelihoods                      = new double[21][4];

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

    /* Splits "car.data" file randomly by using modulo
    ~80% to training
    ~20% to testing 
    */
    public void randomSplit(){
        Random rand = new Random();

        Scanner in = null;
        PrintWriter outTrain = null;
        PrintWriter outTest = null;
        try{
            in = new Scanner(new FileReader(DATA_FILE));
            outTrain = new PrintWriter(RANDOM_TRAINING, "UTF-8");
            outTest = new PrintWriter(RANDOM_TESTING, "UTF-8");
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
            String line = in.next();

            if(rand.nextInt(5) == 0){
                // 1 in 5 write to TEST
                outTest.println(line);
            }
            else{
                // 4 in 5 write to TRAIN
                outTrain.println(line);
            }
        }

        in.close();
        outTrain.close();
        outTest.close();
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

            String correct = result.equals(fields[6]) ? "yes" : "no";
                
            //write the new file with results
            out.print(line);
            out.print("," + result);
            out.println("," + correct);
            
            //write the results to terminal output
            System.out.print(line + "\t\t");
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

            matrix[confusionIndex(fields[6])][confusionIndex(fields[7])]++;
        }

        in.close();

        return matrix;
    }

    /* Prints out the confusion matrix
    */
    public void printConfusionMatrix(int[][] matrix){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /* Does one-hot encoding on the input text files
       This means instead of 6 features + 1 classification, we have
       21 binary features + 1 classification

       Output: 
       two new text files, each line having 21 1s and 0s, followed by the classification
    */
    public void oneHotEncode(String type){
        Scanner inTrain = null;
        Scanner inTest = null;
        PrintWriter outTrain = null;
        PrintWriter outTest = null;
        try{
            if(type.equals("MANUAL")){
                inTrain = new Scanner(new FileReader(MANUAL_TRAINING));
                inTest = new Scanner(new FileReader(MANUAL_TESTING));
                outTrain = new PrintWriter(MANUAL_OH_TRAINING, "UTF-8");
                outTest = new PrintWriter(MANUAL_OH_TESTING, "UTF-8");
            }
            else if(type.equals("RANDOM")){
                inTrain = new Scanner(new FileReader(RANDOM_TRAINING));
                inTest = new Scanner(new FileReader(RANDOM_TESTING));
                outTrain = new PrintWriter(RANDOM_OH_TRAINING, "UTF-8");
                outTest = new PrintWriter(RANDOM_OH_TESTING, "UTF-8");
            }
            else{
                System.out.println("[EXITING] oneHotEncode() function: unsupported run type: " + type);
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

        //create transformed training dataset
        while(inTrain.hasNext()){
            String line = inTrain.next();
            String[] fields = line.split(",");  //size 7
            String newLine = "";

            //loop over all 6 attributes
            for(int i = 0; i < 6; i++){
                newLine += oneHotBinary(i, oneHotIndex(i, fields[i]));
            }

            //add the classification
            newLine += fields[6];

            //write to the transformed dataset
            outTrain.println(newLine);
        }

        //create transformed test dataset
        while(inTest.hasNext()){
            String line = inTest.next();
            String[] fields = line.split(",");  //size 7
            String newLine = "";

            //loop over all 6 attributes
            for(int i = 0; i < 6; i++){
                newLine += oneHotBinary(i, oneHotIndex(i, fields[i]));
            }

            //add the classification
            newLine += fields[6];

            //write to the transformed dataset
            outTest.println(newLine);
        }

        //clean up
        inTrain.close();
        inTest.close();
        outTrain.close();
        outTest.close();
    }

    /* Maps values of the 6 attributes to integeres for one-hot encoding
    */
    private int oneHotIndex(int attr, String field){
        switch(attr){
            case 0: //buying
                if (field.equals("vhigh"))      { return 0; }
                else if (field.equals("high"))  { return 1; }
                else if (field.equals("med"))   { return 2; }
                else if (field.equals("low"))   { return 3; }
                else                            { return -1; }
            case 1: //maint
                if (field.equals("vhigh"))      { return 0; }
                else if (field.equals("high"))  { return 1; }
                else if (field.equals("med"))   { return 2; }
                else if (field.equals("low"))   { return 3; }
                else                            { return -1; }
            case 2: //doors
                if (field.equals("2"))          { return 0; }
                else if (field.equals("3"))     { return 1; }
                else if (field.equals("4"))     { return 2; }
                else if (field.equals("5more")) { return 3; }
                else                            { return -1; }
            case 3: //persons
                if (field.equals("2"))          { return 0; }
                else if (field.equals("4"))     { return 1; }
                else if (field.equals("more"))  { return 2; }
                else                            { return -1; }
            case 4: //lug_boot
                if (field.equals("small"))      { return 0; }
                else if (field.equals("med"))   { return 1; }
                else if (field.equals("big"))   { return 2; }
                else                            { return -1; }
            case 5: //safety
                if (field.equals("low"))        { return 0; }
                else if (field.equals("med"))   { return 1; }
                else if (field.equals("high"))  { return 2; }
                else                            { return -1; }
            default:
                return -1;     
        }
    }

    /* Returns String of 1s and 0s (with commas) representing if the attribute is present
    Example (buying high)
    oneHotBinary(0, 1) --> "0,1,0,0,"
    */
    private String oneHotBinary(int attr, int index){
        int numValues = -1;
        if(attr == 0 || attr == 1 || attr == 2){
            numValues = 4;
        }
        else if(attr == 3 || attr == 4 || attr == 5){
            numValues = 3;
        }
        else{
            System.out.println("[EXITING] oneHotBinary() attr value invalid");
            System.exit(1);
        }

        String result = "";

        for(int i = 0; i < numValues; i++){
            if(i == index){
                result += "1,";
            }
            else{
                result += "0,";
            }
        }

        return result;
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
        
        for(int i = 0; i < 6; i++){
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
        switch(attr){
            case 0: //buying
                if (field.equals("vhigh"))      { return ATTR_BUYING_VHIGH_INDEX; }
                else if (field.equals("high"))  { return ATTR_BUYING_HIGH_INDEX; }
                else if (field.equals("med"))   { return ATTR_BUYING_MED_INDEX; }
                else if (field.equals("low"))   { return ATTR_BUYING_LOW_INDEX; }
                else                            { return -1; }
            case 1: //maint
                if (field.equals("vhigh"))      { return ATTR_MAINT_VHIGH_INDEX; }
                else if (field.equals("high"))  { return ATTR_MAINT_HIGH_INDEX; }
                else if (field.equals("med"))   { return ATTR_MAINT_MED_INDEX; }
                else if (field.equals("low"))   { return ATTR_MAINT_LOW_INDEX; }
                else                            { return -1; }
            case 2: //doors
                if (field.equals("2"))          { return ATTR_DOORS_2_INDEX; }
                else if (field.equals("3"))     { return ATTR_DOORS_3_INDEX; }
                else if (field.equals("4"))     { return ATTR_DOORS_4_INDEX; }
                else if (field.equals("5more")) { return ATTR_DOORS_5MORE_INDEX; }
                else                            { return -1; }
            case 3: //persons
                if (field.equals("2"))          { return ATTR_PERSONS_2_INDEX; }
                else if (field.equals("4"))     { return ATTR_PERSONS_4_INDEX; }
                else if (field.equals("more"))  { return ATTR_PERSONS_MORE_INDEX; }
                else                            { return -1; }
            case 4: //lug_boot
                if (field.equals("small"))      { return ATTR_LUG_BOOT_SMALL_INDEX; }
                else if (field.equals("med"))   { return ATTR_LUG_BOOT_MED_INDEX; }
                else if (field.equals("big"))   { return ATTR_LUG_BOOT_BIG_INDEX; }
                else                            { return -1; }
            case 5: //safety
                if (field.equals("low"))        { return ATTR_SAFETY_LOW_INDEX; }
                else if (field.equals("med"))   { return ATTR_SAFETY_MED_INDEX; }
                else if (field.equals("high"))  { return ATTR_SAFETY_HIGH_INDEX; }
                else                            { return -1; }
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
        Classifier c = new Classifier();

        System.out.println("Step1-----------------------------------------------");
        c.randomSplit();
        System.out.println("----------------------------------------------------");

        System.out.println("Step2-----------------------------------------------");
        c.train(RANDOM_TRAINING);
        System.out.println("----------------------------------------------------");

        System.out.println("Step3-----------------------------------------------");
        c.test("RANDOM");
        System.out.println("----------------------------------------------------");

        System.out.println("Step4-----------------------------------------------");
        c.printConfusionMatrix(c.buildConfusionMatrix("RANDOM"));
        System.out.println("----------------------------------------------------");
    }
}
