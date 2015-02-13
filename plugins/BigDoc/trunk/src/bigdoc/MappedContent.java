package bigdoc;

import javax.swing.text.AbstractDocument;
import javax.swing.text. Segment;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoableEdit;
import javax.swing.text.Position;

public class MappedContent implements AbstractDocument.Content {

    private FileMap map = null;
    
    public MappedContent(FileMap map) {
        this.map = map;   
    }
    
    public Position createPosition(int offset) throws BadLocationException {
        return new MappedPosition(offset);   
    }
    
    public void getChars(int where, int length, Segment segment) throws BadLocationException {
        try {
            byte[] chars = map.get(where, length);
            segment.array = new String(chars).toCharArray();
            segment.count = chars.length;
            segment.offset = 0;
        }
        catch(IndexOutOfBoundsException e) {
            throw new BadLocationException(e.getMessage(), where);
        }
    }
    
    public String getString(int where, int length) throws BadLocationException {
        return new String(map.get(where, length));
    }
    
    public UndoableEdit insertString(int where, String text) throws BadLocationException {
        System.out.println("+++++ MappedContent.insertString: " + where + ", " + text);
        map.insert((long)where, text.getBytes());
        return null;
    }
    
    public int length() {
        return (int)map.getSize();
    }
    
    public UndoableEdit remove(int where, int nitems) throws BadLocationException {
        System.out.println("+++++ MappedContent.remove: " + where + ", " + nitems);
        map.delete(where, nitems);
        return null;
    }
    
    public void save() {
        map.save();   
    }
    
}