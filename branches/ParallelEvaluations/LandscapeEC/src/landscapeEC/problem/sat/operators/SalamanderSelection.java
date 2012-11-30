package landscapeEC.problem.sat.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.util.SharedPRNG;



public class SalamanderSelection implements SelectionOperator {

    private static final int NUMBER_OF_PARENTS = 2;
    private static final float noiseRatio = 50;
    private int noisyCopies = 0;
    private int actualTournaments=0;
    //noiseRatio controls the amount of noise noisyCopy will introduce.
    //A lower value means that the copy will be closer to the original.
    //A higher value means that the copy will be closer to a random bitstring.

    @Override
    public List<Individual> selectParents(List<Individual> inputPopulation, IndividualComparator comparator) {
        List<Individual> population = new ArrayList<Individual>(inputPopulation);
        List<Individual> parents = new ArrayList<Individual>();
        
        int tournamentSize = IntParameter.TOURNAMENT_SIZE.getValue();
        
        for(int i=0; i<NUMBER_OF_PARENTS && !population.isEmpty(); i++) {
            Set<Individual> set = new LinkedHashSet<Individual>();
            while(set.size() < tournamentSize && !population.isEmpty()) {
                Individual individual = population.remove(SharedPRNG.instance().nextInt(population.size()));
                set.add(individual);
            }
            
            Individual winner = Collections.max(set, comparator);
            
            parents.add(winner);
        }
        if(parents.size() != NUMBER_OF_PARENTS){
            parents.add(noisyCopy(parents.get(0)));
            return parents;
        } else if(Arrays.equals(parents.get(0).getBits(), parents.get(1).getBits())){
            parents.remove(1);
            parents.add(noisyCopy(parents.get(0)));
            return parents;
        }
        else{
            actualTournaments++;
            return parents;
        } 
    }
    
    private Individual noisyCopy(Individual original){
        noisyCopies++;
        if(noisyCopies %500==0){
            System.out.println("Actual/Noisy :"+(double)((double)actualTournaments/(double)noisyCopies));
        }
        int[] bits = original.getBits();
        for(int i=0; i<bits.length; i++){
            if(SharedPRNG.instance().nextInt(100) < noiseRatio){
                bits[i]=SharedPRNG.instance().nextInt(1);
            }
        }
        Individual result = new Individual(bits);
        return result;
    }

}
