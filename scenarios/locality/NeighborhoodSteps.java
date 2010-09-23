package locality;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.steps.Steps;

public class NeighborhoodSteps extends Steps {
	private World world;
	private List<Location> neighborhood;

	@Given("a $toroid world of size [$size]")
	public void setupWorld(String toroidalFlag, String sizes) {
		String[] dimensionStrings = sizes.split(", *");
		int[] dimensions = new int[dimensionStrings.length];
		for(int i = 0; i < dimensionStrings.length; i++) {
			dimensions[i] = Integer.parseInt(dimensionStrings[i]);
		}
		boolean isToroidal = "toroidal".equals(toroidalFlag);
		world = new World(dimensions, isToroidal);
	}
	
	@When("I compute the neighborhood of [$location] with radius $radius")
	public void getNeighborhood(String locationString, int radius) {
	    String[] positionStrings = locationString.split(", *");
	    
	    List<Integer> position = new ArrayList<Integer>();
	    for (String s : positionStrings) {
	        position.add(Integer.parseInt(s));
	    }
	    
	    neighborhood = world.getNeighborhood(position, radius);
	}
	
	@Then("The neighborhood contains: $locations")
	public void testNeighborhoodCorrect(String locations) {
	    String[] locationStrings = locations.split("\n");
	    
	    List<Location> expectedNeighborhood = new ArrayList<Location>();
	    
	    String positionString;
	    String[] positionStrings;
	    for(String locationString : locationStrings) {
	        positionString = locationString.replaceAll("\\[|\\]", "");
	        positionStrings = positionString.split(", *");

	        List<Integer> position = new ArrayList<Integer>();
	        for (String s : positionStrings) {
	            position.add(Integer.parseInt(s));
	        }
	        
	        expectedNeighborhood.add(world.getLocation(position));
	    }
	    
	    assertEquals(expectedNeighborhood, neighborhood);
	}
}
