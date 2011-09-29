package sat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.problem.sat.Individual;
import landscapeEC.problem.sat.IndividualComparator;
import landscapeEC.problem.sat.IndividualFactory;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.operators.RandomSelection;
import landscapeEC.util.FrequencyCounter;

import org.junit.Test;

import util.ChiSquaredStatistics;

import static junit.framework.Assert.*;

public class RandomSelectionTest {    
    private static final int NUM_INDIVIDUALS = 10;
    private static final int NUM_VARIABLES = 30;
    private static final int NUM_TESTS = 100;

    @Test
    public void testRandomSelection() {    
        SatInstance satInstance = new SatInstance();
        satInstance.setNumVariables(NUM_VARIABLES);
        
        List<Individual> population = new ArrayList<Individual>();
        
        for(int i=0; i<NUM_INDIVIDUALS; i++) {
            population.add(IndividualFactory.getInstance(satInstance.getNumVariables()));
        }
        
        FrequencyCounter<Individual> counter = new FrequencyCounter<Individual>();
        
        RandomSelection selectionOperator = new RandomSelection();
        
        for(int i=0; i<NUM_TESTS; i++) {
            List<Individual> parents = selectionOperator.selectParents(population, new IndividualComparator(satInstance));
            counter.addItem(parents.get(0));
            counter.addItem(parents.get(1));
        }
        
        Map<Individual, Double> expectedDistribution = new HashMap<Individual,Double>();
        for(int i=0; i<NUM_INDIVIDUALS; i++) {
            expectedDistribution.put(population.get(i), 1.0/NUM_INDIVIDUALS);
        }
        
        assertTrue("Mutation distribution " + counter + " does not match expected distribution " + expectedDistribution + " with confidence.",
                ChiSquaredStatistics.chiSquaredTest(counter, expectedDistribution));
    }
}
