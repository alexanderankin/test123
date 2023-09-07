
package json;

// Imports
import sidekick.SideKickParsedData;

/**
 * Stores a buffer structure tree.
 */
public class JSONSideKickParsedData extends SideKickParsedData {
    /**
     * @param fileName The file name being parsed, used as the root of the
     * tree.
     */
    public JSONSideKickParsedData( String fileName ) {
        super( fileName );
    }
}
