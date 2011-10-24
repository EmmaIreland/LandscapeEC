package locality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.sat.SatInstance;

import org.junit.Test;



public class LocationTest {

    private static final int SIZE = 10;

    @Test
    public void positionTest() throws Exception {
        File paramsFile = new File("properties/test.properties");
        GlobalParameters.setParameters(paramsFile);
        GlobalProblem.setProblem(new SatInstance());

        Vector dimensions = new Vector(new Integer[] { SIZE, SIZE });
        World world = new World(dimensions, true);

        for (int i=0; i<SIZE; ++i) {
            for (int j=0; j<SIZE; ++j) {
                Vector position = new Vector(new Integer[] {i, j});
                Location location = world.getLocation(position);
                assertNotNull("Location " + position + " is null", location);
                assertEquals(0, location.getNumIndividuals());
            }
        }

        assertEquals(SIZE*SIZE, world.getNumLocations());
    }
}
