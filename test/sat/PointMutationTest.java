package sat;

import java.util.Map;

import org.junit.Test;

import static junit.framework.Assert.*;

import parameters.GlobalParameters;
import sat.operators.PointMutation;
import util.ChiSquaredStatistics;
import util.FrequencyCounter;
import util.MockParameters;
import util.ProbabilityUtils;

public class PointMutationTest {
    private static final double AVERAGE_MUTATIONS = 1;
    private static final int NUM_BITS = 9;
    private static final int NUM_TESTS = 100;
    
    @Test
    public void testPointMutation() {
        MockParameters mockParams = new MockParameters();
        mockParams.put("AVERAGE_MUTATIONS", ""+AVERAGE_MUTATIONS);
        GlobalParameters.setParameters(mockParams);
        
        SatInstance satInstance = new SatInstance();
        satInstance.setNumVariables(NUM_BITS);

        FrequencyCounter<Integer> counter = new FrequencyCounter<Integer>();
        
        for(int m=0; m<NUM_TESTS; m++) {
            Individual individual = new Individual(generateStringOfZeroes(NUM_BITS));
            
            PointMutation mutationOperator = new PointMutation();
            individual = mutationOperator.mutate(individual);
            
            int bitsFlipped = 0;
            for(int b=0; b<NUM_BITS; b++) {
                if(individual.getBitString().charAt(b) == '1') bitsFlipped++;
            }
            counter.addItem(bitsFlipped);
        }
        
        Map<Integer, Double> expectedDistribution = ProbabilityUtils.binomialDistribution(NUM_BITS, AVERAGE_MUTATIONS/NUM_BITS);
        
        assertTrue("Mutation distribution " + counter + " does not match expected distribution " + expectedDistribution + " with confidence.",
                ChiSquaredStatistics.chiSquaredTest(counter, expectedDistribution));
    }
    
    private String generateStringOfZeroes(int length) {
        StringBuilder string = new StringBuilder();
        while(string.length() < length) string.append("0");
        return string.toString();
    }
}
