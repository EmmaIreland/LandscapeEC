package locality;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LocationTest {

	private static final int SIZE = 10;

	@Test
	public void positionTest() {
		int[] dimensions = new int[] { SIZE, SIZE };
		World world = new World(dimensions, true);
		
		for (int i=0; i<SIZE; ++i) {
			for (int j=0; j<SIZE; ++j) {
				List<Integer> position = new ArrayList<Integer>();
				position.add(i);
				position.add(j);
				Location location = world.getLocation(position);
				assertNotNull("Location " + position + " is null", location);
				assertEquals(0, location.getNumIndividuals());
			}
		}
		
		assertEquals(SIZE*SIZE, world.getNumLocations());
	}
}
