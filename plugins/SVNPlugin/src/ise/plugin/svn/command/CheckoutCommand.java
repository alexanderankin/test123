package ise.plugin.svn.command;

import java.io.File;
import ise.plugin.svn.SVN2;

public class CheckoutCommand implements Command {

    /**
     * params[0] svn url, required
     * params[1] directory, required
     * params[2] username, optional
     * params[3] password, optional
     */
    public String execute( String[] params ) throws CommandInitializationException, CommandExecutionException {

        if ( params == null || params.length != 2 ) {
            throw new CommandInitializationException( "Must have both URL and local path for checkout." );
        }
        if (params[0] == null || params[0].length() == 0) {
            throw new CommandInitializationException("URL cannot be null for checkout.");
        }
        if (params[1] == null || params[1].length() == 0) {
            throw new CommandInitializationException("Directory cannot be null for checkout.");
        }

        try {
            String[] command = new String[params.length + 1];
            command[0] = "checkout";
            for (int i = 0; i < params.length; i++ ) {
                command[i + 1] = params[i];
            }
            return SVN2.execute( command );
        }
        catch ( Exception e ) {
            throw new CommandExecutionException( e );
        }
    }
}
