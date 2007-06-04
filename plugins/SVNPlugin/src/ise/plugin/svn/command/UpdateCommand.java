package ise.plugin.svn.command;

import java.io.File;
import java.util.*;
import ise.plugin.svn.SVN2;

public class UpdateCommand implements Command {

    /**
     * params[0] directory or filename, required
     */
    public String execute( String[] params ) throws
                CommandInitializationException, CommandExecutionException {

        if ( params == null || params.length != 1 ) {
            throw new CommandInitializationException( "Must have directory or file name to update." );
        }
        String path = params[0];

        List<String> args = new ArrayList<String>();
        args.add("update");
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



        // run svn update on the selected item
        try {
            String[] command = new String[]{"update", params[0]};
            return SVN2.execute(command);
        }
        catch ( Exception e ) {
            throw new CommandExecutionException(e);
        }
    }
}
