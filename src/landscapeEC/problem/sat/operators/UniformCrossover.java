package landscapeEC.problem.sat.operators;

import java.util.List;
import java.util.Random;

import landscapeEC.locality.Location;
import landscapeEC.neo4j.GraphDB;
import landscapeEC.neo4j.RelTypes;
import landscapeEC.problem.Individual;
import landscapeEC.util.SharedPRNG;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;


public class UniformCrossover implements CrossoverOperator {

    private Random prngInstance = SharedPRNG.instance();

    @Override
    public Individual crossover(List<Individual> parents, int genNumber, Location location) {
        if(parents.size() != 2) throw new IllegalArgumentException("Wrong number of parents");
        
        int[] parentA = parents.get(0).getBits();
        int[] parentB = parents.get(1).getBits();
        
        int[] child = new int[parentA.length];
        for(int i=0; i<parentA.length; i++) {
            if(prngInstance.nextBoolean()) {
                child[i] = parentA[i];
            } else {
                child[i] = parentB[i];
            }
        }
        
        Individual offspring = new Individual(child, false);
        addCrossoverToDB(parents, genNumber, offspring, location);
        
        return offspring;
    }
    
    private void addCrossoverToDB(List<Individual> parents, int genNumber, Individual child, Location location) {
		final GraphDatabaseService graphDB = GraphDB.graphDB();
		IndexManager index = graphDB.index();
		Index<Node> individualNodes = index.forNodes("individuals");
		Index<Node> locationNodes = index.forNodes("locations");
	
		Transaction tx = graphDB.beginTx();
		
		Individual parent1 = parents.get(0);
		Individual parent2 = parents.get(1);
		Relationship toParent1;
		Relationship toParent2;
		Relationship node;
		Node individualNode;
		try
		{
			individualNode = graphDB.createNode();
			individualNode.setProperty("fitness", child.getGlobalFitness());
			individualNode.setProperty("id", child.uid.toString());
			Node locationNode = locationNodes.get("locationID", location.getPosition()).next();
			node = individualNode.createRelationshipTo(locationNode, RelTypes.LOCATEDIN);
			node.setProperty("generation", genNumber);
			
			Node parentNode = individualNodes.get("id", parent1.uid.toString()).next();
			toParent1 = individualNode.createRelationshipTo(parentNode, RelTypes.PARENTOF);
			toParent1.setProperty("generation", genNumber);
			

			Node parent2Node = individualNodes.get("id", parent2.uid.toString()).next();
			toParent2 = individualNode.createRelationshipTo(parent2Node, RelTypes.PARENTOF);
			toParent2.setProperty("generation", genNumber);
			
			individualNodes.add(individualNode, "id", child.uid.toString());
								 
			tx.success();
		}
		finally
		{
		    tx.finish();
		}
    }

}
