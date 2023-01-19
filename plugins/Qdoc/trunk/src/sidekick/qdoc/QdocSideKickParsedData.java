
package sidekick.qdoc;

// Imports
import sidekick.SideKickParsedData;

/**
 * Stores a buffer structure tree.
 */
public class QdocSideKickParsedData extends SideKickParsedData {
    /**
     * @param fileName The file name being parsed, used as the root of the
     * tree.
     */
    public QdocSideKickParsedData( String fileName ) {
        super( fileName );
    }
}
