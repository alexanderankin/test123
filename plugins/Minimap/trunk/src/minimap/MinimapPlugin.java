package minimap;
import java.util.HashMap;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;

public class MinimapPlugin extends EBPlugin {

	static private HashMap<EditPane, Minimap> maps;
	
	@Override
	public void handleMessage(EBMessage message) {
		// TODO Auto-generated method stub
		super.handleMessage(message);
	}

	public void stop()
	{
		maps = null;
	}

	public void start()
	{
		maps = new HashMap<EditPane, Minimap>();
	}
	static public void show(View view) {
		EditPane editPane = view.getEditPane();
		if (maps.containsKey(editPane))
			return;
		Minimap map = new Minimap(editPane);
		maps.put(editPane, map);
		editPane.add(map);
		editPane.validate();
	}

}
