package clangcompletion;
import org.gjt.sp.jedit.*;
import java.io.*;
import java.util.*;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;
public class Util
{
	public static boolean isHeaderFile(File f)
	{
		return f.getName().endsWith(".hpp") || f.getName().endsWith(".h");
	}
	
	public static File getPTHFileOfActiveProject()
	{
		View view =  jEdit.getActiveView();
		VPTProject project = ProjectViewer.getActiveProject(view);
		return  new File(EditPlugin.getPluginHome(ClangCompletionPlugin.class), project.getName()+".pth");
	}
	
	public static boolean generatePTHFileForActiveProject()
	{
		View view =  jEdit.getActiveView();
		VPTProject project = ProjectViewer.getActiveProject(view);
		File filePth =  new File(EditPlugin.getPluginHome(ClangCompletionPlugin.class), project.getName()+".pth");
		
		if(filePth.exists())
		{
			return false;
		}
		
		HashMap<String, Vector<String>> properties = ProjectsOptionPane.getProperties(project.getName());
		
		ClangBuilder builder = new ClangBuilder();
		builder.add("-cc1");    
		builder.add("-w");
		
		Vector<String> precompileds = properties.get(ProjectsOptionPane.PRECOMPILEDS);
		if(precompileds == null || precompileds.size() == 0)
		{
			return false;
		}
		
		for(int i = 0; i < precompileds.size(); i++)
		{
			builder.add(precompileds.get(i) );
		}
		
		builder.add("-emit-pth");
		builder.add("-o");
		builder.add(filePth.getPath());
		
		Vector<String> includes = properties.get(ProjectsOptionPane.INCLUDES);
		if(includes != null)
		{
			builder.addIncludes(includes);
		}
		
		Vector<String> definitions = properties.get(ProjectsOptionPane.DEFINITIONS);
		if(definitions != null)
		{
			builder.addDefinitions(definitions);
		}
		
		builder.add("-x");
		builder.add("c++-header");
		
		System.out.println("pth: " + builder);
		
		try
		{
			builder.exec();
		}catch(IOException ex)
		{
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
