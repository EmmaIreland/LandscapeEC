package locality;

import sat.SatInstance;

public class ManhattanDistanceGradiantGeography {

    SatInstance getSubInstance(Vector dimensions, SatInstance satInstance, Vector position, World world) {
        int distance = position.manhattanVectorLength(world);
    
        Vector worldEdge = dimensions.minusToAll(1);
    
        double clausePercentage = distance / (1.0 * worldEdge.manhattanVectorLength(world));
    
        final SatInstance subInstance = satInstance.getSubInstance(clausePercentage);
        return subInstance;
    }

}
