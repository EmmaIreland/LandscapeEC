package landscapeEC.locality.geography;

import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.sat.Clause;
import landscapeEC.problem.sat.GlobalSatInstance;
import landscapeEC.problem.sat.IndividualComparator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.util.SharedPRNG;

public class FractalGeography implements Geography {

    private SatInstance globalSatInstance;
    private World world;

    // Currently only works with 2 dimensions

    @Override
    public void generateGeography(World world) {
        globalSatInstance = GlobalSatInstance.getInstance();
        this.world = world;
        SatInstance emptyInstance = globalSatInstance.getSubInstance(0);

        IndividualComparator fullComparator = new IndividualComparator(globalSatInstance);
        IndividualComparator emptyComparator = new IndividualComparator(emptyInstance);

        Vector topLeft = Vector.origin(world.getDimensions().size());
        Vector bottomRight = world.getDimensions().minusToAll(1);
        Vector middle = topLeft.getMidPoint(bottomRight);
        Vector topRight = Vector.getCorner(bottomRight, topLeft);
        Vector bottomLeft = Vector.getCorner(topLeft, bottomRight);
        Vector midLeft = topLeft.getMidPoint(bottomLeft);
        Vector topMid = topLeft.getMidPoint(topRight);
        Vector midRight = topRight.getMidPoint(bottomRight);
        Vector bottomMid = bottomLeft.getMidPoint(bottomRight);

        world.setLocationComparator(topLeft, emptyComparator);
        world.setLocationComparator(topMid, generateMidpointGoal(globalSatInstance, emptyInstance));
        world.setLocationComparator(topRight, emptyComparator);
        world.setLocationComparator(midLeft, generateMidpointGoal(globalSatInstance, emptyInstance));
        world.setLocationComparator(midRight, generateMidpointGoal(globalSatInstance, emptyInstance));
        world.setLocationComparator(bottomLeft, emptyComparator);
        world.setLocationComparator(bottomMid, generateMidpointGoal(globalSatInstance, emptyInstance));
        world.setLocationComparator(bottomRight, emptyComparator);
        world.setLocationComparator(middle, fullComparator);

        doFractalGeography(topLeft, topMid, middle, midLeft);
        doFractalGeography(topMid, topRight, midRight, middle);
        doFractalGeography(middle, midRight, bottomRight, bottomMid);
        doFractalGeography(midLeft, middle, bottomMid, bottomLeft);
    }

    private IndividualComparator generateMidpointGoal(SatInstance satInstance, SatInstance emptyInstance) {
        return new IndividualComparator(doClauseListCrossover(emptyInstance, doClauseListCrossover(emptyInstance, doClauseListCrossover(satInstance,
                emptyInstance, 0), 0), 0));
    }

    private void doFractalGeography(Vector topLeft, Vector topRight, Vector bottomRight, Vector bottomLeft) {
        SatInstance topLeftInstance = world.getLocation(topLeft).getComparator().getInstance();
        SatInstance topRightInstance = world.getLocation(topRight).getComparator().getInstance();
        SatInstance bottomRightInstance = world.getLocation(bottomRight).getComparator().getInstance();
        SatInstance bottomLeftInstance = world.getLocation(bottomLeft).getComparator().getInstance();

        Vector middle = bottomRight.getMidPoint(topLeft);
        Vector midLeft = topLeft.getMidPoint(bottomLeft);
        Vector bottomMid = bottomLeft.getMidPoint(bottomRight);
        Vector topMid = topLeft.getMidPoint(topRight);
        Vector midRight = topRight.getMidPoint(bottomRight);

        /*
         * System.out.println("TopLeft     " + topLeft);
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
        if (world.getLocation(midpoint).hasNoComparator()) {
            SatInstance midInstance = doClauseListCrossover(firstInstance, secondInstance);
            world.setLocationComparator(midpoint, new IndividualComparator(midInstance));
        }
    }

    private void tryAddingComparator(SatInstance firstInstance, SatInstance secondInstance, SatInstance thirdInstance, SatInstance forthInstance,
            Vector midpoint) {
        if (world.getLocation(midpoint).hasNoComparator()) {
            SatInstance s = doClauseListCrossover(firstInstance, secondInstance);
            SatInstance t = doClauseListCrossover(thirdInstance, forthInstance);
            SatInstance midInstance = doClauseListCrossover(s, t);
            world.setLocationComparator(midpoint, new IndividualComparator(midInstance));
        }
    }

    private SatInstance doClauseListCrossover(SatInstance firstInstance, SatInstance secondInstance, int noiseStrength) {
        SatInstance newSatInstance = new SatInstance();
        int NumClauses = globalSatInstance.getNumClauses();

        for (Clause clause : globalSatInstance.getClauses()) {
            // Decide whether or not this clause will be mutated according to
            // Geography Noise Strength
            // Then if a clauseList contains a clause and mutate=true, then we
            // don't add it
            // If a clauseList doesn't contain a clause and mutate=true then we
            // add it
            // Reverse for when mutate is false
            boolean mutate = SharedPRNG.instance().nextDouble() < (noiseStrength / (double) NumClauses);
            if (SharedPRNG.instance().nextBoolean()) {
                if (firstInstance.contains(clause) != mutate) {
                    newSatInstance.addClause(clause);
                }
            } else {
                if (secondInstance.contains(clause) != mutate) {
                    newSatInstance.addClause(clause);
                }
            }
        }

        return newSatInstance;
    }

    private SatInstance doClauseListCrossover(SatInstance satInstanceA, SatInstance satInstanceB) {
        return doClauseListCrossover(satInstanceA, satInstanceB, IntParameter.GEOGRAPHY_NOISE_STRENGTH.getValue());
    }

}
