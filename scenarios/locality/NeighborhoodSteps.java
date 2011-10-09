package locality;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import landscapeEC.locality.ShellMaker;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.parameters.GlobalParameters;

import static junit.framework.Assert.*;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.annotations.Pending;
import org.jbehave.core.steps.Steps;



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
        world = new World(dimensions, isToroidal);
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
    
    @When("I compute a shell of [$location] with radius $radius")
    @Pending
    public void getShell(String locationString, int radius) {
        String[] positionStrings = locationString.split(", *");

        Vector position = new Vector();
        for (String s : positionStrings) {
            position.add(Integer.parseInt(s));
        }
        ShellMaker shellMaker = new ShellMaker(world);
        neighborhood = shellMaker.makeShell(position, radius);
    }
    
    @Then("the result contains: $locations")
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
