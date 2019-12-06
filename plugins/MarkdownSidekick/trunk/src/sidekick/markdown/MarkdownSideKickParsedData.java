
package sidekick.markdown;

// Imports
import sidekick.SideKickParsedData;

/**
 * Stores a buffer structure tree.
 */
public class MarkdownSideKickParsedData extends SideKickParsedData {
    /**
     * @param fileName The file name being parsed, used as the root of the
     * tree.
     */
    public MarkdownSideKickParsedData( String fileName ) {
        super( fileName );
    }
}
