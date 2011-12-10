package clearcase;

import java.io.IOException;
import java.io.OutputStream;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.util.Log;


import console.Console;
import console.Shell;

public class ClearCasePlugin extends EditPlugin implements ClearCaseConstants
{
    public static ConsoleOutputStream output;

	private static int reloadDelay = FILE_RELOAD_DELAY;
    // private static final ClearCaseShell shell = new ClearCaseShell();
    
    public void start()
    {

        Log.log(Log.DEBUG, this, "ClearCase plugin started");
    }

    public void stop()
    {

        Log.log(Log.DEBUG, this, "ClearCase plugin stopped");
    }
  
    // ================================================
    // ClearTool Plugin methods
    // ================================================

    public static void checkOut(View view) 
    {
        String file = view.getBuffer().getPath();
        
        CheckOutDialog dialog = new CheckOutDialog(view);
        Console console = showConsole(view, true);
        
        
        if(dialog.getExitValue().equals("Ok"))
        {
            CheckOut command = new CheckOut(file, dialog.getReserved(), dialog.getComment());
			executeClearToolCommand(view, command);
			
			// sleep for a few seconds to allow previous cleartool command to execute
			try { Thread.sleep(getFileReloadDelay()*1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

			view.getBuffer().reload(view);
        }
    }
    
    public static void checkIn(View view) 
    {
        String file = view.getBuffer().getPath();
        
        CheckInDialog dialog = new CheckInDialog(view);
        
        if(dialog.getExitValue().equals("Ok"))
        {
            CheckIn command = new CheckIn(file, dialog.getComment());
            executeClearToolCommand(view, command);

			// sleep for a few seconds to allow previous cleartool command to execute
			try { Thread.sleep(getFileReloadDelay()*1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

			view.getBuffer().reload(view);
        }
    }
    
    public static void addToSourceControl(View view) 
    {
        String file = view.getBuffer().getPath();
        
        AddToSourceControlDialog dialog = new AddToSourceControlDialog(view);
        
        if(dialog.getExitValue().equals("Ok"))
        {
            AddToSourceControl command = new AddToSourceControl(
                file, 
                dialog.getKeepCheckedOut(),
                dialog.getCheckOutInParentDirectory(),
				dialog.getComment());
            
			if (dialog.getCheckOutInParentDirectory())
			{
				String parentOfPath = MiscUtilities.getParentOfPath(view.getBuffer().getPath());
				CheckOut parentDirCommand = new CheckOut(parentOfPath, true, "");
				executeClearToolCommand(view, parentDirCommand);
				// sleep for a few seconds to allow previous cleartool command to execute
				try { Thread.sleep(getFileReloadDelay()*1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
			}
			
			executeClearToolCommand(view, command);
			
			if (dialog.getCheckOutInParentDirectory())
			{	// sleep for a few seconds to allow previous cleartool command to execute
				try { Thread.sleep(getFileReloadDelay()*1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
				String parentOfPath = MiscUtilities.getParentOfPath(view.getBuffer().getPath());
				CheckIn parentDirCommand = new CheckIn(parentOfPath, "");
				executeClearToolCommand(view, parentDirCommand);
			}
			
			// sleep for a few seconds to allow previous cleartool command to execute
			try { Thread.sleep(getFileReloadDelay()*1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

			view.getBuffer().reload(view);
        }
    }
    
    public static void unCheckOut(View view) 
    {
        String file = view.getBuffer().getPath();
        
        UnCheckOutDialog dialog = new UnCheckOutDialog(view);
        
        if(dialog.getExitValue().equals("Ok"))
        {
            UnCheckOut command = new UnCheckOut(file, dialog.getKeepFile());
            executeClearToolCommand(view, command);

			// sleep for a few seconds to allow previous cleartool command to execute
			try { Thread.sleep(getFileReloadDelay()*1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

			view.getBuffer().reload(view);
        }
    }

    public static void updateBuffer(View view) 
    {
        String file = view.getBuffer().getPath();

        Update command = new Update(file);
        
        executeClearToolCommand(view, new Update(file));
    }
    
    public static void showHistory(View view) 
    {
        String file = view.getBuffer().getPath();
        
        ShowHistory command = new ShowHistory(file);

        command.setGraphical(true);

        executeClearToolCommand(view, command);
    }

    public static void compareToPreviousVersion(View view) 
    {
        String file = view.getBuffer().getPath();
        
        ComparePreviousVersion command = new ComparePreviousVersion(file);

        command.setGraphical(true);

        executeClearToolCommand(view, command);
    }


    public static void showVersionTree(View view) 
    {
        String file = view.getBuffer().getPath();
        
        ShowVersionTree command = new ShowVersionTree(file);

        command.setGraphical(true);

        executeClearToolCommand(view, command);
    }
    
    // ================================================
    // ClearCase Plugin methods
    // ================================================
    
    public static void findCheckouts(View view) 
    {
        executeCommand(view, new FindCheckouts());
    }

    public static void showDetails(View view) 
    {
        executeCommand(view, new ShowDetails());
    }

    public static void merge(View view) 
    {
        executeCommand(view, new Merge());
    }
    
    
    // ================================================
    // Utility methods
    // ================================================

    public static void executeCommand(View view, Command command)
    {
        Log.log(Log.DEBUG, view, "Trying to execute command " + command);
        
        Console console = showConsole(view, true);
        
        // Run command send output to shell.
        try
        {
            OutputStream input = new ConsoleOutputStream( console.getInfoColor(), console );
            OutputStream error = new ConsoleOutputStream( console.getErrorColor(), console );
            
            // Exec command
            CommandExec exec = new CommandExec(command.getCommandLine(), input, error);
            exec.execute();
        }
        catch(IOException ex)
        {
            console.print(console.getErrorColor(), ex.getMessage());
            ex.printStackTrace();
        }
        catch(InterruptedException ex)
        {
            console.print(console.getErrorColor(), ex.getMessage());
            ex.printStackTrace();
        }
        finally
        {
            console.commandDone();
        }
    }
    
    public static void executeClearToolCommand(View view, ClearToolCommand command)
    {
        Log.log(Log.DEBUG, view, "Trying to execute command " + command);
        
        Console console = showConsole(view, true);
        Shell shell = console.setShell("ClearCase");
        
        // Run command send output to shell.
        try
        {
            // Exec command
            shell.execute(console,  command.getCommandLine(), console.getOutput());
        }
        finally
        {
            
        }
    }
    
    /**
    *   Shows console.
    */
    public static Console showConsole(View view, boolean focus)
    {
        DockableWindowManager wm = view.getDockableWindowManager();
        
        wm.showDockableWindow("console");
        
        Console console = (Console) wm.getDockable("console");
        
        console.setShell("ClearCase");
        
        // Set focus back to text area. Should maybe set to last view.
        if(focus == false)
        {
            view.getTextArea().requestFocus();
        }
        
        return console;
    }

	public static int getFileReloadDelay ()
	{
		try
		{
			reloadDelay = Integer.parseInt(jEdit.getProperty("clearcase.reloadDelay"));
		}
		catch (Exception ex)
		{
			jEdit.setProperty("clearcase.reloadDelay", "" + reloadDelay);	// Slimy way to convert int value to string
			System.out.println("Problem loading clearcase.reloadDelay. Using default value = " + reloadDelay + " Exception: " + ex);
		}			
		
		return reloadDelay;
	}
	
}