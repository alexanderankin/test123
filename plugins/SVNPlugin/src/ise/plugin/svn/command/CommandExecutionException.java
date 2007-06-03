package ise.plugin.svn.command;

/**
 * Thrown when a command fails in its execution for some reason.
 */
public class CommandExecutionException extends Exception {
    public CommandExecutionException() {
        super();
    }
    public CommandExecutionException(String message) {
        super(message);
    }
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
    public CommandExecutionException(Throwable cause) {
        super(cause);
    }

}
