package sat;

import util.SharedPRNG;

public class IndividualFactory {

	@Deprecated
    public static Individual getInstance(SatInstance satInstance) {
    	return getInstance(satInstance.getNumVariables());
    }
    
    public static Individual getInstance(int numBits) {
        int length = numBits;
        
        StringBuilder bitString = new StringBuilder();
        
        for(int i=0; i<length; i++){
            bitString.append(makeBit());
        }
        
        Individual individual = new Individual(bitString.toString());
        
        return individual;    	
    }

    private static String makeBit() {
        if (SharedPRNG.instance().nextBoolean()) {
            return "1";
        }
        return "0";
    }

}
