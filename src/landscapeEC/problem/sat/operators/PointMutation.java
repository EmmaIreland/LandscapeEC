package landscapeEC.problem.sat.operators;

import java.util.Random;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;

import landscapeEC.locality.Location;
import landscapeEC.neo4j.GraphDB;
import landscapeEC.neo4j.RelTypes;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.problem.Individual;
import landscapeEC.util.SharedPRNG;

public class PointMutation implements MutationOperator {

    private Random prngInstance = SharedPRNG.instance();

    @Override
    public Individual mutate(Individual ind, Object... parameters) {
    	int generationNumber = (Integer) parameters[1];
    	Location location = (Location) parameters[0];
    	
        int[] bits = ind.getBits();

        double mutationRate = DoubleParameter.AVERAGE_MUTATIONS.getValue()/bits.length;

        for (int i = 0; i < bits.length; i++) {
            if (prngInstance.nextDouble() < mutationRate) {
                bits[i] = flipBit(bits[i]);
            }
        }

        Individual mutant = new Individual(bits);
        
        addMutantToDB(ind, mutant, generationNumber, location);
        
        return mutant;
    }
    
    private void addMutantToDB(Individual original, Individual mutant, int generationNumber, Location location) {
    	final GraphDatabaseService graphDB = GraphDB.graphDB();
		IndexManager index = graphDB.index();
		Index<Node> individualNodes = index.forNodes("individuals");
		Index<Node> locationNodes = index.forNodes("locations");
	
		Transaction tx = graphDB.beginTx();
		
		Relationship mutantRelationship;
		Relationship node;
		Node individualNode;
		try
		{
			individualNode = graphDB.createNode();
			individualNode.setProperty("fitness", mutant.getGlobalFitness());
			individualNode.setProperty("id", mutant.uid.toString());
			Node locationNode = locationNodes.get("locationID", location.getPosition()).next();
			node = individualNode.createRelationshipTo(locationNode, RelTypes.LOCATEDIN);
			node.setProperty("generation", generationNumber);
			
			Node originalNode = individualNodes.get("id", original.uid.toString()).next();
			mutantRelationship = individualNode.createRelationshipTo(originalNode, RelTypes.MUTANTOF);
			mutantRelationship.setProperty("generation", generationNumber);
			
			individualNodes.add(individualNode, "id", mutant.uid.toString());
								 
			tx.success();
		}
		finally
		{
		    tx.finish();
		}
    }

    private int flipBit(int bit) {
        if (bit == 0)
            return 1;
        return 0;
    }
}
