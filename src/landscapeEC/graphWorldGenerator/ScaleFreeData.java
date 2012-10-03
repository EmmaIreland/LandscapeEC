package landscapeEC.graphWorldGenerator;

public class ScaleFreeData {
	
	private int size;
	private int conn;
	private int corners;
	
	public ScaleFreeData(int size, int conn, int corners) {
		this.size = size;
		this.conn = conn;
		this.corners = corners;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getConn() {
		return conn;
	}
	
	public int getCorners() {
		return corners;
	}

}
