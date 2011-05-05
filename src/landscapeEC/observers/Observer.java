package landscapeEC.observers;

import landscapeEC.locality.World;

public interface Observer {
	public void generationData(int generationNumber, World world, int successes);
}
