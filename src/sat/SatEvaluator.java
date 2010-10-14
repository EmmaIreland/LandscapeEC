package sat;

public class SatEvaluator {

	private static int numEvalutations = 0;
	private static int numResets = 0;
	
	public static double evaluate(SatInstance satInstance, Individual individual) {
		ClauseList clauseList = satInstance.getClauseList();
		if(clauseList.getNumClauses() == 0) {
			return 1.0;
		}
		
		int clausesSolved = 0;

		for(Clause clause:clauseList) {
			if(clause.satisfiedBy(individual)) clausesSolved++;
		}

		numEvalutations++;
		
		return (double)clausesSolved/satInstance.getNumClauses();
	}

	public static int getNumEvaluations() {
		return numEvalutations;
	}
	
	public static void resetEvaluationsCounter() {
		numEvalutations = 0;
		numResets++;
	}
	
	public static int getNumResets() {
		return numResets;
	}
}
