package landscapeEC.problem.onesmax;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public class OnesMaxEvaluator extends Evaluator {
    
    public final static double EPSILON = 0.00001;

    @Override
    protected double doEvaluation(Problem problem, Individual individual) {
        OnesMaxProblem onesMaxProblem = (OnesMaxProblem) problem;
        int numBits = onesMaxProblem.getBitStringSize();
        
        if (numBits == 0) {
            return 1.0;
        }
        
        int onesCount = 0;
        int[] bits = individual.getBits();
        for(int i = 0; i < numBits; i++) {
            if (bits[i] == 1) {
                onesCount++;
            }
        }
        
        return onesCount/(double)numBits;
    }

    @Override
    public String getResultString(Problem problem, Individual individual) {
        Double result = doEvaluation(problem, individual);
        return result.toString();
    }

    @Override
    public boolean solvesSubProblem(Individual individual, Problem locationProblem) {
        return Math.abs(doEvaluation(locationProblem, individual) - 1.0) < EPSILON;
    }

}
