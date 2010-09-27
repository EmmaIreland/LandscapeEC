package locality;

import java.util.ArrayList;
import java.util.List;

public class Position {
    public List<Integer> coordinates;

    public Position() {
        coordinates = new ArrayList<Integer>();
    }

    public Position(List<Integer> coordinates) {
        this.coordinates = coordinates;
    }

    public Position(Integer[] integers) {
        coordinates = new ArrayList<Integer>();
        for (Integer i : integers) {
            coordinates.add(i);
        }
    }

    public Position(Position start) {
        coordinates = new ArrayList<Integer>(start.coordinates);
    }

    public Position minus(int a) {
        Position newPosition = new Position();
        for (Integer i : newPosition.coordinates) {
            newPosition.add(i - a);
        }
        return newPosition;
    }

    public Position plus(int a) {
        Position newPosition = new Position();
        for (Integer i : newPosition.coordinates) {
            newPosition.add(i + a);
        }
        return newPosition;
    }

    public void add(Integer i) {
        coordinates.add(i);
    }

    public int size() {
        return coordinates.size();
    }

    public int get(int i) {
        return coordinates.get(i);
    }

    public void set(int index, int element) {
        coordinates.set(index, element);
    }

    @Override
    public int hashCode() {
        return coordinates.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            return coordinates.equals(((Position) o).coordinates);
        }
        return false;
    }
}
