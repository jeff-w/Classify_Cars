/**
 * @(#)Classifier.java
 *
 *
 * @author 
 * @version 1.00 2015/11/19
 */

/*IMPORTS*/
import java.util.Scanner;
import java.io.*;

public class Classifier {

    // Indices into our arrays (for classifications)
    private static final int CLASS_UNACC_INDEX = 0;
    private static final int CLASS_ACC_INDEX = 1;
    private static final int CLASS_GOOD_INDEX = 2;
    private static final int CLASS_VGOOD_INDEX = 3;

    // Indices into our arrays (for attributes)
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

    private int[] attributeCounts = new int[21];
    private double[] attributePriorProbabilities = new double[21];
    private double[] classificationPriorProbabilities = new double[4];
    private double[][] likelihoods = new double[21][4];

    /**
     * Creates a new instance of <code>Classifier</code>.
     */
    public Classifier() {
    	for (int count : attributeCounts) {
    		count = 1; // for add-one smoothing
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
            in = new Scanner(new FileReader("test_data.txt"));
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
        if(classification.equals("v-good")) col = CLASS_VGOOD_INDEX;
        
        return col;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("yes");
    }
}
