package locality;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import landscapeEC.core.GARun;
import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.SatInstance;

import org.junit.Test;

public class TestRun {

    //Tests for GARun
    //Depends on test.properties
    
    @Test
    public void migrationTest() throws Exception {
        //Test to make sure migration is happening
        File paramsFile = new File("properties/test.properties");
        GlobalParameters.setParameters(paramsFile);
        GlobalProblem.setProblem(new SatInstance(0.0));
        
        GARun run = new GARun("properties/test.properties");

        run.run(); //This should not take very long, but it depends on NUM_EVALS_TO_DO
        
        Integer[] position = {1,1};
        Location<?> nonOriginCell = run.getWorld().getLocation(new Vector(position));
        
        List<Individual> individuals = nonOriginCell.getIndividuals();
        
        assertTrue(individuals.size() > 0);
    }
}
