package problem.ecc;

import static org.junit.Assert.*;

import org.junit.Test;

import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.ParallelEvaluator;
import landscapeEC.problem.ecc.EccEvaluator;
import landscapeEC.problem.ecc.EccProblem;

public class EvaluatorTest {

    @Test
    public void testIdenticalStrings(){
        EccEvaluator evaluator = new EccEvaluator();
        GlobalProblem.setEvaluatorType(evaluator);
        GlobalProblem.setEvaluator(new ParallelEvaluator());
        EccProblem problem = new EccProblem(4, 2);
        GlobalProblem.setProblem(problem);
        Individual allZeros = new Individual("00000000");
        assertEquals(0, evaluator.evaluate(problem, allZeros), .001);
    }
    
    @Test
    public void testHammingDistance3(){
        EccEvaluator evaluator = new EccEvaluator();
        GlobalProblem.setEvaluatorType(evaluator);
        GlobalProblem.setEvaluator(new ParallelEvaluator());
        EccProblem problem = new EccProblem(4,2);
        GlobalProblem.setProblem(problem);
        Individual individual = new Individual("00011111");
        assertEquals(4.5, evaluator.evaluate(problem, individual), .001);
    }
    
    @Test
    public void testHammingDistance2(){
        EccEvaluator evaluator = new EccEvaluator();
        GlobalProblem.setEvaluatorType(evaluator);
        GlobalProblem.setEvaluator(new ParallelEvaluator());
        EccProblem problem = new EccProblem(4,2);
        GlobalProblem.setProblem(problem);
        Individual individual = new Individual("10100011");
        assertEquals(2, evaluator.evaluate(problem, individual), .001);
    }
    
    @Test
    public void testFourCodeWords(){
        EccEvaluator evaluator = new EccEvaluator();
        GlobalProblem.setEvaluatorType(evaluator);
        GlobalProblem.setEvaluator(new ParallelEvaluator());
        EccProblem problem = new EccProblem(4,4);
        GlobalProblem.setProblem(problem);
        Individual individual = new Individual("0000111101010010");
        assertEquals(72.0/257, evaluator.evaluate(problem, individual), .001);
    }
    
}
