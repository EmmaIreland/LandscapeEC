package sat.operators;

import java.util.ArrayList;
import java.util.List;

import sat.Individual;
import util.SharedPRNG;

public class RandomSelection implements SelectionOperator {

    @Override
    public List<Individual> selectParents(List<Individual> population) {
        List<Individual> parents = new ArrayList<Individual>();
        for(int i=0; i<2; i++) {
            parents.add(population.get(SharedPRNG.instance().nextInt(population.size())));
        }
        return parents;
    }

}
