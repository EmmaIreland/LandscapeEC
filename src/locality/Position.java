package locality;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;

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

    public Position minusToAll(int a) {
        Position newPosition = new Position();
        for (Integer i : coordinates) {
            newPosition.add(i - a);
        }
        return newPosition;
    }

    public Position plusToAll(int a) {
        Position newPosition = new Position();
        for (Integer i : coordinates) {
            newPosition.add(i + a);
        }
        return newPosition;
    }
    
    public Position maxWithZero() {
    	Position newPosition = new Position();
    	for (Integer i : coordinates) {
    		newPosition.add(Math.max(i, 0));
    	}
    	return newPosition;
    }
    
    public Position pairwiseMin(Integer[] pos) {
    	Position newPosition = new Position();
    	for (int i = 0; i < coordinates.size(); i++) {
    		newPosition.add(Math.min(get(i), pos[i]));
    	}
    	return newPosition;
    }
    
	public Position minus(Position pos) {
		Position newPosition = new Position();
    	for (int i = 0; i < coordinates.size(); i++) {
    		newPosition.add(get(i) - pos.get(i));
    	}
    	return newPosition;
	}
	
	public Position plus(Position pos) {
		Position newPosition = new Position();
    	for (int i = 0; i < coordinates.size(); i++) {
    		newPosition.add(get(i) + pos.get(i));
    	}
    	return newPosition;
	}

	public Position pairwiseMod(Integer[] pos) {
		Position newPosition = new Position();
    	for (int i = 0; i < coordinates.size(); i++) {
    		int modValue = get(i) % pos[i];
    		if (modValue < 0) {
    			modValue += pos[i];
    		}
			newPosition.add(modValue);
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

    @Override
    public String toString() {
    	return coordinates.toString();
    }

}
