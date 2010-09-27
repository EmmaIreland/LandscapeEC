package locality;

import java.util.Iterator;

public class LocationIterator implements Iterator<Position> {

    private Position start, end, current;
    private int numDimensions;
    private World world;

    public LocationIterator(Position uncheckedStart, Position uncheckedEnd, World world) {
        for (int i = 0; i < uncheckedStart.size(); i++) {
            if (uncheckedStart.get(i) > uncheckedEnd.get(i)) {
                throw new IllegalArgumentException(
                        "Bad range for Location Iterator");
            }
        }
        
        this.world = world;

        if (world.isToroidal()) {
        	start = uncheckedStart;
        	end = uncheckedStart.plus(uncheckedEnd.minus(uncheckedStart).pairwiseMin(world.getDimensions()));
        } else {
        	start = uncheckedStart.maxWithZero();
        	end = uncheckedEnd.pairwiseMin(world.getDimensions());
        }
        this.current = new Position(start);
        this.numDimensions = start.size();
    }

    public LocationIterator(Position position, int radius, World world) {
        this(position.minusToAll(radius), position.plusToAll(radius+1), world);
    }

    @Override
    public boolean hasNext() {
        if (current.get(numDimensions - 1) >= end.get(numDimensions - 1)) {
            return false;
        }
        return true;
    }

    @Override
    public Position next() {
    	//We want to return the first position before we increment it, so
    	//result will always be the previous iteration. This way we don't have to initialize
    	//'start' to be start-1.
        Position result = new Position(current);

        current.set(0, current.get(0) + 1);

        for (int i = 0; i < numDimensions - 1; i++) {
            if (current.get(i) >= end.get(i)) {
                current.set(i, start.get(i));
                current.set(i + 1, current.get(i + 1) + 1);
            }
        }
        
        return result.pairwiseMod(world.getDimensions());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "Remove is not supported on Location Iterators");
    }

}
