package landscapeEC.locality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vector implements Serializable {
    public List<Integer> coordinates;

    public Vector() {
        coordinates = new ArrayList<Integer>();
    }

    public Vector(List<Integer> coordinates) {
        this.coordinates = coordinates;
    }

    public Vector(Integer[] integers) {
        coordinates = new ArrayList<Integer>();
        for (Integer i : integers) {
            coordinates.add(i);
        }
    }

    public Vector(Vector start) {
        coordinates = new ArrayList<Integer>(start.coordinates);
    }

    public static Vector origin(int numDimensions) {
        List<Integer> zeros = new ArrayList<Integer>();
        for (int i = 0; i < numDimensions; ++i) {
            zeros.add(0);
        }
        return new Vector(zeros);
    }

    public Vector minusToAll(int a) {
        Vector newPosition = new Vector();
        for (Integer i : coordinates) {
            newPosition.add(i - a);
        }
        return newPosition;
    }

    public Vector plusToAll(int a) {
        Vector newPosition = new Vector();
        for (Integer i : coordinates) {
            newPosition.add(i + a);
        }
        return newPosition;
    }

    public Vector maxWithZero() {
        Vector newPosition = new Vector();
        for (Integer i : coordinates) {
            newPosition.add(Math.max(i, 0));
        }
        return newPosition;
    }

    public Vector pairwiseMin(Integer[] pos) {
        Vector newPosition = new Vector();
        for (int i = 0; i < coordinates.size(); i++) {
            newPosition.add(Math.min(get(i), pos[i]));
        }
        return newPosition;
    }

    public Vector minus(Vector pos) {
        Vector newPosition = new Vector();
        for (int i = 0; i < coordinates.size(); i++) {
            newPosition.add(get(i) - pos.get(i));
        }
        return newPosition;
    }

    public Vector plus(Vector pos) {
        Vector newPosition = new Vector();
        for (int i = 0; i < coordinates.size(); i++) {
            newPosition.add(get(i) + pos.get(i));
        }
        return newPosition;
    }

    public Vector pairwiseMod(Integer[] pos) {
        Vector newPosition = new Vector();
        for (int i = 0; i < coordinates.size(); i++) {
            int modValue = get(i) % pos[i];
            if (modValue < 0) {
                modValue += pos[i];
            }
            newPosition.add(modValue);
        }
        return newPosition;
    }

    public Vector min(Vector pos) {
        Vector newPosition = new Vector();
        for (int i = 0; i < coordinates.size(); i++) {
            newPosition.add(Math.min(get(i), pos.get(i)));
        }
        return newPosition;
    }

    public Vector mod(Vector v) {
        Vector newPosition = new Vector();
        for (int i = 0; i < coordinates.size(); i++) {
            int modValue = get(i) % v.get(i);
            if (modValue < 0) {
                modValue += v.get(i);
            }
            newPosition.add(modValue);
        }
        return newPosition;
    }
    
    /**
     * Returns the midpoint between one vector and another.
     * Rounds downwards to the nearest integer coordinate.
     */
    public Vector getMidPoint(Vector vector) {
    	Vector newPosition = new Vector();
        for (int i = 0; i < coordinates.size(); i++) {
        	int middle = (int) Math.floor((this.get(i) + vector.get(i))/2.0);
            newPosition.add(middle);
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

    public List<Integer> getCoordinates() {
        return new ArrayList<Integer>(coordinates);
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
        if (o instanceof Vector) {
            return coordinates.equals(((Vector) o).coordinates);
        }
        return false;
    }

    @Override
    public String toString() {
        return coordinates.toString();
    }

    public int manhattanLength() {
        int distance = 0;
        for (int coordinate : coordinates) {
            distance += Math.abs(coordinate);
        }
        return distance;
    }

    /**
     * Combines the first vector's x-position and the second vector's y-position
     * to make a new vector. To get the opposite corner, reverse the order of the
     * arguments. For example: (1, 1) and (5, 5) makes (1, 5)
     */
    public static Vector getCorner(Vector horizontalPos, Vector verticalPos) {
    	Vector cornerVect = new Vector();
    	cornerVect.add(horizontalPos.get(0));
    	cornerVect.add(verticalPos.get(1));
    	return cornerVect;
    }
    
    @Override
    public Vector clone(){
    	Vector result = new Vector(this.getCoordinates());
    	return result;
    }
    
    public void nonToroidalNormalize(Vector worldDimensions){
	this.coordinates = this.maxWithZero().getCoordinates();
	this.coordinates = this.min(worldDimensions).getCoordinates();
	
    }

}
