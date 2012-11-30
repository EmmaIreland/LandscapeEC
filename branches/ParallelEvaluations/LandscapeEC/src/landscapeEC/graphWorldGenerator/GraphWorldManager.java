package landscapeEC.graphWorldGenerator;

import java.util.ArrayList;
import java.util.Collections;

public class GraphWorldManager {

	
	public static void main(String[] args) {
		ArrayList<Integer> threePointList = new ArrayList<Integer>();
		ArrayList<CommunityData> communityList = new ArrayList<CommunityData>();
		ArrayList<ScaleFreeData> scaleFreeList = new ArrayList<ScaleFreeData>();
		
		Collections.addAll(threePointList, 13,22,25);
		Collections.addAll(communityList, new CommunityData(225, 10, .6, .05), new CommunityData(576, 10, .6, .05), new CommunityData(729, 10, .6, .05),
				new CommunityData(225, 22, .8, .1), new CommunityData(576, 57, .8, .1), new CommunityData(729, 73, .8, .1));
		Collections.addAll(scaleFreeList, new ScaleFreeData(225, 2, 2), new ScaleFreeData(576, 6, 6), new ScaleFreeData(729, 7, 7),
				new ScaleFreeData(225, 3, 4), new ScaleFreeData(576, 3, 4), new ScaleFreeData(729, 3, 4));

		ThreePointPlanar threePoint = new ThreePointPlanar();
		for(Integer size : threePointList){
			threePoint.generateWorld(size);
		}
		
		CommunityWorldGenerator community = new CommunityWorldGenerator();
		for(CommunityData data : communityList){
			community.generateWorld(data.getSize(), data.getComm(), data.getCommProb(), data.getInterProb());
		}
		
		ScaleFreeWorldGenerator scaleFree = new ScaleFreeWorldGenerator();
		for(ScaleFreeData data : scaleFreeList) {
			scaleFree.generateWorld(data.getSize(), data.getConn(), data.getCorners());
		}
		
	}
	
	public GraphWorldManager() {
		
	}
	
	public void runCommunity(int size, int comm, double commProb, double interProb) {
		CommunityWorldGenerator commGen = new CommunityWorldGenerator();
		commGen.generateWorld(size, comm, commProb, interProb);
	}
	
	public void runScaleFree(int size, int conn, int corners) {
		ScaleFreeWorldGenerator scaleGen = new ScaleFreeWorldGenerator();
		scaleGen.generateWorld(size, conn, corners);
	}
	
}
