package clangcompletion;
import java.util.*;
import java.io.*;
import org.gjt.sp.jedit.*;
public class ClangBuilder
{
	private ArrayList<String> cmds;
	
	private ClangBuilderListener listener;
	
	public ClangBuilder()
	{
		cmds = new ArrayList<String>();
		cmds.add(jEdit.getProperty( "clangcompletion.clang_path", "clang"));
	}
	
	public ClangBuilder(String clangPath)
	{
		cmds = new ArrayList<String>();
		cmds.add(clangPath);
	}
	
	public void setListener(ClangBuilderListener listener)
	{
		this.listener = listener;
	}
	
	public void add(String arg)
	{
		cmds.add(arg);
	}
	
	public void add(Collection<String> args)
	{
		cmds.addAll(args);
	}
	
	public void addIncludes(Collection<String> args)
	{
		Iterator<String> iterator = args.iterator();
		while(iterator.hasNext())
		{
			cmds.add("-I" + iterator.next());
		}
	}
	
	public void addDefinitions(Collection<String> args)
	{
		Iterator<String> iterator = args.iterator();
		while(iterator.hasNext())
		{
			cmds.add("-D" + iterator.next());
		}
	}
	
	public String toString()
	{
		StringBuilder cmd = new StringBuilder();
		for(int i = 0; i < cmds.size();i++)
		{
			String arg = cmds.get(i);
			if(arg.indexOf(" ") >= 0)
			{
				cmd.append("\"");
			}
			cmd.append(arg);
			if(arg.indexOf(" ") >= 0)
			{
				cmd.append("\"");
			}
			cmd.append(" ");
		}
		return cmd.toString();
	}
	
	public boolean setTarget(Buffer buffer)
	{
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
		}else
		{
			return false;
		}
		
		if(Util.isHeaderFile(buffer.getFile()))
		{
			xparam += "-header";
		}
		
		cmds.add("-x");
		cmds.add(xparam);
		
		return true;
	}
	
	public void exec() throws IOException
	{
		String [] cmdsArr = new String[cmds.size()]; 
		cmds.toArray(cmdsArr);
		
		if(listener == null)
		{
			Runtime.getRuntime().exec(cmdsArr);
			return;
		}
		
		final Process process = Runtime.getRuntime().exec(cmdsArr);
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
						listener.errorRecieved(input);
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
						listener.outputRecieved(input);
						input = reader.readLine();
					}
				}catch(IOException ex)
				{
					ex.printStackTrace();
				}
				
				try
				{
					process.waitFor();
				}catch(InterruptedException ex)
				{
					ex.printStackTrace();
				}finally
				{
					listener.exited();
				}
			}
		}.start();
	}
}
