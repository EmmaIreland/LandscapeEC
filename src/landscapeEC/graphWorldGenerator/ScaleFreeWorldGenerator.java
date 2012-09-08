package landscapeEC.graphWorldGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ScaleFreeWorldGenerator {


	//This generates Ring, fully connected (complete), tree, and grid shaped ontologies
	//See README in /amboro/tests/regression-testing/ for details on this program

	static String FileName = null;
	//These ints are used for multiple purposes depending on the graph type so they must be named generically.

	static int linkNum = 0;
	static Integer numOfNodes;
	static Integer conn;
	static Long seed = null;

	//Here are the global constants that determine the variable amounts.
	//end variable amounts.

	//final static File DIR = new File(new File(new File(new File(".."),"tests"),"regression-test"),"ontologies");
	final static File DIR = new File("graphWorldFiles");
	static Writer output = null; 
	static Random gen = new Random();

	public static void main(String[] args) {
		
		if (args.length != 2 && args.length != 3) {
			throw new IllegalArgumentException("Must be two or three inputs, <Number of Nodes>, <Number of connections per new node>, <optional seed>");
		}

		numOfNodes = Integer.parseInt(args[0]);
		conn = Integer.parseInt(args[1]);

		if (args.length == 3) {
			seed = new Long(args[2]);
		} else {
			seed = gen.nextLong();
		}
		
		gen.setSeed(seed);

		if (numOfNodes <= conn) {
			throw new IllegalArgumentException("The number of nodes must be higher than the number of connections each added node has.");
		}
		if (conn <= 1) {
			throw new IllegalArgumentException("The number of connections a new node makes must be higher than one.");
		}
		
			FileName = "ScaleFreeWorld-" + numOfNodes + "N-" + conn + "C-" + seed;
		
		makeFile();
		writeWorld(makeScaleFree());
	}




	private static void writeWorld(ArrayList<ArrayList<Integer>> worldList) {
		
		for (int i = 0; i < worldList.size(); i++) {
			String name = "--- # " + i + "\n";
			String body = worldList.get(i).toString();
			body = body.replaceAll(",", "");
			body = body + "\n\n";
			
			try {
				output.write(name);
				output.write(body);
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}




	private static ArrayList<ArrayList<Integer>> makeScaleFree() {
		//the way the scale free graph will be generated is an initial line of nodes will be made. Then, more nodes will be added.
		//Each new node will have D (conn) number of connections which will go to a stochastically chosen node. A node with more
		//connections will have a higher chance of getting more connections. Whenever a node gets a connection, the node is put
		//into a hashtable with mapNum as it's key. Then, the node is connected to another node by selecting a random number on
		//the hashtable and selecting the node the key indicated.

		ArrayList<ArrayList<Integer>> worldList = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < numOfNodes; i++) {
			worldList.add(new ArrayList<Integer>());
		}

		HashMap<Integer, Integer> probMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Node> nodeMap =  new HashMap<Integer, Node>();
		int mapNum = 0;


		for (int i = 0; i < (conn-1)+((numOfNodes-conn)*conn); i++) {
			nodeMap.put(i, new Node(i));
		}

		for (int i = 0; i < conn; i ++) {
			worldList.get(i).add(i+1);
			//makeLink(i, i+1);
			nodeMap.get(i).add(i+1);
			probMap.put(mapNum, i);
			mapNum++;
			worldList.get(i+1).add(i);
			//makeLink(i+1, i);
			nodeMap.get(i+1).add(i);
			probMap.put(mapNum, (i+1));
			mapNum++;
		}

		for (int i = conn+1; i < numOfNodes; i++) {
			for (int k = 0; k < conn; k++){
				boolean bool = true;
				Integer linkTo = null;
				while(bool) {
					linkTo = gen.nextInt(probMap.size());
					if (!(probMap.get(linkTo) == i || nodeMap.get(i).contains(probMap.get(linkTo)))) {
						bool = false;
					}
				}

				Integer toNode = probMap.get(linkTo);

				worldList.get(i).add(toNode);
				//makeLink(i, toNode);
				nodeMap.get(i).add(toNode);
				probMap.put(mapNum, toNode);
				mapNum++;
				worldList.get(toNode).add(i);
				//makeLink(toNode, i);
				nodeMap.get(toNode).add(i);
				probMap.put(mapNum, i);
				mapNum++;
			}
		}
		
		return worldList;

	}
	
    private static void makeFile() {
        File file = new File(DIR, FileName);
        try {
            output = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
