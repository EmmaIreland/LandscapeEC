package locality;

import static org.junit.Assert.*;

import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import landscapeEC.locality.GraphWorld;
import landscapeEC.locality.Location;

import org.junit.Ignore;
import org.junit.Test;

public class GraphWorldLocationTest {

	@Test
	@Ignore
	public void graphWorldPositionTest(){
		//expected neighborhoods
		
		ArrayList location1 = new ArrayList();
		int[] location1Stuff = {2,3,4};
		location1.add(location1Stuff);
		
		ArrayList location2 = new ArrayList();
		int[] location2Stuff = {1,3};
		location2.add(location2Stuff);
		
		ArrayList location3 = new ArrayList();
		int[] location3Stuff = {1,2};
		location3.add(location3Stuff);
		
		ArrayList location4 = new ArrayList();
		int[] location4Stuff = {1,5,6};
		location4.add(location4Stuff);
		
		ArrayList location5 = new ArrayList();
		location5.add(4);
		
		ArrayList location6 = new ArrayList();
		location6.add(4);
		
		
		File worldFile = new File("graphWorldFiles/testGraphWorld");
		
		GraphWorld testWorld = new GraphWorld(worldFile);
		
		//change this to a iterator thing.
		assertTrue(location1.containsAll(testWorld.getNeighborhood(1, 1)));
	}
}
