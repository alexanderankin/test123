package ctags;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import options.GeneralOptionPane;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.jedit.textarea.TextArea;

@SuppressWarnings("serial")
public class Preview extends JPanel implements DefaultFocusComponent,
	CaretListener, ListSelectionListener, EBComponent {

	View view;
	JList tags;
	DefaultListModel tagModel;
	TextArea text;
	boolean first = true;
	String file;
	Timer timer;
	boolean tracking;
	
	Preview(View view) {
		super(new BorderLayout());
		this.view = view;
		tracking = false;
		file = null;
		tagModel = new DefaultListModel();
		tags = new JList(tagModel);
		tags.setCellRenderer(new TagListCellRenderer());
		tags.setVisibleRowCount(4);
		tags.addListSelectionListener(this);
		text = new TextArea(true);
		EditPane.initPainter(text.getPainter());
		text.getPainter().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2 && file != null) {
					CtagsInterfacePlugin.jumpToOffset(Preview.this.view, file,
						text.getCaretPosition());
				}
			}
		});
		text.setBuffer(new JEditBuffer());
		propertiesChanged();
		text.getBuffer().setMode(ModeProvider.instance.getMode("text"));
		text.setMinimumSize(new Dimension(150, 50));
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
			new JScrollPane(tags), text);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(100);
		add(split, BorderLayout.CENTER);
		EditBus.addToBus(this);
		this.addHierarchyListener(new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent e) {
				if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) > 0 &&
					tracking) {
					tracking = false;
					Preview.this.view.getTextArea().removeCaretListener(Preview.this);	
				}
			}
		});
		this.addComponentListener(new ComponentListener() {
			private void update() {
				boolean isShown = (Preview.this.isVisible() &&
					Preview.this.getWidth() > 0 && Preview.this.getHeight() > 0); 
				if (isShown && (! tracking)) {
					tracking = true;
					Preview.this.view.getTextArea().addCaretListener(Preview.this);	
				} else if ((! isShown) && tracking) {
					tracking = false;
					Preview.this.view.getTextArea().removeCaretListener(Preview.this);	
				}
			}
			public void componentHidden(ComponentEvent arg0) {
				update();
			}
			public void componentMoved(ComponentEvent arg0) {
			}
			public void componentResized(ComponentEvent arg0) {
				update();
			}
			public void componentShown(ComponentEvent arg0) {
				update();
			}
		});
	}

	private void propertiesChanged()	{
		String wrap;
		if (GeneralOptionPane.getPreviewWrap())
			wrap = "soft";
		else
			wrap = "none";
		text.getBuffer().setProperty("wrap", wrap);
		EditPane.initPainter(text.getPainter());
	}
	public void previewTag() {
		String name = CtagsInterfacePlugin.getDestinationTag(Preview.this.view);
		if (name == null)
			return;
		Vector<Tag> tags = CtagsInterfacePlugin.queryTag(name);
		tagModel.clear();
		for (int i = 0; i < tags.size(); i++)
			tagModel.addElement(tags.get(i));
		if (! tags.isEmpty())
			this.tags.setSelectedIndex(0);
	}
	public void caretUpdate(CaretEvent e) {
		int delay = GeneralOptionPane.getPreviewDelay(); 
		if (delay > 0) {
			timer = new Timer(delay, new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					previewTag();
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
		else
			previewTag();
	}
	
	public void valueChanged(ListSelectionEvent e) {
		int index = tags.getSelectedIndex();
		if (index < 0)
			return;
		Tag t = (Tag) tagModel.getElementAt(index);
		file = t.getFile();
		int line = t.getLine();
		if (line > -1)
		{
			JEditBuffer buffer = text.getBuffer();
			buffer.setReadOnly(false);
			text.setText(getContents(file));
			Mode mode = ModeProvider.instance.getModeForFile(file, buffer.getLineText(0));
			if (mode == null)
				mode = ModeProvider.instance.getMode("text");
			buffer.setMode(mode);
			text.scrollTo(line, 0, true);
			text.setCaretPosition(text.getLineStartOffset(line - 1));
			buffer.setReadOnly(true);
		}
	}

	public void focusOnDefaultComponent() {
		tags.requestFocus();
	}
	
	static public String getContents(String path) {
		StringBuffer contents = new StringBuffer();
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (input!= null)
					input.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return contents.toString();
	}

	public void handleMessage(EBMessage message) {
		if (message instanceof PropertiesChanged) {
			propertiesChanged();
		}
	}

	private final class TagListCellRenderer extends DefaultListCellRenderer {
		//private Font tagListFont = new Font("Monospaced", Font.PLAIN, 12);
		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
			Tag tag = (Tag) tagModel.getElementAt(index);
			//l.setFont(tagListFont );
			l.setText(getText(tag));
			ImageIcon icon = tag.getIcon();
			if (icon != null)
				l.setIcon(icon);
			return l;
		}
		String getText(Tag tag) {
			StringBuffer s = new StringBuffer();
			s.append(tag.getName());
			String signature = tag.getExtension("signature");
			if (signature != null && signature.length() > 0)
				s.append(signature);
			s.append("   ");
			s.append(tag.getFile());
			int line = tag.getLine();
			if (line > -1)
				s.append(":" + line);
			return s.toString();
		}
	}

}
