package clangcompletion;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

import projectviewer.ProjectManager;
import projectviewer.config.OptionsService;
import projectviewer.vpt.VPTProject;

public class ClangCompletionConfiguration extends AbstractOptionPane
{
	public static final String DEFINITIONS = "CLANG_DEFINITIONS";
	
	public static final String INCLUDES = "CLANG_INCLUDES";
	
	public static final String ARGUMENTS = "CLANG_ARGUMENTS";
	
	private JList definitions, includes, arguments;
	
	private DefaultListModel definitionModel, includesModel, argumentsModel;
	
	private VPTProject project;
	
	public ClangCompletionConfiguration(VPTProject project)
	{
		super("ClangCompletion-Configuration");
		this.project = project;
	}
	
	@Override
	protected void _init()
	{
		includesModel = getListModel(INCLUDES);
		includes = createList("Includes:", includesModel,  new Callable<String>()
		{
			public String call() 
			{
				return showIncludeSelectionDialog();
			}
		});
		addSeparator();
		
		definitionModel = getListModel(DEFINITIONS);
		definitions = createList("Pre-compiled definition:", definitionModel, new Callable<String>()
		{
			public String call()
			{
				return ShowInputDefinitionDialog();
			}
		});
		addSeparator();
		
		argumentsModel = getListModel(ARGUMENTS);
		arguments = createList("Other Arguments:", argumentsModel, new Callable<String>()
		{
			public String call()
			{
				return ShowInputArgumentDialog();
			}
		});
		addSeparator();
	}

	private void setListModel(String propertyName, DefaultListModel model)
	{
		Vector<String> list = new Vector<String>();
		for (int i = 0; i < model.size(); i++)
		{
			list.add((String) model.getElementAt(i));
		}
		setListProperty(propertyName, list);
	}
	private DefaultListModel getListModel(String propertyName)
	{
		Vector<String> list = getListProperty(propertyName);
		DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < list.size(); i++)
		{
			model.addElement(list.get(i));
		}
		return model;
	}

	private Vector<String> getListProperty(String propertyName)
	{
		return getListProperty(project, propertyName);
	}
	
	private void setListProperty(String propertyName, Vector<String> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			project.setProperty(propertyName + i, list.get(i));
		}
		
		for (int i = list.size(); true; i++)
		{
			String prop = propertyName + i;
			if (project.getProperty(prop) == null)
			{
				break;
			}else
			{
				project.removeProperty(prop);
			}
		}
	}
	
	private JList createList(String title, final DefaultListModel model, final Callable<String> callable)
	{
		addComponent(new JLabel(title));
		final JList list = new JList(model);
		addComponent(new JScrollPane(list), GridBagConstraints.HORIZONTAL);
		JPanel buttons = new JPanel();
		JButton add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String s = callable.call();
					if (s != null)
					{
						int index = list.getSelectedIndex();
						model.add(index + 1, s);
						list.setSelectedIndex(index + 1);
					}
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		JButton remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int index = list.getSelectedIndex();
				if (index >= 0)
				{
					model.removeElementAt(index);
					if (index < model.size())
					{
						list.setSelectedIndex(index);
					}else if (! model.isEmpty())
					{
						list.setSelectedIndex(model.size() - 1);
					}
				}
			}
		});
		buttons.add(add);
		buttons.add(remove);
		addComponent(buttons);
		return list;
	}

	private String ShowInputArgumentDialog()
	{
		return  (String) JOptionPane.showInputDialog(this, "Input argument:", "Arguments", 
			JOptionPane.QUESTION_MESSAGE);
	}
	
	private String ShowInputDefinitionDialog()
	{
		return  (String) JOptionPane.showInputDialog(this, "Input definition:", "Definitions", 
			JOptionPane.QUESTION_MESSAGE);
	}
	
	private String showIncludeSelectionDialog()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select include path");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int ret = fc.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION)
		{
			return null;
		}
		String dir = fc.getSelectedFile().getAbsolutePath();
		return MiscUtilities.resolveSymlinks(dir);
	}
	
	@Override
	protected void _save()
	{
		setListModel(DEFINITIONS, definitionModel);
		setListModel(INCLUDES, includesModel);
		setListModel(ARGUMENTS, argumentsModel);
	}
	
	public static Vector<String> getListProperty(VPTProject project, String propertyName)
	{
		Vector<String> list = new Vector<String>();
		int i = 0;
		while (true)
		{
			String value = project.getProperty(propertyName + i);
			if (value != null)
			{
				list.add(value);
				i++;
			}else
			{
				break;
			}
		}
		return list;
	}
	
	public static HashMap<String, Vector<String>> getProperties(String projectName)
	{
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		VPTProject project = ProjectManager.getInstance().getProject(projectName);
		if (project == null)
		{
			return map;
		}
		
		Vector<String> definitions = getListProperty(project, DEFINITIONS);
		map.put(DEFINITIONS , definitions);
		
		Vector<String> includes = getListProperty(project, INCLUDES);
		map.put(INCLUDES, includes);
		
		Vector<String> arguments = getListProperty(project, ARGUMENTS);
		map.put(ARGUMENTS, arguments);
		
		return map; 
	}

	public static class ClangCompletionOptionService implements OptionsService
	{
		public OptionGroup getOptionGroup(VPTProject proj)
		{
			return null;
		}
	
		public OptionPane getOptionPane(VPTProject proj)
		{
			return new ProjectDependencies(proj);
		}
	}
}
