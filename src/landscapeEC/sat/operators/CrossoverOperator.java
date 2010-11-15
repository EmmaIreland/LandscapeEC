package landscapeEC.sat.operators;

import java.util.List;

import landscapeEC.sat.Individual;


public interface CrossoverOperator {
    public Individual crossover(List<Individual> parents);
}
