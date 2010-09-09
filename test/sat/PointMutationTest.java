package sat;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import parameters.GlobalParameters;
import util.FrequencyCounter;
import util.MockParameters;


public class PointMutationTest {

	private static final int NUM_BITS = 8;

	@Before
	public void setUp() {
		MockParameters mockParams = new MockParameters();
        mockParams.put("AVERAGE_MUTATIONS", "1");
        GlobalParameters.setParameters(mockParams);
	}
	
	@Test
	public void testMutationDistribution() {
		Individual testIndividual = IndividualFactory.getInstance(NUM_BITS);
		FrequencyCounter<Integer> freqCounter = new FrequencyCounter<Integer>();
		
		for (int i = 0; i < 50; i++) {
			
		}
		fail("Test Incomplete");
	}
	
}
