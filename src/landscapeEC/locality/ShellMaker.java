package landscapeEC.locality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShellMaker {
    //TODO: This doesn't account for toroidal worlds!
    //TODO: Currently, this may run off the end of the world!
    private World world;
    private Vector dimensions;
    private boolean toroidal;
    
    
    public ShellMaker(World world){
	this.world=world;
	this.dimensions=world.getDimensions();
	this.toroidal = world.isToroidal();
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
	process(result);
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
    
    private void process(List<Vector> input){
	if(toroidal){
	    //toroidal case goes here, eventually
	}
	else{
	    for(Vector v:input){
		v.nonToroidalNormalize(dimensions);
	    }
	    removeDuplicates(input);
	}
    }
    
    private void removeDuplicates(List<Vector> input){
	for(int i=0; i<input.size()-1;){
	    if(input.get(i).equals(input.get(i+1))){
		input.remove(i+1);
	    }
	    else{
		i++;
	    }
	}
    }
    
    private List<Vector> sort(List<Vector> input){
	return input;
	//TODO: Write sorting function
    }
}
