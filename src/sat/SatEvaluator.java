package sat;

public class SatEvaluator {

    private static int numEvaluations = 0;
    private static int numResets = 0;

    public static double evaluate(SatInstance satInstance, Individual individual) {
        ClauseList clauseList = satInstance.getClauseList();
        if (clauseList.getNumClauses() == 0) {
            return 1.0;
        }

        int clausesSolved = 0;

        for (Clause clause : clauseList) {
            if (clause.satisfiedBy(individual))
                clausesSolved++;
        }

        numEvaluations++;

        return (double) clausesSolved / satInstance.getNumClauses();
    }

    public static int getNumEvaluations() {
        return numEvaluations;
    }

    public static void resetEvaluationsCounter() {
        numEvaluations = 0;
        numResets++;
    }

    public static int getNumResets() {
        return numResets;
    }

    public static void printUnsolvedClauses(SatInstance satInstance, Individual individual) {
        ClauseList clauseList = satInstance.getClauseList();
        if (clauseList.getNumClauses() == 0) {
            return;
        }

        System.out.print("Didn't solve... ");

        int i = 0;
        for (Clause clause : clauseList) {
            if (!clause.satisfiedBy(individual)) {
                System.out.print(i + " ");
            }
            i++;
        }
    }
}
