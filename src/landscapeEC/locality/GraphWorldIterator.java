package landscapeEC.locality;

import java.util.Iterator;

public class GraphWorldIterator implements Iterator<Location<Integer>> {

	private GraphWorld world;
	private Integer current; 
	
	public GraphWorldIterator(int uncheckedStart, int uncheckedEnd, GraphWorld world) {
            if (uncheckedStart > uncheckedEnd){
                throw new IllegalArgumentException("Bad range for Location Iterator");
            }
            this.world = world;
            current = uncheckedStart;
	}
	
	
	public GraphWorldIterator(GraphWorld world) {
		this(0, world.getNumLocations(), world);
	}
	
	@Override
	public boolean hasNext() {
		return world.has(current);
	}

	@Override
	public Location<Integer> next() {
		current++;
		return world.getLocation(current-1);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove is not supported on Location Iterators");
		
	}


}