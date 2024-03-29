package landscapeEC.locality.geography;

import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Problem;

public class GradientGeography implements Geography {

    private void assignSubProblem(GridWorld world, Vector position) {
        Vector origin = Vector.origin(world.getDimensions().size());
        Vector worldEdge = world.getDimensions().minusToAll(1);
        Vector middle = origin.getMidPoint(worldEdge);
        
        int distance = position.minus(middle).manhattanLength();
        
        double clausePercentage = 1.0 - Math.min(distance / (1.0 * middle.manhattanLength()), 1.0);
        
        if (GlobalParameters.isSet("PEAK_HEIGHT")) {
            double clauseLimit = DoubleParameter.PEAK_HEIGHT.getValue();
            clausePercentage = clausePercentage * clauseLimit; //Limit to half of the clauses
        }
        
        final Problem subProblem = GlobalProblem.getProblem().getSubProblem(clausePercentage);
        world.setLocationProblem(position, subProblem);
    }

    @Override
    public void generateGeography(GridWorld world) {
        for (Location<Vector> location : world) {
            assignSubProblem(world, location.getPosition());
        }
    }

}
