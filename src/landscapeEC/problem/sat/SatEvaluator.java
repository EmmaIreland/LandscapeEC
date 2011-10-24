package landscapeEC.problem.sat;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
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

    public boolean solvesSubProblem(Individual individual, Problem locationProblem) {
        double fitness = evaluate(locationProblem, individual);
        int numClauses = ((SatInstance) locationProblem).getNumClauses();
        int correctClauses = (int) Math.round(fitness * numClauses);
        boolean satisfiesAllClauses = numClauses == correctClauses;
        return satisfiesAllClauses;
    }
    
    // Not Generic, can't be used in GA run
//    public static void printUnsolvedClauses(Individual individual) {
//        SatInstance satInstance = GlobalSatInstance.getInstance();
//        if (satInstance.getNumClauses() == 0) {
//            return;
//        }
//
//        System.out.print("Unsolved Clause IDs:");
//
//        int i = 0;
//        for (Clause clause : satInstance) {
//            if (!clause.satisfiedBy(individual)) {
//                System.out.print(clause.getId() + " ");
//            }
//            i++;
//        }
//        System.out.print("\n");
//    }
    
    
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
