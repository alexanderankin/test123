package superabbrevs;

import java.util.SortedSet;

public interface ModeService {
	public SortedSet<String> getModesNames();
	public String getCurrentModeName();
}
