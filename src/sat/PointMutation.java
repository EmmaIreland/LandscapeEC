package sat;

import parameters.DoubleParameter;
import util.SharedPRNG;

public class PointMutation implements MutationOperator {

    @Override
    public Individual mutate(Individual ind) {
        String bitString = ind.getBitString();
        StringBuilder newBitString = new StringBuilder();

        for (int i = 0; i < bitString.length(); i++) {
            char character = bitString.charAt(i);
            if (SharedPRNG.instance().nextDouble() < DoubleParameter.MUTATION_RATE
                    .getValue()) {
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
