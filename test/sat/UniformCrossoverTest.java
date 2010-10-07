package sat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static junit.framework.Assert.*;

import sat.operators.UniformCrossover;
import util.ChiSquaredStatistics;
import util.FrequencyCounter;

public class UniformCrossoverTest {
    private static final int NUM_BITS = 9;
    private static final int NUM_TESTS = 100;
    
    @Test
    public void testUniformCrossover() {        
        SatInstance satInstance = new SatInstance();
        satInstance.setNumVariables(NUM_BITS);

        FrequencyCounter<Integer> counter = new FrequencyCounter<Integer>();
        
        for(int m=0; m<NUM_TESTS; m++) {
            List<Individual> parents = new ArrayList<Individual>();
            parents.add(new Individual(generateStringOfZeroes(NUM_BITS)));
            parents.add(new Individual(generateStringOfOnes(NUM_BITS)));
            
            UniformCrossover crossoverOperator = new UniformCrossover();
            Individual child = crossoverOperator.crossover(parents);
            
            for(int b=0; b<NUM_BITS; b++) {
                counter.addItem(child.getBits()[b]);
            }
        }
        
        Map<Integer, Double> expectedDistribution = new HashMap<Integer,Double>();
        expectedDistribution.put(0, 0.5);
        expectedDistribution.put(1, 0.5);
        
        assertTrue("Mutation distribution " + counter + " does not match expected distribution " + expectedDistribution + " with confidence.",
                ChiSquaredStatistics.chiSquaredTest(counter, expectedDistribution));
    }
    
    private String generateStringOfZeroes(int length) {
        StringBuilder string = new StringBuilder();
        while(string.length() < length) string.append("0");
        return string.toString();
    }
    
    private String generateStringOfOnes(int length) {
        StringBuilder string = new StringBuilder();
        while(string.length() < length) string.append("1");
        return string.toString();
    }
}
