package landscapeEC.sat;

import java.util.Collections;
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
//        System.out.println("We've seen " + seenClauseLists.size() + " different clause strings");
        if (seenClauseLists.contains(clauseString)) {
            System.out.println("This is a previously seen clause string");
        }
//        } else {
//            System.out.println("This is an unseen clause string");
//        }
//        System.out.println("We have " + seenClauseLists.size() + " clauses so far");
        seenClauseLists.add(clauseString);
//        System.out.println("We have " + seenClauseLists.size() + " clauses after addition");
    }
    
    public static Set<String> clauseLists() {
        return seenClauseLists;
    }
    
    public static double clauseListPercentage(String clauseListString) {
        return (double) clauseListCounter.getCount(clauseListString) / (double) clauseListCounter.totalCount();
    }
    
    public static double calculateDiversity() {
        return (double)individualCounts.numKeys()/(double)individualCounts.totalCount();
    }
}
