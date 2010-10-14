package observers;

import locality.World;
import sat.SatEvaluator;
import sat.SatInstance;

public interface Observer {
    public void generationData(int generationNumber, World world, SatInstance satInstance);
}
