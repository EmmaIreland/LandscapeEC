package landscapeEC.problem.sat.operators;

import java.util.List;

import landscapeEC.problem.Individual;


public interface CrossoverOperator {
    public Individual crossover(List<Individual> parents);
}
