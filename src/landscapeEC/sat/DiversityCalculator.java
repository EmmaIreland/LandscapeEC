package landscapeEC.sat;

import java.util.LinkedHashSet;
import java.util.Set;

import landscapeEC.util.FrequencyCounter;

public class DiversityCalculator {

    private static FrequencyCounter<String> individualCounts = new FrequencyCounter<String>();
    private static FrequencyCounter<String> clauseListCounter = new FrequencyCounter<String>();
    private static Set<String> seenClauseLists = new LinkedHashSet<String>();
    
    public static void reset() {
        individualCounts.reset();
        clauseListCounter.reset();
        seenClauseLists.clear();
    }

    public static void addIndividual(Individual individual) {
        individualCounts.addItem(individual.toString());
        String clauseString = SatEvaluator.getSolvedClausesBitstring(individual);
        clauseListCounter.addItem(clauseString);
        
        //Add clause list to the set, it will not add duplicates
        seenClauseLists.add(clauseString);
    }
    
    public static Set<String> clauseLists() {
        return seenClauseLists;
    }
    
    public static double clauseListPercentage(String clauseListString) {
        return (double) clauseListCounter.getCount(clauseListString) / (double) clauseListCounter.totalCount();
    }

    public static double calculateClauseListDiversity() {
        return (double)clauseListCounter.numKeys()/(double)clauseListCounter.totalCount();
    }
    
    public static double calculateBitStringDiversity() {
        return (double)individualCounts.numKeys()/(double)individualCounts.totalCount();
    }
}
