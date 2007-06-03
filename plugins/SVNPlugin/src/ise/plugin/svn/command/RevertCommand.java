package ise.plugin.svn.command;

import java.io.File;
import ise.plugin.svn.SVN2;

public class RevertCommand implements Command {

    /**
     * params[0] directory or filename, required
     * params[1] --recursive, this is optional
     */
    public String execute( String[] params ) throws
                CommandInitializationException, CommandExecutionException {

        if ( params == null || params.length < 1 ) {
            throw new CommandInitializationException( "Must have directory or file name to revert." );
        }

        boolean recursive = params.length > 1 && params[1].equals("--recursive");

        // run svn update on the selected item
        try {
            String[] command = new String[]{"revert", (recursive ? "--recursive" : ""), params[0]};
            return SVN2.execute(command);
        }
        catch ( Exception e ) {
            throw new CommandExecutionException(e);
        }
    }
}
