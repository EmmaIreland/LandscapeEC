package landscapeEC.problem.ecc;

import java.util.ArrayList;
import java.util.Arrays;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public class EccEvaluator extends Evaluator {

    int numOfBitsPerWord;
    int numOfCodeWords;
    int[] individualString;
    
    @Override
    protected double doEvaluation(Problem problem, Individual individual) {
        int numOfBitsPerWord = ((EccProblem) problem).getNumOfBitsPerWord();
        int numOfCodeWords = ((EccProblem) problem).getNumOfCodeWords();
        int[] individualString = individual.getBits();
        ArrayList<int[]> listOfCodeWords = new ArrayList<int[]>();
        
        for(int i=0; i<numOfCodeWords; i++){
            listOfCodeWords.set(i, Arrays.copyOfRange(individualString, numOfBitsPerWord*i, numOfBitsPerWord*(i+1)-1));
        }
        
        int minHamming = findMinHammingDistance(listOfCodeWords);
        
        double sumOfHamm = 0;
        
        for(int i=0; i<numOfCodeWords; i++){
            for(int j=0; j<numOfCodeWords; j++){
                if(i != j){
                    double hamm = hammingDistance(listOfCodeWords.get(i), listOfCodeWords.get(j));
                    sumOfHamm += 1/(hamm*hamm);
                }
            }
        }
        
        
        return 1/sumOfHamm;
    }

    
    private int findMinHammingDistance(ArrayList<int[]> listOfCodeWords) {
        int minHamming = numOfBitsPerWord;
        
        for (int i = 0; i < numOfCodeWords; i++) {
            for (int j = i+1; j < numOfCodeWords; j++) {
                int currHamming = hammingDistance(listOfCodeWords.get(i), listOfCodeWords.get(j));
                if (currHamming < minHamming) {
                    minHamming = currHamming;
                }
            }
        }
        
        return minHamming;
    }
    

    private int hammingDistance(int[] codeWord1, int[] codeWord2){
        int count = 0;
        for(int i=0; i<numOfBitsPerWord; i++){
            if(codeWord1[i] != codeWord2[i]){
                count++;
            }
        }
        return count;
    }
    
    @Override
    public String getResultString(Problem problem, Individual individual) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean solvesSubProblem(Individual individual, Problem locationProblem) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
