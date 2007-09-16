package ctags.sidekick;

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

public class ModeMapperPane extends JPanel implements IModeOptionPane {
	JList mapperList;
	DefaultListModel mapperModel;
	HashMap<String, DefaultListModel> mapperModels;
	JList componentList;
	DefaultListModel componentListModel;
	String mode;
	
	public ModeMapperPane()
	{
		setLayout(new BorderLayout());
		TitledBorder b = new TitledBorder("Grouping");
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(b);
		add(topPanel);
		
		mapperModels = new HashMap<String, DefaultListModel>();
		mapperList = new JList();
		mapperList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		topPanel.add(new JScrollPane(mapperList), BorderLayout.CENTER);
		
		JButton add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				addMapper();
			}
		});
		JButton remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				removeMapper();
			}
		});
		JButton up = new RolloverButton(GUIUtilities.loadIcon("ArrowU.png"));
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				mapperUp();
			}
		});
		JButton down = new RolloverButton(GUIUtilities.loadIcon("ArrowD.png"));
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				mapperDown();
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
		mapperModel = getModelForMode(mode);
		mapperModels.put(mode, mapperModel);
		mapperList.setModel(mapperModel);
	}

	private DefaultListModel getModelForMode(String mode) {
		DefaultListModel model = mapperModels.get(mode);
		if (model == null)
		{
			model = new DefaultListModel();
			ListTreeMapper mapper;
			mapper = (ListTreeMapper) MapperManager.getMapperForMode(mode);
			Vector<ITreeMapper> components = mapper.getComponents();
			for (int i = 0; i < components.size(); i++)
				model.addElement(components.get(i));
		}
		return model;
	}
	
	protected void addMapper() {
		ITreeMapper mapper = new TreeMapperEditor(
				GUIUtilities.getParentDialog(this)).getMapper();
		if (mapper != null) {
			int index = mapperList.getSelectedIndex();
			mapperModel.add(index + 1, mapper);
			mapperList.setSelectedIndex(index + 1);
		}
	}

	protected void removeMapper() {
		int index = mapperList.getSelectedIndex();
		if (index >= 0) {
			mapperModel.remove(index);
			if (index < mapperModel.size())
				mapperList.setSelectedIndex(index);
		}
	}

	private void mapperDown() {
		int index = mapperList.getSelectedIndex();
		if (index < mapperModel.size() - 1) {
			ITreeMapper current = (ITreeMapper) mapperModel.get(index);
			ITreeMapper other = (ITreeMapper) mapperModel.get(index + 1);
			mapperModel.set(index + 1, current);
			mapperModel.set(index, other);
			mapperList.setSelectedIndex(index + 1);
		}
	}
	private void mapperUp() {
		int index = mapperList.getSelectedIndex();
		if (index > 0) {
			ITreeMapper current = (ITreeMapper) mapperModel.get(index);
			ITreeMapper other = (ITreeMapper) mapperModel.get(index - 1);
			mapperModel.set(index - 1, current);
			mapperModel.set(index, other);
			mapperList.setSelectedIndex(index - 1);
		}
	}

	public void save()
	{
		Iterator models = mapperModels.entrySet().iterator();
		while (models.hasNext()) {
			Entry e = (Entry) models.next();
			String mode = (String) e.getKey();
			DefaultListModel model = (DefaultListModel) e.getValue();
			ListTreeMapper mapper = new ListTreeMapper();
			for (int i = 0; i < model.getSize(); i++)
				mapper.add((ITreeMapper) model.get(i));
			MapperManager.setMapperForMode(mode, mapper);
		}
	}

	public void resetCurrentMode() {
		DefaultListModel model = getModelForMode(null);
		mapperModel.clear();
		for (int i = 0; i < model.size(); i++)
			mapperModel.addElement(model.elementAt(i)); 
	}

}
