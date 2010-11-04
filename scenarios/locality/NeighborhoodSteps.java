package locality;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.apache.commons.io.FileUtils;
import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.steps.Steps;

import parameters.GlobalParameters;

import sat.SatInstance;

public class NeighborhoodSteps extends Steps {
    private World world;
    private List<Vector> neighborhood;

    @Given("a $toroid world of size [$size]")
    public void setupWorld(String toroidalFlag, String sizes) throws Exception {
    	File paramsFile = new File("properties/test.properties");
        GlobalParameters.setParameters(paramsFile);
    	
        String[] dimensionStrings = sizes.split(", *");
        Vector dimensions = new Vector();
        for (int i = 0; i < dimensionStrings.length; i++) {
            dimensions.add(Integer.parseInt(dimensionStrings[i]));
        }
        boolean isToroidal = toroidalFlag.equals("toroidal");
        world = new World(dimensions, isToroidal, new SatInstance());
    }

    @When("I compute the neighborhood of [$location] with radius $radius")
    public void getNeighborhood(String locationString, int radius) {
        String[] positionStrings = locationString.split(", *");

        Vector position = new Vector();
        for (String s : positionStrings) {
            position.add(Integer.parseInt(s));
        }

        neighborhood = world.getNeighborhood(position, radius);
    }

    @Then("the neighborhood contains: $locations")
    public void testNeighborhoodCorrect(String locations) {
        String[] locationStrings = locations.split("\n");

        List<Vector> expectedNeighborhood = new ArrayList<Vector>();

        String positionString;
        String[] positionStrings;
        for (String locationString : locationStrings) {
            positionString = locationString.replaceAll("\\[|\\]", "");
            positionStrings = positionString.split(", *");

            Vector position = new Vector();
            for (String s : positionStrings) {
                position.add(Integer.parseInt(s.trim()));
            }

            expectedNeighborhood.add(position);
        }

        assertTrue("Actual neighborhood = " + neighborhood, expectedNeighborhood.containsAll(neighborhood));
        assertEquals(expectedNeighborhood.size(), neighborhood.size());
    }
}
