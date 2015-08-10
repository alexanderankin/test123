package sidekick.util;

import javax.swing.text.Position;

// a concrete implementation of Position
public class SideKickPosition implements Position {
    
    private int offset;
    
    public SideKickPosition(int offset) {
        this.offset = offset;   
    }
    
    public int getOffset() {
        return offset;   
    }
}