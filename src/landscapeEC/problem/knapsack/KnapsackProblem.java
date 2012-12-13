package landscapeEC.problem.knapsack;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Problem;

public class KnapsackProblem implements Problem {
    
    private int weightLimit;
    private int[] values;
    private int[] weights;

    public KnapsackProblem(int weightLimit, int[] values, int[] weights) {
        this.weightLimit = weightLimit;
        this.values = values;
        this.weights = weights;
    }
    
    @Override
    public int getBitStringSize() {
        return values.length;
    }
    
    public int getWeightLimit(){
    	return weightLimit;
    }
    
    public int[] getValues(){
    	return values.clone();
    }
    
    public int[] getWeights(){
    	return weights.clone();
    }

    @Override
    public Problem getSubProblem(double difficulty) {
        return this;
    }

    @Override
    public Evaluator getEvaluator() {
        return new KnapsackEvaluator();
    }

    @Override
    public double getDifficulty() {
        return 1;
    }

}
