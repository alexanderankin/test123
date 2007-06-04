package ise.plugin.svn.command;

import java.io.File;
import java.util.*;
import ise.plugin.svn.SVN2;

public class InfoCommand implements Command {

    /**
     * params[0] directory or filename, required
     * params[2] username, optional
     * params[3] password, required if username, otherwise optional
     */
    public String execute( String[] params ) throws
                CommandInitializationException, CommandExecutionException {

        if ( params == null || params.length < 1 ) {
            throw new CommandInitializationException( "Must have directory or file name for info." );
        }
        String path = params[0];

        List<String> args = new ArrayList<String>();
        args.add("info");
        args.add(path);

        // check for username/password
        if (params.length == 3) {
            String username = params[2] != null && params[2].length() > 0 ? params[2] : null;
            String password = params[3] != null && params[3].length() > 0 ? params[3] : null;
            if (username != null && password != null) {
                args.add("--username " + username);
                args.add("--password " + password);
            }
        }

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
