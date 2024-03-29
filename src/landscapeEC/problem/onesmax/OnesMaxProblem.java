package landscapeEC.problem.onesmax;

import java.io.Serializable;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Problem;

public class OnesMaxProblem implements Problem, Serializable {
    private static final long serialVersionUID = -2160444805760035225L;
    private int bitStringSize;
    private double difficulty;
    
    public OnesMaxProblem(int bitStringSize) {
        this.bitStringSize = bitStringSize;
        difficulty = 1.0;
    }

    public OnesMaxProblem(int bitStringSize, double difficulty) {
        this.bitStringSize = bitStringSize;
        this.difficulty = difficulty;
    }
    
    @Override
    public int getBitStringSize() {
        return bitStringSize;
    }

    @Override
    public Problem getSubProblem(double difficulty) {
        int numBits = (int) Math.ceil(bitStringSize * difficulty);
        double newDifficulty = this.difficulty*difficulty;
        
        return new OnesMaxProblem(numBits, newDifficulty);
    }

    @Override
    public Evaluator getEvaluator() {
        return new OnesMaxEvaluator();
    }

    @Override
    public double getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString() {
        return "<Requires first " + bitStringSize + " bits>";
    }
}
