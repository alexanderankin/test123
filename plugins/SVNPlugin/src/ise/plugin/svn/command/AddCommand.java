package ise.plugin.svn.command;

import java.io.File;
import ise.plugin.svn.SVN2;

public class AddCommand implements Command {

    /**
     * params[0] directory or filename, required
     */
    public String execute( String[] params ) throws
                CommandInitializationException, CommandExecutionException {

        if ( params == null || params.length != 1 || params[0].length() == 0 ) {
            throw new CommandInitializationException( "Must have directory or file name to add." );
        }

        // run svn update on the selected item
        try {
            String[] command = new String[]{"add", params[0]};
            return SVN2.execute(command);
        }
        catch ( Exception e ) {
            throw new CommandExecutionException(e);
        }
    }
}
