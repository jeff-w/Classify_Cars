/**
 * @(#)Classifier.java
 *
 *
 * @author 
 * @version 1.00 2015/11/19
 */


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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("yes");
    }
}
