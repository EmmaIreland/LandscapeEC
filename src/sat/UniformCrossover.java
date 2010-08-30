package sat;

import java.util.List;

import util.SharedPRNG;

public class UniformCrossover implements CrossoverOperator {

    @Override
    public Individual crossover(List<Individual> parents) {
        if(parents.size() != 2) throw new IllegalArgumentException("Wrong number of parents");
        
        String parentAString = parents.get(0).getBitString();
        String parentBString = parents.get(1).getBitString();
        
        StringBuilder childString = new StringBuilder();
        
        for(int i=0; i<parentAString.length(); i++) {
            if(SharedPRNG.instance().nextBoolean()) {
                childString.append(parentAString.charAt(i));
            } else {
                childString.append(parentBString.charAt(i));
            }
        }
        
        return new Individual(childString.toString());
    }

}
