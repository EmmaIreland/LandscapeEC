package landscapeEC.problem;

public interface Problem {

    public int getBitStringSize();
    
    public Problem getSubProblem(double difficulty);
    
    public Evaluator getEvaluator();
    
    public double getDifficulty();
}
