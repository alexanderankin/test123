package rename;

import java.util.*;

public class PropertyComparator implements Comparator {
	private static PropertyComparator instance;
	
	private PropertyComparator() {}
	
	public static PropertyComparator getInstance() {
		if (instance == null) {
			instance = new PropertyComparator();
		}
		return instance;
	}
	
	public int compare(Object first, Object second) {
		return first.toString().compareTo(second);
	}
}
