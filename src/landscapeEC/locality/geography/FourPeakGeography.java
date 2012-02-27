package landscapeEC.locality.geography;

import landscapeEC.locality.Vector;
import landscapeEC.locality.GridWorld;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.SeparableProblem;
import landscapeEC.problem.sat.SatInstance;

public class FourPeakGeography implements Geography {

    private SatInstance globalSatInstance;
    private GridWorld world;
    
    // Currently only compatible with 2 dimensions
    // and only 3Sat Problems

    @Override
    public void generateGeography(GridWorld world) {
        if (!usingTestCaseProblem()) {
            throw new RuntimeException("Four Peak Geography currently only supports 3SAT problems! You are not using a 3SAT problem.");
        }
        if (world.getDimensions().get(0) < 5 || world.getDimensions().get(1) < 5) {
            throw new RuntimeException("World dimensions are too small to support Four Peak Geography");
        }
        
        globalSatInstance = (SatInstance) GlobalProblem.getProblem();
        this.world = world;
        SatInstance emptyInstance = (SatInstance) globalSatInstance.getSubProblem(0);

        /* We need to set up a total of 16 squares to make the main features of the geography
         * Below is the diagram how the world will be divded into squares. A, B, C and D
         * represent equal 25% chunks of the total clause list, with no clauses shared between
         * them. 0 represents no clauses, and the middle 1/2 is a random 50% of all clauses.
         * 
         *     0   1   2   3   4
         *     
         *  0  0---A---AC--C---0
         *     |   |   |   |   |
         *  1  A--AC--ABC--BC--C
         *     |   |   |   |   |
         *  2  AD-ACD-1/2-BCD-BC
         *     |   |   |   |   |
         *  3  D--AD--ABD--BD--B
         *     |   |   |   |   |
         *  4  0---D---BD--B---0
         */
        
        //Get vector positions from the world using midpoints and corners (25 total)
        Vector[][] vects = new Vector[5][5]; //first position is x, second is y
        vects[0][0] = Vector.origin(world.getDimensions().size());
        vects[4][4] = world.getDimensions().minusToAll(1);
        vects[4][0] = Vector.getCorner(vects[4][4], vects[0][0]);
        vects[0][4] = Vector.getCorner(vects[0][0], vects[4][4]);
        vects[2][2] = vects[0][0].getMidPoint(vects[4][4]);
        vects[0][2] = vects[0][0].getMidPoint(vects[0][4]);
        vects[2][0] = vects[0][0].getMidPoint(vects[4][0]);
        vects[4][2] = vects[4][0].getMidPoint(vects[4][4]);
        vects[2][4] = vects[0][4].getMidPoint(vects[4][4]);
        vects[1][1] = vects[0][0].getMidPoint(vects[2][2]);
        vects[3][3] = vects[2][2].getMidPoint(vects[4][4]);
        vects[1][3] = vects[2][2].getMidPoint(vects[0][4]);
        vects[3][1] = vects[4][0].getMidPoint(vects[2][2]);
        vects[2][1] = vects[2][0].getMidPoint(vects[2][2]);
        vects[1][2] = vects[0][2].getMidPoint(vects[2][2]);
        vects[2][3] = vects[2][2].getMidPoint(vects[2][4]);
        vects[3][2] = vects[2][2].getMidPoint(vects[4][2]);
        vects[0][1] = Vector.getCorner(vects[0][0], vects[1][1]);
        vects[1][0] = Vector.getCorner(vects[1][1], vects[0][0]);
        vects[0][3] = Vector.getCorner(vects[0][4], vects[1][3]);
        vects[3][0] = Vector.getCorner(vects[3][1], vects[4][0]);
        vects[1][4] = Vector.getCorner(vects[1][3], vects[0][4]);
        vects[4][1] = Vector.getCorner(vects[4][0], vects[3][1]);
        vects[3][4] = Vector.getCorner(vects[3][3], vects[4][4]);
        vects[4][3] = Vector.getCorner(vects[4][4], vects[3][3]);
        
        //Fill in corner vectors with the empty clause list
        int[][] edgeVectors = {{0, 0}, {4, 0}, {0, 4}, {4, 4}};
        for(int[] position : edgeVectors) {
            world.setLocationProblem(vects[position[0]][position[1]], emptyInstance);
        }
        
        //Generate clause lists of remaining vectors (the peaks and valleys)
        SatInstance ABC = (SatInstance) globalSatInstance.getSubProblem(0.75, 0.0);
        SatInstance BCD = (SatInstance) globalSatInstance.getSubProblem(0.75, 0.25);
        SatInstance ACD = (SatInstance) globalSatInstance.getSubProblem(0.75, 0.5);
        SatInstance ABD = (SatInstance) globalSatInstance.getSubProblem(0.75, 0.75);
        SatInstance BC = ABC.intersection(BCD);
        SatInstance AC = ABC.intersection(ACD);
        SatInstance BD = ABD.intersection(BCD);
        SatInstance AD = ACD.intersection(ABD);
        SatInstance A = (SatInstance) globalSatInstance.getSubProblem(0.25, 0.0);
        SatInstance B = (SatInstance) globalSatInstance.getSubProblem(0.25, 0.25);
        SatInstance C = (SatInstance) globalSatInstance.getSubProblem(0.25, 0.5);
        SatInstance D = (SatInstance) globalSatInstance.getSubProblem(0.25, 0.75);
        SatInstance randomHalf = generateMidpointGoal(globalSatInstance, emptyInstance);
        
        //Set clause lists to the world locations
        world.setLocationProblem(vects[2][1], ABC);
        world.setLocationProblem(vects[3][2], BCD);
        world.setLocationProblem(vects[1][2], ACD);
        world.setLocationProblem(vects[2][3], ABD);
        world.setLocationProblem(vects[2][0], AC);
        world.setLocationProblem(vects[1][1], AC);
        world.setLocationProblem(vects[3][1], BC);
        world.setLocationProblem(vects[4][2], BC);
        world.setLocationProblem(vects[3][3], BD);
        world.setLocationProblem(vects[2][4], BD);
        world.setLocationProblem(vects[0][2], AD);
        world.setLocationProblem(vects[1][3], AD);
        world.setLocationProblem(vects[1][0], A);
        world.setLocationProblem(vects[0][1], A);
        world.setLocationProblem(vects[3][0], C);
        world.setLocationProblem(vects[4][1], C);
        world.setLocationProblem(vects[4][3], B);
        world.setLocationProblem(vects[3][4], B);
        world.setLocationProblem(vects[0][3], D);
        world.setLocationProblem(vects[1][4], D);
        world.setLocationProblem(vects[2][2], randomHalf);

        //Do recursive fractal geography on the 16 squares we generated
        for (int y = 0; y < 4; y++) {
            for(int x = 0; x < 4; x++) {
                doFractalGeography(vects[x][y], vects[x+1][y], vects[x+1][y+1], vects[x][y+1]);
            }
        }
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

    private SatInstance doClauseListCrossover(SatInstance firstInstance, SatInstance secondInstance, int noiseStrength) {
        SatInstance newSatInstance = (SatInstance) firstInstance.crossover(firstInstance, secondInstance, noiseStrength);

        return newSatInstance;
    }

    private SatInstance doClauseListCrossover(SatInstance satInstanceA, SatInstance satInstanceB) {
        return doClauseListCrossover(satInstanceA, satInstanceB, IntParameter.GEOGRAPHY_NOISE_STRENGTH.getValue());
    }

    private boolean usingTestCaseProblem() {
        return GlobalProblem.getProblem() instanceof SeparableProblem;
    }
}
