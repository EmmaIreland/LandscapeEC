package landscapeEC.problem;

public abstract class Evaluator {
    
    private static int numEvaluations = 0;
    private static int numResets = 0;
    
    //Method which evaluates the individual using a given problem, returns a fitness as a decimal from 0-1
    public abstract double evaluate(Problem problem, Individual individual);
    
    public static int getNumberEvaluations() {
        return numEvaluations;
    }
    
    public static void resetEvaluationsCounter() {
        numEvaluations = 0;
        numResets++;
    }

    public static int getNumResets() {
        return numResets;
    }
    
}
