package landscapeEC.problem;

import java.util.LinkedHashSet;
import java.util.Set;

import landscapeEC.util.FrequencyCounter;

public class DiversityCalculator {

    private static FrequencyCounter<String> individualCounts = new FrequencyCounter<String>();
    private static FrequencyCounter<String> resultStringCounter = new FrequencyCounter<String>();
    private static Set<String> seenResultStrings = new LinkedHashSet<String>();
    private static Evaluator evaluator = GlobalProblem.getEvaluator();
    
    public static void reset() {
        individualCounts.reset();
        resultStringCounter.reset();
        seenResultStrings.clear();
    }

    public static void addIndividual(Individual individual) {
        individualCounts.addItem(individual.toString());
        String resultString = evaluator.getResultString(individual);
        resultStringCounter.addItem(resultString);
        
        //Add clause list to the set, it will not add duplicates
        seenResultStrings.add(resultString);
    }
    
    public static Set<String> resultStrings() {
        return seenResultStrings;
    }
    
    private String getMostCommon() {
        int maxNumber = 0;
        String currentMaxString = "";
        for(String resultString : resultStringCounter) {
            if(resultStringCounter.getCount(resultString) > maxNumber) {
                maxNumber = resultStringCounter.getCount(resultString);
                currentMaxString = resultString;
            }
        }
        return currentMaxString;
    }
    
    public static double resultStringPercentage(String clauseListString) {
        return (double) resultStringCounter.getCount(clauseListString) / (double) resultStringCounter.totalCount();
    }

    public static double calculateResultStringDiversity() {
        return (double)resultStringCounter.numKeys()/(double)resultStringCounter.totalCount();
    }
    
    public static double calculateBitStringDiversity() {
        return (double)individualCounts.numKeys()/(double)individualCounts.totalCount();
    }
}
