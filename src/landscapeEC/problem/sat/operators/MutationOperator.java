package landscapeEC.problem.sat.operators;

import landscapeEC.locality.Location;
import landscapeEC.problem.Individual;

public interface MutationOperator {
    public Individual mutate(Individual ind, Object... parameters);
}
