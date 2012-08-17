package clangcompletion;
//{{{ Imports
import java.io.BufferedReader;
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

import org.gjt.sp.jedit.Buffer;
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
		String prefix = CompletionUtil.getCompletionPrefix(view);
		if(prefix == null || prefix.trim().length() == 0)
		{
			return new ArrayList<CompletionCandidate>();
		}
		prefix = prefix.toLowerCase();
		
		Buffer buffer = view.getBuffer(); 
		buffer.autosave();
		
		TextArea textArea = view.getTextArea();
		int line = textArea.getCaretLine();
		int column = textArea.getCaretPosition() - textArea.getLineStartOffset(line);
		
		column -= CompletionUtil.getCompletionPrefix(view).length();
		column+=1;//clang counts col starts from 1
		line += 1;
		String xparam = null;
		if(buffer.getMode().equals(jEdit.getMode("c")))
		{
			xparam = "c";
		}else if(buffer.getMode().equals(jEdit.getMode("objective-c")))
		{
			xparam = "objective-c";
		}else //if(buffer.getMode().equals(jEdit.getMode("c++")))
		{
			xparam = "c++";
		}
		
		if(buffer.getFile().getName().endsWith("hpp") || buffer.getFile().getName().endsWith("h"))
		{
			xparam += "-header";
		}
		
		String path = buffer.getPath();
		if(buffer.getAutosaveFile().exists())
		{
			path = buffer.getAutosaveFile().getPath();
		}
		
		ArrayList<String> args = new ArrayList<String>();
		args.add("clang");
		args.add("-cc1");    
		args.add("-w");
		args.add("-fsyntax-only");
		args.add("-fno-caret-diagnostics");
		args.add("-fdiagnostics-print-source-range-info");
		args.add("-code-completion-at="+path+":"+line+":"+column);
		args.add(path);
		args.add("-x");
		args.add(xparam);
		/*
		StringBuilder cmd = new StringBuilder("clang -cc1 -w -fsyntax-only -fno-caret-diagnostics -fdiagnostics-print-source-range-info -code-completion-at=");
		cmd.append(path);
		cmd.append(":" );
		cmd.append(line );
		cmd.append(":" );
		cmd.append(column );
		cmd.append(" " );
		cmd.append(path );
		cmd.append(" -x " );
		cmd.append(xparam );
		*/
		VPTProject project = ProjectViewer.getActiveProject(view);
		HashMap<String, Vector<String>> properties = ClangCompletionConfiguration.getProperties(project.getName());
		Vector<String> includes = properties.get(ClangCompletionConfiguration.INCLUDES);
		if(includes != null)
		{
			for(int i = 0; i < includes.size(); i++)
			{
				args.add("-I"  +includes.get(i) );
			}
		}
		
		Vector<String> definitions = properties.get(ClangCompletionConfiguration.DEFINITIONS);
		if(definitions != null)
		{
			for(int i = 0; i < definitions.size(); i++)
			{
				args.add("-D"  +definitions.get(i) );
			}
		}
		
		Vector<String> arguments = properties.get(ClangCompletionConfiguration.ARGUMENTS);
		if(arguments != null)
		{
			for(int i = 0; i < arguments.size(); i++)
			{
				args.add(arguments.get(i));
			}
		}
		
		List<CompletionCandidate> codeCompletions = new ArrayList<CompletionCandidate>();
		
		StringBuilder cmd = new StringBuilder();
		for(int i = 0; i < args.size();i++)
		{
			cmd.append(args.get(i) +" ");
		}
		System.out.println(cmd);
		
		try
		{
			String [] argsArr = new String[args.size()]; 
			args.toArray(argsArr);
			final Process process = Runtime.getRuntime().exec(argsArr);
			
			new Thread()
			{
				public void run()
				{
					try
					{
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
						String input = reader.readLine();
						while(input!=null)
						{
							//System.out.println("stderr: " + input);
							//parseError(input);
							input = reader.readLine();
							
						}
					}catch(IOException ex)
					{
						ex.printStackTrace();
					}
				}
			}.start();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String input = reader.readLine();
			while(input != null)
			{
				ClangCompletionCandidate candidate = ClangCompletionCandidate.parse(input);
				
				if(candidate != null && candidate.getDescription().toLowerCase().startsWith(prefix))
				{
					//if(candidate != null && !codeCompletions.contains(candidate) )
					codeCompletions.add(candidate );
					
				}
				input = reader.readLine();
			}
			
			
		}catch(IOException ex)
		{
			System.out.println(ex);
			ex.printStackTrace();
		}finally
		{
			//cleanUp();
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
