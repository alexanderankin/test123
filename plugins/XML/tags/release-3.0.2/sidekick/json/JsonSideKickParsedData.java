
package sidekick.json;

// Imports
import sidekick.SideKickParsedData;

/**
 * Stores a buffer structure tree.
 */
public class JsonSideKickParsedData extends SideKickParsedData {
    /**
     * @param fileName The file name being parsed, used as the root of the
     * tree.
     */
    public JsonSideKickParsedData( String fileName ) {
        super( fileName );
    }
}
