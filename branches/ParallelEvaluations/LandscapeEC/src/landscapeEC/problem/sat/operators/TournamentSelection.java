package landscapeEC.problem.sat.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.util.SharedPRNG;



public class TournamentSelection implements SelectionOperator {

    private static final int NUMBER_OF_PARENTS = 2;

    @Override
    public List<Individual> selectParents(List<Individual> population, IndividualComparator comparator) {
        List<Individual> parents = new ArrayList<Individual>();
        
        int tournamentSize = IntParameter.TOURNAMENT_SIZE.getValue();
        
        for(int i=0; i<NUMBER_OF_PARENTS; i++) {
            Set<Individual> set = new LinkedHashSet<Individual>();
            while(set.size() < tournamentSize) {
                Individual individual = population.get(SharedPRNG.instance().nextInt(population.size()));
                set.add(individual);
            }
            
            Individual winner = Collections.max(set, comparator);
            
            parents.add(winner);
        }
        
        return parents;
    }

}
