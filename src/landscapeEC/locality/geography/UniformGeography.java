package landscapeEC.locality.geography;

import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Problem;

public class UniformGeography implements Geography {

    @Override
    public void generateGeography(World world) {
        Problem problem = GlobalProblem.getProblem().getSubProblem(0);
        for (Vector position : world) {
            world.setLocationProblem(position, problem);
        }
    }

}
