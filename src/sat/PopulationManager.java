package sat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PopulationManager {

    public List<Individual> getElite(List<Individual> individuals, double eliteProportion,
            SatInstance satInstance, SatEvaluator satEvaluator) {
        IndividualComparator comparator = new IndividualComparator(satInstance, satEvaluator);
        Collections.sort(individuals, comparator);
        
        List<Individual> result = new ArrayList<Individual>();
        int eliteSize = (int)Math.ceil(individuals.size()*eliteProportion);
        for(int i=0; i<eliteSize; i++) {
            result.add(individuals.get(i));
        }
        return result;
    }

}
