package sat;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import landscapeEC.core.PopulationManager;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.GlobalSatInstance;
import landscapeEC.problem.sat.IndividualFactory;
import landscapeEC.problem.sat.SatInstance;

import org.junit.Before;
import org.junit.Test;


public class InstantiationTests {
    
    private int individualLength;
    private int carryingCapacity;
    
    @Before
    public void setupParameters() throws IOException{
        GlobalParameters.setParameters(new File("properties/test.properties"));
        carryingCapacity = IntParameter.CARRYING_CAPACITY.getValue();
        
        SatInstance satInstance = new SatInstance();
        individualLength = 20;
        satInstance.setNumVariables(individualLength);
        GlobalSatInstance.setInstance(satInstance);
    }
    
    @Test
    public void individualFactoryTest(){                
        Individual individual = IndividualFactory.getInstance(individualLength);
        
        assertEquals("Individuals are not the same length", individualLength, individual.getBits().length);
    }
    
    @Test
    public void populationManagerTest() {
        PopulationManager manager =  new PopulationManager();  
                
        List<Individual> pop =manager.generatePopulation();
        
        assertEquals("Pools are different sizes", carryingCapacity, pop.size());
    }
}
