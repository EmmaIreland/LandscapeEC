package SatGA;
import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.SatParser;

import org.jbehave.scenario.annotations.BeforeStory;
import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Named;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.steps.Steps;


public class EvaluationSteps extends Steps {
    private String bitString;
    private SatInstance satInstance;
    private SatEvaluator evaluator;

    @BeforeStory
    public void beforeScenario() {
        evaluator = new SatEvaluator();
    }
    
    @Given("a sat evaluator")
    public void constructParser() {
        //
    }
    
    @When("I have a bitstring of <bitstring>")
    public void getBitString(@Named("bitstring") String string) {
        bitString=string;
    }
    
    @When("I have this clauseList $clauseList")
    public void getSatInstance(String clauseList) throws IOException {
        SatParser satParser= new SatParser();
        StringReader stringReader = new StringReader(clauseList);
        this.satInstance = satParser.parseProblem(stringReader);
    }
    
    @Then("the fitness should be <fitness>")
    public void confirmEvaluation(@Named("fitness") double expectedFitness) {
        double actualFitness = evaluator.evaluate(satInstance, new Individual(bitString));
        
        assertEquals(expectedFitness, actualFitness);
    }
    
    @Then("the evaluation count should be <count>")
    public void confirmEvaluationCount(@Named("count") int expectedCount) {
        int actualCount = evaluator.getNumEvaluations();
        
        assertEquals(expectedCount, actualCount);
    }
}
