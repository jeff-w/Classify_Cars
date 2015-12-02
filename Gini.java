import java.lang.*;

public class Gini implements AttributeSelectionMeasurement {
	public double measure(String[] lines) {
        if(lines.length == 0){
            return 0.0;
        }

        int[] classCounts = Utility.getClassCounts(lines);
        int numLines = lines.length;

        return 1 
            - Math.pow(((double)classCounts[0]/numLines), 2)
            - Math.pow(((double)classCounts[1]/numLines), 2) 
            - Math.pow(((double)classCounts[2]/numLines), 2) 
            - Math.pow(((double)classCounts[3]/numLines), 2);
    }
}