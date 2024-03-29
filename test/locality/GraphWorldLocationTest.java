package locality;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import landscapeEC.locality.GraphWorld;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.sat.SatInstance;

import org.junit.Test;

public class GraphWorldLocationTest {
	
	@Test
	public void graphWorldPositionTest() throws Exception{
		//expected neighborhoods
		
		ArrayList<Integer> location0 = new ArrayList<Integer>();
		location0.add(2);
		location0.add(3);
		location0.add(4);
		
		ArrayList<Integer> location1 = new ArrayList<Integer>();
		location1.add(1);
		location1.add(3);
		
		ArrayList<Integer> location2 = new ArrayList<Integer>();
		location2.add(1);
		location2.add(2);
		
		ArrayList<Integer> location3 = new ArrayList<Integer>();
		location3.add(1);
		location3.add(5);
		location3.add(6);
		
		ArrayList<Integer> location4 = new ArrayList<Integer>();
		location4.add(4);
		
		ArrayList<Integer> location5 = new ArrayList<Integer>();
		location5.add(4);
		
		
		File paramsFile = new File("properties/graphTest.properties");
		GlobalParameters.setParameters(paramsFile);
                GlobalProblem.setProblem(new SatInstance(0.0));
		
		GraphWorld testWorld = null;
		testWorld = new GraphWorld();
		
		assertTrue(location0.containsAll(testWorld.getNeighborhood(0, 1)));
		assertTrue(location1.containsAll(testWorld.getNeighborhood(1, 1)));
		assertTrue(location2.containsAll(testWorld.getNeighborhood(2, 1)));
		//assertTrue(location3.containsAll(testWorld.getNeighborhood(3, 1)));
	}
}
