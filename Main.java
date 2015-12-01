/**
 * @(#)Classifier.java
 *
 * @author 
 * @version 1.00 2015/11/19
 */

/*
Step 1      DONE    TESTED
Step 2      DONE    TESTED
Step 3      DONE    TESTED
Step 4      DONE    TESTED
Step 5      TODO    ***
Step 6      TODO    ***
Step 7      DONE
Step 8      TODO    ***
*/

/*IMPORTS*/
import java.util.*;
import java.io.*;

public class Main {

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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	    
	Main self = new Main();
        Classifier c = new Classifier();
        OneHot_Classifier ohc = new OneHot_Classifier();

        System.out.println("Step1-----------------------------------------------");
        self.randomSplit();
        System.out.println("DONE");
        System.out.println("----------------------------------------------------\n");

        System.out.println("Step2-----------------------------------------------");
        c.train(RANDOM_TRAINING);
        System.out.println("----------------------------------------------------\n");

        System.out.println("Step3-----------------------------------------------");
        c.test("RANDOM");
        System.out.println("----------------------------------------------------\n");

        System.out.println("Step4-----------------------------------------------");
        c.printConfusionMatrix(c.buildConfusionMatrix("RANDOM"));
        System.out.println("----------------------------------------------------\n");

        System.out.println("Step5-----------------------------------------------");
        DNode node = c.giniDecide();
        node.print(0);
        System.out.println("----------------------------------------------------\n");

        System.out.println("Step6-----------------------------------------------");
        
        System.out.println("----------------------------------------------------\n");

        System.out.println("Step7-----------------------------------------------");
        self.oneHotEncode("RANDOM");
        System.out.println("DONE");
        System.out.println("----------------------------------------------------\n");

        System.out.println("Step8-----------------------------------------------");
        
        System.out.println("----------------------------------------------------");


        ohc.train(RANDOM_OH_TRAINING);
        ohc.test("RANDOM");
        ohc.printConfusionMatrix(ohc.buildConfusionMatrix("RANDOM"));
        System.out.println("----------------------------------------------------\n");
	
	    //PRINT DECISION TREE TEST
	    /*
	    DNode age = new DNode("Age", null);
	    DNode gender = new DNode("Gender", null);
	    DNode height = new DNode("Height", null);
	    DNode yes = new DNode(null, "YES");
	    DNode no = new DNode(null, "NO");

	    age.addChild("> 30", yes);
	    age.addChild("<=30", gender);
	    gender.addChild("M", no);
	    gender.addChild("F", height);
	    height.addChild(">6", yes);
	    height.addChild("<=6", no);

	    age.print(0);*/
    }
}
