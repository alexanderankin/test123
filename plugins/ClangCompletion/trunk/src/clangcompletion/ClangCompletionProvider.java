package clangcompletion;
//{{{ Imports
import java.io.*;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;

import completion.service.CompletionCandidate;
import completion.service.CompletionProvider;
import completion.util.CompletionUtil;
import completion.util.BaseCompletionCandidate;

import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;
import errorlist.DefaultErrorSource.DefaultError;
import clangcompletion.ClangCompletionPlugin;

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
		buffer.autosave();
		
		TextArea textArea = view.getTextArea();
		int line = textArea.getCaretLine();
		int column = textArea.getCaretPosition() - textArea.getLineStartOffset(line);
		
		column -= CompletionUtil.getCompletionPrefix(view).length();
		column+=1;//clang counts col starts from 1
		line += 1;
		
		String path = buffer.getPath();
		if(buffer.getAutosaveFile().exists())
		{
			path = buffer.getAutosaveFile().getPath();
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
		HashMap<String, Vector<String>> properties = ProjectsOptionPane.getProperties(project.getName());
		Vector<String> includes = properties.get(ProjectsOptionPane.INCLUDES);
		if(includes != null)
		{
			builder.addIncludes(includes);
		}else
		{
			Util.generatePTHFileForActiveProject();
		}
		
		Vector<String> definitions = properties.get(ProjectsOptionPane.DEFINITIONS);
		if(definitions != null)
		{
			builder.addDefinitions(definitions);
		}
		
		Vector<String> arguments = properties.get(ProjectsOptionPane.ARGUMENTS);
		if(arguments != null)
		{
			builder.addArguments(arguments);
		}
		
		//tryGeneratePth(project);
		File filePth = Util.getPTHFileOfActiveProject();
		if(filePth.exists())
		{
			builder.add("-include-pth");
			builder.add(filePth.getPath() );
		}
		
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
