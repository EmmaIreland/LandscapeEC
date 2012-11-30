package util;
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;


public class MockEvaluator extends Evaluator {

    //Used for testing purposes only
    
    @Override
    public double evaluate(Individual individual) {
        return 0.0;
    }
    
    @Override
    protected double doEvaluation(Problem problem, Individual individual) {
        return 0.0;
    }

    @Override
    public String getResultString(Problem problem, Individual individual) {
        return "";
    }

    @Override
    public boolean solvesSubProblem(Individual individual, Problem locationProblem) {
        return false;
    }
    
    
}
