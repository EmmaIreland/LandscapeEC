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
    	
    	IndividualComparator fullComparator = new IndividualComparator(satInstance);
        IndividualComparator emptyComparator = new IndividualComparator(satInstance.getSubInstance(0));
        
        Vector origin = Vector.origin(world.getDimensions().size());
        Vector oppositeCorner = world.getDimensions().minusToAll(1);

        world.setLocationComparator(origin, emptyComparator);
		world.setLocationComparator(oppositeCorner, fullComparator);
        
        doFractalGeography(origin, oppositeCorner);
    }
    
    private void doFractalGeography(Vector topLeft, Vector bottomRight) {
		SatInstance topLeftInstance = world.getLocation(topLeft).getComparator().getInstance();
		SatInstance bottomRightInstance = world.getLocation(bottomRight).getComparator().getInstance();
		
		Vector middle = bottomRight.getMidPoint(topLeft);
		Vector topRight = getCrossDiagonalVector(topLeft, bottomRight);
		Vector bottomLeft = getCrossDiagonalVector(bottomRight, topLeft);
		
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
		
		if(!middle.equals(topLeft) && !middle.equals(bottomRight)) {
			SatInstance midInstance = doClauseListCrossover(topLeftInstance, bottomRightInstance);
			world.setLocationComparator(middle, new IndividualComparator(midInstance));
		}
		
		SatInstance topRightInstance = doClauseListCrossover(topLeftInstance, bottomRightInstance);
		world.setLocationComparator(topRight, new IndividualComparator(topRightInstance));
		
		SatInstance bottomLeftInstance = doClauseListCrossover(topLeftInstance, bottomRightInstance);
		world.setLocationComparator(bottomLeft, new IndividualComparator(bottomLeftInstance));
		
		if(bottomRight.get(0) - topLeft.get(0) <= 1 && bottomRight.get(1) - topLeft.get(1) <= 1 ) {
			return;
		}
		
		Vector midLeft = topLeft.getMidPoint(bottomLeft);
		Vector bottomMid = bottomLeft.getMidPoint(bottomRight);
		Vector topMid = topLeft.getMidPoint(topRight);
		Vector midRight = topRight.getMidPoint(bottomRight);
		
		doFractalGeography(topLeft, middle);  //this order is very important
		doFractalGeography(middle, bottomRight);
		doFractalGeography(midLeft, bottomMid);
		doFractalGeography(topMid, midRight);
	}

	private Vector getCrossDiagonalVector(Vector verticalPos, Vector horizontalPos) {
		Vector crossDiagonal = new Vector();
		crossDiagonal.add(horizontalPos.get(0));
		crossDiagonal.add(verticalPos.get(1));
		return crossDiagonal;
	}

	private SatInstance doClauseListCrossover(SatInstance satInstanceA, SatInstance satInstanceB) {
    	ClauseList newClauseList = new ClauseList();
    	int NumClauses = globalClauseList.getNumClauses();
    	
    	
    	for(int i = 0; i < NumClauses; i++) {
    		Clause currentClause = globalClauseList.getClause(i);
    		
    		// Decide whether or not this clause will be mutated according to Geography Noise Strength
    		// Then if a clauseList contains a clause and mutate=true, then we don't add it
    		// If a clauseList doesn't contain a clause and mutate=true then we add it
    		// Reverse for when mutate is false
    		boolean mutate = SharedPRNG.instance().nextDouble() < (IntParameter.GEOGRAPHY_NOISE_STRENGTH.getValue()/(double)NumClauses);
    		if(SharedPRNG.instance().nextBoolean()) {
    			if(satInstanceA.getClauseList().contains(currentClause) != mutate) {
    				newClauseList.addClause(currentClause);
    			}
    		} else {
    			if(satInstanceB.getClauseList().contains(currentClause) != mutate) {
    				newClauseList.addClause(currentClause);
    			}
    		}
    	}
    	
    	return new SatInstance(newClauseList, totalNumVariables);
    }

}
