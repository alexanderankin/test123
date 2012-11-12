package clangcompletion;
import org.gjt.sp.jedit.*;
import java.io.*;
import java.util.*;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;
import completion.util.CompletionUtil;
import org.gjt.sp.jedit.textarea.TextArea;
public class Util
{
	public static boolean isHeaderFile(File f)
	{
		return f.getName().endsWith(".hpp") || f.getName().endsWith(".h");
	}
	    
	public static boolean isHeaderFile(String f)
	{
		return f.endsWith(".hpp") || f.endsWith(".h");
	}
	
	public static File getPTHFileOfActiveProject()
	{
		View view =  jEdit.getActiveView();
		VPTProject project = ProjectViewer.getActiveProject(view);
		if(project != null)
		{
			return  new File(EditPlugin.getPluginHome(ClangCompletionPlugin.class), project.getName()+".pth");
		}else
		{
			return null;
		}
	}
	
	public static String getCompletionPrefix(View view)
   {
   	   String prefix = CompletionUtil.getCompletionPrefix(view);
   	   if(prefix != null && prefix.trim().length() > 0)
   	   {
   	   	   return prefix;
   	   }
   	   
   	   TextArea textArea = view.getTextArea();
       int caret = textArea.getCaretPosition() - 1;
       if (caret <= 0) 
       {
           return "";
       }
       String token = textArea.getText(caret, 1);
       if(token.equals("."))
       {
       	   return token;
       }else if(--caret >=0)
       {
       	   token = textArea.getText(caret, 1) + token;
       	   if(token.equals("->") || token.equals("::"))
       	   {
       	   	   return token;
       	   }
       }
	   
       return "";
   }
	
	public static boolean generatePTHFileForActiveProject()
	{
		View view =  jEdit.getActiveView();
		VPTProject project = ProjectViewer.getActiveProject(view);
		if(project == null)
		{
			return false;
		}
		
		File filePth =  new File(EditPlugin.getPluginHome(ClangCompletionPlugin.class), project.getName()+".pth");
		
		if(filePth.exists())
		{
			return false;
		}
		
		HashMap<String, Vector<String>> properties = ProjectsOptionPane.getProperties(project.getName());
		
		ClangBuilder builder = new ClangBuilder();
		builder.add("-cc1"); 
		builder.add("-fblocks");
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
		
		Vector<String> sysroots = properties.get(ProjectsOptionPane.SYSROOT);
		if(sysroots != null && sysroots.size() > 0 && sysroots.get(0).trim().length() > 0)
		{
			builder.add("-isysroot");
			builder.add(sysroots.get(0));
		}
		
		builder.addIncludes(properties.get(ProjectsOptionPane.INCLUDES));
		builder.addDefinitions(properties.get(ProjectsOptionPane.DEFINITIONS));
		builder.addArguments(properties.get(ProjectsOptionPane.ARGUMENTS));
		
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
