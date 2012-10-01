package landscapeEC.problem.ecc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public class EccEvaluator extends Evaluator {

    int numOfBitsPerWord;
    int numOfCodeWords;
    private Map<Individual, String> resultStrings = new HashMap<Individual, String>();
    
    @Override
    protected double doEvaluation(Problem problem, Individual individual) {
        numOfBitsPerWord = ((EccProblem) problem).getNumOfBitsPerWord();
        numOfCodeWords = ((EccProblem) problem).getNumOfCodeWords();
        int[] individualString = individual.getBits();
        ArrayList<int[]> listOfCodeWords = new ArrayList<int[]>();
        
        for(int i=0; i<numOfCodeWords; i++){
            listOfCodeWords.add(Arrays.copyOfRange(individualString, numOfBitsPerWord*i, numOfBitsPerWord*(i+1)));
        }
        
        
        double sumOfHamm = 0;
        List<Integer> resultList = new ArrayList<Integer>();
        
        for(int i=0; i<numOfCodeWords; i++){
            for(int j=0; j<numOfCodeWords; j++){
                if(i != j){
                    double hamm = hammingDistance(listOfCodeWords.get(i), listOfCodeWords.get(j));
                    if(hamm >= numOfBitsPerWord/4) {
                        resultList.add(1);
                    } else {
                        resultList.add(0);
                    }
                    sumOfHamm += 1/(hamm*hamm);
                }
            }
        }
        
        Collections.sort(resultList);
        resultStrings.put(individual, convertIntArrayToString(resultList));
        
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
        return resultStrings.get(individual);
//        int[] individualString = individual.getBits();
//        String resultString = convertIntArrayToString(individualString);
//        return resultString;
    }

    private String convertIntArrayToString(List<Integer> bitArray) {
        String resultString = "";
        for (int b : bitArray) {
            if(b==0){
                resultString+="0";
            } else {
                resultString+="1";
            }
        }
        return resultString;
    }

    @Override
    public boolean solvesSubProblem(Individual individual, Problem locationProblem) {
        return true;
    }

}
