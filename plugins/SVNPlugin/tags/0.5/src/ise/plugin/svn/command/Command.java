package ise.plugin.svn.command;

/**
 * Interface for the various command classes in this package, this is the
 * standard Command pattern.
 */
public interface Command {
    public String execute( String[] params ) throws
                CommandInitializationException, CommandExecutionException;
}
