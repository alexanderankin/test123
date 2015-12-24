package clangcompletion;
import java.util.*;
import java.io.*;
import org.gjt.sp.jedit.*;
public class ClangBuilder
{
	private ArrayList<String> cmds;
	
	private ClangBuilderListener listener;
	
	private String prefix;
	
	private int codeCompleteAtPosition = -1;
	
	private int codeCompleteAtLine;
	
	private int codeCompleteAtColumn;
	
	private String path;
	
	private String pathAutoSave;
	
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
		if(args == null)
		{
			return;
		}
		
		Iterator<String> iterator = args.iterator();
		while(iterator.hasNext())
		{
			cmds.add("-I" + iterator.next());
		}
	}
	
	public void addDefinitions(Collection<String> args)
	{
		if(args == null)
		{
			return;
		}
		
		Iterator<String> iterator = args.iterator();
		while(iterator.hasNext())
		{
			cmds.add("-D" + iterator.next());
		}
	}
	
	public void addArguments(Collection<String> args)
	{
		if(args == null)
		{
			return;
		}
		
		Iterator<String> iterator = args.iterator();
		while(iterator.hasNext())
		{
			cmds.add( iterator.next());
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
	
	public void codeCompleteAt(Buffer buffer, int line, int column, String prefix)
	{
		 path = buffer.getPath();
		 if(buffer.isDirty())
		 {
		 	 buffer.autosave();
		 	 Thread.yield();
			 pathAutoSave = buffer.getAutosaveFile().getPath();
		 }
		 
		 codeCompleteAtPosition = cmds.size();
		 codeCompleteAtLine = line;
		 codeCompleteAtColumn = column;
		 
		 this.prefix = prefix;
	}
	
	public boolean setTarget(Buffer buffer)
	{
		
		
		String xparam = null;
		if(buffer.getMode().equals(jEdit.getMode("c")))
		{
			xparam = "c";
		}else if(jEdit.getBooleanProperty("clangcompletion.support_objcpp", true) && buffer.getPath().endsWith(".mm"))
		{
			xparam = "objective-c++";
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
		
		if(Util.isHeaderFile(buffer.getPath()))
		{
			xparam += "-header";
		}
		
		cmds.add("-x");
		cmds.add(xparam);
		
		return true;
	}
	
	public void exec() throws IOException
	{
		
		new Thread()
		{
			public void run()
			{
				try
				{
					if(codeCompleteAtPosition > 0)
					{
						if(pathAutoSave != null && new File(pathAutoSave).exists())
						{
							path = pathAutoSave;
						}
						cmds.add(codeCompleteAtPosition, path);
						cmds.add(codeCompleteAtPosition, "-code-completion-macros");
						cmds.add(codeCompleteAtPosition, "-code-completion-at="+path+":"+codeCompleteAtLine+":"+codeCompleteAtColumn);
					}
						
					String [] cmdsArr = null;
					if (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1)
					{
						cmdsArr = new String[]{ ClangBuilder.this.toString() + (prefix == null ?"" : "|findstr /I /B /C:\"COMPLETION: " + prefix + "\"")};
						System.out.println(cmdsArr[0]);
					}else
					{
						cmdsArr = new String[]{"/bin/sh", "-c", ClangBuilder.this.toString() + (prefix == null ?"" : "|grep -i \"^COMPLETION: " + prefix + "\"")};
						System.out.println(cmdsArr[2]);
					}
					
					
					
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
									System.out.println(input);
									listener.errorRecieved(input);
									input = reader.readLine();
								}
							}catch(IOException ex)
							{
								ex.printStackTrace();
							}
						}
					}.start();
					
					try
					{
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String input = reader.readLine();
						while(input != null)
						{
							System.out.println(input);
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
					
				}catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}.start();
		
	}
}
