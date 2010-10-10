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
    private List<Position> neighborhood;

    @Given("a $toroid world of size [$size]")
    public void setupWorld(String toroidalFlag, String sizes) {
        String[] dimensionStrings = sizes.split(", *");
        Integer[] dimensions = new Integer[dimensionStrings.length];
        for (int i = 0; i < dimensionStrings.length; i++) {
            dimensions[i] = Integer.parseInt(dimensionStrings[i]);
        }
        boolean isToroidal = toroidalFlag.equals("toroidal");
        world = new World(dimensions, isToroidal);
    }

    @When("I compute the neighborhood of [$location] with radius $radius")
    public void getNeighborhood(String locationString, int radius) {
        String[] positionStrings = locationString.split(", *");

        Position position = new Position();
        for (String s : positionStrings) {
            position.add(Integer.parseInt(s));
        }

        neighborhood = world.getNeighborhood(position, radius);
    }

    @Then("the neighborhood contains: $locations")
    public void testNeighborhoodCorrect(String locations) {
        String[] locationStrings = locations.split("\n");

        List<Position> expectedNeighborhood = new ArrayList<Position>();

        String positionString;
        String[] positionStrings;
        for (String locationString : locationStrings) {
            positionString = locationString.replaceAll("\\[|\\]", "");
            positionStrings = positionString.split(", *");

            Position position = new Position();
            for (String s : positionStrings) {
                position.add(Integer.parseInt(s.trim()));
            }

            expectedNeighborhood.add(position);
        }

        assertTrue("Actual neighborhood = " + neighborhood, expectedNeighborhood.containsAll(neighborhood));
        assertEquals(expectedNeighborhood.size(), neighborhood.size());
    }
}
