package sat.operators;

import java.util.List;

import sat.Individual;

public interface SelectionOperator {
    public List<Individual> selectParents(List<Individual> population);
}
