package landscapeEC.graphWorldGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;

import landscapeEC.util.SharedPRNG;

public class CommunityWorldGenerator {



	static String FileName = null;
	//These ints are used for multiple purposes depending on the graph type so they must be named generically.

	static int linkNum = 0;
	static Integer numOfNodes = 0;
	static Integer commNum;
	static Long seed;
	static Double commProb;
	static Double interProb;
	static String worldType;


	final static File DIR = new File("graphWorldFiles");
	static Writer output = null; 

	public static void main(String[] args) {

		if (args.length != 5 && args.length != 6) {
			throw new IllegalArgumentException("Must be five or six inputs, <Number of Nodes>, <Number of Communities>, " +
			"<Prob of Community Connection>, <Prob of Inter-Community Connection>, <World Type>, <optional seed>");
		}

		numOfNodes = Integer.parseInt(args[0]);
		commNum = Integer.parseInt(args[1]);
		commProb = Double.parseDouble(args[2]);
		interProb = Double.parseDouble(args[3]);
		worldType = args[4];
		
		if((worldType.equals("Connected")) && (worldType.equals("Original"))) {
			throw new IllegalArgumentException("Fifth input should be Connected, or Original");
		}


		if (args.length == 6) {
			seed = new Long(args[5]);
		} else {
			seed = SharedPRNG.instance().nextLong();
			//seed = gen.nextLong();
		}

		FileName = "CommunityWorld-" + numOfNodes + "N-" + commNum + "C-" + commProb + "CP-" +interProb + "IP-" + worldType + "-" + seed;

		makeFile();
		writeWorld(makeCommunity());
		writeCorners(getCorners());
	}

	public CommunityWorldGenerator() {

	}

	public void generateWorld(int size, int comm, double commProb, double interProb) {

		this.numOfNodes = size;
		this.commNum = comm;
		this.commProb = commProb;
		this.interProb = interProb;

		seed = SharedPRNG.instance().nextLong();

		FileName = "CommunityWorld-" + numOfNodes + "N-" + commNum + "C-" + commProb + "CP-" +interProb + "IP-" + worldType + "-" + seed;

		makeFile();
		writeWorld(makeCommunity());
		writeCorners(getCorners());
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


		ArrayList<ArrayList<Integer>> worldList = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < numOfNodes; i++) {
			worldList.add(new ArrayList<Integer>());
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
					if (SharedPRNG.instance().nextDouble() < commProb) {
						worldList.get(i).add(k);
						worldList.get(k).add(i);
						//makeLink(i, k);
						//makeLink(k, i);
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
			if (SharedPRNG.instance().nextDouble() < interProb) {
				int rand = SharedPRNG.instance().nextInt(numOfNodes);
				while (curr.getName().equals(rand) || curr.contains(rand) || curr.getCommunity().equals(nodeList.get(rand).getCommunity())) {
					rand = SharedPRNG.instance().nextInt(numOfNodes);
				}
				worldList.get(i).add(rand);
				worldList.get(rand).add(i);
				//makeLink(i, rand);
				//makeLink(rand, i);
				curr.add(rand);
				nodeList.get(rand).add(i);
			}
		}
		
		//Add connections between communities in a ring formation
		if(worldType.equals("Connected")) {
			for (int j = 0; j < worldList.size(); j++) {
				if (j%commSelector == 0) {
					//add connections to either side
					if(j+commSelector >= worldList.size()){
						worldList.get(j).add(0);
					}else{
					worldList.get(j).add(j+commSelector);
					}
					if(j-commSelector < 0) {
						worldList.get(j).add(worldList.size()-1);
					}else{
					worldList.get(j).add(j-commSelector);
					}
				}
			}
		}
		
		return worldList;
	}
	
	private static boolean communityDisconnected(int community, ArrayList<ArrayList<Integer>> worldList) {
		boolean bool = true;
		
		int commSize = numOfNodes/commNum;
		
		for(int i = 0; i < commSize; i++) {
			int currLoc = (commSize*community) + i;
			
			for (int loc : worldList.get(i)) {
				if (loc < (commSize*community) || loc > (commSize*(community+1)-1)) {
					bool = false;
				}
			}
		}
		
		return bool;
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
	}

	private static ArrayList<Integer> getCorners() {

		ArrayList<Integer> corners = new ArrayList<Integer>();

		for (int i = 0; i <= commNum; i++) {
			corners.add(i*((int) numOfNodes/commNum));
		}

		return corners;
	}

	private static void writeCorners(ArrayList<Integer> corners) {

		String name = "--- # Corners\n";
		String body = "[Corners";

		for (int i = 0; i < commNum; i++) {
			body = body + " " + corners.get(i);
		}

		body = body + "]";

		try {
			output.write(name);
			output.write(body);
			output.flush();
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

