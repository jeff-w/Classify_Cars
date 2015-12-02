public class Constants {
	// Variables for manual text files
    public static String MANUAL_TESTING          = "test_manual_split.txt";      //1-355
    public static String MANUAL_RESULTS          = "results_manual_split.txt";

    // Variables for random text files
    public static String RANDOM_TESTING          = "test_random_split.txt";
    public static String RANDOM_RESULTS          = "results_random_split.txt";

    // Indices into our arrays (for classification values)
    public static final int CLASS_UNACC_INDEX          = 0;
    public static final int CLASS_ACC_INDEX            = 1;
    public static final int CLASS_GOOD_INDEX           = 2;
    public static final int CLASS_VGOOD_INDEX          = 3;
    public static final int NUM_CLASSIFICATIONS        = 4;
    
    // Indices into our arrays (for attribute names)
    public static final int ATTR_BUYING_INDEX          = 0;
    public static final int ATTR_MAINT_INDEX           = 1;
    public static final int ATTR_DOORS_INDEX           = 2;
    public static final int ATTR_PERSONS_INDEX         = 3;
    public static final int ATTR_SAFETY_INDEX          = 5;
    public static int NUM_ATTRIBUTES                   = 6;
    
    // Indices into our arrays (for attribute values)
    public static final int ATTR_BUYING_VHIGH_INDEX    = 0;
    public static final int ATTR_BUYING_HIGH_INDEX     = 1;
    public static final int ATTR_BUYING_MED_INDEX      = 2;
    public static final int ATTR_BUYING_LOW_INDEX      = 3;
    public static final int ATTR_MAINT_VHIGH_INDEX     = 4;
    public static final int ATTR_MAINT_HIGH_INDEX      = 5;
    public static final int ATTR_MAINT_MED_INDEX       = 6;
    public static final int ATTR_MAINT_LOW_INDEX       = 7;
    public static final int ATTR_DOORS_2_INDEX         = 8;
    public static final int ATTR_DOORS_3_INDEX         = 9;
    public static final int ATTR_DOORS_4_INDEX         = 10;
    public static final int ATTR_DOORS_5MORE_INDEX     = 11;
    public static final int ATTR_PERSONS_2_INDEX       = 12;
    public static final int ATTR_PERSONS_4_INDEX       = 13;
    public static final int ATTR_PERSONS_MORE_INDEX    = 14;
    public static final int ATTR_LUG_BOOT_SMALL_INDEX  = 15;
    public static final int ATTR_LUG_BOOT_MED_INDEX    = 16;
    public static final int ATTR_LUG_BOOT_BIG_INDEX    = 17;
    public static final int ATTR_SAFETY_LOW_INDEX      = 18;
    public static final int ATTR_SAFETY_MED_INDEX      = 19;
    public static final int ATTR_SAFETY_HIGH_INDEX     = 20; 
    public static int NUM_ATTRIBUTE_VALUES             = 21;
}