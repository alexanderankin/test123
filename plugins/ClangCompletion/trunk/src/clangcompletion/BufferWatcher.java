package clangcompletion;

import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.msg.BufferUpdate;

import java.io.BufferedReader;
import java.io.*;
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
import java.util.concurrent.atomic.AtomicBoolean;
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
public class BufferWatcher implements ClangBuilderListener
{
	private  AtomicBoolean isClangBuilderAlive = new AtomicBoolean();
	
	private Pattern errorPattern = Pattern.compile("((?:\\w:)?[^:]+?):(\\d+):(\\d+):[\\d:\\-\\{\\}]*\\s*([^:]+):(.+)");
	
	private   DefaultErrorSource errorSrc;
	
	public BufferWatcher()
	{
		EditBus.addToBus(this);
		errorSrc = new DefaultErrorSource(this.getClass().getName(), jEdit.getActiveView());
		ErrorSource.registerErrorSource(errorSrc);
	}
	
	public void shutdown()
	{
		EditBus.removeFromBus(this);
		if(errorSrc != null)
		{
			errorSrc = null;
			ErrorSource.unregisterErrorSource(errorSrc);
		}
	}
	
	@EBHandler
	public void handleBufferUpdate(BufferUpdate bu)
	{
		if (( bu.getWhat() == BufferUpdate.SAVED) && jEdit.getBooleanProperty("clangcompletion.parse_buffer", true))
		{
			Buffer buffer = bu.getBuffer();
			String path = buffer.getPath();
			
			ClangBuilder builder = new ClangBuilder();
			builder.add("-cc1");    
			builder.add("-fblocks");
			builder.add("-fsyntax-only");
			builder.add("-fno-caret-diagnostics");
			builder.add("-fdiagnostics-print-source-range-info");
			builder.add(path);
			if(!builder.setTarget(buffer))
			{
				return;
			}
			
			// Do not check header files because in most case clang cannot compile
			// a header file correctly without implementation cpp sources.
			if(Util.isHeaderFile(buffer.getPath()))
			{
				return;
			}
			
			clearErrors();
			
			VPTProject project = ProjectViewer.getActiveProject(bu.getView());
			HashMap<String, Vector<String>> properties = ProjectsOptionPane.getProperties(project.getName());
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
			
			Vector<String> arguments = properties.get(ProjectsOptionPane.ARGUMENTS);
			if(arguments != null)
			{
				builder.addArguments(arguments);
			}
			
			File filePth = Util.getPTHFileOfActiveProject();
			if(filePth.exists())
			{
				builder.add("-include-pth");
				builder.add(filePth.getPath() );
			}
			
			builder.setListener(this);
			System.out.println(builder);
			if(!isClangBuilderAlive.get())
			{
				try
				{
					isClangBuilderAlive.set(true);
					builder.exec();
				}catch(IOException ex)
				{
					isClangBuilderAlive.set(false);
					ex.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void errorRecieved(String line)
	{
		System.out.println("error: " + line);
		final Matcher matcher = errorPattern.matcher(line);
		if(matcher.find() && matcher.groupCount() >= 5)
		{
			
			SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						errorSrc.addError(new DefaultError(errorSrc,
							matcher.group(4).indexOf("error") >= 0?ErrorSource.ERROR:ErrorSource.WARNING, 
							matcher.group(1), 
							Integer.parseInt(matcher.group(2)) - 1, 
							0,
							0,
							matcher.group(5) ));
					}
				});
			
		}
	}
	
	public void outputRecieved(String line)
	{
		
	}
	
	public void exited()
	{
		isClangBuilderAlive.set(false);
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
	
}
