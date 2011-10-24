package landscapeEC.locality.geography;

import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Problem;

public class ManhattanDistanceGeography implements Geography {

    private void assignSubProblem(World world, Vector position) {
        Vector origin = Vector.origin(world.getDimensions().size());
        Vector worldEdge = world.getDimensions().minusToAll(1);
        Vector middle = origin.getMidPoint(worldEdge);
        
        int distance = position.minus(middle).manhattanLength();
        
        double clausePercentage = 1.0 - Math.min(distance / (1.0 * middle.manhattanLength()), 1.0);
        
        final Problem subProblem = GlobalProblem.getProblem().getSubProblem(clausePercentage);
        world.setLocationProblem(position, subProblem, clausePercentage);
    }

    @Override
    public void generateGeography(World world) {
        for (Vector position : world) {
            assignSubProblem(world, position);
        }
    }

}
