package landscapeEC.problem.ecc;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Problem;
import landscapeEC.problem.onesmax.OnesMaxEvaluator;

public class EccProblem implements Problem {

    private int bitStringSize;
    
    // This problem is from page 214 of the Cellular Genetic Algorithms book.
    private int numberOfBits;
    private int numOfCodeWords;
    private int numOfBitsPerWord;
    
    public EccProblem(int numOfBitsPerWord, int numOfCodeWords){
        this.numOfBitsPerWord = numOfBitsPerWord;
        this.numOfCodeWords = numOfCodeWords;
        this.numberOfBits = numOfBitsPerWord*numOfCodeWords;
    }
    
    @Override
    public int getBitStringSize() {
        return bitStringSize;
    }

    public int getNumOfCodeWords(){
        return numOfCodeWords;
    }
    
    public int getNumOfBitsPerWord(){
        return numOfBitsPerWord;
    }
    
    @Override
    public Problem getSubProblem(double difficulty) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Evaluator getEvaluator() {
        return new EccEvaluator();
    }

    @Override
    public double getDifficulty() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
