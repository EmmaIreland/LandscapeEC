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
    private int poolSize;
    
    @Before
    public void setupParameters() throws IOException{
        GlobalParameters.setParameters(new File("properties/test.properties"));   
        individualLength = IntParameter.INDIVIDUAL_LENGTH.getValue();
        poolSize = IntParameter.POOL_SIZE.getValue();
    }
    
    @Test
    public void individualFactoryTest(){
        IndividualFactory factory =  new IndividualFactory();
                
        Individual individual = factory.getInstance();
        
        assertEquals("Individuals are not the same length", individualLength, individual.getBitString().length());
    }
    
    @Test
    public void populationManagerTest() throws IOException{
        PopulationManager manager =  new PopulationManager();  
                
        List<Individual> pop =manager.generatePopulation();
        
        assertEquals("Pools are different sizes", poolSize, pop.size());
    }
}
