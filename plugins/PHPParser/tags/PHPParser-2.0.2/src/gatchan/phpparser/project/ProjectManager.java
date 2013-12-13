package gatchan.phpparser.project;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.Task;
import org.gjt.sp.util.ThreadUtilities;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * The project Manager.
 *
 * @author Matthieu Casanova
 * @author $Id$
 */
public class ProjectManager
{
	/**
	 * The current project.
	 */
	private Project project;

	/**
	 * The instance of the project manager.
	 */
	private static ProjectManager instance;

	/**
	 * jEdit settings directory.
	 */
	public static final String settingsDirectory = jEdit.getSettingsDirectory();

	/**
	 * PHPParser project directory.
	 */
	public static final String projectDirectory =
		settingsDirectory + File.separator + "PHPParserPlugin" + File.separator + "projects";

	/**
	 * PHPParser project version.
	 */
	public static final String projectVersion =
		jEdit.getProperty("plugin.gatchan.phpparser.projects.formatversion");
	private static final String PROJECT_NAME_PROPERTY = "gatchan.phpparser.project.file";

	private ProjectList projectList;

	/**
	 * Instantiate the project manager.
	 */
	private ProjectManager()
	{
		init();
		String projectFilePath = jEdit.getProperty(PROJECT_NAME_PROPERTY);
		if (projectFilePath != null)
		{
			Log.log(Log.DEBUG, this, "Opening project " + projectFilePath);
			File projectFile =
				new File(projectDirectory + File.separator + projectFilePath + ".project.props");
			openProject(projectFile);
		}
	}

	/**
	 * Returns the project list. It's also a {@link ListModel} and a {@link ComboBoxModel}
	 *
	 * @return the project list
	 */
	public ProjectList getProjectList()
	{
		return projectList;
	}

	/**
	 * Returns the current project.
	 *
	 * @return the current project
	 */
	public Project getProject()
	{
		return project;
	}

	/**
	 * Return the instance of the project manager.
	 *
	 * @return the project manager
	 */
	public static ProjectManager getInstance()
	{
		if (instance == null)
		{
			instance = new ProjectManager();
		}
		return instance;
	}

	/**
	 * Get the list of the projects.
	 */
	private void init()
	{
		if (settingsDirectory != null)
		{
			File projectDirFile = new File(projectDirectory);
			if (projectDirFile.exists())
			{
				String[] projectsNames = projectDirFile.list(new FilenameFilter()
				{
					public boolean accept(File dir, String name)
					{
						return name.endsWith(".project.props");
					}
				});
				List list = new ArrayList();
				for (int i = 0; i < projectsNames.length; i++)
				{
					File projectFile = new File(projectDirectory, projectsNames[i]);
					if (projectFile.isFile())
					{
						try
						{
							list.add(new Project(projectFile));
						}
						catch (InvalidProjectPropertiesException e)
						{
							Log.log(Log.WARNING, this,
								"Warning the file " + projectFile.getAbsolutePath()
								+ " is not a valid project");
						}
						catch (FileNotFoundException e)
						{
							Log.log(Log.ERROR, this,
								"This error should never happens !!!!");
							Log.log(Log.ERROR, this, e);
						}
					}
				}
				projectList = new ProjectList(list);
			}
			else
			{
				projectDirFile.mkdirs();
				projectList = new ProjectList(new ArrayList());
			}
		}
	}

	/**
	 * Dispose the project manager. it will set the project name in the jEdit properties and close the current project.
	 */
	public void dispose()
	{
		instance = null;
		if (project != null)
		{
			jEdit.setProperty(PROJECT_NAME_PROPERTY, project.getName());
		}
		closeProject();
	}

	/**
	 * Create a project.
	 */
	public void createProject()
	{
		String projectName = JOptionPane.showInputDialog("Project name : ");
		if (projectName == null)
		{
			Log.log(Log.DEBUG, this, "Project creation cancelled");
		}
		else if (projectName.length() == 0)
		{
			JOptionPane.showMessageDialog(jEdit.getActiveView(), "The project name cannot be empty");
		}
		else
		{
			Project project = new Project(projectName, projectVersion);
			project.save();
			EditBus.send(new PHPProjectChangedMessage(this, project, PHPProjectChangedMessage.SELECTED));
			this.project = project;
		}
	}

	/**
	 * delete a project.
	 *
	 * @param project the project to be deleted
	 */
	public void deleteProject(Project project)
	{
		project.delete();
		if (project == this.project)
		{
			jEdit.setProperty(PROJECT_NAME_PROPERTY, null);
			this.project = null;
			EditBus.send(new PHPProjectChangedMessage(this, project, PHPProjectChangedMessage.DELETED));
		}
	}

	/**
	 * Close the current project.
	 */
	public void closeProject()
	{
		if (project != null)
		{
			if (project.needSave()) project.save();
			project.unload();
		}
		project = null;
		EditBus.send(new PHPProjectChangedMessage(this, null, PHPProjectChangedMessage.SELECTED));
	}

	/**
	 * open a project.
	 *
	 * @param projectFile the file of the project
	 */
	public void openProject(File projectFile)
	{
		if (project != null)
		{
			closeProject();
		}
		try
		{
			Project project = projectList.getProject(projectFile);
			openProject(project);
		}
		catch (InvalidProjectPropertiesException e)
		{
			Log.log(Log.ERROR, this, e.getMessage());
			project = null;
		}
		catch (FileNotFoundException e)
		{
			Log.log(Log.ERROR, this, e.getMessage());
			project = null;
			jEdit.setProperty(PROJECT_NAME_PROPERTY, null);
		}
	}

	public void openProject(Project project)
	{
		Task projectLoadingTask = new ProjectLoadingTask(project);
		ThreadUtilities.runInBackground(projectLoadingTask);
	}

	private class ProjectLoadingTask extends Task
	{
		private final Project project;

		private ProjectLoadingTask(Project project)
		{
			this.project = project;
			setLabel("PHP Project loading");
		}

		@Override
		public void _run()
		{
			if (ProjectManager.this.project != null)
			{
				closeProject();
			}
			project.load(this);
			ProjectManager.this.project = project;

			EditBus.send(new PHPProjectChangedMessage(this, ProjectManager.this.project,
								  PHPProjectChangedMessage.SELECTED));
		}


	}
}
