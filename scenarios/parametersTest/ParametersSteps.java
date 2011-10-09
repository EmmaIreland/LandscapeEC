package parametersTest;
import java.io.File;
import java.io.IOException;

import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.IntParameter;

import static junit.framework.Assert.*;

import org.apache.commons.io.FileUtils;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;



public class ParametersSteps extends Steps {
    
    
    @When("I set these properties $sampleProps")
    public void setProperties(String sampleProps) throws IOException {
        File paramsFile = new File("testing.properties");
        FileUtils.writeStringToFile(paramsFile, sampleProps);
        GlobalParameters.setParameters(paramsFile);
        paramsFile.delete();
    }

    
    @Then("the carrying capacity is $poolSize")
    public void confirmPoolSize(int poolSize) {
        assertEquals(poolSize, IntParameter.CARRYING_CAPACITY.getValue());
    }
    
    @Then("the number of runs is $numRuns")
    public void confirmNumRuns(int numRuns) {        
        assertEquals(numRuns, IntParameter.NUM_RUNS.getValue());
    }
    
    @Then("the mutation rate is $mutationRate")
    public void confirmMutationRate(double mutationRate) {
        assertEquals(mutationRate, DoubleParameter.AVERAGE_MUTATIONS.getValue());
    }

}
