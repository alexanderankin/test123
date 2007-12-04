package infonode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.infonode.docking.View;
import net.infonode.docking.util.StringViewMap;

public class JEditViewMap extends StringViewMap {

	private WindowManager wm;
	
	public JEditViewMap(WindowManager wm) {
		this.wm = wm;
	}

	@Override
	public View readView(ObjectInputStream in) throws IOException {
		String name = in.readUTF();
		View v = getView(name);
		if (v == null)
			v = wm.createDummyView(name);
		return v;
	}

	@Override
	public void writeView(View view, ObjectOutputStream out) throws IOException {
		out.writeUTF(view.getName());
	}

}
