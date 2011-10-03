package landscapeEC.locality;

import java.util.ArrayList;
import java.util.List;

public class ShellMaker {
    private World world;
    private Vector dimensions;
    private boolean isToroidal;
    
    public ShellMaker(World world){
	this.world=world;
	this.dimensions=world.getDimensions();
	this.isToroidal = world.isToroidal();
    }
    
    public List<Vector> makeShell(final Vector position, int radius){
	List<Vector> result = new ArrayList<Vector>();
	Vector currentPosition = new Vector(position.getCoordinates());
	for(int lockedDimension = 0; lockedDimension < dimensions.size(); lockedDimension++){
	    int original = position.get(lockedDimension);
	    currentPosition.set(lockedDimension, original-radius);
	    result.addAll(leftSolve(currentPosition, radius, lockedDimension, 0));
	    
	    currentPosition.set(lockedDimension, original+radius);
	    result.addAll(leftSolve(currentPosition, radius, lockedDimension, 0));
	    
	    currentPosition.set(lockedDimension, original);
	}
	System.out.println(result.toString());
	return result;
    }
    
    private List<Vector> leftSolve(final Vector position, int inputRadius, int lockedDimension, int current){
	List<Vector> result = new ArrayList<Vector>();
	if(current<lockedDimension){
	    int radius = inputRadius-1;
	    int original = position.get(current);
	    Vector currentPosition = new Vector(position.getCoordinates());
	    for(int i = original-radius; i<=original+radius; i++){
		currentPosition.set(current, i);
		result.addAll(leftSolve(currentPosition, inputRadius, lockedDimension, current+1));
	    }
	}
	else{
	    result.addAll(rightSolve(position, inputRadius, lockedDimension+1));
	}
	return result;
    }
    
    private List<Vector> rightSolve(final Vector position, int radius, int current){
	List<Vector> result = new ArrayList<Vector>();
	if(current<dimensions.size()){
	    int original = position.get(current);
	    Vector currentPosition = new Vector(position.getCoordinates());
	    for(int i = original-radius; i<=original+radius; i++){
		currentPosition.set(current, i);
		result.addAll(rightSolve(currentPosition, radius, current+1));
	    }
	}
	else{
	    result.add(position.clone());
	}
	return result;
    }
}
