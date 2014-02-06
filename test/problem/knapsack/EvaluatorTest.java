package problem.knapsack;

import static org.junit.Assert.assertEquals;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.knapsack.KnapsackEvaluator;
import landscapeEC.problem.knapsack.KnapsackProblem;

import org.junit.Test;

public class EvaluatorTest {

    @Test
    public void testKnapsack(){
        KnapsackEvaluator evaluator = new KnapsackEvaluator();
        GlobalProblem.setEvaluator(evaluator);
        
        int weightLimit = 10;
        int[] values = {2, 4, 6, 8};
        int[] weights = {1, 3, 7, 5};
        KnapsackProblem problem = new KnapsackProblem(weightLimit, values, weights);
        
        GlobalProblem.setProblem(problem);
        Individual individual = new Individual("1111");
        assertEquals(14, evaluator.evaluate(problem, individual), 0);
    }
    
    @Test
    public void testKnapsack2(){
        KnapsackEvaluator evaluator = new KnapsackEvaluator();
        GlobalProblem.setEvaluator(evaluator);
        
        int weightLimit = 8;
        int[] values = {2, 4, 6, 8};
        int[] weights = {1, 9, 5, 13};
        KnapsackProblem problem = new KnapsackProblem(weightLimit, values, weights);
        
        GlobalProblem.setProblem(problem);
        Individual individual = new Individual("1010");
        assertEquals(8, evaluator.evaluate(problem, individual), 0);
    }
    
    @Test
    public void testEmptyKnapsack(){
        KnapsackEvaluator evaluator = new KnapsackEvaluator();
        GlobalProblem.setEvaluator(evaluator);
        
        int weightLimit = 5;
        int[] values = {2, 4, 6, 8};
        int[] weights = {6, 7, 8, 9};
        KnapsackProblem problem = new KnapsackProblem(weightLimit, values, weights);
        
        GlobalProblem.setProblem(problem);
        Individual individual = new Individual("1111");
        assertEquals(0, evaluator.evaluate(problem, individual), 0);
    }
    
}
