package landscapeEC.problem.sat.operators;

import landscapeEC.problem.sat.Individual;

public interface MutationOperator {
    public Individual mutate(Individual ind);

}
