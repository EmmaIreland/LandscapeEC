package locality;

import static org.junit.Assert.*;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import landscapeEC.locality.GraphWorld;
import landscapeEC.locality.Location;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.sat.SatInstance;

import org.junit.Ignore;
import org.junit.Test;

public class GraphWorldLocationTest {
	
	@Ignore
	@Test
	public void graphWorldPositionTest() throws IOException{
		//expected neighborhoods
		
		ArrayList location0 = new ArrayList();
		int[] location0Stuff = {2,3,4};
		location0.add(location0Stuff);
		
		ArrayList location1 = new ArrayList();
		int[] location1Stuff = {1,3};
		location1.add(location1Stuff);
		
		ArrayList location2 = new ArrayList();
		int[] location2Stuff = {1,2};
		location2.add(location2Stuff);
		
		ArrayList location3 = new ArrayList();
		int[] location3Stuff = {1,5,6};
		location3.add(location3Stuff);
		
		ArrayList location4 = new ArrayList();
		location4.add(4);
		
		ArrayList location5 = new ArrayList();
		location5.add(4);
		
		
		File worldFile = new File("graphWorldFiles/testGraphWorld");
		File paramsFile = new File("properties/graphTest.properties");
		GlobalParameters.setParameters(paramsFile);
        GlobalProblem.setProblem(new SatInstance(0.0));
		
		GraphWorld testWorld = null;
		try {
			testWorld = new GraphWorld(worldFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(location1.containsAll(testWorld.getNeighborhood(1, 1)));
		assertTrue(location2.containsAll(testWorld.getNeighborhood(2, 1)));
		assertTrue(location3.containsAll(testWorld.getNeighborhood(3, 1)));
		assertTrue(location4.containsAll(testWorld.getNeighborhood(4, 1)));
	}
}
