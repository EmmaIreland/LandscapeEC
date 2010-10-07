package sat;

import util.SharedPRNG;

public class IndividualFactory {

	@Deprecated
    public static Individual getInstance(SatInstance satInstance) {
    	return getInstance(satInstance.getNumVariables());
    }
    
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
