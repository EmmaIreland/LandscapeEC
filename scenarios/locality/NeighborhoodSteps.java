package locality;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.steps.Steps;

public class NeighborhoodSteps extends Steps {
	private World world;

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
		
	}
}
