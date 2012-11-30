package landscapeEC.problem;

import landscapeEC.util.SharedPRNG;

public class IndividualFactory {
    
    public static Individual getInstance(int numBits) {
        int length = numBits;
        
        int[] bits = new int[length];
        
        for(int i=0; i<length; i++){
            bits[i] = makeBit();
        }
        
        Individual individual = new Individual(bits);
        
        return individual;    	
    }

    private static int makeBit() {
        if (SharedPRNG.instance().nextBoolean()) {
            return 1;
        }
        return 0;
    }

}
