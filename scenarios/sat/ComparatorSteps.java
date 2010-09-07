package sat;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Named;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.steps.Steps;

public class ComparatorSteps extends Steps {
    private SatInstance satInstance;
    private Individual a;
    private Individual b;
    private SatEvaluator satEvaluator;
    
    @Given("a SAT evaluator")
    public void constructSATEvaluator() {
        satEvaluator = new SatEvaluator();
    }
    
    @When("I have this clauseList $clauseList")
    public void getSatInstance(String clauseList) throws IOException {
        SatParser satParser= new SatParser();
        StringReader stringReader = new StringReader(clauseList);
        satInstance = satParser.parseInstance(stringReader);        
    }
    
    @When("I have individuals: <individualA> and <individualB>")
    public void getIndividuals(@Named("individualA") String aString, @Named("individualB") String bString) {
        a = new Individual(aString);
        b = new Individual(bString);
    }
    
    @Then("the result of the comparison is <result>")
    public void getComparisonResult(@Named("result") String expectedResult) {
        IndividualComparator comparator = new IndividualComparator(satInstance, satEvaluator);
        
        int actualResult = comparator.compare(a, b);
        
        switch (expectedResult.charAt(0)) {
            case '+': assertTrue(actualResult>0); break;
            case '-': assertTrue(actualResult<0); break;
            case '0': assertTrue(actualResult==0); break;
        } 
    }

}
