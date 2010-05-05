package sat;

import parameters.IntParameter;
import util.SharedPRNG;

public class IndividualFactory {

    public Individual getInstance() {
       int length = IntParameter.INDIVIDUAL_LENGTH.getValue();
       
       
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
        } else {
            return "0";
        }
    }

}
