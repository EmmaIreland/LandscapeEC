package landscapeEC.problem.sat;

import java.util.ArrayList;
import java.util.List;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public class SatEvaluator extends Evaluator {
    
    @Override
    public double doEvaluation(Problem problem, Individual individual) {
        SatInstance satInstance = (SatInstance) problem;
        if (satInstance.getNumClauses() == 0) {
            return 1.0;
        }

        int clausesSolved = 0;

        for (Clause clause : satInstance) {
            if (clause.satisfiedBy(individual)) {
                clausesSolved++;
            }
        }

        return (double) clausesSolved / satInstance.getNumClauses();
    }

    @Override
    public boolean solvesSubProblem(Individual individual, Problem locationProblem) {
        double fitness = evaluate(locationProblem, individual);
        int numClauses = ((SatInstance) locationProblem).getNumClauses();
        return 1 - fitness < 1/(2.0 * numClauses);
    }
    
    public List<Clause> getUnsolvedClauses(Individual individual, Problem locationProblem) {
        SatInstance satInstance = (SatInstance) locationProblem;
        List<Clause> unsolvedClauses = new ArrayList<Clause>();
        
        for (Clause clause : satInstance) {
            if (!clause.satisfiedBy(individual)) {
                unsolvedClauses.add(clause);
            }
        }
        
        return unsolvedClauses;
    }
    
    
    @Override
    public String getResultString(Problem problem, Individual individual) {
         SatInstance satInstance = (SatInstance) problem;
        if (satInstance.getNumClauses() == 0) {
            return "";
        }
        
        String bitString = "";

        for (Clause clause : satInstance) {
            if (clause.satisfiedBy(individual)) {
                bitString += "1";
            } else  {
                bitString += "0";
            }
        }
        
        return bitString;
    }

}
