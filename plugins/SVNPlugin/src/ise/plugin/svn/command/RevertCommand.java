package ise.plugin.svn.command;

import java.io.File;
import java.util.*;
import ise.plugin.svn.SVN2;

public class RevertCommand implements Command {

    /**
     * params[0] directory or filename, required
     * params[1] --recursive, this is optional
     * params[2] username, optional
     * params[3] password, required if username, otherwise optional
     */
    public String execute( String[] params ) throws
                CommandInitializationException, CommandExecutionException {

        if ( params == null || params.length < 1 ) {
            throw new CommandInitializationException( "Must have directory or file name to revert." );
        }
        String path = params[0];

        boolean recursive = params.length > 1 && params[1].equals("--recursive");

        List<String> args = new ArrayList<String>();
        args.add("revert");
        if (recursive) {
            args.add("--recursive");
        }
        args.add(path);

        // run the command
        String[] command = new String[args.size()];
        command = args.toArray(command);

        try {
            return SVN2.execute(command);
        }
        catch ( Exception e ) {
            throw new CommandExecutionException(e);
        }
    }
}
