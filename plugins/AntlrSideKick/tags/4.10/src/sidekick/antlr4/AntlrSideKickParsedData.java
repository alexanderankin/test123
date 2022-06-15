
package sidekick.antlr4;

// Imports
import sidekick.SideKickParsedData;

/**
 * Stores a buffer structure tree.
 */
public class AntlrSideKickParsedData extends SideKickParsedData {
    /**
     * @param fileName The file name being parsed, used as the root of the
     * tree.
     */
    public AntlrSideKickParsedData( String fileName ) {
        super( fileName );
    }
}
