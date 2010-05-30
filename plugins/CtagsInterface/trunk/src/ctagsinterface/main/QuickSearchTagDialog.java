package ctagsinterface.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.lucene.document.Document;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

import ctagsinterface.index.TagIndex;
import ctagsinterface.index.TagIndex.DocHandler;
import ctagsinterface.index.TagIndex.Origin;
import ctagsinterface.index.TagIndex.OriginType;
import ctagsinterface.options.ProjectsOptionPane;

@SuppressWarnings("serial")
public class QuickSearchTagDialog extends JDialog {

	enum Mode {
		SUBSTRING,
		PREFIX
	};
	private Mode mode;
	private JTextField name;
	private JList tags;
	private DefaultListModel model;
	private View view;
	private Vector<QuickSearchTag> tagNames;
	private String baseQuery;
	private Timer filterTimer;
	private String query;
	private boolean showImmediately;
	private JCheckBox caseSensitive;

	/** This window will contains the scroll with the items. */
	final JWindow window = new JWindow(this);

	public QuickSearchTagDialog(View view, Mode mode)
	{
		this(view, mode, "Search tag", null, false);
	}

	public QuickSearchTagDialog(View view, Mode mode, String title,
		String query, boolean showImmediately)
	{
		super(view, title, false);
		this.view = view;
		this.mode = mode;
		this.query = query;
		this.showImmediately = showImmediately;
		JPanel p = new JPanel();
		p.add(new JLabel("Type part of the tag name:"));
		name = new JTextField(30);
		p.add(name);
		caseSensitive = new JCheckBox("Case-sensitive", false);
		p.add(caseSensitive);
		add(p, BorderLayout.NORTH);
		model = new DefaultListModel();
		tags = new JList(model);
		tags.setBorder(BorderFactory.createEtchedBorder());
		tags.setCellRenderer(new TagListCellRenderer());
		window.setContentPane(new JScrollPane(tags));
		name.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setFilter();
			}
			public void insertUpdate(DocumentEvent e) {
				setFilter();
			}
			public void removeUpdate(DocumentEvent e) {
				setFilter();
			}
		});
		name.addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e) {
					if (handledByList(e)) {
						tags.dispatchEvent(e);
					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						setVisible(false);
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						jumpToSelected();
					}
				}
			}
		);
		tags.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				jumpToSelected();
			}
		});
		tags.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				name.dispatchEvent(e);
			}

			public void keyPressed(KeyEvent e) {
				if (!handledByList(e)) {
					name.dispatchEvent(e);
				}
				else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
					jumpToSelected();
				}
			}
		});
		filterTimer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyFilter();
			}
		});
		filterTimer.setRepeats(false);
		prepareData();
		pack();
		setLocationRelativeTo(view);
		setVisible(true);
		if (showImmediately)
			applyFilter();
	}

	private void prepareData()
	{
		TagIndex index = CtagsInterfacePlugin.getIndex();
		String s = "";
		if (query == null)
		{
			if (ProjectsOptionPane.getSearchActiveProjectOnly())
			{
				String project = CtagsInterfacePlugin.getProjectWatcher().
					getActiveProject(view);
				if (project != null)
				{
					Origin origin = index.getOrigin(OriginType.PROJECT,
						project, false);
					s = index.getOriginScopedQuery(origin);
				}
			}
		}
		else
			s = query;
		switch (mode) {
		case SUBSTRING:
			tagNames = new Vector<QuickSearchTag>();
			index.runQuery(s, TagIndex.MAX_RESULTS, new DocHandler()
			{
				public void handle(Document doc)
				{
					tagNames.add(new QuickSearchTag(doc));
				}
			});
			break;
		case PREFIX:
			baseQuery = s;
			break;
		}
	}

	private void jumpToSelected() {
		QuickSearchTag t = (QuickSearchTag) tags.getSelectedValue();
		if (t != null)
			CtagsInterfacePlugin.jumpTo(view, t.file, t.line);
		dispose();
	}

	private void setFilter() {
		if (filterTimer.isRunning())
			filterTimer.restart();
		else
			filterTimer.start();
	}

	private void applyFilter()
	{
		model.removeAllElements();
		final String input = caseSensitive.isSelected() ? name.getText():
			name.getText().toLowerCase();
		if (showImmediately || (! input.isEmpty()))
		{
			switch (mode)
			{
			case SUBSTRING:
				for (int i = 0; i < tagNames.size(); i++)
				{
					QuickSearchTag t = tagNames.get(i);
					String name = t.name;
					if (! caseSensitive.isSelected())
						name = name.toLowerCase();
					if (name.contains(input))
						model.addElement(t);
				}
				break;
			case PREFIX:
				TagIndex index = CtagsInterfacePlugin.getIndex();
				String s = baseQuery;
				if (! input.isEmpty())
				{
					if (s.length() > 0)
						s = s + " AND ";
					String field = caseSensitive.isSelected() ?
						TagIndex._NAME_FLD : TagIndex.NAME_FLD;
					s = s + field + ":" + input + "*";
				}
				index.runQuery(s, TagIndex.MAX_RESULTS, new DocHandler()
				{
					public void handle(Document doc)
					{
						String name = doc.getField(TagIndex._NAME_FLD).stringValue();
						if (! caseSensitive.isSelected())
							name = name.toLowerCase();
						if (name.startsWith(input))
							model.addElement(new QuickSearchTag(doc));
					}
				});
				break;
			}
		}
		if (model.isEmpty())
		{
			window.setVisible(false);
		}
		else
		{
			tags.setVisibleRowCount(Math.min(10, model.size()));
			window.pack();
			window.setVisible(true);
		}
	}

	private static boolean handledByList(KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_DOWN ||
		e.getKeyCode() == KeyEvent.VK_UP ||
		e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
		e.getKeyCode() == KeyEvent.VK_PAGE_UP;
	}

	public void setVisible(boolean b) {
		Rectangle bounds = getBounds();
		window.setLocation(bounds.x, bounds.y + bounds.height);
		GUIUtilities.requestFocus(this, name);
		window.setVisible(false);
		super.setVisible(b);
	}

	static private class QuickSearchTag
	{
		String file;
		int line;
		String name;
		String desc;
		String kind;
		public QuickSearchTag(Document doc)
		{
			StringBuffer text = new StringBuffer();
			name = doc.get(TagIndex._NAME_FLD);
			text.append(name);
			kind = doc.get("kind");
			if (kind != null)
				text.append(" (" + kind + ")");
			file = doc.get(TagIndex._PATH_FLD);
			String lineStr = doc.get(TagIndex.LINE_FLD);
			if (lineStr != null)
				line = Integer.valueOf(lineStr);
			else
				line = -1;
			desc = text.toString();
			if (isValid())
				desc = desc + "   [" + file + ":" + line + "]";
		}
		public boolean isValid()
		{
			return (desc.length() > 0 && file != null && line >= 0);
		}
		public String toString()
		{
			return desc;
		}
		public ImageIcon getIcon()
		{
			return KindIconProvider.getIcon(kind);
		}
	}

	static public class TagListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel l = (JLabel) super.getListCellRendererComponent(
				list, value, index, isSelected, cellHasFocus);
			if (value instanceof QuickSearchTag) {
				ImageIcon icon = ((QuickSearchTag)value).getIcon();
				if (icon != null)
					l.setIcon(icon);
			}
			return l;
		}

	}

}
