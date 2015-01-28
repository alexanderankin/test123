package bigdoc;

import javax.swing.text.Position;

// a concrete implementation of Position
public class MappedPosition implements Position {
    
    private int offset;
    
    public MappedPosition(int offset) {
        this.offset = offset;   
    }
    
    public int getOffset() {
        return offset;   
    }
}