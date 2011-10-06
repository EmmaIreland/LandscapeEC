package landscapeEC.problem.sat.operators;

import landscapeEC.parameters.DoubleParameter;
import landscapeEC.problem.Individual;
import landscapeEC.util.SharedPRNG;

public class PointMutation implements MutationOperator {

    @Override
    public Individual mutate(Individual ind) {
        int[] bits = ind.getBits();

        double mutationRate = DoubleParameter.AVERAGE_MUTATIONS.getValue()/bits.length;
        
        for (int i = 0; i < bits.length; i++) {
            if (SharedPRNG.instance().nextDouble() < mutationRate) {
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
