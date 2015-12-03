public class Utility {
	public static int[] getClassCounts(String[] lines){
        int nUnacc = 0;
        int nAcc = 0;
        int nGood = 0;
        int nVgood = 0;
        int numLines = lines.length;

        if(numLines == 0){
        	return new int[]{0,0,0,0};
        }

        String firstLine = lines[0];
        String[] fields = firstLine.split(",");	//size 7 or 22
        int classificationIndex = -1;

        if(fields.length == 7){
        	//regular
        	classificationIndex = Constants.NUM_ATTRIBUTES;
        }
        else if(fields.length == 22){
        	//oneHot
        	classificationIndex = Constants.NUM_ATTRIBUTE_VALUES;
        }
        else{
        	System.out.println("ERROR in Utility class");
        	System.exit(1);
        }

        for(int i = 0; i < numLines; i++){
            String classification = lines[i].split(",")[classificationIndex];

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

    public static String bestClass(String[] lines){
        int[] classCounts = Utility.getClassCounts(lines);
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
}