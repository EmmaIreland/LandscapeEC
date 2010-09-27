package locality;

import java.util.Iterator;

public class LocationIterator implements Iterator<Position> {

    private Position start, end, current;
    private int numDimensions;

    public LocationIterator(Position start, Position end) {
        for (int i = 0; i < start.size(); i++) {
            if (start.get(i) > end.get(i)) {
                throw new IllegalArgumentException(
                        "Bad range for Location Iterator");
            }
        }

        this.start = start;
        this.end = end;
        this.current = new Position(start);
        this.numDimensions = start.size();
    }

    public LocationIterator(Position position, int radius) {
        this(position.minus(radius), position.plus(radius));
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
        Position result = new Position(current);

        current.set(0, current.get(0) + 1);

        for (int i = 0; i < numDimensions - 1; i++) {
            if (current.get(i) >= end.get(i)) {
                current.set(i, start.get(i));
                current.set(i + 1, current.get(i + 1) + 1);
            }
        }

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "Remove is not supported on Location Iterators");
    }

}
