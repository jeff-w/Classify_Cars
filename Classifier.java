/**
 * @(#)Classifier.java
 *
 * @author 
 * @version 1.00 2015/11/19
 */


/*IMPORTS*/
import java.util.*;
import java.io.*;
import java.lang.*;

public class Classifier {

    // Variables for manual text files
    protected String MANUAL_TESTING          = "test_manual_split.txt";      //1-355
    protected String MANUAL_RESULTS          = "results_manual_split.txt";

    // Variables for random text files
    protected String RANDOM_TESTING          = "test_random_split.txt";
    protected String RANDOM_RESULTS          = "results_random_split.txt";

    // Indices into our arrays (for classification values)
    private static final int CLASS_UNACC_INDEX          = 0;
    private static final int CLASS_ACC_INDEX            = 1;
    private static final int CLASS_GOOD_INDEX           = 2;
    private static final int CLASS_VGOOD_INDEX          = 3;
    protected final int NUM_CLASSIFICATIONS        = 4;
    
    // Indices into our arrays (for attribute names)
    private static final int ATTR_BUYING_INDEX          = 0;
    private static final int ATTR_MAINT_INDEX           = 1;
    private static final int ATTR_DOORS_INDEX           = 2;
    private static final int ATTR_PERSONS_INDEX         = 3;
    private static final int ATTR_SAFETY_INDEX          = 5;
    protected int NUM_ATTRIBUTES             = 6;
    
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
    protected int NUM_ATTRIBUTE_VALUES       = 21;

    private int numDataPoints                           = 0;
    private int[] classificationCounts                  = new int[NUM_CLASSIFICATIONS];
    private double[] classificationPriorProbabilities   = new double[NUM_CLASSIFICATIONS];
    protected int[][] jointCounts;
    protected double[][] likelihoods;

    public Classifier() {
    	jointCounts = new int[NUM_ATTRIBUTE_VALUES][NUM_CLASSIFICATIONS];
	likelihoods = new double[NUM_ATTRIBUTE_VALUES][NUM_CLASSIFICATIONS];
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
            System.out.println(classificationPriorProbabilities[i]);
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
            System.out.format("%-50s", line);
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

        int[][] matrix = new int[NUM_CLASSIFICATIONS][NUM_CLASSIFICATIONS]; //default initialized to all 0

        while(in.hasNext()){
            String line = in.next();
            String[] fields = line.split(",");  //size 9

            /*original 6 attr | Actual | Measured | Correct*/

            matrix[confusionIndex(fields[NUM_ATTRIBUTES])][confusionIndex(fields[NUM_ATTRIBUTES+1])]++;
        }

        in.close();

        return matrix;
    }

