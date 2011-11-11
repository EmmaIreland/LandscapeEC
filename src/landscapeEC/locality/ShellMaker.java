package landscapeEC.locality;

import java.util.ArrayList;
import java.util.List;

public class ShellMaker {
    private World world;
    private Vector dimensions;

    public ShellMaker(World world) {
	this.world = world;
	this.dimensions = world.getDimensions();
    }

    public List<Vector> makeShell(final Vector position, int radius) {
	List<Vector> result = new ArrayList<Vector>();
	Vector currentPosition = new Vector(position.getCoordinates());
	for (int lockedDimension = 0; lockedDimension < dimensions.size(); lockedDimension++) {
	    int original = position.get(lockedDimension);
	    currentPosition.set(lockedDimension, original - radius);
	    result.addAll(leftSolve(currentPosition, radius, lockedDimension, 0));

	    currentPosition.set(lockedDimension, original + radius);
	    result.addAll(leftSolve(currentPosition, radius, lockedDimension, 0));

	    currentPosition.set(lockedDimension, original);
	}
	result = process(result);
	
	return result;
    }

    private List<Vector> leftSolve(final Vector position, int inputRadius,
	    int lockedDimension, int current) {
	List<Vector> result = new ArrayList<Vector>();
	if (current < lockedDimension) {
	    int radius = inputRadius - 1;
	    int original = position.get(current);
	    Vector currentPosition = new Vector(position.getCoordinates());
	    for (int i = original - radius; i <= original + radius; i++) {
		currentPosition.set(current, i);
		result.addAll(leftSolve(currentPosition, inputRadius,
			lockedDimension, current + 1));
	    }
	} else {
	    result
		    .addAll(rightSolve(position, inputRadius,
			    lockedDimension + 1));
	}
	return result;
    }

    private List<Vector> rightSolve(final Vector position, int radius,
	    int current) {
	List<Vector> result = new ArrayList<Vector>();
	if (current < dimensions.size()) {
	    int original = position.get(current);
	    Vector currentPosition = new Vector(position.getCoordinates());
	    for (int i = original - radius; i <= original + radius; i++) {
		currentPosition.set(current, i);
		result.addAll(rightSolve(currentPosition, radius, current + 1));
	    }
	} else {
	    result.add(position.clone());
	}
	return result;
    }

    private List<Vector> process(List<Vector> input) {
	List<Vector> result = new ArrayList<Vector>();
	if (world.isToroidal()) {
	    for (Vector v : input) {
		result.add(v.mod(dimensions));
	    }
	    removeDuplicates(result);
	} else {
	    for (Vector v : input) {
		if (isValid(v)) {
		    result.add(v);
		}
	    }
	    removeDuplicates(result);
	}
	return result;
    }

    private void removeDuplicates(List<Vector> input) {
	List<Vector> seen = new ArrayList<Vector>();
	for (int i = 0; input.size() > seen.size();) {
	    if (seen.contains(input.get(i))) {
	        //System.out.println("Removing a point: "+input.get(i).toString());
		input.remove(i);
	    } else {
		seen.add(input.get(i));
		i++;
	    }
	}
    }

    private boolean isValid(Vector v) {
	for (int i = 0; i < dimensions.size(); i++) {
	    if (v.get(i) < 0 || v.get(i) > dimensions.get(i)-1) {
		return false;
	    }
	}
	return true;
    }
}
