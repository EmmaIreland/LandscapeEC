package landscapeEC.util;

import java.util.HashMap;
import java.util.Map;

public class VizHelper {

	Map<Integer, String> colorMap = new HashMap<Integer, String>();
	
	public VizHelper() {
		colorMap.put(0, "0");
		colorMap.put(1, "1");
		colorMap.put(2, "2");
		colorMap.put(3, "3");
		colorMap.put(4, "4");
		colorMap.put(5, "5");
		colorMap.put(6, "6");
		colorMap.put(7, "7");
		colorMap.put(8, "8");
		colorMap.put(9, "9");
		colorMap.put(10, "A");
		colorMap.put(11, "B");
		colorMap.put(12, "C");
		colorMap.put(13, "D");
		colorMap.put(14, "E");
		colorMap.put(15, "F");
	}
	
	public String decimalColorToHexColor(int decimal) {
		int remainder = decimal % 16;
		int product = decimal/16;
		return colorMap.get(product) + colorMap.get(remainder);
	}

	public int bitsToInt(int[] bits, int start, int end) {
		int result = 0;
		for (int i=start; i<end; ++i) {
			result = 2 * result + bits[i];
		}

		return Math.abs(result);
	}
	
}
