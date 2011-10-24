package landscapeEC.problem;

import java.io.Serializable;
import java.util.Comparator;

public class IndividualComparator implements Comparator<Individual>, Serializable{
    private static final long serialVersionUID = 6890926228504302829L;
    private static IndividualComparator instance = new IndividualComparator();
    
    private IndividualComparator() {
        //private constructor
    }
    
    @Override
    public int compare(Individual individual1, Individual individual2) {
        double fitness1 = individual1.getGlobalFitness();
        double fitness2 = individual2.getGlobalFitness();
        if(fitness1 > fitness2) return 1;
        if(fitness1 < fitness2) return -1;
        return 0;
    }
    
    public static IndividualComparator getComparator() {
        return instance;
    }
}
