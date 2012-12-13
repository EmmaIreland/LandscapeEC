package landscapeEC.problem.sat.operators;

import java.util.List;
import java.util.Random;

import landscapeEC.problem.Individual;
import landscapeEC.util.SharedPRNG;


public class UniformCrossover implements CrossoverOperator {

    private Random prngInstance = SharedPRNG.instance();

    @Override
    public Individual crossover(List<Individual> parents) {
        if(parents.size() != 2) throw new IllegalArgumentException("Wrong number of parents");
        
        int[] parentA = parents.get(0).getBits();
        int[] parentB = parents.get(1).getBits();
        
        int[] child = new int[parentA.length];
        for(int i=0; i<parentA.length; i++) {
            if(prngInstance.nextBoolean()) {
                child[i] = parentA[i];
            } else {
                child[i] = parentB[i];
            }
        }
        
        return new Individual(child, false);
    }

}
