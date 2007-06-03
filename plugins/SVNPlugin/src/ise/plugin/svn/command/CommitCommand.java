package ise.plugin.svn.command;

import java.io.File;
import ise.plugin.svn.SVN2;

public class CommitCommand implements Command {

    /**
     * params[0] directory or filename, required
     * params[1] message for commit, required
     */
    public String execute( String[] params ) throws CommandInitializationException, CommandExecutionException {
        if ( params == null || params.length != 2 ) {
            throw new CommandInitializationException( "Must have directory or filename and commit message." );
        }

        if ( params[ 0 ] == null || params[ 0 ].equals( "" ) ) {
            throw new CommandInitializationException( "Directory or filename cannot be null for commit." );
        }

        // make sure there is a commit message
        String message = null;
        if ( params[ 1 ] != null && params[ 1 ].length() > 0 ) {
            message = params[ 1 ];
        }
        if ( message == null ) {
            throw new CommandInitializationException( "Message required for commit." );
        }

        try {
            String[] command = new String[] {"commit", "-m \"" + params[1] + "\"", params[0]};
            return SVN2.execute( command );
        }
        catch ( Exception e ) {
            throw new CommandExecutionException( e );
        }
    }
}