    /* Prints out the confusion matrix
    */
    public void printConfusionMatrix(int[][] matrix){
        for(int i = 0; i < NUM_CLASSIFICATIONS; i++){
            for(int j = 0; j < NUM_CLASSIFICATIONS; j++){
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public DNode giniDecide(String[] lines) {
        String[] attributes = {"buying", "maint", "doors", "persons", "lug_boot", "safety"};
        return giniDecide(lines, attributes);
    }

    private String giniBestClass(String[] lines){
        int[] classCounts = giniGetClassCounts(lines);
        String bestClass = null;
        int maxCount = 0;

        if(classCounts[0] > maxCount){
            maxCount = classCounts[0];
            bestClass = "unacc";
        }
        if(classCounts[1] > maxCount){
            maxCount = classCounts[1];
            bestClass = "acc";
        }
        if(classCounts[2] > maxCount){
            maxCount = classCounts[2];
            bestClass = "good";
        }
        if(classCounts[3] > maxCount){
            maxCount = classCounts[3];
            bestClass = "vgood";
        }

        return bestClass;
    }

    private DNode giniDecide(String[] lines, String[] attributes) {
        if(attributes.length == 0){
            return new DNode(null, giniBestClass(lines));
        }

        double lowestGiniSplit = Double.MAX_VALUE;
        String bestAttribute = null;

        for (String attribute : attributes) {
            double curGiniSplit = giniSplit(lines, attribute);
            if (curGiniSplit < lowestGiniSplit) {
                lowestGiniSplit = curGiniSplit;
                bestAttribute = attribute;
            }
        }

        DNode node = new DNode(bestAttribute, null);
        String[] subtractedArray = arraySubtract(attributes, bestAttribute);

        if (bestAttribute.equals("buying")) {
            return node
                .addChild("vhigh", giniDecide(subLines(lines, bestAttribute, "vhigh"), subtractedArray))
                .addChild("high", giniDecide(subLines(lines, bestAttribute, "high"), subtractedArray))
                .addChild("med", giniDecide(subLines(lines, bestAttribute, "med"), subtractedArray))
                .addChild("low", giniDecide(subLines(lines, bestAttribute, "low"), subtractedArray));
        }
        if (bestAttribute.equals("maint")) {
            return node
                .addChild("vhigh", giniDecide(subLines(lines, bestAttribute, "vhigh"), subtractedArray))
                .addChild("high", giniDecide(subLines(lines, bestAttribute, "high"), subtractedArray))
                .addChild("med", giniDecide(subLines(lines, bestAttribute, "med"), subtractedArray))
                .addChild("low", giniDecide(subLines(lines, bestAttribute, "low"), subtractedArray));
        }
        if (bestAttribute.equals("doors")) {
            return node
                .addChild("2", giniDecide(subLines(lines, bestAttribute, "2"), subtractedArray))
                .addChild("3", giniDecide(subLines(lines, bestAttribute, "3"), subtractedArray))
                .addChild("4", giniDecide(subLines(lines, bestAttribute, "4"), subtractedArray))
                .addChild("5more", giniDecide(subLines(lines, bestAttribute, "5more"), subtractedArray));
        }
        if (bestAttribute.equals("persons")) {
            return node
                .addChild("2", giniDecide(subLines(lines, bestAttribute, "2"), subtractedArray))
                .addChild("4", giniDecide(subLines(lines, bestAttribute, "4"), subtractedArray))
                .addChild("more", giniDecide(subLines(lines, bestAttribute, "more"), subtractedArray));
        }
        if (bestAttribute.equals("lug_boot")) {
            return node
                .addChild("small", giniDecide(subLines(lines, bestAttribute, "small"), subtractedArray))
                .addChild("med", giniDecide(subLines(lines, bestAttribute, "med"), subtractedArray))
                .addChild("big", giniDecide(subLines(lines, bestAttribute, "big"), subtractedArray));
        }
        if (bestAttribute.equals("safety")) {
            return node
                .addChild("low", giniDecide(subLines(lines, bestAttribute, "low"), subtractedArray))
                .addChild("med", giniDecide(subLines(lines, bestAttribute, "med"), subtractedArray))
                .addChild("high", giniDecide(subLines(lines, bestAttribute, "high"), subtractedArray));
        }
        return null;
    }

    private String[] arraySubtract(String[] attributes, String attributeToSubtract) {
        Set<String> set = new HashSet<String>(Arrays.asList(attributes));
        set.remove(attributeToSubtract);
        return (String[]) set.toArray();
    }

    private int[] giniGetClassCounts(String[] lines){
        int nUnacc = 0;
        int nAcc = 0;
        int nGood = 0;
        int nVgood = 0;
        int numLines = lines.length;

        for(int i = 0; i < numLines; i++){
            String classification = lines[i].split(",")[NUM_ATTRIBUTES];

            if(classification.equals("unacc")){
                nUnacc++;
            }
            if(classification.equals("acc")){
                nAcc++;
            }
            if(classification.equals("good")){
                nGood++;
            }
            if(classification.equals("vgood")){
                nVgood++;
            }
        }

        int ret[] = {nUnacc, nAcc, nGood, nVgood};
        return ret;
    }

    /* lines should only have the particular attribute value (ex Maint=vhigh)
    */
    private double gini(String[] lines){
        int[] classCounts = giniGetClassCounts(lines);
        int numLines = lines.length;

        return 1 
            - Math.pow(((double)classCounts[0]/numLines), 2)
            - Math.pow(((double)classCounts[1]/numLines), 2) 
            - Math.pow(((double)classCounts[2]/numLines), 2) 
            - Math.pow(((double)classCounts[3]/numLines), 2);
    }

    /* computes gini split for a particular attribute
    */
    private double giniSplit(String[] lines, String attribute){
        if(attribute.equals("buying")){
            String[] n1Lines = subLines(lines, attribute, "vhigh");
            String[] n2Lines = subLines(lines, attribute, "high");
            String[] n3Lines = subLines(lines, attribute, "med");
            String[] n4Lines = subLines(lines, attribute, "low");
            return      (n1Lines.length/(double)lines.length)*gini(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*gini(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*gini(n3Lines)
                    +   (n4Lines.length/(double)lines.length)*gini(n4Lines);
        }
        if(attribute.equals("maint")){
            String[] n1Lines = subLines(lines, attribute, "vhigh");
            String[] n2Lines = subLines(lines, attribute, "high");
            String[] n3Lines = subLines(lines, attribute, "med");
            String[] n4Lines = subLines(lines, attribute, "low");
            return      (n1Lines.length/(double)lines.length)*gini(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*gini(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*gini(n3Lines)
                    +   (n4Lines.length/(double)lines.length)*gini(n4Lines);
        }
        if(attribute.equals("doors")){
            String[] n1Lines = subLines(lines, attribute, "2");
            String[] n2Lines = subLines(lines, attribute, "3");
            String[] n3Lines = subLines(lines, attribute, "4");
            String[] n4Lines = subLines(lines, attribute, "5more");
            return      (n1Lines.length/(double)lines.length)*gini(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*gini(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*gini(n3Lines)
                    +   (n4Lines.length/(double)lines.length)*gini(n4Lines);
        }
        if(attribute.equals("persons")){
            String[] n1Lines = subLines(lines, attribute, "2");
            String[] n2Lines = subLines(lines, attribute, "4");
            String[] n3Lines = subLines(lines, attribute, "more");
            return      (n1Lines.length/(double)lines.length)*gini(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*gini(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*gini(n3Lines);
        }
        if(attribute.equals("lug_boot")){
            String[] n1Lines = subLines(lines, attribute, "small");
            String[] n2Lines = subLines(lines, attribute, "med");
            String[] n3Lines = subLines(lines, attribute, "big");
            return      (n1Lines.length/(double)lines.length)*gini(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*gini(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*gini(n3Lines);
        }
        if(attribute.equals("safety")){
            String[] n1Lines = subLines(lines, attribute, "low");
            String[] n2Lines = subLines(lines, attribute, "med");
            String[] n3Lines = subLines(lines, attribute, "high");
            return      (n1Lines.length/(double)lines.length)*gini(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*gini(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*gini(n3Lines);
        }
        else{
            return 0.0;
        }
    }

    /* Subset of lines
    */
    private String[] subLines(String[] lines, String attribute, String attributeValue){
        ArrayList<String> list = new ArrayList<String>();

        for(String line : lines){
            if(line.split(",")[indexOfAttributeType(attribute)].equals(attributeValue)){
                list.add(line);
            }
        }

        //make it back to String[]
        return (String[])list.toArray();
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

    /* duh
    */
    private int indexOfAttributeType(String attr){
        if(attr.equals("buying"))   {return 0;}
        if(attr.equals("maint"))    {return 1;}
        if(attr.equals("doors"))    {return 2;}
        if(attr.equals("persons"))  {return 3;}
        if(attr.equals("lug_boot")) {return 4;}
        if(attr.equals("safety"))   {return 5;}

        return -1;
    }

    /* Gets the index (0-20) of attribute "attr" and value "field"
     **/
    protected int indexOfAttribute(int attr, String field){
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
}
