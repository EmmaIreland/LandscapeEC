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
	
	@Test
	public void graphWorldPositionTest() throws IOException{
		//expected neighborhoods
		
		ArrayList location0 = new ArrayList();
		location0.add(2);
		location0.add(3);
		location0.add(4);
		
		ArrayList location1 = new ArrayList();
		location1.add(1);
		location1.add(3);
		
		ArrayList location2 = new ArrayList();
		location2.add(1);
		location2.add(2);
		
		ArrayList location3 = new ArrayList();
		location3.add(1);
		location3.add(5);
		location3.add(6);
		
		ArrayList location4 = new ArrayList();
		location4.add(4);
		
		ArrayList location5 = new ArrayList();
		location5.add(4);
		
		
		File paramsFile = new File("properties/graphTest.properties");
		GlobalParameters.setParameters(paramsFile);
        GlobalProblem.setProblem(new SatInstance(0.0));
		
		GraphWorld testWorld = null;
		try {
			testWorld = new GraphWorld();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(location0.containsAll(testWorld.getNeighborhood(0, 1)));
		assertTrue(location1.containsAll(testWorld.getNeighborhood(1, 1)));
		assertTrue(location2.containsAll(testWorld.getNeighborhood(2, 1)));
		//assertTrue(location3.containsAll(testWorld.getNeighborhood(3, 1)));
	}
}
