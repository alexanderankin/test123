package marker;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JFileChooser;
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

import marker.FileMarker.Selection;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.Gutter;
import org.gjt.sp.jedit.textarea.GutterPopupHandler;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MarkerSetsPlugin extends EditPlugin {

	private static final String MARKER_SETS_ELEM = "MarkerSets";
	private static final String MARKER_SET_ELEM = "MarkerSet";
	private static final String ACTIVE_ATTR = "active";
	static public final String OPTION = "options.MarkerSets.";
	private static final String MARKER_SET_EXTENSION = "MarkerSetExtension";
	static private final String GLOBAL_SET = "Global";
	private static HashMap<String, MarkerSet> markerSets;
	private static MarkerSet active;
	private static String xmlFile;
	private static Vector<ChangeListener> listeners;
	private boolean replaceBuiltInMarkers = false;

	public enum Event {
		MARKER_SET_ADDED,
		MARKER_SET_REMOVED,
		MARKER_ADDED,
		MARKER_REMOVED,
		MARKER_SET_CHANGED,
		ACTIVE_MARKER_SET_CHANGED
	}
	
	public interface ChangeListener
	{
		// e - the change that occurred
		// marker - the marker associated with the change, or null
		// markerSet - the markerSet associated with the change
		void changed(Event e, FileMarker marker, MarkerSet markerSet);
	}
	
	public void start()
	{
		listeners = new Vector<ChangeListener>();
		markerSets = new HashMap<String, MarkerSet>();
		File f = getPluginHome();
		if (! f.exists())
			f.mkdir();
		xmlFile = f.getAbsolutePath() + File.separator + "markerSets.xml";
		loadState();
		jEdit.visit(new MarkerSetVisitor(true));
		EditBus.addToBus(this);
		replaceBuiltInMarkers(MarkerSetsOptions.shouldReplaceBuiltInMarkers());
	}

	public void stop()
	{
		replaceBuiltInMarkers(false);
		EditBus.removeFromBus(this);
		saveState();
		jEdit.visit(new MarkerSetVisitor(false));
		markerSets.clear();
		active = null;
	}

	static public void loadState()
	{
		importXml(xmlFile);
	}
	static public void saveState()
	{
		exportXml(xmlFile);
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
	
	static public void notifyChange(Event e, FileMarker m, MarkerSet ms)
	{
		for (ChangeListener cl: listeners)
			cl.changed(e, m, ms);
	}
	
	static public void notifyChange(Event e, MarkerSet ms)
	{
		notifyChange(e, null, ms);
	}
	
	static private void addMarkerSet(MarkerSet ms)
	{
		MarkerSet current = markerSets.get(ms.getName()); 
		if (current != null)
			removeMarkerSet(current);
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

	@EBHandler
	public void handleEditPaneUpdate(EditPaneUpdate epu)
	{
		Object event = epu.getWhat();
		EditPane ep = epu.getEditPane();
		if (event == EditPaneUpdate.CREATED)
			initEditPane(ep);
		else if (event == EditPaneUpdate.DESTROYED)
			uninitEditPane(ep);
		MarkerSetManager dockable = (MarkerSetManager)
			ep.getView().getDockableWindowManager().getDockableWindow(
				"marker-set-manager");
		if (dockable != null)
			dockable.bufferChanged(ep.getBuffer());
	}
	@EBHandler
	public void handleBufferUpdate(BufferUpdate bu)
	{
		for (MarkerSet ms: markerSets.values())
			ms.handleBufferUpdate(bu);
	}
	@EBHandler
	public void handlePropertiesChanged(PropertiesChanged pc)
	{
		boolean newValue = MarkerSetsOptions.shouldReplaceBuiltInMarkers();
		if (replaceBuiltInMarkers == newValue)
			return;
		replaceBuiltInMarkers = newValue;
		replaceBuiltInMarkers(replaceBuiltInMarkers);
	}

	public void replaceBuiltInMarkers(final boolean replace)
	{
		jEdit.visit(new JEditVisitorAdapter() {
			@Override
			public void visit(final EditPane editPane)
			{
				final JEditTextArea textArea = editPane.getTextArea();
				Gutter g = textArea.getGutter();
				g.setSelectionPopupHandler(new GutterPopupHandler()
				{
					public void handlePopup(int x, int y, int line)
					{
						if (replace)
							toggleMarker((Buffer)textArea.getBuffer(), line);
						else
						{
							Buffer buffer = editPane.getBuffer();
							buffer.addOrRemoveMarker('\0',
								buffer.getLineStartOffset(line));
						}
					}
				});
			}
		});
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
		if (markerSets.remove(ms.getName()) != null)
			notifyChange(Event.MARKER_SET_REMOVED, ms);
		if (active == ms)
			useGlobalMarkerSet();
	}
	
	static private void importXml(String file)
	{
		String activeName = GLOBAL_SET;
		File f = new File(file);
		if (f.exists())
		{
			// Do not test 'file' using File.canRead(), it can return false even
			// when the file is readable and can be successfully parsed.
			Document doc = null;
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(f);
				doc.getDocumentElement().normalize();
				NodeList markerSetNodes = doc.getElementsByTagName(MARKER_SET_ELEM);
				for (int i = 0; i < markerSetNodes.getLength(); i++)
				{
					MarkerSet ms = new MarkerSet((Element) markerSetNodes.item(i));
					addMarkerSet(ms);
				}
				activeName = doc.getDocumentElement().getAttribute(ACTIVE_ATTR);
				if ((activeName == null) || (activeName.length() == 0))
					activeName = GLOBAL_SET;
			} catch (final Exception e) {
				try
				{
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(jEdit.getActiveView(),
								"Failed to load marker sets from XML. Error: " + e.getMessage());
						}
					});
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		active = markerSets.get(activeName);
		if (active == null)
			active = addMarkerSet(activeName);
	}
	static private void exportXml(String file)
	{
		// Do not test 'file' using File.canWrite(), it can return false even
		// when the file is writable and can be successfully built.
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element root = doc.createElement(MARKER_SETS_ELEM);
			root.setAttribute(ACTIVE_ATTR, active.getName());
			doc.appendChild(root);
			for (MarkerSet ms: markerSets.values())
				ms.exportXml(root);
			Source source = new DOMSource(doc);
	        File f = new File(file);
	        FileOutputStream fos = new FileOutputStream(f);
	        Result result = new StreamResult(fos);
	        Transformer trans = TransformerFactory.newInstance().newTransformer();
	        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
	        trans.transform(source, result);
	        fos.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Failed to save marker sets. Error:\n" +
				Arrays.toString(e.getStackTrace()));
			return;
		}
	}

	static public void jump(final View view, final String file, final int line,
		final Vector<Selection> selections)
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
				if (ta.getLineCount() <= line)
					return;
				int offset = ta.getLineStartOffset(line);
				ta.setCaretPosition(offset);
				boolean first = true;
				if (selections != null) {
					for (Selection s: selections) {
						org.gjt.sp.jedit.textarea.Selection.Range r =
							new org.gjt.sp.jedit.textarea.Selection.Range(
								offset+s.start, offset+s.end);
						if(ta.isMultipleSelectionEnabled() || (! first))
							ta.addToSelection(r);
						else
							ta.setSelection(r);
					}
				}
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

	static public boolean setActiveMarkerSet(String activeName)
	{
		if ((activeName == null) || (activeName.length() == 0))
			return false;
		MarkerSet newActive = markerSets.get(activeName);
		if (newActive == null)
			return false;
		active = newActive;
		notifyChange(Event.ACTIVE_MARKER_SET_CHANGED, active);
		return true;
	}

	// Interface for plugins
	
	static public void toggleMarker(FileMarker marker)
	{
		if (active.toggle(marker))
			notifyChange(Event.MARKER_ADDED, marker, active);
		else
			notifyChange(Event.MARKER_REMOVED, marker, active);
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
		setActiveMarkerSet(name);
	}
	static public void useGlobalMarkerSet()
	{
		active = markerSets.get(GLOBAL_SET);
	}
	static private void toggleMarker(final Buffer b, int line)
	{
		FileMarker m = new FileMarker(b, line);
		if (active.toggle(m))
			notifyChange(Event.MARKER_ADDED, m, active);
		else
			notifyChange(Event.MARKER_REMOVED, m, active);
	}
	static public void toggleMarker(View view)
	{
		toggleMarker(view.getBuffer(), view.getTextArea().getCaretLine());
	}
	static public void jumpToMarker(View view)
	{
		String s = JOptionPane.showInputDialog(view, "Marker:");
		if (s == null || s.length() == 0)
			return;
		for (MarkerSet ms: markerSets.values())
		{
			FileMarker marker = ms.getMarkerByShortcut(s);
			if (marker != null)
			{
				marker.jump(view);
				break;
			}
		}
	}
	static public void importMarkerSets(View view)
	{
		JFileChooser fc = new JFileChooser(xmlFile);
		if (fc.showOpenDialog(view) != JFileChooser.APPROVE_OPTION)
			return;
		importXml(fc.getSelectedFile().getAbsolutePath());
	}
	static public void exportMarkerSets(View view)
	{
		JFileChooser fc = new JFileChooser(xmlFile);
		if (fc.showSaveDialog(view) != JFileChooser.APPROVE_OPTION)
			return;
		exportXml(fc.getSelectedFile().getAbsolutePath());
	}
	static public MarkerSet getActiveMarkerSet()
	{
		return active;
	}
}

