package landscapeEC.problem;

public class GlobalProblem {
    private static Problem globalProblem;
    private static Evaluator evaluator;
    
    public static void setProblem(Problem problem) {
        globalProblem = problem;
    }
    
    public static Problem getProblem() {
        if (globalProblem == null) {
            throw new RuntimeException("There was no global problem set.");
        }
        return globalProblem;
    }
    
    public static void setEvaluator(Evaluator anEvaluator) {
        evaluator = anEvaluator;
    }
    
    public static Evaluator getEvaluator() {
        if (evaluator == null && globalProblem == null) {
            throw new RuntimeException("There was no global evaluator set.");
        } else if (evaluator == null && globalProblem != null) {
            evaluator = globalProblem.getEvaluator();
        }
        return evaluator;
    }
}
