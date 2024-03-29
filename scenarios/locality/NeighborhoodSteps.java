package locality;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import landscapeEC.locality.GridWorld;
import landscapeEC.locality.ShellMaker;
import landscapeEC.locality.Vector;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.sat.SatInstance;

import org.jbehave.scenario.annotations.AfterScenario;
import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.steps.Steps;



public class NeighborhoodSteps extends Steps {
    private GridWorld world;
    private List<Vector> neighborhood;

    @Given("a $toroid world of size [$size]")
    public void setupWorld(String toroidalFlag, String sizes) throws Exception {
    	File paramsFile = new File("properties/test.properties");
        GlobalParameters.setParameters(paramsFile);
        GlobalProblem.setProblem(new SatInstance(0.0));
    	
        String[] dimensionStrings = sizes.split(", *");
        Vector dimensions = new Vector();
        for (int i = 0; i < dimensionStrings.length; i++) {
            dimensions.add(Integer.parseInt(dimensionStrings[i]));
        }
        boolean isToroidal = toroidalFlag.equals("toroidal");
        world = new GridWorld(dimensions, isToroidal);
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
    
    
    @AfterScenario
    public void clearGlobalSatInstance() {
        GlobalProblem.unsetProblem();
    }
}
