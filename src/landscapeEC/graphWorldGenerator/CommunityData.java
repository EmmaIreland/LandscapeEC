package landscapeEC.graphWorldGenerator;

public class CommunityData {
	
	private int size;
	private int comm;
	private double commProb;
	private double interProb;
	
	public CommunityData(int size, int comm, double commProb, double interProb) {
		this.size = size;
		this.comm = comm;
		this.commProb = commProb;
		this.interProb = interProb;
	}

	public int getSize() {
		return size;
	}
	
	public int getComm() {
		return comm;
	}
	
	public double getCommProb() {
		return commProb;
	}

	public double getInterProb() {
		return interProb;
	}
}
