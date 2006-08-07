package sidekick.util;

import javax.swing.text.Position;
import org.gjt.sp.jedit.Buffer;

public class ElementUtil {
    /**
     * Convert the start of a Location to a Position.
     * Need to create Positions for each node. The javacc parser finds line and
     * column location, need to convert this to a Position in the buffer. The
     * SideKickElement contains a column offset based on the current tab size as 
     * set in the Buffer, need to use getOffsetOfVirtualColumn to account for 
     * soft and hard tab handling.
     *
     * @param buffer
     * @param child
     * @return        Description of the Returned Value
     */
    public static Position createStartPosition(Buffer buffer, SideKickElement child) {
        final int line_offset = buffer.getLineStartOffset(
                Math.max(child.getStartLocation().line - 1, 0));
        final int col_offset = buffer.getOffsetOfVirtualColumn(
                Math.max(child.getStartLocation().line - 1, 0), 
                Math.max(child.getStartLocation().column - 1, 0), 
                null);
        Position p =
            new Position() {
                public int getOffset() {
                    return line_offset + col_offset;
                }
            };
        child.setStartPosition(p);
        return p;
    }

    /**
     * Convert the end of a Location to a Position.
     * Need to create Positions for each node. The javacc parser finds line and
     * column location, need to convert this to a Position in the buffer. The
     * SideKickElement contains a column offset based on the current tab size as 
     * set in the Buffer, need to use getOffsetOfVirtualColumn to account for 
     * soft and hard tab handling.
     *
     * @param buffer
     * @param child
     * @return        Description of the Returned Value
     */
    public static Position createEndPosition(Buffer buffer, SideKickElement child) {
        final int line_offset = buffer.getLineStartOffset(
                Math.max(child.getEndLocation().line - 1, 0));
        final int col_offset = buffer.getOffsetOfVirtualColumn(
                Math.max(child.getEndLocation().line - 1, 0),
                Math.max(child.getEndLocation().column - 1, 0), 
                null);
        Position p =
            new Position() {
                public int getOffset() {
                    return line_offset + col_offset;
                }
            };
        child.setEndPosition(p);
        return p;
    }

}
