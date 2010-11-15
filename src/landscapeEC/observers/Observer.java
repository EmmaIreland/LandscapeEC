package landscapeEC.observers;

import landscapeEC.locality.World;
import landscapeEC.sat.SatInstance;

public interface Observer {
	public void generationData(int generationNumber, World world,
			SatInstance satInstance, int successes);
}
