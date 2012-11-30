package landscapeEC.problem.sat.operators;

import java.util.ArrayList;
import java.util.List;

import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.util.SharedPRNG;


public class RandomSelection implements SelectionOperator {

    @Override
    public List<Individual> selectParents(List<Individual> population, IndividualComparator comparator) {
        List<Individual> parents = new ArrayList<Individual>();
        
        for(int i=0; i<2; i++) {
            parents.add(population.get(SharedPRNG.instance().nextInt(population.size())));
        }
        
        return parents;
    }

}
