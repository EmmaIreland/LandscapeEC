package landscapeEC.problem;

public class GlobalProblem {
    private static Problem globalProblem;
    private static ParallelEvaluator evaluator;
    private static Evaluator evaluatorType;
    
    public static void setProblem(Problem problem) {
        globalProblem = problem;
    }
    
    public static void unsetProblem() {
    	globalProblem = null;
    	evaluator = null;
    }
    
    public static Problem getProblem() {
        if (globalProblem == null) {
            throw new RuntimeException("There was no global problem set.");
        }
        return globalProblem;
    }
    
    public static void setEvaluator(ParallelEvaluator anEvaluator) {
        evaluator = anEvaluator;
    }
    
    public static ParallelEvaluator getEvaluator() {
        if (evaluator == null) {
            throw new RuntimeException("There was no global evaluator set.");
        }
        return evaluator;
    }
    
    public static void setEvaluatorType(Evaluator anEvaluator) {
    	evaluatorType = anEvaluator;
    }
    
    public static Evaluator getEvaluatorType() {
    	return evaluatorType;
    }
}
