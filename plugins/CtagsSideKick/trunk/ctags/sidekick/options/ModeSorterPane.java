package ctags.sidekick.options;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.RolloverButton;

import ctags.sidekick.SorterManager;
import ctags.sidekick.TreeSorterEditor;
import ctags.sidekick.sorters.ITreeSorter;
import ctags.sidekick.sorters.ListSorter;

public class ModeSorterPane extends JPanel implements IModeOptionPane {
	JList SorterList;
	DefaultListModel SorterModel;
	HashMap<String, DefaultListModel> SorterModels;
	JList componentList;
	DefaultListModel componentListModel;
	String mode;
	
	public ModeSorterPane()
	{
		setLayout(new BorderLayout());
		TitledBorder b = new TitledBorder("Sorting");
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(b);
		add(topPanel);
		
		SorterModels = new HashMap<String, DefaultListModel>();
		SorterList = new JList();
		SorterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		topPanel.add(new JScrollPane(SorterList), BorderLayout.CENTER);
		
		JButton add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				addSorter();
			}
		});
		JButton remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				removeSorter();
			}
		});
		JButton up = new RolloverButton(GUIUtilities.loadIcon("ArrowU.png"));
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SorterUp();
			}
		});
		JButton down = new RolloverButton(GUIUtilities.loadIcon("ArrowD.png"));
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SorterDown();
			}
		});

		JPanel buttons = new JPanel(new GridLayout(1, 0));
		buttons.add(add);
		buttons.add(remove);
		buttons.add(up);
		buttons.add(down);
		topPanel.add(buttons, BorderLayout.SOUTH);
		
		mode = null;
	}

	public void modeSelected(String mode) {
		this.mode = mode;
		SorterModel = getModelForMode(mode);
		SorterModels.put(mode, SorterModel);
		SorterList.setModel(SorterModel);
	}

	private DefaultListModel getModelForMode(String mode) {
		DefaultListModel model = SorterModels.get(mode);
		if (model == null)
		{
			model = new DefaultListModel();
			ListSorter Sorter;
			Sorter = (ListSorter) SorterManager.getSorterForMode(mode);
			Vector<ITreeSorter> components = Sorter.getComponents();
			for (int i = 0; i < components.size(); i++)
				model.addElement(components.get(i));
		}
		return model;
	}
	
	protected void addSorter() {
		ITreeSorter Sorter = new TreeSorterEditor(
				GUIUtilities.getParentDialog(this)).getSorter();
		if (Sorter != null) {
			int index = SorterList.getSelectedIndex();
			SorterModel.add(index + 1, Sorter);
			SorterList.setSelectedIndex(index + 1);
		}
	}

	protected void removeSorter() {
		int index = SorterList.getSelectedIndex();
		if (index >= 0) {
			SorterModel.remove(index);
			if (index < SorterModel.size())
				SorterList.setSelectedIndex(index);
		}
	}

	private void SorterDown() {
		int index = SorterList.getSelectedIndex();
		if (index < SorterModel.size() - 1) {
			ITreeSorter current = (ITreeSorter) SorterModel.get(index);
			ITreeSorter other = (ITreeSorter) SorterModel.get(index + 1);
			SorterModel.set(index + 1, current);
			SorterModel.set(index, other);
			SorterList.setSelectedIndex(index + 1);
		}
	}
	private void SorterUp() {
		int index = SorterList.getSelectedIndex();
		if (index > 0) {
			ITreeSorter current = (ITreeSorter) SorterModel.get(index);
			ITreeSorter other = (ITreeSorter) SorterModel.get(index - 1);
			SorterModel.set(index - 1, current);
			SorterModel.set(index, other);
			SorterList.setSelectedIndex(index - 1);
		}
	}

	public void save()
	{
		Iterator models = SorterModels.entrySet().iterator();
		while (models.hasNext()) {
			Entry e = (Entry) models.next();
			String mode = (String) e.getKey();
			DefaultListModel model = (DefaultListModel) e.getValue();
			ListSorter Sorter = new ListSorter();
			for (int i = 0; i < model.getSize(); i++)
				Sorter.add((ITreeSorter) model.get(i));
			SorterManager.setSorterForMode(mode, Sorter);
		}
	}

	public void resetCurrentMode() {
		DefaultListModel model = getModelForMode(null);
		SorterModel.clear();
		for (int i = 0; i < model.size(); i++)
			SorterModel.addElement(model.elementAt(i)); 
	}

}
