package clangcompletion;
//{{{ Imports
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;

import completion.service.CompletionCandidate;
import completion.service.CompletionProvider;
import completion.util.CompletionUtil;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;
//}}}
public class ClangCompletionProvider implements CompletionProvider
{
	private Set<Mode> completionModes;
	
	
	
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
		
		String path = buffer.getPath();
		if(buffer.isDirty())
		{
			buffer.autosave();
			for(int i = 0; i < 10; i++)
			{
				try
				{
					Thread.sleep(50);
				}catch(Exception ex)
				{
				}
				
				if(buffer.getAutosaveFile().exists())
				{
					path = buffer.getAutosaveFile().getPath();
					break;
				}
			}
		}
		
		ClangBuilder builder = new ClangBuilder();
		builder.add("-cc1");  
		builder.add("-fblocks");
		builder.add("-w");
		builder.add("-fsyntax-only");
		builder.add("-fno-caret-diagnostics");
		builder.add("-fdiagnostics-print-source-range-info");
		builder.add("-code-completion-at="+path+":"+line+":"+column);
		builder.add(path);
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
		
		
		System.out.println(builder);
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
		
		
		if(jEdit.getBooleanProperty("clangcompletion.show_macro", true))
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
		}
		
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
	
}
