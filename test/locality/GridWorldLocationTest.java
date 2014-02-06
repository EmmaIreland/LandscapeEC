package locality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.ShellMaker;
import landscapeEC.locality.Vector;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.sat.SatInstance;

import org.junit.Test;

public class GridWorldLocationTest {

    private static final int SIZE = 10; //this depends on what's in test.properties

    @Test
    public void positionTest() throws Exception {
        File paramsFile = new File("properties/test.properties");
        GlobalParameters.setParameters(paramsFile);
        GlobalProblem.setProblem(new SatInstance(0.0));

        GridWorld world = new GridWorld();

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
    
    @Test
    public void shellTest() throws Exception {
        File paramsFile = new File("properties/test.properties");
        GlobalParameters.setParameters(paramsFile);
        GlobalProblem.setProblem(new SatInstance(0.0));

        GridWorld world = new GridWorld();
        
        Vector origin = new Vector(new Integer[] { 2,3 });
        ShellMaker shellMaker = new ShellMaker(world);
        List<Vector> shell = shellMaker.makeShell(origin, 1);
        Vector check = new Vector(Vector.origin(2));
        for (int x=1; x<3; x++){
            for (int y=2; y<4; y++){
                if(!(x==2&&y==3)){
                    check.set(0,x);
                    check.set(1,y);
                    assertTrue(shell.contains(check));
                }
            }
        }
        
    }
}
