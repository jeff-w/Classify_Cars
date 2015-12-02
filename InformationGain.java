import java.lang.*;

public class InformationGain implements AttributeSelectionMeasurement {
	public double measure(String[] lines) {
        int[] counts = Utility.getClassCounts(lines);

        double total = 0.0;
        double ret = 0.0;
        for (int count : counts) {
            total += count;
        }

        double probability;
        double logDivisor = Math.log10(2);
        for (int i = 0; i < counts.length; i++) {
            probability = counts[i] / total;
            ret += probability * Math.log10(probability) / logDivisor;
        }

        return -ret;
    }
}