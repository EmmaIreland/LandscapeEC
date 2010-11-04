package locality.geography;

import locality.Vector;
import locality.World;
import sat.IndividualComparator;
import sat.SatInstance;

public class UniformGeography implements Geography {

    @Override
    public void generateGeography(SatInstance satInstance, World world) {
        IndividualComparator comparator = new IndividualComparator(satInstance.getSubInstance(0));
        for (Vector position : world) {
            world.setLocationComparator(position, comparator);
        }
    }

}
