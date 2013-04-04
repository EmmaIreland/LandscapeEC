package landscapeEC.neo4j;

import org.neo4j.graphdb.RelationshipType;

	public enum RelTypes implements RelationshipType
	{
	    LOCATEDIN, PARENTOF, MUTANTOF, CROSSOVEROF
	}

