package marker;

import java.awt.Color;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.Gutter;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MarkerSetsPlugin extends EBPlugin {

	static public final String OPTION = "options.MarkerSets.";
	private static final String MARKER_SET_EXTENSION = "MarkerSetExtension";
	static private final String GLOBAL_SET = "Global";
	private static HashMap<String, MarkerSet> markerSets;
	private static MarkerSet active;
	private static String xmlFile;
	private static Vector<ChangeListener> listeners;
	
	public enum Event {
		MARKER_SET_ADDED,
		MARKER_SET_REMOVED,
		MARKER_ADDED,
		MARKER_REMOVED,
		MARKER_SET_CHANGED
	}
	
	public interface ChangeListener
	{
		// e - the change that occurred
		// o - the object (marker or marker set) for which the change occurred
		void changed(Event e, Object o);
	}
	
	public void start()
	{
		listeners = new Vector<ChangeListener>();
		markerSets = new HashMap<String, MarkerSet>();
		File f = getPluginHome();
		if (! f.exists())
			f.mkdir();
		xmlFile = f.getAbsolutePath() + File.separator + "markerSets.xml";
		f = new File(xmlFile);
		if (f.canRead())
			importXml(xmlFile);
		active = markerSets.get(GLOBAL_SET);
		if (active == null)
			active = addMarkerSet(GLOBAL_SET);
		jEdit.visit(new MarkerSetVisitor(true));
	}

	public void stop()
	{
		jEdit.visit(new MarkerSetVisitor(false));
		markerSets.clear();
		active = null;
	}

	static public void addChangeListener(ChangeListener cl)
	{
		if (! listeners.contains(cl))
			listeners.add(cl);
	}
	static public void removeChangeListener(ChangeListener cl)
	{
		listeners.remove(cl);
	}
	
	static private void notifyChange(Event e, Object o)
	{
		for (ChangeListener cl: listeners)
			cl.changed(e, o);
	}
	
	static private void addMarkerSet(MarkerSet ms)
	{
		if (markerSets.containsKey(ms.getName()))
			return;
		markerSets.put(ms.getName(), ms);
		notifyChange(Event.MARKER_SET_ADDED, ms);
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
		Collections.sort(names);
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
		addMarkerSet(ms);
		return ms;
	}
	static private MarkerSet addMarkerSet(String name, Color c)
	{
		MarkerSet ms = new MarkerSet(name);
		ms.setColor(c);
		addMarkerSet(ms);
		return ms;
	}
	static public void removeMarkerSet(MarkerSet ms)
	{
		if (ms == markerSets.get(GLOBAL_SET)) // Cannot remove global marker set
			return;
		if (markerSets.remove(ms) != null)
			notifyChange(Event.MARKER_SET_REMOVED, ms);
		if (active == ms)
			useGlobalMarkerSet();
	}
	
	static private void importXml(String file)
	{
		Document doc = null;
		try {
			File f = new File(file);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(f);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			System.err.println(e.getStackTrace());
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Failed to load marker sets from XML. Error: " + e.getMessage());
			return;
		}
		NodeList markerSetNodes = doc.getElementsByTagName("MarkerSet");
		for (int i = 0; i < markerSetNodes.getLength(); i++)
		{
			MarkerSet ms = new MarkerSet((Element) markerSetNodes.item(i));
			markerSets.put(ms.getName(), ms);
		}
	}
	static private void exportXml(String file)
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element root = doc.createElement("MarkerSets");
			doc.appendChild(root);
			for (MarkerSet ms: markerSets.values())
				ms.exportXml(root);
			Source source = new DOMSource(doc);
	        File f = new File(file);
	        Result result = new StreamResult(f);
	        Transformer trans = TransformerFactory.newInstance().newTransformer();
	        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
	        trans.transform(source, result);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Failed to save marker sets. Error:\n" + e.getMessage());
			return;
		}
	}

	static public void jump(final View view, final String file, final int line)
	{
		if (file == null)
			return;
		final Buffer buffer = jEdit.openFile(view, file);
		if(buffer == null) {
			view.getStatus().setMessage("Unable to open: " + file);
			return;
		}
		final Runnable moveCaret = new Runnable() {
			public void run() {
				JEditTextArea ta = view.getTextArea();
				ta.setCaretPosition(ta.getLineStartOffset(line));
			}
		};
		if (buffer.isLoaded())
		{
			moveCaret.run();
		}
		else
		{
			buffer.addBufferListener(new BufferAdapter() {
				@Override
				public void bufferLoaded(JEditBuffer buffer) {
					SwingUtilities.invokeLater(moveCaret);
				}
			});
		}
	}
	
	// Actions
	static public void setActiveMarkerSet(View view)
	{
		String current = (active == null) ? null : active.getName();
		MarkerSetSelectionDialog dlg = new MarkerSetSelectionDialog(view, current);
		dlg.setVisible(true);
		String name = dlg.getSelectedName();
		if (name == null)
			return;
		Color c = dlg.getSelectedColor();
		MarkerSet ms = markerSets.get(name);
		if (ms == null)
			ms = addMarkerSet(name, c);
		else
		{
			if (! ms.getColor().equals(c))
			{
				ms.setColor(c);
				notifyChange(Event.MARKER_SET_CHANGED, ms);
			}
		}
		exportXml(xmlFile);
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
		FileMarker m = new FileMarker(b.getPath(), ta.getCaretLine(), "");
		if (active.toggle(m))
			notifyChange(Event.MARKER_ADDED, m);
		else
			notifyChange(Event.MARKER_REMOVED, m);
		exportXml(xmlFile);
	}
}

