package csidekick;

import java.util.Properties;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;

/** An instance of this contains the properties for the
    current C++ project. However, if there is no active project, then we use jedit's global properties instead.
    It is a model used by the CSideKickOptionPane. */

public class ProjectProperties 
{
	public static final String USEINPROJECT = "csidekick.useinproject";
	public static final String INCLUDEPATH = "csidekick.includepath"; 
	
    private VPTProject project = null;
	private Properties props;
	
	public ProjectProperties() 
	{
		try 
        {
			View v = jEdit.getActiveView();
			project = ProjectViewer.getActiveProject(v);
		}
		catch (Exception e) {}

		if (project == null) props = jEdit.getProperties();
		else props = project.getProperties();
	}
	
	public void setProperty(String propName, String propValue) {
		props.setProperty(propName, propValue);
	}
	
	public String getProperty(String propName) {
		return getProperty(propName, null); 
	}
	
	public boolean isCProject() 
	{
		String str = getProperty(USEINPROJECT);
		Boolean b = new Boolean(str);
		return b.booleanValue();
	}
	
	public void setCProject(boolean newValue) 
	{
		props.put(USEINPROJECT, String.valueOf(newValue));
	}
	
	public String getProperty(String propName, String def) 
	{
		try { return props.getProperty(propName); }
		catch (NullPointerException npe) { return def; }
	}
	
	public void setIncludePath(String inclPath) 
	{
		setProperty(INCLUDEPATH, inclPath);
	}
	
	public String getIncludePath() {
		return getProperty(INCLUDEPATH);
	}

	private static final long serialVersionUID = 522826115474974212L;
}
