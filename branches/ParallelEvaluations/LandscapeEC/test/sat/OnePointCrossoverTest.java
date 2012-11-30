package sat;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.operators.OnePointCrossover;
import landscapeEC.util.FrequencyCounter;

import org.junit.Test;

import util.ChiSquaredStatistics;

public class OnePointCrossoverTest {
    private static final int NUM_BITS = 9;
    private static final int NUM_TESTS = 100;
    
    @Test
    public void testOnePointCrossover() {        
        SatInstance satInstance = new SatInstance(1.0);
        satInstance.setNumVariables(NUM_BITS);
        GlobalProblem.setProblem(satInstance);

        FrequencyCounter<Integer> counter = new FrequencyCounter<Integer>();
        
        for(int m=0; m<NUM_TESTS; m++) {
            List<Individual> parents = new ArrayList<Individual>();
            parents.add(new Individual(generateStringOfZeroes(NUM_BITS), false));
            parents.add(new Individual(generateStringOfOnes(NUM_BITS), false));
            
            OnePointCrossover crossoverOperator = new OnePointCrossover();
            Individual child = crossoverOperator.crossover(parents);
            
            int i=0;
            int checker = child.getBit(0);
            System.out.println(child.toString());
            while(child.getBit(i)==checker && i<NUM_BITS){
                System.out.println(child.getBit(i));
            	i++;
            }
            assertTrue((checker == 1 && child.getBit(i) == 0) || (checker == 0) && child.getBit(i) == 1);
            checker=child.getBit(i);
            while(i<NUM_BITS){
            	assertTrue(checker==child.getBit(i));
            	i++;
            }
        }
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

