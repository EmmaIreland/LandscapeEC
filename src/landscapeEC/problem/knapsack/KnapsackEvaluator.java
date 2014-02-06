package landscapeEC.problem.knapsack;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public class KnapsackEvaluator extends Evaluator {

    int weightLimit;
    int[] values;
    int[] weights;
    
    @Override
    protected double doEvaluation(Problem problem, Individual individual) {
        weightLimit = ((KnapsackProblem) problem).getWeightLimit();
        values = ((KnapsackProblem) problem).getValues();
        weights = ((KnapsackProblem) problem).getWeights();
        int[] bits = individual.getBits();
        
        int currentWeight = 0;
        int currentValue = 0;
        for(int i = 0; i < values.length; i++){
            if(bits[i] == 1 && (currentWeight + weights[i]) <= weightLimit){
                currentWeight = currentWeight + weights[i];
        	currentValue = currentValue + values[i];
            }
        }
        return currentValue;
        
    }

    @Override
    public String getResultString(Problem problem, Individual individual) {
        StringBuilder bits = new StringBuilder();
        for(int b: individual.getBits()){
            bits.append(b);
        }
        return bits.toString();
    }

    @Override
    public boolean solvesSubProblem(Individual individual,
            Problem locationProblem) {
        return true;
    }

}