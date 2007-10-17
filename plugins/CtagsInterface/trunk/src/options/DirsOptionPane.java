package options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

import ctags.CtagsInterfacePlugin;

@SuppressWarnings("serial")
public class DirsOptionPane extends AbstractOptionPane {

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
		addComponent(scroller);
		JPanel buttons = new JPanel();
		JButton add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		buttons.add(add);
		JButton remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		buttons.add(remove);
		addComponent(buttons);

		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Select root of source tree");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ret = fc.showOpenDialog(DirsOptionPane.this);
				if (ret != JFileChooser.APPROVE_OPTION)
					return;
				dirsModel.addElement(
					fc.getSelectedFile().getAbsolutePath());
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int i = dirs.getSelectedIndex();
				if (i >= 0)
					dirsModel.removeElementAt(i);
			}
		});
	}

	public void save() {
		int nDirs = dirsModel.size(); 
		jEdit.setIntegerProperty(DIRS + "size", nDirs);
		for (int i = 0; i < nDirs; i++)
			jEdit.setProperty(DIRS + i, (String)dirsModel.getElementAt(i));
	}
	
	static public Vector<String> getDirs() {
		Vector<String> dirs = new Vector<String>();
		int nDirs = jEdit.getIntegerProperty(DIRS + "size");
		for (int i = 0; i < nDirs; i++)
			dirs.add(jEdit.getProperty(DIRS + i));
		return dirs;
	}
}
