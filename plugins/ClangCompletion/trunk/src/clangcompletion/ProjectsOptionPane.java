package clangcompletion;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.HashMap;
import java.util.Vector;
import java.io.*;
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
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;

import projectviewer.ProjectManager;
import projectviewer.config.OptionsService;
import projectviewer.vpt.VPTProject;
import projectviewer.ProjectViewer;
public class ProjectsOptionPane extends AbstractOptionPane
{
	public static final String DEFINITIONS = "CLANG_DEFINITIONS";
	
	public static final String INCLUDES = "CLANG_INCLUDES";
	
	public static final String PRECOMPILEDS = "CLANG_PRECOMPILEDS";
	
	public static final String ARGUMENTS = "CLANG_ARGUMENTS";
	
	private JList definitions, includes, precompileds, arguments;
	
	private DefaultListModel definitionModel, includesModel, precompiledsModel, argumentsModel;
	
	private VPTProject project;
	
	public ProjectsOptionPane(VPTProject project)
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
		//addSeparator();
		
		definitionModel = getListModel(DEFINITIONS);
		definitions = createList("Pre-compiled definition:", definitionModel, new Callable<String>()
		{
			public String call()
			{
				return ShowInputDefinitionDialog();
			}
		});
		//addSeparator();
		
		argumentsModel = getListModel(ARGUMENTS);
		arguments = createList("Additional arguments:", argumentsModel, new Callable<String>()
		{
			public String call()
			{
				return ShowInputArgumentsDialog();
			}
		});
		//addSeparator();
		
		precompiledsModel = getListModel(PRECOMPILEDS);
		precompileds = createListPrecompileds("Pre-compiled headers(PTH):", precompiledsModel, new Callable<String>()
		{
			public String call()
			{
				return ShowInputPrecompiledsDialog();
			}
		});
		//addSeparator();
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
	
	
	
	private JList createListPrecompileds(String title, final DefaultListModel model, final Callable<String> callable)
	{
		//addComponent(new JLabel(title));
		final JList list = new JList(model);
		
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
						
						File pth = Util.getPTHFileOfActiveProject();
						if(pth != null)
						{
							pth.delete();
						}
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
					File pth = Util.getPTHFileOfActiveProject();
					if(pth != null)
					{
						pth.delete();
					}
				}
			}
		});
		
		
		JButton refresh = new RolloverButton(GUIUtilities.loadIcon("Reload.png"));
		refresh.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					File pth = Util.getPTHFileOfActiveProject();
					if(pth != null)
					{
						pth.delete();
					}
					Util.generatePTHFileForActiveProject();
					try
					{
						Thread.sleep(1000);
					}catch(Exception ex)
					{
					}
				}
			});
		buttons.add(new JLabel(title));
		buttons.add(add);
		buttons.add(remove);
		buttons.add(refresh);
		addComponent(buttons);
		addComponent(new JScrollPane(list), GridBagConstraints.HORIZONTAL);
		return list;
	}
	
	private JList createList(String title, final DefaultListModel model, final Callable<String> callable)
	{
		//addComponent(new JLabel(title));
		final JList list = new JList(model);
		
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
		
		buttons.add(new JLabel(title));
		buttons.add(add);
		buttons.add(remove);
		addComponent(buttons);
		addComponent(new JScrollPane(list), GridBagConstraints.HORIZONTAL);
		return list;
	}

	private String ShowInputPrecompiledsDialog()
	{
		View view =  jEdit.getActiveView();
		String path = jEdit.getProperty("projectviewer.filechooser.directory",
										 System.getProperty("user.home"));
		String [] result = GUIUtilities.showVFSFileDialog(GUIUtilities.getParentDialog(this),
			view, path, VFSBrowser.OPEN_DIALOG, false);
		if(result != null && result.length > 0)
		{
			return result[0];
		}else
		{
			return null;			
		}
	}
	
	 private String ShowInputArgumentsDialog()
	{
		return  (String) JOptionPane.showInputDialog(this, "Input argument:", "Argument", 
			JOptionPane.QUESTION_MESSAGE);
	}
	
	private String ShowInputDefinitionDialog()
	{
		return  (String) JOptionPane.showInputDialog(this, "Input definition:", "Definitions", 
			JOptionPane.QUESTION_MESSAGE);
	}
	
	private String showIncludeSelectionDialog()
	{
		View view =  jEdit.getActiveView();
		String path = jEdit.getProperty("projectviewer.filechooser.directory",
										 System.getProperty("user.home"));
		String [] result = GUIUtilities.showVFSFileDialog(GUIUtilities.getParentDialog(this),
			view, path, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false);
		if(result != null && result.length > 0)
		{
			return result[0];
		}else
		{
			return null;			
		}
	}
	
	@Override
	protected void _save()
	{
		setListModel(DEFINITIONS, definitionModel);
		setListModel(INCLUDES, includesModel);
		setListModel(PRECOMPILEDS, precompiledsModel);
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
		
		Vector<String> precompileds = getListProperty(project, PRECOMPILEDS);
		map.put(PRECOMPILEDS, precompileds);
		
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
			return new ProjectsOptionPane(proj);
		}
	}
}
