package locality;

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
        
        world.setLocationComparator(Vector.origin(world.getDimensions().size()), emptyComparator);
        world.setLocationComparator(world.getDimensions().minusToAll(1), fullComparator);
        
        doFractalGeography(Vector.origin(world.getDimensions().size()), world.getDimensions().minusToAll(1));
    }
    
    private void doFractalGeography(Vector topLeft, Vector bottomRight) {
		SatInstance topLeftInstance = world.getLocation(topLeft).getComparator().getInstance();
		SatInstance bottomRightInstance = world.getLocation(topLeft).getComparator().getInstance();
		
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

	private Vector getCrossDiagonalVector(Vector topLeft, Vector bottomRight) {
		Vector topRight = new Vector();
		topRight.add(bottomRight.get(0));
		topRight.add(topLeft.get(1));
		return topRight;
	}

	private SatInstance doClauseListCrossover(SatInstance satInstanceA, SatInstance satInstanceB) {
		//TODO THIS DOES NOT WORK, DO NOT KNOW WHY
    	ClauseList newClauseList = new ClauseList();
    	
    	for(int i = 0; i < globalClauseList.getNumClauses(); i++) {
    		Clause currentClause = globalClauseList.getClause(i);
    		if(SharedPRNG.instance().nextBoolean()) {
    			if(satInstanceA.getClauseList().contains(currentClause)) {
    				newClauseList.addClause(currentClause);
    			}
    		} else {
    			if(satInstanceB.getClauseList().contains(currentClause)) {
    				newClauseList.addClause(currentClause);
    			}
    		}
    	}
    	
    	System.out.println(newClauseList);
    	
    	return new SatInstance(newClauseList, totalNumVariables);
    }

}
