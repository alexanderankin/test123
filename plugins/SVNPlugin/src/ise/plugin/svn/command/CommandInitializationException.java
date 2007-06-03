package ise.plugin.svn.command;

/**
 * Thrown when a command fails to initialize properly.  I probably could have
 * used IllegalArgumentException instead.
 */
public class CommandInitializationException extends Exception {
    public CommandInitializationException() {
        super();
    }
    public CommandInitializationException(String message) {
        super(message);
    }
    public CommandInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
    public CommandInitializationException(Throwable cause) {
        super(cause);
    }
}
