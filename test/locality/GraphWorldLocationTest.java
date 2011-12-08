package locality;

import static org.junit.Assert.*;

import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import landscapeEC.locality.GraphWorld;

import org.junit.Test;

public class GraphWorldLocationTest {

	@Test
	public void graphWorldPositionTest(){
		//expected neighborhoods
		
		ArrayList location1 = new ArrayList();
		int[] location1Stuff = {2,3,4};
		location1.add(location1Stuff);
		
		
		File worldFile = new File("graphWorldFiles/testGraphWorld");
		
		GraphWorld testWorld = new GraphWorld(worldFile);
		
		//change this to a iterator thing.
		assertTrue(location1.containsAll(testWorld.getNeighborhood(1, 1)));
	}
}
