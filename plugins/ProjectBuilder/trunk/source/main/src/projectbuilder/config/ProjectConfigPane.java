package projectbuilder.config;
//{{{ imports
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.util.Log;

import common.gui.pathbuilder.PathBuilder;
import common.gui.pathbuilder.ClasspathFilter;
import common.gui.FileTextField;
import common.gui.EasyOptionPane;

import java.util.StringTokenizer;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import javax.swing.text.JTextComponent;
import javax.swing.*;

import projectbuilder.ProjectBuilderPlugin;
import projectviewer.vpt.VPTProject;
import projectviewer.gui.OptionPaneBase;
//}}}
public class ProjectConfigPane extends EasyOptionPane {
	private VPTProject project;
	private String name;
	private HashMap<String, JComponent> map;
	//public ProjectConfigPane() { super(null, null); }
	public ProjectConfigPane(VPTProject proj, String name) {
		super("project.options."+name, proj.getProperty("project.options."+name));
		jEdit.setTemporaryProperty("options.projectbuilder."+name+".label",
			proj.getProperty("project.config.pane."+name+".label"));
		StringTokenizer tokenizer = new StringTokenizer(proj.getProperty("project.options."+name));
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String[] list = token.split(",");
			try {
				jEdit.setTemporaryProperty(list[1], proj.getProperty(list[1]));
			} catch (Exception e) {}
		}
		this.project = proj;
		this.name = name;
		map = new HashMap<String, JComponent>();
		setPropertyStore(proj.getProperties());
	}
	protected Object createComponent(String type, String label, String value, String config) {
		Log.log(Log.DEBUG,this,"Creating "+type);
		if (type.equals("textArea")) {
			ConfigTextArea c = new ConfigTextArea(label, value, config);
			addComponent(label, c);
			return c;
		}
		else if (type.equals("pathbuilder")) {
			PathBuilder builder = new PathBuilder();
			builder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			builder.setPath(value);
			addComponent(label, builder);
			return builder;
		}
		return null;
	}
	protected String parseComponent(Object comp, String name) {
		Log.log(Log.DEBUG,this,"Parsing "+comp+", name = "+name);
		if (comp instanceof ConfigTextArea) {
			ConfigTextArea textArea = (ConfigTextArea) comp;
			return textArea.getText();
		}
		else if (comp instanceof PathBuilder) {
			PathBuilder builder = (PathBuilder) comp;
			Log.log(Log.DEBUG,this,"Saving: "+builder.getPath());
			return builder.getPath();
		}
		return super.parseComponent(comp, name);
	}
	
	public String getName() {
		return "projectbuilder."+name;
	}
	
	public void _init() { super._init(); }
	public void _save() { 
		super._save();
		ProjectBuilderPlugin.updateProjectConfig(project);
	}
	
	class ConfigTextArea extends JPanel {
		private final JTextArea txt;
		private RolloverButton edit;
		public ConfigTextArea(final String label, String value, String config) {
			setLayout(new BorderLayout());
			txt = new JTextArea(10, 50);
			txt.setEditable(false);
			if (value == null) value = "";
			txt.setText(value);
			edit = new RolloverButton(GUIUtilities.loadIcon("Properties.png"));
			edit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TextDialog dialog = new TextDialog("Edit "+label, txt.getText());
						dialog.setVisible(true);
						String new_text = dialog.getText();
						if (new_text != null) {
							txt.setText(new_text);
						}
					}
			});
			/*
			JPanel buttons = new JPanel();
			buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
			buttons.add(edit);
			buttons.add(Box.createHorizontalGlue());
			*/
			add(BorderLayout.CENTER, new JScrollPane(txt));
			//add(BorderLayout.SOUTH, buttons);
			add(BorderLayout.SOUTH, edit);
		}
		public void setText(String text) {
			txt.setText(text);
		}
		public String getText() {
			return txt.getText();
		}
		class TextDialog extends JDialog {
			private JPanel contents;
			private RolloverButton close;
			private JTextArea txt;
			private boolean save;
			public TextDialog(String title, String old_text) {
				super(jEdit.getActiveView(), title, true);
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				save = false;
				contents = new JPanel(new BorderLayout());
				txt = new JTextArea(15, 100);
				txt.setText(old_text);
				close = new RolloverButton(GUIUtilities.loadIcon("Save.png"));
				close.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							save = true;
							dispose();
						}
				});
				contents.add(BorderLayout.CENTER, new JScrollPane(txt));
				contents.add(BorderLayout.SOUTH, close);
				setContentPane(contents);
				pack();
				setLocationRelativeTo(jEdit.getActiveView());
			}
			public String getText() {
				if (!save) return null;
				return txt.getText();
			}
		}
	}
}	
