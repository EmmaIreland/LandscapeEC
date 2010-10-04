package observers;

import locality.World;
import sat.IndividualComparator;
import sat.SatEvaluator;
import sat.SatInstance;

public interface Observer {
    public void generationData(World world, SatEvaluator satEvaluator, SatInstance satInstance);
}
