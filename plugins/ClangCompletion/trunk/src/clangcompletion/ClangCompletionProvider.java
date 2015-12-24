package clangcompletion;
//{{{ Imports
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.regex.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;

import completion.service.CompletionCandidate;
import completion.service.CompletionProvider;
import completion.util.*;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;

import javax.swing.ListCellRenderer;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;

import superabbrevs.SuperAbbrevs;

import completion.service.CompletionCandidate;
//}}}
public class ClangCompletionProvider implements CompletionProvider
{
	private Set<Mode> completionModes;
	
	private static Pattern incPattern = Pattern.compile("^\\s*#(include|import)\\s*(\\\"|<)");
	
	public ClangCompletionProvider()
	{
		super();
		completionModes = new HashSet<Mode>();
		completionModes.add(jEdit.getMode("c"));
		completionModes.add(jEdit.getMode("c++"));
		completionModes.add(jEdit.getMode("objective-c"));
	}
	
	/**
	* @param view
	* @return The list of possible completions based on the current caret location.
	*/
	@Override
	public List<CompletionCandidate> getCompletionCandidates(View view)
	{
		final Vector<CompletionCandidate> codeCompletions = new Vector<CompletionCandidate>();
		
		if(getCompletionCandidatesInclude( view,  codeCompletions))
		{
			return codeCompletions;
		}
		
		
		String prefix = Util.getCompletionPrefix(view);
		if(prefix == null || prefix.trim().length() == 0)
		{
			return codeCompletions;
		}
		
		Buffer buffer = view.getBuffer();
		
		
		TextArea textArea = view.getTextArea();
		int line = textArea.getCaretLine();
		int column = textArea.getCaretPosition() - textArea.getLineStartOffset(line);
		
		column -= CompletionUtil.getCompletionPrefix(view).length();
		column+=1;//clang counts col starts from 1
		line += 1;
		
		
		
		ClangBuilder builder = new ClangBuilder();
		
		builder.add("-cc1");  
		builder.add("-fblocks");
		builder.add("-w");
		builder.add("-fsyntax-only");
		builder.add("-fno-caret-diagnostics");
		builder.add("-fdiagnostics-print-source-range-info");
		// builder.add("-code-completion-at="+path+":"+line+":"+column);
		// builder.add("-code-completion-macros");
		// builder.add(path);
		builder.codeCompleteAt(buffer, line, column, prefix);
		// setTarget should be here after adding code-completion-at for code completion
		if(!builder.setTarget(buffer))
		{
			return codeCompletions;
		}
		
		VPTProject project = ProjectViewer.getActiveProject(view);
		if(project != null)
		{
			HashMap<String, Vector<String>> properties = ProjectsOptionPane.getProperties(project.getName());
			
			Vector<String> sysroots = properties.get(ProjectsOptionPane.SYSROOT);
			if(sysroots != null && sysroots.size() > 0 && sysroots.get(0).trim().length() > 0)
			{
				builder.add("-isysroot");
				builder.add(sysroots.get(0));
			}
			
			Vector<String> includes = properties.get(ProjectsOptionPane.INCLUDES);
			if(includes != null)
			{
				builder.addIncludes(includes);
			}else
			{
				Util.generatePTHFileForActiveProject();
			}
			builder.addDefinitions(properties.get(ProjectsOptionPane.DEFINITIONS));
			builder.addArguments(properties.get(ProjectsOptionPane.ARGUMENTS));
			
			File filePth = Util.getPTHFileOfActiveProject();
			if(filePth.exists())
			{
				builder.add("-include-pth");
				builder.add(filePth.getPath() );
			}
		}
		//tryGeneratePth(project);
		
		// builder.setCompletionPrefix(prefix);
		
		// System.out.println(builder);
		final AtomicBoolean isClangBuilderAlive = new AtomicBoolean();
		builder.setListener(new ClangBuilderListener()
			{
				public void errorRecieved(String line){};
				
				public void outputRecieved(String line)
				{
					ClangCompletionCandidate candidate = ClangCompletionCandidate.parse(line);
					if(candidate != null)
					{
						codeCompletions.add(candidate );
					}
				};
				
				public void exited()
				{
					isClangBuilderAlive.set(false);
				};
			});
		
		try
		{
			isClangBuilderAlive.set(true);
			
			
			
			builder.exec();
			while(isClangBuilderAlive.get())
			{
				Thread.yield();
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		/* if(jEdit.getBooleanProperty("clangcompletion.show_macro", true))
		{
			try 
			{  
				Vector<ctagsinterface.main.Tag> macros = ctagsinterface.main.CtagsInterfacePlugin.runScopedQuery(view, "_nameLC:" + prefix.toLowerCase() + "* AND kind:macro");
				for(Iterator<ctagsinterface.main.Tag> iter = macros.iterator(); iter.hasNext();)
				{
					codeCompletions.add(new ctagsinterface.jedit.CtagsCompletionCandidate(iter.next()));
				}
			} catch (Exception e) 
			{  
				e.printStackTrace();
			}  
		} */
		
		return codeCompletions;
	}
	
	/**
	* @return A list of supported modes (usually only one if any), or null if not mode specific.
	*/
	@Override
	public Set<Mode> restrictToModes()
	{
		return completionModes;
	}
	
	private boolean getCompletionCandidatesInclude(View view, Vector<CompletionCandidate> codeCompletions)
	{
		VPTProject project = ProjectViewer.getActiveProject(view);
		if(project == null)
		{
			return false;
		}
		
		TextArea textArea = view.getTextArea();
		Buffer buffer = view.getBuffer();    
		int start = view.getBuffer().getLineStartOffset(textArea.getCaretLine());
		// get current line between the start of line and the current caret
		String lineText = buffer.getText(start,textArea.getCaretPosition() - start );
		Matcher matcher = incPattern.matcher(lineText); 
		if(!matcher.find())
		{
			return false;
		}
		
		HashMap<String, Vector<String>> properties = ProjectsOptionPane.getProperties(project.getName());
		Vector<String> includes = properties.get(ProjectsOptionPane.INCLUDES);
		if(includes == null)
		{
			return true;
		}
		
		// remove include " from the start of line
		lineText = lineText.substring(matcher.end());
		
		// check every include dir 
		for(Iterator<String> iter = includes.iterator(); iter.hasNext();)
		{
			String includeRoot = iter.next();
			File dir = new File(includeRoot, lineText);
			if(!dir.exists() || !dir.isDirectory() || !lineText.endsWith(File.separator))
			{
				dir = dir.getParentFile();
			}
			
			if(dir.exists() && dir.isDirectory())
			{
				String[] list = dir.list();
				
				for(int j = 0; j < list.length; j++)
				{
					codeCompletions.add(new IncludeCompletionCandidate( list[j]));
				}
			}
		}
		
		return true;
	}
}

