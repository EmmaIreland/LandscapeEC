package landscapeEC.sat;

import java.util.Map;
import java.util.TreeMap;

public class DiversityCalculator {

    private static int populationSize = 0;
    private static Map<Integer, Integer> individualCounts = new TreeMap<Integer, Integer>();
    
    public static void reset() {
        populationSize = 0;
        individualCounts.clear();
    }

    public static void addIndividual(Individual individual) {
        int key = computeIndividualHashCode(individual);
        int numIndividuals;
        if (individualCounts.containsKey(key)) {
            numIndividuals = individualCounts.get(key);
            individualCounts.put(key, numIndividuals++);
        } else {
            individualCounts.put(key, 1);
        }
        
        populationSize++;
    }

    private static int computeIndividualHashCode(Individual individual) {
        StringBuilder bitString = new StringBuilder();
        for (int bit : individual.getBits()) {
            bitString.append(bit);
        }
        String individualString = bitString.toString();
        return individualString.hashCode();
    }
    
    public static double calculateDiversity() {
        return (double)individualCounts.size()/(double)populationSize;
    }
    
//    public void test() {
//        for (int key : individualCounts.keySet()) {
//            individualCounts.
//        }
//    }
}
