package sat;

import util.SharedPRNG;

public class IndividualFactory {

    public Individual getInstance(SatInstance satInstance) {
       int length = satInstance.getNumVariables();
       
       StringBuilder bitString = new StringBuilder();
       
       for(int i=0; i<length; i++){
           bitString.append(makeBit());
       }
       
       Individual individual = new Individual(bitString.toString());
       
       return individual;
    }

    private String makeBit() {
        if (SharedPRNG.instance().nextBoolean()) {
            return "1";
        }
        return "0";
    }

}
