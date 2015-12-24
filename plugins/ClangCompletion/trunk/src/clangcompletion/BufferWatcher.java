package clangcompletion;

import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.jEdit;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.AtomicBoolean;

import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;
import errorlist.DefaultErrorSource.DefaultError;

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
			ErrorSource.unregisterErrorSource(errorSrc);
			errorSrc = null;		
		}
	}
	
	private void updateErrorSourceView(View v)
	{
		
		if (errorSrc.getView() != v) {
			ErrorSource.unregisterErrorSource(errorSrc);
			errorSrc = new DefaultErrorSource(this.getClass().getName(), v);
			ErrorSource.registerErrorSource(errorSrc);
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
			// setTarget should be first parameter for checking syntax
			if(!builder.setTarget(buffer))
			{
				return;
			}
			
			
			builder.add("-fblocks");
			builder.add("-fsyntax-only");
			builder.add("-fno-caret-diagnostics");
			builder.add("-fdiagnostics-print-source-range-info");
			builder.add("-fno-caret-diagnostics");
			builder.add(path);
			
			// Do not check header files because in most case clang cannot compile
			// a header file correctly without implementation cpp sources.
			if(Util.isHeaderFile(buffer.getPath()))
			{
				return;
			}
			
			clearErrors();
			updateErrorSourceView(bu.getView());
			
			VPTProject project = ProjectViewer.getActiveProject(bu.getView());
			if (project != null) 
			{
				HashMap<String, Vector<String>> properties = ProjectsOptionPane.getProperties(project.getName());
				
				Vector<String> sysroots = properties.get(ProjectsOptionPane.SYSROOT);
				if(sysroots != null && sysroots.size() > 0 && sysroots.get(0).trim().length() > 0)
				{
					builder.add("-isysroot");
					builder.add(sysroots.get(0));
				}
				
				builder.addIncludes(properties.get(ProjectsOptionPane.INCLUDES));
				builder.addDefinitions(properties.get(ProjectsOptionPane.DEFINITIONS));
				builder.addArguments(properties.get(ProjectsOptionPane.ARGUMENTS));
				
				File filePth = Util.getPTHFileOfActiveProject();
				if(filePth.exists())
				{
					builder.add("-include-pth");
					builder.add(filePth.getPath() );
				}				
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
		errorSrc.clear();
	}
	
}
