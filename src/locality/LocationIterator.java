package locality;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocationIterator implements Iterator<List<Integer>> {
	
	private List<Integer> start, end, current;
	private int numDimensions;

	public LocationIterator(List<Integer> start, List<Integer> end) {
		for (int i = 0; i < start.size(); i++) {
			if(start.get(i) > end.get(i)) {
				throw new IllegalArgumentException("Bad range for Location Iterator");
			}
		}
		
		this.start = start;
		this.end = end;
		this.current = new ArrayList<Integer>(start);
		this.numDimensions = start.size();
	}
	
	public LocationIterator(List<Integer> position, int radius) {
//	    startPos = 
//	    this(startPos, endPos);
	}
	
	@Override
	public boolean hasNext() {
		if (current.get(numDimensions-1) >= end.get(numDimensions-1)) {
			return false;
		}
		return true;
	}

	@Override
	public List<Integer> next() {
		List<Integer> result = new ArrayList<Integer>(current);
		
		current.set(0, current.get(0)+1);
		
		for(int i = 0; i < numDimensions-1; i++) {
			if(current.get(i) >= end.get(i)) {
				current.set(i, start.get(i));
				current.set(i+1, current.get(i+1)+1);
			}
		}
		
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove is not supported on Location Iterators");
	}

}
