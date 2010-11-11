package locality.geography;

import locality.Vector;
import locality.World;
import parameters.DoubleParameter;
import parameters.IntParameter;
import sat.Clause;
import sat.ClauseList;
import sat.IndividualComparator;
import sat.SatInstance;
import util.SharedPRNG;

public class FractalGeography implements Geography {

	private ClauseList globalClauseList;
	private int totalNumVariables;
	private World world;
	
	//Currently only works with 2 dimensions
	
    @Override
    public void generateGeography(SatInstance satInstance, World world) {
    	globalClauseList = satInstance.getClauseList();
    	totalNumVariables = satInstance.getNumVariables();
    	this.world = world;
    	SatInstance emptyInstance = satInstance.getSubInstance(0);
    	
    	IndividualComparator fullComparator = new IndividualComparator(satInstance);
		IndividualComparator emptyComparator = new IndividualComparator(emptyInstance);
        
        Vector topLeft = Vector.origin(world.getDimensions().size());
        Vector bottomRight = world.getDimensions().minusToAll(1);
        Vector middle = topLeft.getMidPoint(bottomRight);
        Vector topRight = getCorner(topLeft, bottomRight);
        Vector bottomLeft = getCorner(bottomRight, topLeft);
        Vector midLeft = topLeft.getMidPoint(bottomLeft);
        Vector topMid = topLeft.getMidPoint(topRight);
        Vector midRight = topRight.getMidPoint(bottomRight);
        Vector bottomMid = bottomLeft.getMidPoint(bottomRight);

        world.setLocationComparator(topLeft, emptyComparator);
        world.setLocationComparator(topMid, getInitalMidpointComparator(satInstance, emptyInstance));
        world.setLocationComparator(topRight, emptyComparator);
        world.setLocationComparator(midLeft, getInitalMidpointComparator(satInstance, emptyInstance));
        world.setLocationComparator(midRight, getInitalMidpointComparator(satInstance, emptyInstance));
        world.setLocationComparator(bottomLeft, emptyComparator);
        world.setLocationComparator(bottomMid, getInitalMidpointComparator(satInstance, emptyInstance));
        world.setLocationComparator(bottomRight, emptyComparator);
		world.setLocationComparator(middle, fullComparator);
        
        doFractalGeography(topLeft, topMid, middle, midLeft);
        doFractalGeography(topMid, topRight, midRight, middle);
		doFractalGeography(middle, midRight, bottomRight, bottomMid);
		doFractalGeography(midLeft, middle, bottomMid, bottomLeft);
    }

	private IndividualComparator getInitalMidpointComparator(SatInstance satInstance, SatInstance emptyInstance) {
		return new IndividualComparator(doClauseListCrossover(satInstance, emptyInstance));
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
		System.out.println("TopLeft     " + topLeft);
		System.out.println("Top Right   " + topRight);
		System.out.println("Middle      " + middle);
		System.out.println("Bottom Left " + bottomLeft);
		System.out.println("BottomRight " + bottomRight);
		System.out.println("------------------");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
		tryAddingComparator(topLeftInstance, topRightInstance, topMid);
		tryAddingComparator(topRightInstance, bottomRightInstance, midRight);
		tryAddingComparator(bottomRightInstance, bottomLeftInstance, bottomMid);
		tryAddingComparator(topLeftInstance, bottomLeftInstance, midLeft);
		tryAddingComparator(topLeftInstance, topRightInstance, bottomRightInstance, bottomLeftInstance, middle);
		
		if(bottomRight.get(0) - topLeft.get(0) <= 1 && bottomRight.get(1) - topLeft.get(1) <= 1 ) {
			return;
		}
		
		doFractalGeography(topLeft, topMid, middle, midLeft);
        doFractalGeography(topMid, topRight, midRight, middle);
		doFractalGeography(middle, midRight, bottomRight, bottomMid);
		doFractalGeography(midLeft, middle, bottomMid, bottomLeft);
	}

	private void tryAddingComparator(SatInstance firstInstance, SatInstance secondInstance, Vector midpoint) {
		if(world.getLocation(midpoint).hasNoComparator()) {
			SatInstance midInstance = doClauseListCrossover(firstInstance, secondInstance);
			world.setLocationComparator(midpoint, new IndividualComparator(midInstance));
		}
	}

	private void tryAddingComparator(SatInstance firstInstance, SatInstance secondInstance, SatInstance thirdInstance, SatInstance forthInstance, Vector midpoint) {
		if(world.getLocation(midpoint).hasNoComparator()) {
			SatInstance s = doClauseListCrossover(firstInstance, secondInstance);
			SatInstance t = doClauseListCrossover(thirdInstance, forthInstance);
			SatInstance midInstance = doClauseListCrossover(s, t);
			world.setLocationComparator(midpoint, new IndividualComparator(midInstance));
		}
	}
	
	private Vector getCorner(Vector verticalPos, Vector horizontalPos) {
		Vector crossDiagonal = new Vector();
		crossDiagonal.add(horizontalPos.get(0));
		crossDiagonal.add(verticalPos.get(1));
		return crossDiagonal;
	}

	private SatInstance doClauseListCrossover(SatInstance satInstanceA, SatInstance satInstanceB) {
    	ClauseList newClauseList = new ClauseList();
    	int NumClauses = globalClauseList.getNumClauses();
    	
    	for(Clause clause:globalClauseList.getClauses()) {    		
    		// Decide whether or not this clause will be mutated according to Geography Noise Strength
    		// Then if a clauseList contains a clause and mutate=true, then we don't add it
    		// If a clauseList doesn't contain a clause and mutate=true then we add it
    		// Reverse for when mutate is false
    		boolean mutate = SharedPRNG.instance().nextDouble() < (IntParameter.GEOGRAPHY_NOISE_STRENGTH.getValue()/(double)NumClauses);
    		if(SharedPRNG.instance().nextBoolean()) {
    			if(satInstanceA.getClauseList().contains(clause) != mutate) {
    				newClauseList.addClause(clause);
    			}
    		} else {
    			if(satInstanceB.getClauseList().contains(clause) != mutate) {
    				newClauseList.addClause(clause);
    			}
    		}
    	}
    	
    	return new SatInstance(newClauseList, totalNumVariables);
    }

}
