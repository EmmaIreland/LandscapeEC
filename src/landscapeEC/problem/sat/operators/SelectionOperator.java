package landscapeEC.problem.sat.operators;

import java.util.List;

import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;


public interface SelectionOperator {
    public List<Individual> selectParents(List<Individual> population, IndividualComparator comparator);
}
