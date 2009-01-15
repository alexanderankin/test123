package minimap;

import java.util.Map;
import java.util.HashMap;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;

public class MinimapPlugin extends EditPlugin {

	private static Map<EditPane, Minimap> maps;

	public void stop() {
		maps = null;
	}

	public void start() {
		maps = new HashMap<EditPane, Minimap>();
	}
	
	public static void show(View view) {
		EditPane editPane = view.getEditPane();
		if (maps.containsKey(editPane))
			return;
		Minimap map = new Minimap(editPane);
		map.start();
		maps.put(editPane, map);
	}
	
	public static void hide(View view) {
		EditPane editPane = view.getEditPane();
		if (! maps.containsKey(editPane))
			return;
		Minimap map = maps.get(editPane);
		map.stop();
	}

}
