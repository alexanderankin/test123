package ise.plugin.svn.command;

import java.io.File;
import java.util.*;
import ise.plugin.svn.SVN2;

public class CommitCommand implements Command {

    /**
     * params[0] directory or filename, required
     * params[1] message for commit, required
     * params[2] username, optional
     * params[3] password, required if username, otherwise optional
     */
    public String execute( String[] params ) throws CommandInitializationException, CommandExecutionException {
        if ( params == null || params.length > 2 ) {
            throw new CommandInitializationException( "Must have directory or filename and commit message." );
        }

        // make sure there is a directory or filename
        if ( params[ 0 ] == null || params[ 0 ].equals( "" ) ) {
            throw new CommandInitializationException( "Directory or filename cannot be null for commit." );
        }
        String path = params[0];

        // make sure there is a commit message
        String message = null;
        if ( params[ 1 ] == null || params[ 1 ].equals("") ) {
            throw new CommandInitializationException( "Message required for commit." );
        }
        message = params[1];

        // gather the command arguments
        List<String> args = new ArrayList<String>();
        args.add("commit");
        args.add("-m \"" + message + "\"");
        args.add(path);

        // check for username/password
        if (params.length == 4) {
            String username = params[3] != null && params[3].length() > 0 ? params[3] : null;
            String password = params[4] != null && params[4].length() > 0 ? params[4] : null;
            if (username != null && password != null) {
                args.add("--username " + username);
                args.add("--password " + password);
            }
        }

        // run the command
        String[] command = new String[args.size()];
        command = args.toArray(command);

        try {
            return SVN2.execute( command );
        }
        catch ( Exception e ) {
            throw new CommandExecutionException( e );
        }
    }
}
