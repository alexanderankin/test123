package options;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;
import org.gjt.sp.jedit.gui.RolloverButton;

import ctags.CtagsInterfacePlugin;
import db.TagDB;

@SuppressWarnings("serial")
public class DirsOptionPane extends AbstractOptionPane {

	private static final String DIR_ORIGIN = TagDB.DIR_ORIGIN;
	static public final String OPTION = CtagsInterfacePlugin.OPTION;
	static public final String MESSAGE = CtagsInterfacePlugin.MESSAGE;
	static public final String DIRS = OPTION + "dirs.";
	JList dirs;
	DefaultListModel dirsModel;
	
	public DirsOptionPane() {
		super("CtagsInterface-Dirs");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		dirsModel = new DefaultListModel();
		Vector<String> trees = getDirs();
		for (int i = 0; i < trees.size(); i++)
			dirsModel.addElement(trees.get(i));
		dirs = new JList(dirsModel);
		JScrollPane scroller = new JScrollPane(dirs);
		scroller.setBorder(BorderFactory.createTitledBorder(
				jEdit.getProperty(MESSAGE + "dirs")));
		addComponent(scroller, GridBagConstraints.HORIZONTAL);
		JPanel buttons = new JPanel();
		JButton add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		buttons.add(add);
		JButton remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		buttons.add(remove);
		JButton tag = new JButton("Tag");
		buttons.add(tag);
		addComponent(buttons);

		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				VFSFileChooserDialog chooser = new VFSFileChooserDialog(
					GUIUtilities.getParentDialog(DirsOptionPane.this),
					jEdit.getActiveView(), System.getProperty("user.home"),
					VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false, false);
				chooser.setTitle("Select root of source tree");
				chooser.setVisible(true);
				if (chooser.getSelectedFiles() == null)
					return;
				String dir = chooser.getSelectedFiles()[0];
				dirsModel.addElement(MiscUtilities.resolveSymlinks(dir));
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int i = dirs.getSelectedIndex();
				if (i >= 0)
					dirsModel.removeElementAt(i);
			}
		});
		tag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int i = dirs.getSelectedIndex();
				if (i >= 0) {
					String tree = (String) dirsModel.getElementAt(i);
					CtagsInterfacePlugin.refreshOrigin(DIR_ORIGIN, tree);
				}
			}
		});
	}

	public void save() {
		Vector<String> names = new Vector<String>();
		int nDirs = dirsModel.size(); 
		for (int i = 0; i < nDirs; i++)
			names.add((String) dirsModel.getElementAt(i));
		CtagsInterfacePlugin.updateOrigins(DIR_ORIGIN, names);
	}
	
	static public Vector<String> getDirs() {
		return CtagsInterfacePlugin.getDB().getOrigins(DIR_ORIGIN);
	}
}
