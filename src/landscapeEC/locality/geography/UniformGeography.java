package landscapeEC.locality.geography;

import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Problem;

public class UniformGeography implements Geography {

    @Override
    public void generateGeography(World world) {
        Problem problem = GlobalProblem.getProblem().getSubProblem(0);
        for (Location location : world) {
            world.setLocationProblem(location.getPosition(), problem);
        }
    }

}
