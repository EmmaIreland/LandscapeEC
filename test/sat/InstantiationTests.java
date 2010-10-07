package sat;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import parameters.GlobalParameters;
import parameters.IntParameter;

public class InstantiationTests {
    
    private int individualLength;
    private int carryingCapacity;
    
    SatInstance satInstance;
    
    @Before
    public void setupParameters() throws IOException{
        GlobalParameters.setParameters(new File("properties/test.properties"));
        carryingCapacity = IntParameter.CARRYING_CAPACITY.getValue();
        
        satInstance = new SatInstance();
        individualLength = 20;
        satInstance.setNumVariables(individualLength);
    }
    
    @Test
    public void individualFactoryTest(){                
        Individual individual = IndividualFactory.getInstance(satInstance.getNumVariables());
        
        assertEquals("Individuals are not the same length", individualLength, individual.getBits().length);
    }
    
    @Test
    public void populationManagerTest() {
        PopulationManager manager =  new PopulationManager();  
                
        List<Individual> pop =manager.generatePopulation(satInstance);
        
        assertEquals("Pools are different sizes", carryingCapacity, pop.size());
    }
}
