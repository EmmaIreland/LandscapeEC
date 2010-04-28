package sat;

public class SatEvaluator {

    public double evaluate(SatInstance satInstance, Individual individual) {
        ClauseList clauseList = satInstance.getClauseList();
        
        double fitness = 0;
        
        for(Clause clause:clauseList) {
            if(clause.satisfiedBy(individual)) fitness++;
        }
        
        return fitness/satInstance.getNumClauses();
    }

}
