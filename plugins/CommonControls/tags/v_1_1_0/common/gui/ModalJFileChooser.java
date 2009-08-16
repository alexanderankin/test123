/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package common.gui;

import java.io.File;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.EnhancedDialog;

/**
 *  An implementation of JFileChooser that fixes the modal dialog being
 *	created from another JDialog. The original JFileChooser would let the
 *	calling dialog be activated, even though the JFileChooser was modal, due
 *	to the wrong parent widget being used for the dialog.
 *
 *	<p>It will also show hidden files when the "All files (including hidden)"
 *	filter is selected. This filter is added automatically when this chooser
 *	is instantiated. This is to overcome the lack of a GUI option to do this
 *	(at least on Linux) and me not willing to come up with a better
 *	solution to this (like a checkbox in the UI).</p>
 *
 *	<p>Copied from the ProjectViewer plugin.</p>
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		CC 0.9.4
 */
public class ModalJFileChooser extends JFileChooser
								implements ActionListener {

	//{{{ Private members
	private boolean 		defaultHiddenFileStatus;
	private FileFilter 		hiddenFileFilter;
	//}}}

	//{{{ +ModalJFileChooser() : <init>
	public ModalJFileChooser() {
		init();
	} //}}}

	//{{{ +ModalJFileChooser(File) : <init>
	public ModalJFileChooser(File currentDirectory) {
		super(currentDirectory);
		init();
	} //}}}

	//{{{ +ModalJFileChooser(File, FileSystemView) : <init>
	public ModalJFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
		init();
	} //}}}

	//{{{ +ModalJFileChooser(FileSystemView) : <init>
	public ModalJFileChooser(FileSystemView fsv) {
		super(fsv);
		init();
	} //}}}

	//{{{ +ModalJFileChooser(String) : <init>
	public ModalJFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
		init();
	} //}}}

	//{{{ +ModalJFileChooser(String, FileSystemView) : <init>
	public ModalJFileChooser(String currentDirectoryPath, FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	} //}}}

	//{{{ -init() : void
	private void init() {
		hiddenFileFilter = new HiddenFileFilter();
		defaultHiddenFileStatus = isFileHidingEnabled();
		addActionListener(this);
		addChoosableFileFilter(hiddenFileFilter);
		setFileFilter(getAcceptAllFileFilter());
	} //}}}

	//{{{ #createDialog(Component) : JDialog
	/** Creates the modal dialog to show this file chooser. */
	protected JDialog createDialog(Component parent) {
		JDialog parentDlg = null;
		if (parent == null) {
			parent = jEdit.getActiveView();
		} else {
			parentDlg = (parent instanceof JDialog)
							? (JDialog) parent
							: GUIUtilities.getParentDialog(parent);
		}
		JDialog dialog;
		if (parentDlg != null) {
			parent = parentDlg;
			dialog = new JDialog(parentDlg, true);
		} else {
			dialog = new JDialog(JOptionPane.getFrameForComponent(parent), true);
		}

		dialog.setTitle(getUI().getDialogTitle(this));
		dialog.getContentPane().add(this);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		return dialog;
	} //}}}

	//{{{ +setFileFilter(FileFilter) : void
	public void setFileFilter(FileFilter filter) {
		super.setFileFilter(filter);
		fireActionPerformed(FILE_FILTER_CHANGED_PROPERTY);
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(FILE_FILTER_CHANGED_PROPERTY)) {
			if (getFileFilter() == hiddenFileFilter) {
				setFileHidingEnabled(false);
			} else {
				setFileHidingEnabled(defaultHiddenFileStatus);
			}
		}
	} //}}}

	//{{{ +class _HiddenFileFilter_
	public static class HiddenFileFilter extends FileFilter {

		public boolean accept(File f) {
			return true;
		}

		public String getDescription() {
			return jEdit.getProperty("projectviewer.general.hidden_file_filter");
		}

	} //}}}

}

