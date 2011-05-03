package landscapeEC.sat;

import java.io.Serializable;
import java.util.Arrays;

public class Individual implements Serializable {
    private int[] bits;
    private double globalFitness = -1.0;

    public Individual(String bitString) {
        int[] newBits = new int[bitString.length()];
        char[] chars = bitString.toCharArray();
        for(int i=0; i<chars.length; i++) {
            if(chars[i] == '0') newBits[i] = 0;
            else newBits[i] = 1;
        }
        this.bits = newBits;
    }
    
    public Individual(int[] bits) {
        this.bits = bits.clone();
    }

    public int[] getBits() {
        return bits.clone();
    }
    
    public void setGlobalFitness(SatInstance globalInstance) {
        double oldFitness = globalFitness;
        globalFitness = SatEvaluator.evaluate(globalInstance, this);
        if (oldFitness >= 0 && Math.abs(oldFitness - globalFitness) > 1e-5) {
            System.out.println("We're re-computing global fitness!");
            System.out.println("Old fitness = " + oldFitness + " and new fitness = " + globalFitness);
            System.out.println("Instance = " + globalInstance);
        }
    }
    
    public double getGlobalFitness() {
        return globalFitness;
    }
    
    public double getGlobalFitness(SatInstance globalInstance) {
        //TODO FIND OUT WHY THIS WORKS WHEN IT SHOULD NOT BE DIFFERENT
//        double oldFitness = globalFitness;
//        globalFitness = SatEvaluator.evaluate(globalInstance, this);
//        if (oldFitness >= 0 && Math.abs(oldFitness - globalFitness) > 1e-5) {
//            System.out.println("Old fitness was " + oldFitness + " and new fitness is " + globalFitness);
//            System.out.println(globalFitness);
//        }
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