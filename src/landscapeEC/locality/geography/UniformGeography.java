package landscapeEC.locality.geography;

import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.problem.sat.IndividualComparator;
import landscapeEC.problem.sat.SatInstance;

public class UniformGeography implements Geography {

    @Override
    public void generateGeography(World world) {
        IndividualComparator comparator = new IndividualComparator();
        for (Vector position : world) {
            world.setLocationComparator(position, comparator);
        }
    }

}
