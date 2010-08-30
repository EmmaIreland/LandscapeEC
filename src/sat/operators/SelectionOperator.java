package sat.operators;

import java.util.List;

import sat.Individual;
import sat.IndividualComparator;

public interface SelectionOperator {
    public List<Individual> selectParents(List<Individual> population, IndividualComparator comparator);
}
