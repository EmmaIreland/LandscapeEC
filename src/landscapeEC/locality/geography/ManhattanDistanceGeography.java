package landscapeEC.locality.geography;

import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.sat.IndividualComparator;
import landscapeEC.sat.SatInstance;

public class ManhattanDistanceGeography implements Geography {

    private SatInstance getSubInstance(Vector dimensions, SatInstance satInstance, Vector position) {
        int distance = position.manhattanLength();
    
        Vector worldEdge = dimensions.minusToAll(1);
    
        double clausePercentage = distance / (1.0 * worldEdge.manhattanLength());
    
        final SatInstance subInstance = satInstance.getSubInstance(clausePercentage);
        return subInstance;
    }

    public void generateGeography(SatInstance satInstance, World world) {
        for (Vector position : world) {
            final SatInstance subInstance = getSubInstance(world.getDimensions(), satInstance, position);
            IndividualComparator locationComparator = new IndividualComparator(subInstance);
    
            world.setLocationComparator(position, locationComparator);
        }
    }

}