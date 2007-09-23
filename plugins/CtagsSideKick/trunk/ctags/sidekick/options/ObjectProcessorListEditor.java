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

import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.ListObjectProcessor;
import ctags.sidekick.ObjectProcessorEditor;
import ctags.sidekick.ObjectProcessorManager;

@SuppressWarnings("serial")
public class ObjectProcessorListEditor extends JPanel implements
		IModeOptionPane {

	JList list;
	DefaultListModel processorModel;
	HashMap<String, DefaultListModel> modeModels;
	String mode;
	ObjectProcessorManager manager;
	
	public ObjectProcessorListEditor(ObjectProcessorManager manager)
	{
		setLayout(new BorderLayout());
		this.manager = manager;
		String title = manager.getProcessorTypeName() + "s";
		TitledBorder b = new TitledBorder(title);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(b);
		add(topPanel);
		
		modeModels = new HashMap<String, DefaultListModel>();
		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		topPanel.add(new JScrollPane(list), BorderLayout.CENTER);
		
		JButton add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				addProcessor();
			}
		});
		JButton remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				removeProcessor();
			}
		});
		JButton up = new RolloverButton(GUIUtilities.loadIcon("ArrowU.png"));
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				moveProcessorUp();
			}
		});
		JButton down = new RolloverButton(GUIUtilities.loadIcon("ArrowD.png"));
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				moveProcessorDown();
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
		processorModel = getModelForMode(mode);
		modeModels.put(mode, processorModel);
		list.setModel(processorModel);
	}

	private DefaultListModel getModelForMode(String mode) {
		DefaultListModel model = modeModels.get(mode);
		if (model == null)
		{
			model = new DefaultListModel();
			ListObjectProcessor processor = manager.getProcessorForMode(mode);
			Vector<IObjectProcessor> processors = processor.getProcessors();
			for (int i = 0; i < processors.size(); i++)
				model.addElement(processors.get(i));
		}
		return model;
	}
	
	protected void addProcessor() {
		IObjectProcessor processor = new ObjectProcessorEditor(
				GUIUtilities.getParentDialog(this), manager).getProcessor();
		if (processor != null) {
			int index = list.getSelectedIndex();
			processorModel.add(index + 1, processor);
			list.setSelectedIndex(index + 1);
		}
	}

	protected void removeProcessor() {
		int index = list.getSelectedIndex();
		if (index >= 0) {
			processorModel.remove(index);
			if (index < processorModel.size())
				list.setSelectedIndex(index);
		}
	}

	private void moveProcessorDown() {
		int index = list.getSelectedIndex();
		if (index < processorModel.size() - 1)
			moveProcessor(index, index + 1);
	}
	private void moveProcessorUp() {
		int index = list.getSelectedIndex();
		if (index > 0)
			moveProcessor(index, index - 1);
	}
	private void moveProcessor(int from, int to) {
		IObjectProcessor current = (IObjectProcessor) processorModel.get(from);
		IObjectProcessor other = (IObjectProcessor) processorModel.get(to);
		processorModel.set(to, current);
		processorModel.set(from, other);
		list.setSelectedIndex(to);
	}

	public void save()
	{
		Iterator models = modeModels.entrySet().iterator();
		while (models.hasNext()) {
			Entry e = (Entry) models.next();
			String mode = (String) e.getKey();
			DefaultListModel model = (DefaultListModel) e.getValue();
			ListObjectProcessor processor = manager.createProcessorForMode(mode);
			for (int i = 0; i < model.getSize(); i++)
				processor.add((IObjectProcessor) model.get(i));
			manager.setProcessorForMode(mode, processor);
		}
	}

	public void resetCurrentMode() {
		DefaultListModel model = getModelForMode(null);
		processorModel.clear();
		for (int i = 0; i < model.size(); i++)
			processorModel.addElement(model.elementAt(i)); 
	}

}
