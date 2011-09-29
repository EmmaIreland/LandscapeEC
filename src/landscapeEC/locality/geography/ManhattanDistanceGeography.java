package landscapeEC.locality.geography;

import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.problem.sat.GlobalSatInstance;
import landscapeEC.problem.sat.IndividualComparator;
import landscapeEC.problem.sat.SatInstance;

public class ManhattanDistanceGeography implements Geography {

    private SatInstance getSubInstance(World world, Vector position) {
        Vector origin = Vector.origin(world.getDimensions().size());
        Vector worldEdge = world.getDimensions().minusToAll(1);
        Vector middle = origin.getMidPoint(worldEdge);
        
        int distance = position.minus(middle).manhattanLength();
        
        double clausePercentage = 1.0 - Math.min(distance / (1.0 * middle.manhattanLength()), 1.0);
        
        final SatInstance subInstance = GlobalSatInstance.getInstance().getSubInstance(clausePercentage);
        return subInstance;
    }

    public void generateGeography(World world) {
        for (Vector position : world) {
            final SatInstance subInstance = getSubInstance(world, position);
            IndividualComparator locationComparator = new IndividualComparator(subInstance);
    
            world.setLocationComparator(position, locationComparator);
        }
    }

}
