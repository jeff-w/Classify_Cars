/**
 * @(#)Classifier.java
 *
 * @author 
 * @version 1.00 2015/11/19
 */

/*
Step 1      DONE
Step 2      TODO    In Progress - Kevin
Step 3      DONE
Step 4      DONE
Step 5      TODO    ***
Step 6      TODO    ***
Step 7      TODO    ***
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

    // Indices into our arrays (for classifications)
    private static final int CLASS_UNACC_INDEX          = 0;
    private static final int CLASS_ACC_INDEX            = 1;
    private static final int CLASS_GOOD_INDEX           = 2;
    private static final int CLASS_VGOOD_INDEX          = 3;

    // Indices into our arrays (for attributes)
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

    private int[] attributeCounts                       = new int[21];
    private double[] attributePriorProbabilities        = new double[21];
    private double[] classificationPriorProbabilities   = new double[4];
    private double[][] likelihoods                      = new double[21][4];

    /**
     * Creates a new instance of <code>Classifier</code>.
     */
    public Classifier() {
    	for (int count : attributeCounts) {
    		count = 1; // for add-one smoothing
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

            matrix[confusionIndex(fields[6])][confusionIndex(fields[7])]++;
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
        int row = -1;
        switch(attr){
            case 0: //buying
                if(field.equals("vhigh"))   row = ATTR_BUYING_VHIGH_INDEX;
                if(field.equals("high"))    row = ATTR_BUYING_HIGH_INDEX;
                if(field.equals("med"))     row = ATTR_BUYING_MED_INDEX;
                if(field.equals("low"))     row = ATTR_BUYING_LOW_INDEX;
                break;
            case 1: //maint
                if(field.equals("vhigh"))   row = ATTR_MAINT_VHIGH_INDEX;
                if(field.equals("high"))    row = ATTR_MAINT_HIGH_INDEX;
                if(field.equals("med"))     row = ATTR_MAINT_MED_INDEX;
                if(field.equals("low"))     row = ATTR_MAINT_LOW_INDEX;
                break;
            case 2: //doors
                if(field.equals("2"))       row = ATTR_DOORS_2_INDEX;
                if(field.equals("3"))       row = ATTR_DOORS_3_INDEX;
                if(field.equals("4"))       row = ATTR_DOORS_4_INDEX;
                if(field.equals("5more"))   row = ATTR_DOORS_5MORE_INDEX;
                break;
            case 3: //persons
                if(field.equals("2"))       row = ATTR_PERSONS_2_INDEX;
                if(field.equals("4"))       row = ATTR_PERSONS_4_INDEX;
                if(field.equals("more"))    row = ATTR_PERSONS_MORE_INDEX;
                break;
            case 4: //lug_boot
                if(field.equals("small"))   row = ATTR_LUG_BOOT_SMALL_INDEX;
                if(field.equals("med"))     row = ATTR_LUG_BOOT_MED_INDEX;
                if(field.equals("big"))     row = ATTR_LUG_BOOT_BIG_INDEX;
                break;
            case 5: //safety
                if(field.equals("low"))     row = ATTR_SAFETY_LOW_INDEX;
                if(field.equals("med"))     row = ATTR_SAFETY_MED_INDEX;
                if(field.equals("high"))    row = ATTR_SAFETY_HIGH_INDEX;
                break;
            default:
                break;      
        }
        return row;
    }

    /* Gets the index (0-3) of the classification
     **/
    private int indexOfClassification(String classification){
        int col = -1;
        
        if(classification.equals("unacc"))  col = CLASS_UNACC_INDEX;
        if(classification.equals("acc"))    col = CLASS_ACC_INDEX;
        if(classification.equals("good"))   col = CLASS_GOOD_INDEX;
        if(classification.equals("vgood"))  col = CLASS_VGOOD_INDEX;
        
        return col;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Classifier c = new Classifier();
        // c.test();

        System.out.println("\n" + "yes");
    }
}
