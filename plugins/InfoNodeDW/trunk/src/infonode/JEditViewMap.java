package infonode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import net.infonode.docking.View;
import net.infonode.docking.util.StringViewMap;

public class JEditViewMap extends StringViewMap {

	private WindowManager wm;
	private HashMap<View, String> names;
	
	public JEditViewMap(WindowManager wm) {
		this.wm = wm;
		names = new HashMap<View, String>();
	}

	@Override
	public void addView(String id, View view) {
		super.addView(id, view);
		names.put(view, id);
	}

	@Override
	public void removeView(String id) {
		View v = getView(id);
		if (v == null)
			return;
		super.removeView(id);
		names.remove(v);
	}

	@Override
	public View readView(ObjectInputStream in) throws IOException {
		String name = in.readUTF();
		System.err.println("readView: " + name);
		if (getView(name) == null)
			wm.constructDockableView(name);
		System.err.println("readView: " + getView(name));
		return getView(name);
	}

	@Override
	public void writeView(View view, ObjectOutputStream out) throws IOException {
		System.err.println("writeView: " + names.get(view));
		out.writeUTF(names.get(view));
	}

}
