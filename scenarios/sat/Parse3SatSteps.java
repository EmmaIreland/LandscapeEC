package sat;
import java.io.IOException;
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
    public void parseInstance(String instance) throws IOException {
        StringReader reader = new StringReader(instance);
        satInstance = satParser.parseInstance(reader);
    }
    
    @Then("the number of variables is $numVariables")
    public void confirmNumberOfVariables(int numVariables) {
        assertEquals(numVariables, satInstance.getNumVariables());
    }
    
    @Then("the number of clauses is $numClauses")
    public void confirmNumberOfClauses(int numClauses) {
        assertEquals(numClauses, satInstance.getNumClauses());
    }

    @Then("the instance contains these clauses")
    public void confirmCNFInstance(String clauses) {
        
    }
}
