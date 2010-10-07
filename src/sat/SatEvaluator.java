package sat;

public class SatEvaluator {

	private int numEvalutations = 0;

	public double evaluate(SatInstance satInstance, Individual individual) {
		ClauseList clauseList = satInstance.getClauseList();

		int clausesSolved = 0;

		for(Clause clause:clauseList) {
			if(clause.satisfiedBy(individual)) clausesSolved++;
		}

		numEvalutations++;

		return (double)clausesSolved/satInstance.getNumClauses();
	}

	public int getNumEvaluations() {
		return numEvalutations;
	}
	
	public void resetEvaluationsCounter() {
		numEvalutations = 0;
	}
}
