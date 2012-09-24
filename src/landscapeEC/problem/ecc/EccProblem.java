package landscapeEC.problem.ecc;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Problem;
import landscapeEC.problem.onesmax.OnesMaxEvaluator;

public class EccProblem implements Problem {

    private int bitStringSize;
    
    private int numOfCodeWords;
    private int numOfBitsPerWord;

    private EccEvaluator eccEvaluator;
    
    public EccProblem(int numOfBitsPerWord, int numOfCodeWords){
        this.numOfBitsPerWord = numOfBitsPerWord;
        this.numOfCodeWords = numOfCodeWords;
        bitStringSize = numOfBitsPerWord*numOfCodeWords;
        eccEvaluator = new EccEvaluator();
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
        return this;
    }

    @Override
    public Evaluator getEvaluator() {
        return eccEvaluator;
    }

    @Override
    public double getDifficulty() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
