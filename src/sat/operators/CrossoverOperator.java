package sat.operators;

import java.util.List;

import sat.Individual;

public interface CrossoverOperator {
    public Individual crossover(List<Individual> parents);
}
