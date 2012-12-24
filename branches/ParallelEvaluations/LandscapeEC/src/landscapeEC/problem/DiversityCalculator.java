package landscapeEC.problem;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import landscapeEC.util.FrequencyCounter;

public class DiversityCalculator {

    private static FrequencyCounter<Individual> individualCounts = new FrequencyCounter<Individual>();
    private static FrequencyCounter<String> resultStringCounter = new FrequencyCounter<String>();
    private static Set<String> seenResultStrings = new LinkedHashSet<String>();
    private static Set<Individual> bestIndividuals = new LinkedHashSet<Individual>();
    private static ParallelEvaluator evaluator = GlobalProblem.getEvaluator();
    
    public static void reset() {
        individualCounts.reset();
        resultStringCounter.reset();
        seenResultStrings.clear();
        bestIndividuals.clear();
    }

    public static void addIndividual(Individual individual) {
        individualCounts.addItem(individual);
        String resultString = evaluator.getResultString(individual);
        resultStringCounter.addItem(resultString);
        
        //Add clause list to the set, it will not add duplicates
        seenResultStrings.add(resultString);
    }
    
    public static void addCounter(FrequencyCounter<Individual> counter){
    	Iterator iter = counter.keys();
    	while(iter.hasNext()){
    		Individual next = (Individual) iter.next();
    		addIndividuals(next, counter.getCount(next));
    	}
    }
    
    public static void addIndividuals(Individual individual, int count){
        individualCounts.addItem(individual, count);
        String resultString = evaluator.getResultString(individual);
        resultStringCounter.addItem(resultString, count);
        seenResultStrings.add(resultString);
    }
    
    public static Set<String> resultStrings() {
        return seenResultStrings;
    }
    
    public synchronized static void addBestIndividual(Individual individual) {
        bestIndividuals.add(individual);
    }
    
    public static Set<Individual> getBestIndividuals() {
        return bestIndividuals;
    }
    
    public static Individual getMostCommonIndividual() {
        int maxNumber = 0;
        Individual currentMaxIndividual = new Individual("0"); //default individual
        currentMaxIndividual.setGlobalFitness(0);
        for(Individual individual : individualCounts) {
            if(individualCounts.getCount(individual) > maxNumber) {
                maxNumber = individualCounts.getCount(individual);
                currentMaxIndividual = individual;
            }
        }
        return currentMaxIndividual;
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
