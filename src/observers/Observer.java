package observers;

import locality.World;
import sat.SatInstance;

public interface Observer {
    public void generationData(int generationNumber, World world, SatInstance satInstance, int successes);
}
