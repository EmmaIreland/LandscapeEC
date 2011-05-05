package landscapeEC.sat;

import java.io.Serializable;

public class Individual implements Serializable {
    private static final long serialVersionUID = 8709627944120749083L;
    private int[] bits;
    private double globalFitness = -1.0;

    public Individual(String bitString) {
        this(parseBits(bitString));
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
        this.bits = bits.clone();
        globalFitness = SatEvaluator.evaluate(this);
    }

    public int[] getBits() {
        return bits.clone();
    }
    
    public double getGlobalFitness() {
        return globalFitness;
    }

    public final boolean valueIsFalse(Literal literal) {
        return bits[literal.variable] == 0;
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