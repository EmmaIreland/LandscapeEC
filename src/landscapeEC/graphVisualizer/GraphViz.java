package landscapeEC.graphVisualizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;


import landscapeEC.locality.GraphWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.World;
import landscapeEC.util.VizHelper;
import landscapeEC.util.YamlLoader;

public class GraphViz {
	//number of total snapshots
	int numOfSnapshots;
	int totalEvals;
	World<GraphWorld> world;
	static Writer output = null; 
	//this array keeps track of which snapshots we've taken. It's all false (none have been taken)
	//but as we take one we change that spot to true.
	boolean[] snapshotTakenTracker;

	public GraphViz(int snapshots, int totalEvals, World<GraphWorld> world) {
		numOfSnapshots = snapshots;
		this.totalEvals = totalEvals;
		this.world = world;
		snapshotTakenTracker = new boolean[numOfSnapshots];
		//Sets array to all false
		for (int i = 0; i < numOfSnapshots; i++){
			snapshotTakenTracker[i] = false;
		}
	}
	
	public void makeDotFile(int numEvals) {
		if(this.checkIfTakeSnapshot(numEvals)){
			this.write(numEvals);
		}
	}

	public void write(int numEvals) {
		this.makeFile("SnapshotNum" + numEvals/(totalEvals/numOfSnapshots));

		//write the header
		try {
			output.write("graph G {\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//write the first part of the file which figures out what colors to make the circles
		this.writeColors();

		try {
			output.write("\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//write the second part of the file which denotes what nodes are connected to what.
		this.writeConnections();

		try {
			output.write("}");
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void writeColors() {
		//go through all the locations
		for (Location loc : world) {
			//get "average" individual from the location
			int[] displayString = loc.getAverageIndividual();
			//get colors
			VizHelper vizHelper = new VizHelper();
			int red = vizHelper.bitsToInt(displayString, 0, displayString.length / 3);
			int green = vizHelper.bitsToInt(displayString, displayString.length / 3, 2 * displayString.length / 3);
			int blue = vizHelper.bitsToInt(displayString, 2 * displayString.length / 3, displayString.length);
			//dot likes colors as hexidecimal values so gotta convert them to that.
			String color = vizHelper.decimalColorToHexColor(red%256) + vizHelper.decimalColorToHexColor(green%256) + vizHelper.decimalColorToHexColor(blue%256);
			//write it.
			try {
				output.write("  " + loc.getPosition() + " [style=filled, fillcolor=\"#" + color + "\"]\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private void writeConnections() {
		//for each location
		for (Location loc : world) {
			//get their neighborhood list
			List<?> neighborhood = world.getNeighborhood(loc.getPosition(), 1);
			//and write it
			for (int i = 0; i < neighborhood.size(); i++) {
				try {
					output.write("  " + loc.getPosition() + "--" + neighborhood.get(i)+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void makeFile(String filename) {
		File file = new File("graphWorldSnapshots", filename);
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//The issue here is that we can't just tell it to take a snapshot at so many evals
	//because there's a low chance that we will finish a generation with exactly the 
	//right number of evals, so we have to check the number of evals are over a given 
	//threshold (each threshold is totalEvals/numOfSnapshots) and that we haven't made
	//a snapshot yet, and if that's true take one.
	private boolean checkIfTakeSnapshot(int numEvals) {
		boolean bool = false;
		//finds which threshold we are in by number of evals
		Integer currentThreshold = numEvals/(totalEvals/numOfSnapshots); 
		//checks if we've written for this threshold, if not, we do.
		if(currentThreshold != 0 && !snapshotTakenTracker[currentThreshold-1]){
			bool = true;
			snapshotTakenTracker[currentThreshold-1] = true;
		}
		return bool;
	}
}
