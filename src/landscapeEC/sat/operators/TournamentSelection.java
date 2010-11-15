package landscapeEC.sat.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import landscapeEC.parameters.IntParameter;
import landscapeEC.sat.Individual;
import landscapeEC.sat.IndividualComparator;
import landscapeEC.util.SharedPRNG;



public class TournamentSelection implements SelectionOperator {

    @Override
    public List<Individual> selectParents(List<Individual> population, IndividualComparator comparator) {
        List<Individual> parents = new ArrayList<Individual>();
        
        int tournamentSize = IntParameter.TOURNAMENT_SIZE.getValue();
        
        for(int i=0; i<2; i++) {
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
