//{{{ imports
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.browser.*;
//}}}

public class GfxViewHistoryDialog extends JDialog
		implements ListSelectionListener,ActionListener {
	private JButton buttonFile_local,buttonFile_remote;
	private PropertyChangeSupport changes;
	private Object oldURL;
	private String oldURL_display;
	private GfxViewListModel model;
	private JList list;
	private JButton butOk,butCancel;
	private int previousIndex;

	//{{{ +GfxViewHistoryDialog(Frame) : <init>
	public GfxViewHistoryDialog(Frame owner) {
		super(owner,"Choose picture from GfxView history",true);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		changes = new PropertyChangeSupport(this);
		model = new GfxViewListModel();

		list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.addListSelectionListener(this);
		JScrollPane pane = new JScrollPane(list);
		getContentPane().add(pane, BorderLayout.CENTER);

		JPanel panel = new JPanel(new FlowLayout());
		butOk = new JButton("Pick this entry");
		butOk.addActionListener(this);
		butCancel = new JButton("Cancel");
		butCancel.addActionListener(this);
		panel.add(butOk);
		panel.add(butCancel);
		getContentPane().add(panel, BorderLayout.SOUTH);

		pack();
		setLocation((owner.getWidth()-this.getWidth())/2,
			(owner.getHeight()-this.getHeight())/2);
	} //}}}

	//{{{ +show() : void
	public void show() {
		super.show();
		previousIndex=list.getSelectedIndex();
	}//}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent evt)	{
		if (evt.getSource()==butCancel) {
			selectValue(previousIndex);
		}
		dispose(); // also for butOk
	}//}}}

	//{{{ +valueChanged(ListSelectionEvent) : void
	public void valueChanged(ListSelectionEvent evt) {
		// TODO: A examiner et parachever !!
		if (!evt.getValueIsAdjusting()) {
				selectValue(list.getSelectedIndex());
		}
	} //}}}


	//{{{ -selectValue(int) : void
	private void selectValue(int index) {
			if (index!=-1) {
				Object newURL = model.getElementAt(index);
				changes.firePropertyChange("UrlGfxView-path",oldURL,newURL);
				oldURL = newURL;

				String newURL_display;
				if (newURL instanceof URL) {
					newURL_display = newURL.toString().substring(newURL.toString().lastIndexOf('/')+1);
				}
				else {
					newURL_display = newURL.toString().substring(newURL.toString().lastIndexOf(File.separator)+1);
				}
				changes.firePropertyChange("UrlGfxView-display",oldURL_display,newURL_display);
				oldURL_display = newURL_display;
			}
	}//}}}

	//{{{ +loadNext() : void
	public void loadNext() {
		int size=list.getModel().getSize();
		int index = list.getSelectedIndex();
		if (size > 0 && index < (size-1)) { //  index=-1 is also matched !
			list.setSelectedIndex(index+1);
		}
	}//}}}

	//{{{ +loadPrev() : void
	public void loadPrev() {
		int index = list.getSelectedIndex();
		if (index > 0) {
			list.setSelectedIndex(index-1);
		}
	}//}}}

	//{{{ +addEntry(Object) : void
	public void addEntry(Object urlPath) {
		model.addEntry(urlPath);
	}//}}}

	//{{{ +removeEntry(Object) : void
	public void removeEntry(Object urlPath) {
		model.removeEntry(urlPath);
	}//}}}


	//{{{ +addPropertyChangeListener(PropertyChangeListener) : void
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
			changes.addPropertyChangeListener(listener);
	} //}}}

	//{{{ +removePropertyChangeListener(PropertyChangeListener) : void
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
			changes.removePropertyChangeListener(listener);
	} //}}}
}

/* :folding=explicit:tabSize=2:indentSize=2: */