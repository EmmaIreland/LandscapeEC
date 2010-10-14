package locality;

import static org.junit.Assert.*;

import org.junit.Test;

import sat.SatInstance;

public class LocationTest {

	private static final int SIZE = 10;

	@Test
	public void positionTest() {
		Integer[] dimensions = new Integer[] { SIZE, SIZE };
		World world = new World(dimensions, true, new SatInstance());
		
		for (int i=0; i<SIZE; ++i) {
			for (int j=0; j<SIZE; ++j) {
				Position position = new Position(new Integer[] {i, j});
				Location location = world.getLocation(position);
				assertNotNull("Location " + position + " is null", location);
				assertEquals(0, location.getNumIndividuals());
			}
		}
		
		assertEquals(SIZE*SIZE, world.getNumLocations());
	}
}
