package clangcompletion;

import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.msg.BufferUpdate;

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
public class BufferWatcher
{
	
	public BufferWatcher()
	{
		EditBus.addToBus(this);
	}
	
	public void shutdown()
	{
		EditBus.removeFromBus(this);
	}
	
	@EBHandler
	public void handleBufferUpdate(BufferUpdate bu)
	{
		// TOOD: handleBufferUpdate was called 4 times and throw exceptions when save a source file.
		if (( bu.getWhat() == BufferUpdate.SAVED) )
		{
			Buffer buffer = bu.getBuffer();
			String path = buffer.getPath();
			
			String xparam = null;
			if(buffer.getMode().equals(jEdit.getMode("c")))
			{
				xparam = "c";
			}else if(buffer.getMode().equals(jEdit.getMode("objective-c")))
			{
				xparam = "objective-c";
			}else if(buffer.getMode().equals(jEdit.getMode("c++")))
			{
				xparam = "c++";
			}
			
			if(xparam != null)
			{
			
				if(buffer.getFile().getName().endsWith("hpp") || buffer.getFile().getName().endsWith("h"))
				{
					xparam += "-header";
				}
				
				final ArrayList<String> args = new ArrayList<String>();
				args.add("clang");
				args.add("-cc1");    
				args.add("-w");
				args.add("-fsyntax-only");
				args.add("-fno-caret-diagnostics");
				args.add("-fdiagnostics-print-source-range-info");
				//args.add("-code-completion-at="+path+":"+line+":"+column);
				args.add(path);
				args.add("-x");
				args.add(xparam);
				
				clearErrors();
				
				
				VPTProject project = ProjectViewer.getActiveProject(bu.getView());
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
				
				StringBuilder cmd = new StringBuilder();
				for(int i = 0; i < args.size();i++)
				{
					cmd.append(args.get(i));
				}
				System.out.println(cmd);
				
				String [] argsArr = new String[args.size()]; 
				args.toArray(argsArr);
				try
				{
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
									parseError(input);
									input = reader.readLine();
									
								}
							}catch(IOException ex)
							{
								ex.printStackTrace();
							}
						}
					}.start();
					
					new Thread()
					{
						public void run()
						{
							try
							{
								BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
								String input = reader.readLine();
								while(input != null)
								{
									input = reader.readLine();
								}
							}catch(IOException ex)
							{
								ex.printStackTrace();
							}
						}
					}.start();
					
				}catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	private void clearErrors()
	{
		SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					jEdit.getAction("error-list-clear").invoke(null);
				}
			});
	}
	
	private Pattern errorPattern = Pattern.compile("((?:\\w:)?[^:]+?):(\\d+):\\s*(.+)"); 
	
	private void parseError(String clangOutput)
	{
		System.out.println("error: " + clangOutput);
		final Matcher matcher = errorPattern.matcher(clangOutput);
		if(matcher.find() && matcher.groupCount() >= 3)
		{
			SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						ClangCompletionPlugin.errorSrc.addError(new DefaultError(ClangCompletionPlugin.errorSrc,
							ErrorSource.ERROR, 
							matcher.group(1), 
							Integer.parseInt(matcher.group(2)) - 1, 
							0,
							0,
							matcher.group(3) ));
					}
				});
		}
	}
}
