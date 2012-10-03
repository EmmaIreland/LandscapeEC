package landscapeEC.graphWorldGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;



public class ThreePointPlanar {

	final static File DIR = new File("graphWorldFiles");
	static Writer output = null; 
	static String FileName = null;
	static Integer L = null;
	static Integer numOfNodes = null;


	public static void main(String[] args) {

		if (args.length != 1) {
			throw new IllegalArgumentException("Must have one input, the number of \"levels\" to the world, the number of nodes is (L+1)^2+2(L)");
		}

		L = Integer.parseInt(args[0]);
		
		numOfNodes = ((L+1) * (L+1)) + 2*L;

		FileName = "ThreePointPlanar-" + L + "L-" + numOfNodes + "N";

		makeFile();
		writeWorld(makeThreePoint());
		writeCorners(getCorners());

	}
	
	public ThreePointPlanar() {
		
	}
	
	public void generateWorld(int num) {
		L = num;
		
		numOfNodes = ((L+1) * (L+1)) + 2*L;

		FileName = "ThreePointPlanar-" + L + "L-" + numOfNodes + "N";

		makeFile();
		writeWorld(makeThreePoint());
		writeCorners(getCorners());
	}


	private static ArrayList<ArrayList<Integer>> makeThreePoint() {

		ArrayList<ArrayList<Integer>> worldList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < numOfNodes; i++) {
			worldList.add(new ArrayList<Integer>());
		}
		/*this method constructs the world. The world is a hexagonal lattice arranged in
		an equilateral triangle with the point up. The main for loop creates the bulk of 
		the world in a level system. If you draw horizontal lines through the points where
		lines meet in the lattice each line is a layer of a level, there are two layers
		in a level, so every two lines is a level. The top layer has the points that connect
		to two different points below it at an angle. The lower layer has the points that 
		connect to one point directly below it. Whenever we make a connection we always connect
		both ways, so we can constuct from the top down and make all the connections.
		
		After the main for loop there is a floating connection, another smaller for loop and a
		second floating connection. The floating connections are the corners, and the for loop
		creates the top layer of the last level that rounds off the bottom. The second layer is
		never implicitly made but the creation of the upper layer makes it by the two way links.
		
		*/
		//creates world step by step
		for (int i = 1; i < L+1; i++) {
			//do top of level
			for (int j = (i*i)-1; j < (i*i)-1+i; j++) {
				worldList.get(j).add(j+i);
				worldList.get(j+i).add(j);
				
				worldList.get(j).add(j+i+1);
				worldList.get(j+i+1).add(j);
			}
			for (int k = (i*i)+i-1; k < ((i+1)*(i+1))-1; k++) {
				worldList.get(k).add(k+i+1);
				worldList.get(k+i+1).add(k);
			}
		}
		
		//creates left corner
		worldList.get(((L+1)*(L+1))-1).add(((L+1)*(L+1))+L);
		worldList.get(((L+1)*(L+1))+L).add(((L+1)*(L+1))-1);
		
		//creates "bottom" except for corners
		for (int j = ((L+1)*(L+1)); j < ((L+1)*(L+1))+L-1; j++) {
			worldList.get(j).add(j+L);
			worldList.get(j+L).add(j);
			
			worldList.get(j).add(j+L+1);
			worldList.get(j+L+1).add(j);
		}
		
		//creates right corner
		worldList.get((L+1)*(L+1)+L-1).add((L+1)*(L+1)+(2*L)-1);
		worldList.get((L+1)*(L+1)+(2*L)-1).add((L+1)*(L+1)+L-1);

		
		return worldList;
	}
	
	private static ArrayList<Integer> getCorners() {
		
		ArrayList<Integer> corners = new ArrayList<Integer>();
		
		corners.add(0);
		corners.add(((L+1)*(L+1))-1);
		corners.add(((L+1)*(L+1))+L);
		
		return corners;
	}
	
	private static void writeCorners(ArrayList<Integer> corners) {
		
		String name = "--- # Corners\n";
		String body = "[Corners";
		
		body = body + " " + corners.get(0);
		body = body + " " + corners.get(1);
		body = body + " " + corners.get(2);
		
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

	private static void makeFile() {
		File file = new File(DIR, FileName);
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
