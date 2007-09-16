package ctags.sidekick;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.GUIUtilities;

import ctags.sidekick.mappers.ITreeMapper;

@SuppressWarnings("serial")
public class TreeMapperEditor extends JDialog {

	private static final String GEOMETRY = "tree.mapper.editor.geometry";
	private ITreeMapper mapper;
	private JComboBox mapperList;
	private JTextField paramTF;
	
	public TreeMapperEditor(JDialog parent) {
		super(parent, "Mapper Editor", true);
		loadGeometry();
		mapper = null;
		addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
            	saveGeometry();	
            }
        });
		setLayout(new GridLayout(0, 1));
		JPanel mapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		mapperPanel.add(new JLabel("Mapper name:"));
		mapperList = new JComboBox();
		Vector<String> names = MapperManager.getMapperNames();
		Collections.sort(names);
		for (int i = 0; i < names.size(); i++)
			mapperList.addItem(names.get(i));
		mapperPanel.add(mapperList);
		add(mapperPanel);
		JPanel paramPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		paramPanel.add(new JLabel("Parameters:"));
		paramTF = new JTextField(40);
		paramPanel.add(paramTF);
		add(paramPanel);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close(true);
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close(false);
			}
		});
		buttons.add(ok);
		buttons.add(cancel);
		add(buttons);
		pack();
		setVisible(true);
	}

	protected void close(boolean b) {
		if (b) {
			String name = (String) mapperList.getSelectedItem();
			mapper = MapperManager.getMapper(name);
			if (mapper != null)
				mapper = mapper.getMapper(paramTF.getText());
		} else
			mapper = null;
		saveGeometry();
		setVisible(false);
	}

	public ITreeMapper getMapper() {
		return mapper;
	}
	private void loadGeometry() {
		GUIUtilities.loadGeometry(this, GEOMETRY);
	}
	private void saveGeometry() {
		GUIUtilities.saveGeometry(this, GEOMETRY);
	}
}
