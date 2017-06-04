
package sidekick.xquery;

// Imports
import sidekick.SideKickParsedData;

/**
 * Stores a buffer structure tree.
 */
public class XQuerySideKickParsedData extends SideKickParsedData {
    /**
     * @param fileName The file name being parsed, used as the root of the
     * tree.
     */
    public XQuerySideKickParsedData( String fileName ) {
        super( fileName );
    }
}
