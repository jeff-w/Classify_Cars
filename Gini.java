import java.lang.*;

public class Gini implements AttributeSelectionMeasurement {
	public double measure(String[] lines) {
        if(lines.length == 0){
            return 0.0;
        }

        int[] counts = Utility.getClassCounts(lines);
        double ret = 1.0;
        for (int i = 0; i < counts.length; i++) {
            ret -= Math.pow(((double)counts[i]/lines.length), 2);
        }

        return ret;
    }
}