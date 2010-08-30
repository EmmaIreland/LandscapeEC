package sat;

public class SatEvaluator {

    public double evaluate(SatInstance satInstance, Individual individual) {
        ClauseList clauseList = satInstance.getClauseList();
        
        int clausesSolved = 0;
        
        for(Clause clause:clauseList) {
            if(clause.satisfiedBy(individual)) clausesSolved++;
        }
        
        return (double)clausesSolved/satInstance.getNumClauses();
    }

}
