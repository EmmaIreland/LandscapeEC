package sat;
import java.io.StringReader;

import static junit.framework.Assert.*;

import org.jbehave.scenario.steps.Steps;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.annotations.Then;

import sat.SatInstance;
import sat.SatParser;

public class Parse3SatSteps extends Steps {
    private SatParser satParser;
    private SatInstance satInstance;

    @Given("a 3-SAT parser")
    public void constructParser() {
        satParser = new SatParser();
    }
    
    @When("I parse this 3-SAT instance $instance")
    public void parseInstance(String instance) {
        StringReader reader = new StringReader(instance);
        satInstance = satParser.parseInstance(reader);
    }
    
    @Then("the number of variables is $numVariables")
    public void confirmNumberOfVariables(int numVariables) {
        assertEquals(numVariables, satInstance.getNumVariables());
    }
}
