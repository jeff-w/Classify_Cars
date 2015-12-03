import java.lang.*;

public class InformationGain implements AttributeSelectionMeasurement {
	public double measure(String[] lines) {
        if(lines.length == 0){
            return 0.0;
        }

        int[] counts = Utility.getClassCounts(lines);
        double ret = 0.0;
        double probability;
        double logDivisor = Math.log10(2);
        for (int i = 0; i < counts.length; i++) {
            probability = counts[i] / (double)lines.length;
            if (probability != 0) {
                ret -= probability * Math.log10(probability) / logDivisor;
            }
        }

        return ret;
    }
}