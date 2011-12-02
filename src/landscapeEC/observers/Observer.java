package landscapeEC.observers;

import landscapeEC.locality.GridWorld;

public interface Observer {
	public void generationData(int generationNumber, GridWorld world, int successes);
}
