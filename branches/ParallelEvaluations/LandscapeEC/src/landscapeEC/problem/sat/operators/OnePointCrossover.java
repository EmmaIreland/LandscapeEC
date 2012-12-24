package landscapeEC.problem.sat.operators;

import java.util.List;

import landscapeEC.problem.Individual;
import landscapeEC.util.SharedPRNG;

public class OnePointCrossover implements CrossoverOperator {

    @Override
    public Individual crossover(List<Individual> parents) {
        if(parents.size() != 2) throw new IllegalArgumentException("Wrong number of parents");
        
        int[] parentA = parents.get(0).getBits();
        int[] parentB = parents.get(1).getBits();
        
        int[] child = new int[parentA.length];
        
        int point = SharedPRNG.instance().nextInt(parentA.length-2)+1;
        // We add some constants onto parent length so that we can keep
        // the point being selected from being at one end of the parents'
        // strings - producing a child who is a clone of one of its parents.

        if(SharedPRNG.instance().nextBoolean()){	    // I don't like code duplication any more
            for(int i=0; i<point; i++){			    // than the next guy, but without a way to
                child[i] = parentA[i];			    // alter memory addresses directly, I don't
            }				                    // see a way to perform this 'flip' without
            for(int i = point; i<parentA.length; i++){	    // having to copy an array item-by-item
            	child[i] = parentB[i];			    // each time crossover is performed.
            }					            // 
        } else {					    // At least this way, the function is doubled
            for(int i=0; i<point; i++){		            // in length, but at an O(1) increase in
            	child[i] = parentB[i];	                    // computational complexity.
            }						    //
            for(int i = point; i<parentA.length; i++){	    // Improvements welcome!
            	child[i] = parentA[i];
            }
        }
        return new Individual(child);
    }
}
