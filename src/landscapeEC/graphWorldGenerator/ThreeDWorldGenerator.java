package landscapeEC.graphWorldGenerator;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class ThreeDWorldGenerator {

	private int width;
	private int depth;
	private int height;
	private int planeArea;
	private int worldArea;
	private LinkedList<LinkedList<Integer>> listOfNeighborhoods;

	public static void main(String[] args) throws Exception{
		ThreeDWorldGenerator gen = new ThreeDWorldGenerator(4, 3, 2);
		gen.generate3DWorld();
	}

	private ThreeDWorldGenerator(int width, int depth, int height) {
		this.width = width;
		this.depth = depth;
		this.height = height;
		planeArea = width*depth;
		worldArea = width*depth*height;
	}

	private void generate3DWorld () {
		listOfNeighborhoods = new LinkedList<LinkedList<Integer>>();

		for (int i = 0; i < worldArea; i++) {
			listOfNeighborhoods.add(this.addLocations(i));
		}

		String name = "graphWorldFiles/3DWorldSize" + width + "by" + depth + "by" + height;
		this.writeYAMLFile(name);

	}

	private void writeYAMLFile(String name) {
		Writer output = null;
		File file = new File(name);
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < worldArea; i++) {
			String header = "--- # " + i + "\n";
			try {
				output.write(header);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String body = listOfNeighborhoods.get(i) + "\n\n";
			try {
				output.write(body);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private LinkedList<Integer> addLocations (int location) {
		LinkedList<Integer> loc = new LinkedList<Integer>();
// 		int x = location % worldSize;
//		int y = (location/worldSize) % worldSize;
//		int z = (location/worldSizeSq) % worldSize;
//		
//		for (int i = Math.max(x-1, 0); i <= Math.min(x+1, worldSize - 1); ++i){
//			for (int j = Math.max(y-1, 0); j <= Math.min(y+1, worldSize - 1); ++j){
//				for (int k = Math.max(z-1, 0); k <= Math.min(z+1, worldSize - 1); ++k){
//					loc.add(i+worldSize*j+worldSizeSq*k);
//				}
//			}
//		}
		int x = location % width;
		int y = (location / width) % depth;
		int z = (location / planeArea) % height;
		
		for (int i = Math.max(x-1, 0); i <= Math.min(x+1, width - 1); i++){
			for (int j = Math.max(y-1, 0); j <= Math.min(y+1, depth - 1); j++){
				for (int k = Math.max(z-1, 0); k <= Math.min(z+1, height - 1); k++){
					loc.add(i+width*j+planeArea*k);
				}
			}
		}
		loc.removeFirstOccurrence(location);
		return loc;

		//		LinkedList<Integer> loc = this.addAllLocations(location);
		//		return this.removeInvalidLocations(location, loc);
	}




}

