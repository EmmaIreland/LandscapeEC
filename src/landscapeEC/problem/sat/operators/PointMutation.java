package landscapeEC.problem.sat.operators;

import java.util.Random;

import landscapeEC.parameters.DoubleParameter;
import landscapeEC.problem.Individual;
import landscapeEC.util.SharedPRNG;

public class PointMutation implements MutationOperator {

    private Random prngInstance = SharedPRNG.instance();

    @Override
    public Individual mutate(Individual ind, Object... parameters) {
        int[] bits = ind.getBits();

        double mutationRate = DoubleParameter.AVERAGE_MUTATIONS.getValue()/bits.length;

        for (int i = 0; i < bits.length; i++) {
            if (prngInstance.nextDouble() < mutationRate) {
                bits[i] = flipBit(bits[i]);
            }
        }

        return new Individual(bits);
    }

    private int flipBit(int bit) {
        if (bit == 0)
            return 1;
        return 0;
    }
}
