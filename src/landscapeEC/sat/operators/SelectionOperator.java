package landscapeEC.sat.operators;

import java.util.List;

import landscapeEC.sat.Individual;
import landscapeEC.sat.IndividualComparator;


public interface SelectionOperator {
    public List<Individual> selectParents(List<Individual> population, IndividualComparator comparator);
}
