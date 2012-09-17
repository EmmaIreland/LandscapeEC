package landscapeEC.locality.geography;

import landscapeEC.locality.Vector;
import landscapeEC.locality.GridWorld;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.FloatParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.SeparableProblem;
import landscapeEC.problem.sat.SatInstance;

public class FractalGeography implements Geography {

    private SatInstance globalSatInstance;
    private GridWorld world;
    
    // Currently only works with 2 dimensions
    // and only 3Sat Problems

    @Override
    public void generateGeography(GridWorld world) {
        if (!usingTestCaseProblem()) {
            throw new RuntimeException("Fractal Geography currently only supports 3SAT problems! You are not using a 3SAT problem.");
        }
        
        globalSatInstance = (SatInstance) GlobalProblem.getProblem();
        this.world = world;
        SatInstance emptyInstance = (SatInstance) globalSatInstance.getSubProblem(0);

        Vector topLeft = Vector.origin(world.getDimensions().size());
        Vector bottomRight = world.getDimensions().minusToAll(1);
        Vector middle = topLeft.getMidPoint(bottomRight);
        Vector topRight = Vector.getCorner(bottomRight, topLeft);
        Vector bottomLeft = Vector.getCorner(topLeft, bottomRight);
        Vector midLeft = topLeft.getMidPoint(bottomLeft);
        Vector topMid = topLeft.getMidPoint(topRight);
        Vector midRight = topRight.getMidPoint(bottomRight);
        Vector bottomMid = bottomLeft.getMidPoint(bottomRight);

        world.setLocationProblem(topLeft, emptyInstance);
        world.setLocationProblem(topMid, generateMidpointGoal(globalSatInstance, emptyInstance));
        world.setLocationProblem(topRight, emptyInstance);
        world.setLocationProblem(midLeft, generateMidpointGoal(globalSatInstance, emptyInstance));
        world.setLocationProblem(midRight, generateMidpointGoal(globalSatInstance, emptyInstance));
        world.setLocationProblem(bottomLeft, emptyInstance);
        world.setLocationProblem(bottomMid, generateMidpointGoal(globalSatInstance, emptyInstance));
        world.setLocationProblem(bottomRight, emptyInstance);
        world.setLocationProblem(middle, globalSatInstance);

        doFractalGeography(topLeft, topMid, middle, midLeft);
        doFractalGeography(topMid, topRight, midRight, middle);
        doFractalGeography(middle, midRight, bottomRight, bottomMid);
        doFractalGeography(midLeft, middle, bottomMid, bottomLeft);
    }

    private SatInstance generateMidpointGoal(SatInstance satInstance, SatInstance emptyInstance) {
        return doClauseListCrossover(emptyInstance, doClauseListCrossover(emptyInstance, doClauseListCrossover(satInstance, emptyInstance, 0), 0), 0);
    }

    private void doFractalGeography(Vector topLeft, Vector topRight, Vector bottomRight, Vector bottomLeft) {
        SatInstance topLeftInstance = (SatInstance) world.getLocation(topLeft).getProblem();
        SatInstance topRightInstance = (SatInstance) world.getLocation(topRight).getProblem();
        SatInstance bottomRightInstance = (SatInstance) world.getLocation(bottomRight).getProblem();
        SatInstance bottomLeftInstance = (SatInstance) world.getLocation(bottomLeft).getProblem();

        Vector middle = bottomRight.getMidPoint(topLeft);
        Vector midLeft = topLeft.getMidPoint(bottomLeft);
        Vector bottomMid = bottomLeft.getMidPoint(bottomRight);
        Vector topMid = topLeft.getMidPoint(topRight);
        Vector midRight = topRight.getMidPoint(bottomRight);

        /*
         * System.out.println("TopLeft     " + topLeft);p cnf 4 4
         * System.out.println("Top Right   " + topRight);
         * System.out.println("Middle      " + middle);
         * System.out.println("Bottom Left " + bottomLeft);
         * System.out.println("BottomRight " + bottomRight);
         * System.out.println("------------------");
         * 
         * try { Thread.sleep(1000); } catch (InterruptedException e) {
         * e.printStackTrace(); }
         */

        tryAddingComparator(topLeftInstance, topRightInstance, topMid);
        tryAddingComparator(topRightInstance, bottomRightInstance, midRight);
        tryAddingComparator(bottomRightInstance, bottomLeftInstance, bottomMid);
        tryAddingComparator(topLeftInstance, bottomLeftInstance, midLeft);
        tryAddingComparator(topLeftInstance, topRightInstance, bottomRightInstance, bottomLeftInstance, middle);

        if (bottomRight.get(0) - topLeft.get(0) <= 1 && bottomRight.get(1) - topLeft.get(1) <= 1) {
            return;
        }

        doFractalGeography(topLeft, topMid, middle, midLeft);
        doFractalGeography(topMid, topRight, midRight, middle);
        doFractalGeography(middle, midRight, bottomRight, bottomMid);
        doFractalGeography(midLeft, middle, bottomMid, bottomLeft);
    }

    private void tryAddingComparator(SatInstance firstInstance, SatInstance secondInstance, Vector midpoint) {
        if (world.getLocation(midpoint).hasNoProblem()) {
            SatInstance midInstance = doClauseListCrossover(firstInstance, secondInstance);
            world.setLocationProblem(midpoint, midInstance);
        }
    }

    private void tryAddingComparator(SatInstance firstInstance, SatInstance secondInstance, SatInstance thirdInstance, SatInstance forthInstance,
            Vector midpoint) {
        if (world.getLocation(midpoint).hasNoProblem()) {
            SatInstance s = doClauseListCrossover(firstInstance, secondInstance);
            SatInstance t = doClauseListCrossover(thirdInstance, forthInstance);
            SatInstance midInstance = doClauseListCrossover(s, t);
            world.setLocationProblem(midpoint, midInstance);
        }
    }

    private SatInstance doClauseListCrossover(SatInstance firstInstance, SatInstance secondInstance, float noiseStrength) {
        SatInstance newSatInstance = (SatInstance) firstInstance.crossover(firstInstance, secondInstance, noiseStrength);

        return newSatInstance;
    }

    private SatInstance doClauseListCrossover(SatInstance satInstanceA, SatInstance satInstanceB) {
        return doClauseListCrossover(satInstanceA, satInstanceB, FloatParameter.GEOGRAPHY_NOISE_STRENGTH.getValue());
    }


    private boolean usingTestCaseProblem() {
        return GlobalProblem.getProblem() instanceof SeparableProblem;
    }
}
