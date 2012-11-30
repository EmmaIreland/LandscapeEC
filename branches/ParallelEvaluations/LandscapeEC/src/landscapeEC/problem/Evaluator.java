package landscapeEC.problem;

public abstract class Evaluator {
    
    private int numEvaluations = 0;
    private int numResets = 0;
    
    public double evaluate(Problem problem, Individual individual) {
        numEvaluations++;
        return doEvaluation(problem, individual);
    }
    
    public double evaluate(Individual individual) {
        numEvaluations++;
        return doEvaluation(GlobalProblem.getProblem(), individual);
    }
    
    //Method which evaluates the individual using a given problem, returns a fitness as a decimal from 0-1
    protected abstract double doEvaluation(Problem problem, Individual individual);
    
    public int getNumEvaluations() {
        return numEvaluations;
    }
    
    public void resetEvaluationsCounter() {
        numEvaluations = 0;
        numResets++;
    }

    public int getNumResets() {
        return numResets;
    }
    
    public abstract String getResultString(Problem problem, Individual individual);
    
    public String getResultString(Individual individual){
        return getResultString(GlobalProblem.getProblem(), individual);
    }

    public abstract boolean solvesSubProblem(Individual individual, Problem locationProblem);
    
}
