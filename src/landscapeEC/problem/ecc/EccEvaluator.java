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
        numOfBitsPerWord = ((EccProblem) problem).getNumOfBitsPerWord();
        numOfCodeWords = ((EccProblem) problem).getNumOfCodeWords();
        individualString = individual.getBits();
        ArrayList<int[]> listOfCodeWords = new ArrayList<int[]>();
        
        for(int i=0; i<numOfCodeWords; i++){
            listOfCodeWords.add(Arrays.copyOfRange(individualString, numOfBitsPerWord*i, numOfBitsPerWord*(i+1)));
        }
        
        
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
