package sat.operators;

import parameters.DoubleParameter;
import sat.Individual;
import util.SharedPRNG;

public class PointMutation implements MutationOperator {

    @Override
    public Individual mutate(Individual ind) {
        String bitString = ind.getBitString();
        StringBuilder newBitString = new StringBuilder();

        double mutationRate = DoubleParameter.AVERAGE_MUTATIONS.getValue()/bitString.length();
        
        for (int i = 0; i < bitString.length(); i++) {
            char character = bitString.charAt(i);
            if (SharedPRNG.instance().nextDouble() < mutationRate) {
                newBitString.append(flipBit(character));
            } else {
                newBitString.append(character);
            }
        }

        return new Individual(bitString.toString());
    }

    private String flipBit(char character) {
        if (character == '0')
            return "1";
        return "0";
    }
}
