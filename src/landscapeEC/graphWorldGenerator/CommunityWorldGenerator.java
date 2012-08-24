package landscapeEC.graphWorldGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CommunityWorldGenerator {

	//This generates Ring, fully connected (complete), tree, and grid shaped ontologies
	//See README in /amboro/tests/regression-testing/ for details on this program

	static String FileName = null;
	//These ints are used for multiple purposes depending on the graph type so they must be named generically.

	static int linkNum = 0;
	static Integer numOfNodes = 0;
	static Integer commNum;
	static Long seed;
	static Double commProb;
	static Double interProb;


	final static File DIR = new File("graphWorldFiles");
	static Writer output = null; 
	static Random gen = new Random();

	public static void main(String[] args) {

		if (args.length != 4 && args.length != 5) {
			throw new IllegalArgumentException("Must be four or five inputs, <Number of Nodes>, <Number of Communities>, " +
					"<Prob of Community Connection>, <Prob of Inter-Community Connection>, <optional seed>");
		}

		numOfNodes = Integer.parseInt(args[0]);
		commNum = Integer.parseInt(args[1]);
		commProb = Double.parseDouble(args[2]);
		interProb = Double.parseDouble(args[3]);
		
		
		if (args.length == 5) {
			seed = new Long(args[4]);
		}

		
		if (seed == null) {
			FileName = "CommunityWorld-" + numOfNodes + "N-" + commNum + "C-" + commProb + "CP-" +interProb + "IP";
		} else {
			FileName = "CommunityWorld-" + numOfNodes + "N-" + commNum + "C-" + commProb + "CP-" +interProb + "IP" + seed;
		}

		makeFile();
		writeWorld(makeCommunity());
	}


	private static ArrayList<ArrayList<Integer>> makeCommunity() {
		//The number of nodes will be split as evenly as possible between the number of communities. Each node has a chance equal
		//to the 5th input to be connected with any single node in it's community. Additionally, it has a chance equal to the 6th
		//input to have a connection with a random node outside of the community.
		//Note: "comm" below refers to community, not communications

		if (commProb < 0 || commProb > 1) {
			throw new IllegalArgumentException("The third input must be between or equal to 0 to 1 for community graphs.");
		}
		if (interProb < 0 || interProb > 1) {
			throw new IllegalArgumentException("The fourth input must be between or equal to 0 to 1 for community graphs.");
		}

		//community-#N-#Com-#ComProb-#InterProb.owl

		if (seed != null) {
			gen.setSeed(seed);
		}


		//put all the nodes in their communities
		//HashMap<Integer, Integer> commMap = new HashMap<Integer, Integer>();
		ArrayList<Node> nodeList = new ArrayList<Node>();
		int commSelector = numOfNodes/commNum;

		//System.out.println("---------------------------------------------------------------");
		//initialize all the nodes as well as sort them into their communities
		for (int i = 0; i < numOfNodes; i++) {
			nodeList.add(new Node(i, i/commSelector));
			//System.out.println("Node " + nodeList.get(i).getName() + " is in community " + nodeList.get(i).getCommunity());

		}
		//System.out.println("---------------------------------------------------------------");
		//add community links
		for (int i = 0; i < numOfNodes; i++) {
			Node curr = nodeList.get(i);
			for (int k = (i+1); k < numOfNodes; k++) {
				Node toNode = nodeList.get(k);
				if (curr.getCommunity().equals(toNode.getCommunity()) && !toNode.contains(curr.getName()) ) {
					if (gen.nextDouble() < commProb) {
						makeLink(i, k);
						makeLink(k, i);
						//System.out.println("Community link from " + i + " to " + k);
					}
					curr.add(toNode.getName());
					toNode.add(curr.getName());
				}
			}
		}
		//System.out.println("---------------------------------------------------------------");
		//add inter community links
		for (int i = 0; i < numOfNodes; i++) {
			Node curr = nodeList.get(i);
			if (gen.nextDouble() < interProb) {
				int rand = gen.nextInt(numOfNodes);
				while (curr.getName().equals(rand) || curr.contains(rand) || curr.getCommunity().equals(nodeList.get(rand).getCommunity())) {
					rand = gen.nextInt(numOfNodes);
				}
				//System.out.println("inter community link from " + i + " to " + rand);
				makeLink(i, rand);
				makeLink(rand, i);
				curr.add(rand);
				nodeList.get(rand).add(i);
			}
		}

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

	private static void makeFile() {
		File file = new File(DIR, FileName);
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

