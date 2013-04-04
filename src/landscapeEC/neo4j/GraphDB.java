package landscapeEC.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class GraphDB {
	public static void main(String args[]) {
		graphDB();
		Transaction tx = graphDB.beginTx();
		
		Node firstNode;
		Node secondNode;
		Relationship relationship;
		
		try
		{
			firstNode = graphDB.createNode();
			firstNode.setProperty( "message", "Hello, " );
			secondNode = graphDB.createNode();
			secondNode.setProperty( "message", "World!" );
			 
			relationship = firstNode.createRelationshipTo( secondNode, RelTypes.PARENTOF );
			relationship.setProperty( "message", "brave Neo4j " );
			tx.success();
		}
		finally
		{
		    tx.finish();
		}
		
		System.out.print( firstNode.getProperty( "message" ) );
		System.out.print( relationship.getProperty( "message" ) );
		System.out.print( secondNode.getProperty( "message" ) );
	}
	
	private static final String DB_PATH = "neo4j/DB";
	private static GraphDatabaseService graphDB;
	
	private GraphDB(){

	}
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running example before it's completed)
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	
	public static GraphDatabaseService graphDB(){
		if(graphDB == null){
			graphDB = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
			registerShutdownHook( graphDB );
		}
		return graphDB;
	}
}
