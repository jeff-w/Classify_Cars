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
    protected final int NUM_CLASSIFICATIONS             = 4;
    
    // Indices into our arrays (for attribute names)
    private static final int ATTR_BUYING_INDEX          = 0;
    private static final int ATTR_MAINT_INDEX           = 1;
    private static final int ATTR_DOORS_INDEX           = 2;
    private static final int ATTR_PERSONS_INDEX         = 3;
    private static final int ATTR_SAFETY_INDEX          = 5;
    protected int NUM_ATTRIBUTES                        = 6;
    
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
    protected int NUM_ATTRIBUTE_VALUES                  = 21;

    private int numDataPoints                           = 0;
    private int[] classificationCounts                  = new int[NUM_CLASSIFICATIONS];
    private double[] classificationPriorProbabilities   = new double[NUM_CLASSIFICATIONS];
    protected int[][] jointCounts;
    protected double[][] likelihoods;
    private String[] trainingLines;
    private Gini gini;
    private InformationGain ig;

    public Classifier() {
    	jointCounts = new int[NUM_ATTRIBUTE_VALUES][NUM_CLASSIFICATIONS];
        likelihoods = new double[NUM_ATTRIBUTE_VALUES][NUM_CLASSIFICATIONS];
    	for (int i = 0; i < NUM_ATTRIBUTE_VALUES; i++) {
    		for (int j = 0; j < NUM_CLASSIFICATIONS; j++) {
    			jointCounts[i][j] = 1; // Initialize with add-one smoothing
    		}
    	}
        gini = new Gini();
        ig = new InformationGain();
    }

    private void loadCountsFromFile(String filename) {
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			int attributeIndex;
			int classificationIndex;
            ArrayList<String> list = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",", -1);
     			classificationIndex = indexOfClassification(values[NUM_ATTRIBUTES]);
        		classificationCounts[classificationIndex]++;
				for (int i = 0; i < NUM_ATTRIBUTES; i++) {
					attributeIndex = indexOfAttribute(i, values[i]);
					jointCounts[attributeIndex][classificationIndex]++;
				}
                list.add(line);
                numDataPoints++;
			}
            trainingLines = list.toArray(new String[list.size()]);
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

    /* 1. read 'testfile' line by line
     * 2. for each line, compute the 4 probabilities
     *      predicted class = max of the 4
     * 3. original 6 attr | Actual | Measured | Correct?
     *      (print these to terminal AND a new file)
     */
    public void test(String testfile, String resultfile) {
        Scanner in =  null;
        PrintWriter out = null;
        try{
            in = new Scanner(new FileReader(testfile));
            if (resultfile != null) {
                out = new PrintWriter(resultfile, "UTF-8");
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
            if (resultfile != null) {
                out.print(line);
                out.print("," + result);
                out.println("," + correct);                
            }
            
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
    public int[][] buildConfusionMatrix(String resultfile){
        Scanner in = null;
        try{
            in = new Scanner(new FileReader(resultfile));
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

    public double getTreeAccuracy(DNode tree, String fileName){
        Scanner in = null;
        try{
            in = new Scanner(new FileReader(fileName));
        }
        catch(FileNotFoundException e){
            System.out.println("[EXITING] In getTreeAccuracy(): file not found (Scanner): " + e);
            System.exit(1);
        }
        
        int numLines = 0;
        int numCorrect = 0;

        while(in.hasNext()){
            String line = in.next();
            String[] fields = line.split(",");  //size 7

            if(fields[6].equals(classifyLine(tree, line))){
                numCorrect++;
            }
            numLines++;
        }

        return ((double)numCorrect) / numLines;
    }

    private String classifyLine(DNode root, String line){
        if(root == null){
            return null;
        }

        DNode cur = root;

        //traverse until hit a leaf
        while(cur.getAttribute() != null){
            cur = getChild(cur, line);
        }

        //now cur points to a leaf
        return cur.getResult();
    }

    /* the DNode is guaranteed not to be a leaf
    The child we want to go to may not exist; in that case, pick randomly
    There will be at least one child
    */
    private DNode getChild(DNode cur, String line){
        String[] fields = line.split(",");
        String branchToTake = fields[indexOfAttributeType(cur.getAttribute())];

        Map<String, DNode> children = cur.getChildren();
        DNode child = children.get(branchToTake);

        if(child == null){
            //pick one randomly from the ones in the map
            Random rand = new Random();
            List<String> keys = new ArrayList<String>(children.keySet());
            String randomKey = keys.get(rand.nextInt(keys.size()));
            return children.get(randomKey);
        }

        return child;
    }

    /* Checks if the classification of every line is the same
    */
    private boolean pureClassification(String[] lines){
        if(lines.length == 0){
            return true;
        }

        String classification = lines[0].split(",")[6];

        for(String line : lines){
            if(!classification.equals(line.split(",")[6])){
                return false;
            }
        }

        return true;
    }

    public DNode createDecisionNode(boolean useGini) {
        String[] attributes = {"buying", "maint", "doors", "persons", "lug_boot", "safety"};
        return createDecisionNode(trainingLines, attributes, useGini);
    }

    private DNode createDecisionNode(String[] lines, String[] attributes, boolean useGini) {
        AttributeSelectionMeasurement measurement;
        if (useGini) {
            measurement = new Gini();
        } else {
            measurement = new InformationGain();
        }

        if(lines.length == 0){
            return null;
        }

        //majority voting when went through all attributes
        if(attributes.length == 0){
            return new DNode(null, Utility.bestClass(lines));
        }

        //pure check - if pure, return a result node
        if(pureClassification(lines)){
            return new DNode(null, lines[0].split(",")[6]);
        }

        double bestSplitValue = Double.MAX_VALUE;
        String bestAttribute = null;

        for (String attribute : attributes) {
            double curSplitValue = attributeSplit(lines, attribute, measurement);
            if (curSplitValue < bestSplitValue) {
                bestSplitValue = curSplitValue;
                bestAttribute = attribute;
            }
        }

        DNode node = new DNode(bestAttribute, null);
        String[] subtractedArray = arraySubtract(attributes, bestAttribute);

        if (bestAttribute.equals("buying")) {
            return node
                .addChild("vhigh", createDecisionNode(subLines(lines, bestAttribute, "vhigh"), subtractedArray, useGini))
                .addChild("high", createDecisionNode(subLines(lines, bestAttribute, "high"), subtractedArray, useGini))
                .addChild("med", createDecisionNode(subLines(lines, bestAttribute, "med"), subtractedArray, useGini))
                .addChild("low", createDecisionNode(subLines(lines, bestAttribute, "low"), subtractedArray, useGini));
        }
        if (bestAttribute.equals("maint")) {
            return node
                .addChild("vhigh", createDecisionNode(subLines(lines, bestAttribute, "vhigh"), subtractedArray, useGini))
                .addChild("high", createDecisionNode(subLines(lines, bestAttribute, "high"), subtractedArray, useGini))
                .addChild("med", createDecisionNode(subLines(lines, bestAttribute, "med"), subtractedArray, useGini))
                .addChild("low", createDecisionNode(subLines(lines, bestAttribute, "low"), subtractedArray, useGini));
        }
        if (bestAttribute.equals("doors")) {
            return node
                .addChild("2", createDecisionNode(subLines(lines, bestAttribute, "2"), subtractedArray, useGini))
                .addChild("3", createDecisionNode(subLines(lines, bestAttribute, "3"), subtractedArray, useGini))
                .addChild("4", createDecisionNode(subLines(lines, bestAttribute, "4"), subtractedArray, useGini))
                .addChild("5more", createDecisionNode(subLines(lines, bestAttribute, "5more"), subtractedArray, useGini));
        }
        if (bestAttribute.equals("persons")) {
            return node
                .addChild("2", createDecisionNode(subLines(lines, bestAttribute, "2"), subtractedArray, useGini))
                .addChild("4", createDecisionNode(subLines(lines, bestAttribute, "4"), subtractedArray, useGini))
                .addChild("more", createDecisionNode(subLines(lines, bestAttribute, "more"), subtractedArray, useGini));
        }
        if (bestAttribute.equals("lug_boot")) {
            return node
                .addChild("small", createDecisionNode(subLines(lines, bestAttribute, "small"), subtractedArray, useGini))
                .addChild("med", createDecisionNode(subLines(lines, bestAttribute, "med"), subtractedArray, useGini))
                .addChild("big", createDecisionNode(subLines(lines, bestAttribute, "big"), subtractedArray, useGini));
        }
        if (bestAttribute.equals("safety")) {
            return node
                .addChild("low", createDecisionNode(subLines(lines, bestAttribute, "low"), subtractedArray, useGini))
                .addChild("med", createDecisionNode(subLines(lines, bestAttribute, "med"), subtractedArray, useGini))
                .addChild("high", createDecisionNode(subLines(lines, bestAttribute, "high"), subtractedArray, useGini));
        }
        return null;
    }

    private String[] arraySubtract(String[] attributes, String attributeToSubtract) {
        ArrayList<String> list = new ArrayList<String>();

        for(String attribute : attributes){
            if(!attribute.equals(attributeToSubtract)){
                list.add(attribute);
            }
        }

        return list.toArray(new String[list.size()]);
    }

    /* computes split score for a particular attribute
    */
    private double attributeSplit(String[] lines, String attribute, AttributeSelectionMeasurement measurement){
        double ret;
        if(attribute.equals("buying")){
            String[] n1Lines = subLines(lines, attribute, "vhigh");
            String[] n2Lines = subLines(lines, attribute, "high");
            String[] n3Lines = subLines(lines, attribute, "med");
            String[] n4Lines = subLines(lines, attribute, "low");
            ret =      (n1Lines.length/(double)lines.length)*measurement.measure(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*measurement.measure(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*measurement.measure(n3Lines)
                    +   (n4Lines.length/(double)lines.length)*measurement.measure(n4Lines);
        }
        else if(attribute.equals("maint")){
            String[] n1Lines = subLines(lines, attribute, "vhigh");
            String[] n2Lines = subLines(lines, attribute, "high");
            String[] n3Lines = subLines(lines, attribute, "med");
            String[] n4Lines = subLines(lines, attribute, "low");
            ret =      (n1Lines.length/(double)lines.length)*measurement.measure(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*measurement.measure(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*measurement.measure(n3Lines)
                    +   (n4Lines.length/(double)lines.length)*measurement.measure(n4Lines);
        }
        else if(attribute.equals("doors")){
            String[] n1Lines = subLines(lines, attribute, "2");
            String[] n2Lines = subLines(lines, attribute, "3");
            String[] n3Lines = subLines(lines, attribute, "4");
            String[] n4Lines = subLines(lines, attribute, "5more");
            ret =      (n1Lines.length/(double)lines.length)*measurement.measure(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*measurement.measure(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*measurement.measure(n3Lines)
                    +   (n4Lines.length/(double)lines.length)*measurement.measure(n4Lines);
        }
        else if(attribute.equals("persons")){
            String[] n1Lines = subLines(lines, attribute, "2");
            String[] n2Lines = subLines(lines, attribute, "4");
            String[] n3Lines = subLines(lines, attribute, "more");
            ret =      (n1Lines.length/(double)lines.length)*measurement.measure(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*measurement.measure(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*measurement.measure(n3Lines);
        }
        else if(attribute.equals("lug_boot")){
            String[] n1Lines = subLines(lines, attribute, "small");
            String[] n2Lines = subLines(lines, attribute, "med");
            String[] n3Lines = subLines(lines, attribute, "big");
            ret =      (n1Lines.length/(double)lines.length)*measurement.measure(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*measurement.measure(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*measurement.measure(n3Lines);
        }
        else if(attribute.equals("safety")){
            String[] n1Lines = subLines(lines, attribute, "low");
            String[] n2Lines = subLines(lines, attribute, "med");
            String[] n3Lines = subLines(lines, attribute, "high");
            ret =      (n1Lines.length/(double)lines.length)*measurement.measure(n1Lines)
                    +   (n2Lines.length/(double)lines.length)*measurement.measure(n2Lines)
                    +   (n3Lines.length/(double)lines.length)*measurement.measure(n3Lines);
        }
        else{
             ret = -1;
        }
        return ret;
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
        return list.toArray(new String[list.size()]);
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
