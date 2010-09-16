package locality;

public class Location {
	private final int[] position;
	
	public Location(int[] aPosition) {
		position = aPosition;
	}

	public String getPositionString() {
		StringBuilder result = new StringBuilder("");
		for(int i = 0; i < position.length; i++) {
			result.append(position[i]);
			if(i != position.length-1) {
				result.append(", ");
			}
		}
		
		return result.toString();
	}

}
