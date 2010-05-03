package sat;

import java.util.Comparator;

public class IndividualComparator implements Comparator<Individual> {
    private SatInstance satInstance;
    private SatEvaluator satEvaluator;
    
    public IndividualComparator(SatInstance satInstance, SatEvaluator satEvaluator) {
        this.satInstance = satInstance;
        this.satEvaluator = satEvaluator;
    }

    @Override
    public int compare(Individual individual1, Individual individual2) {
        double fitness1 = satEvaluator.evaluate(satInstance, individual1);
        double fitness2 = satEvaluator.evaluate(satInstance, individual2);
        if(fitness1 < fitness2) return 1;
        if(fitness2 < fitness1) return -1;
        return 0;
    }
}
