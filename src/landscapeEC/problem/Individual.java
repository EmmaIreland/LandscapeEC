package landscapeEC.problem;

import java.io.Serializable;

import landscapeEC.problem.sat.SatEvaluator;

public class Individual implements Serializable {
    private static final long serialVersionUID = 8709627944120749083L;
    private int[] bits;
    private double globalFitness = -1.0;

    public Individual(String bitString) {
        this(parseBits(bitString));
    }
    
    public Individual(String bitString, boolean evaluateOnCreation) {
        instantiate(parseBits(bitString), evaluateOnCreation);
    }

    private void instantiate(int[] newBits, boolean evaluateOnCreation) {
        this.bits = newBits.clone();
        if(evaluateOnCreation){
            globalFitness = GlobalProblem.getEvaluator().evaluate(this);
        }
    }
    
    
    private static int[] parseBits(String bitString) {
        int[] newBits = new int[bitString.length()];
        char[] chars = bitString.toCharArray();
        for(int i=0; i<chars.length; i++) {
            if(chars[i] == '0') newBits[i] = 0;
            else newBits[i] = 1;
        }
        return newBits;
    }
    
    public Individual(int[] bits) {
        instantiate(bits, true);
    }
    
    public Individual(int[] bits, boolean evaluateOnCreation) {
        instantiate(bits, evaluateOnCreation);
        
    }

    public int[] getBits() {
        return bits.clone();
    }
    
    public double getGlobalFitness() {
        return globalFitness;
    }
    
    public int getBit(int index) {
        return bits[index];
    }
    
    @Override
    public String toString() {
        String str = "";
        for(int i:bits) {
            str += i;
        }
        return str;
    }
}