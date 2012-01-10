package landscapeEC.problem.sat.operators.localizedMutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.problem.IndividualComparator;

public class WorldCrawl implements ConcentrationRanker{
    private GridWorld world;
    private int maxGroupSize = 0;
    private int[] maxGroupSpecies = null;
    static List<Vector> unprocessed = new ArrayList<Vector>();
    private List<Vector> waitingList = new LinkedList<Vector>();
    private static Hashtable<Vector, Integer> speciesConcentrationMap = new Hashtable<Vector, Integer>();
    
    private Vector left;
    private Vector right;
    private Vector up;
    private Vector down;
    private ArrayList<Vector> directions = new ArrayList<Vector>();
    
    public WorldCrawl(){
	Integer[] set = new Integer[2];
	set[0]=-1;
	set[1]=0;
	left = new Vector(set);
	set[0]=1;
	right = new Vector(set);
	set[0]=0;
	set[1]=-1;
	down=new Vector(set);
	set[1]=1;
	up=new Vector(set);
	directions.add(up);
	directions.add(down);
	directions.add(left);
	directions.add(right);
    }
    @Override
    public int getAmp(Vector vector) {
	if(speciesConcentrationMap.containsKey(vector))
        return speciesConcentrationMap.get(vector);
	System.out.println("Something bad happened!");
	return 0;
    }

    @Override
    public void initialize(GridWorld newWorld, int generationNumber){
	world = newWorld;
        unprocessed.clear();
        Vector current;
        for(Location l : world){
            if(!l.getIndividuals().isEmpty()){
        	unprocessed.add(l.getPosition());
            }
        }
        
        List<Vector> currentGroup;
        while(!unprocessed.isEmpty()){
            current = unprocessed.get(unprocessed.size()-1);
            unprocessed.remove(unprocessed.size()-1);
            currentGroup = worldCrawl(current);
            if(currentGroup.size()>maxGroupSize){
                maxGroupSize=currentGroup.size();
                maxGroupSpecies=findBestInCell(currentGroup.get(0));
            }
            for(Vector v : currentGroup){
        	speciesConcentrationMap.put(v, currentGroup.size());
            }
            
        }
        
    }
    
    private List<Vector> worldCrawl(Vector current){
	List<Vector> result = new ArrayList<Vector>();
	result.add(current);
	int[] species = findBestInCell(current);
	Vector next = current.plus(down);
        if(unprocessed.contains(next) && species.equals(findBestInCell(next))){
            unprocessed.remove(next);
            result.addAll(continueCrawl(next, species));
        }
        next = current.plus(left);
        if(unprocessed.contains(next)&& species.equals(findBestInCell(next))){
            unprocessed.remove(next);
            result.addAll(continueCrawl(next, species));
        }
	return result;
    }
    
    private List<Vector> continueCrawl(Vector current, int[] species){
	List<Vector> result = new ArrayList<Vector>();
	result.add(current);
	Vector next;
	for(Vector v : directions){
	    next = current.plus(v);
	    if(unprocessed.contains(next)){
		if(findBestInCell(next).equals(species)){
                    unprocessed.remove(next);
		    result.addAll(continueCrawl(next, species));
		}
	    }
	}
	return result;
    }
    
    private int[] findBestInCell(Vector position) {
        if (world.getIndividualsAt(position).isEmpty()) {
            return null;
        }
        IndividualComparator comparator = IndividualComparator.getComparator();
        return Collections.max(world.getIndividualsAt(position), comparator).getBits();
    }

}
