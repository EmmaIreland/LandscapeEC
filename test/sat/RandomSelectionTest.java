package sat;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.IndividualFactory;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.operators.RandomSelection;
import landscapeEC.util.FrequencyCounter;

import org.junit.Test;

import util.ChiSquaredStatistics;

public class RandomSelectionTest {    
    private static final int NUM_INDIVIDUALS = 10;
    private static final int NUM_VARIABLES = 30;
    private static final int NUM_TESTS = 100;

    @Test
    public void testRandomSelection() {    
        SatInstance satInstance = new SatInstance(1.0);
        satInstance.setNumVariables(NUM_VARIABLES);
        GlobalProblem.setProblem(satInstance);
        Evaluator satEvaluator = new SatEvaluator();
        GlobalProblem.setEvaluator(satEvaluator);
        
        List<Individual> population = new ArrayList<Individual>();
        
        for(int i=0; i<NUM_INDIVIDUALS; i++) {
            population.add(IndividualFactory.getInstance(satInstance.getBitStringSize()));
        }
        
        FrequencyCounter<Individual> counter = new FrequencyCounter<Individual>();
        
        RandomSelection selectionOperator = new RandomSelection();
        
        for(int i=0; i<NUM_TESTS; i++) {
            List<Individual> parents = selectionOperator.selectParents(population, IndividualComparator.getComparator());
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
