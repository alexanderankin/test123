package marker;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.Gutter;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;

public class MarkerSetsPlugin extends EBPlugin {

	private static final String MARKER_SET_EXTENSION = "MarkerSetExtension";
	static private final String GLOBAL_SET = "Global";
	private static HashMap<String, MarkerSet> markerSets;
	private static MarkerSet active;
	
	public void start()
	{
		markerSets = new HashMap<String, MarkerSet>();
		active = addMarkerSet(GLOBAL_SET);
		jEdit.visit(new MarkerSetVisitor(true));
	}

	public void stop()
	{
		jEdit.visit(new MarkerSetVisitor(false));
		markerSets.clear();
		active = null;
	}

	private class MarkerSetVisitor extends JEditVisitorAdapter
	{
		boolean start;
		
		public MarkerSetVisitor(boolean start)
		{
			this.start = start;
		}
		
		public void visit(EditPane editPane) {
			if (start)
				initEditPane(editPane);
			else
				uninitEditPane(editPane);
		}
	}
	
	private void initEditPane(EditPane ep)
	{
		Gutter g = ep.getTextArea().getGutter();
		MarkerExtension ext = new MarkerExtension(ep); 
		g.addExtension(ext);
		g.putClientProperty(MARKER_SET_EXTENSION, ext);
	}
	
	private void uninitEditPane(EditPane ep)
	{
		Gutter g = ep.getTextArea().getGutter();
		MarkerExtension ext = (MarkerExtension)
			g.getClientProperty(MARKER_SET_EXTENSION);
		if (ext != null)
		{
			g.removeExtension(ext);
			g.putClientProperty(MARKER_SET_EXTENSION, null);
		}
	}
	
	private void handleEditPaneUpdate(EditPaneUpdate epu)
	{
		Object event = epu.getWhat();
		EditPane ep = epu.getEditPane();
		if (event == EditPaneUpdate.CREATED)
			initEditPane(ep);
		else if (event == EditPaneUpdate.DESTROYED)
			uninitEditPane(ep);
	}
	
	@Override
	public void handleMessage(EBMessage message)
	{
		if (message instanceof EditPaneUpdate)
			handleEditPaneUpdate((EditPaneUpdate) message);
	}

	static public Vector<String> getMarkerSetNames()
	{
		Vector<String> names = new Vector<String>();
		for (String s: markerSets.keySet())
			names.add(s);
		return names;
	}
	
	static public MarkerSet getMarkerSet(String name)
	{
		return markerSets.get(name);
	}

	static public Collection<MarkerSet> getMarkerSets()
	{
		return markerSets.values();
	}
	
	static private MarkerSet addMarkerSet(String name)
	{
		MarkerSet ms = new MarkerSet(name);
		markerSets.put(name, ms);
		return ms;
	}

	// Actions
	static public void setActiveMarkerSet()
	{
		String name = JOptionPane.showInputDialog("Set active marker set to:");
		MarkerSet ms = markerSets.get(name);
		if (ms == null)
		{
			ms = addMarkerSet(name);
			ms.setColor(Color.green);
		}
		active = ms;
	}
	static public void useGlobalMarkerSet()
	{
		active = markerSets.get(GLOBAL_SET);
	}
	static public void toggleMarker(View view)
	{
		Buffer b = view.getBuffer();
		JEditTextArea ta = view.getTextArea();
		FileMarker m = new FileMarker(b.getPath(), ta.getCaretLine(), '\0');
		active.toggle(m);
	}
}

