package jimporter.importer;

import org.gjt.sp.jedit.Buffer;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Created by IntelliJ IDEA.
 * User: mflower
 * Date: Oct 8, 2002
 * Time: 11:50:08 PM
 * To change this template use Options | File Templates.
 */
public abstract class ImportList{
    protected ArrayList importList = new ArrayList();
    protected Buffer sourceBuffer;
    protected boolean sourceBufferParsed = false;
    protected int startingOffset = Integer.MAX_VALUE;
    protected int endingOffset = -1;

    /**
     * Get the current list of imports.
     *
     * @return A <CODE>ArrayList</CODE> item containing all of the <CODE>ImportItem</CODE>
     * objects found which parsing the source buffer.  Note that this method will parse
     * the source buffer if it hasn't already been parsed.
     */
    public ArrayList getImportList() {
        if (!sourceBufferParsed) {
            parseImports();
        }

        return importList;
    }

    /**
     * Get an iterator pointing to the list of <CODE>ImportItem</CODE> objects that
     * this class contains.
     *
     * @return A <CODE>Iterator</CODE> object pointing to the list of import items that this
     * object contains.
     */
    public Iterator iterator() {
        if (!sourceBufferParsed)
            parseImports();

        return importList.iterator();
    }

    /**
     * This method sets the offset of the first character of the first import
     * statement that we found while parsing the buffer.
     *
     * @param startingOffset The offset of the first character of the first import statement we found while
     * parsing.
     * @see #getStartingOffset()
     */
    public void setStartingOffset(int startingOffset) {
        this.startingOffset = startingOffset;
    }

    /**
     * Get the offset of the first character of the first import that we found while
     * parsing the source buffer for import statements.
     *
     * @return An <CODE>int</CODE> value containing the offset of the first character of the
     * first import statement that we found while parsing.
     * @see #setStartingOffset(int)
     */
    public int getStartingOffset() {
        return startingOffset;
    }

    /**
     * Set the offset of the last character of the last import statement that we found
     * in the source buffer.
     *
     * @param endingOffset The offset of the last character of the last import statement we found in the
     * source buffer.
     * @see #getEndingOffset
     */
    public void setEndingOffset(int endingOffset) {
        this.endingOffset = endingOffset;
    }

    /**
     * Get the offset of the last character in the last import statement that we found
     * while parsing the source buffer.
     *
     * @return An <CODE>int</CODE> value that indicates the last character offset of the last
     * import statement in the source buffer.
     * @see #setEndingOffset
     */
    public int getEndingOffset() {
        return endingOffset;
    }

    /**
     * Set the buffer that we are going to parse to find import declarations.
     *
     * @param sourceBuffer a pointer to the buffer we are going to parse to find
     * import declarations.
     * @see #getSourceBuffer()
     */
    public void setSourceBuffer(Buffer sourceBuffer) {
        this.sourceBuffer = sourceBuffer;

        //This is a new buffer, indicate that the buffer needs to be reparsed.
        sourceBufferParsed = false;
        importList.clear();
    }

    /**
     * Get the source buffer that we are going to parse to find import declarations.
     *
     * @return The source buffer that we are going to parse to find import declarations.
     * @see #setSourceBuffer(Buffer)
     */
    public Buffer getSourceBuffer() {
        return sourceBuffer;
    }

    /**
     * Gets the last attribute of the JavaImportList object.
     *
     * @return   The last value in the import list.
     */
    public ImportItem getLast() {
        if (!sourceBufferParsed)
            parseImports();

        return (ImportItem)getImportList().get(importList.size() - 1);
    }

    /**
     * Adds an ImportItem to the list of imports.
     *
     * @param importItem  The importItem to be added to the list.
     */
    public void addImport(ImportItem importItem) {
        importList.add(importItem);
    }

    /**
     * Returns the number of items in the import list.
     *
     * @return   A <code>int</code> value containing the number of items in the list.
     */
    public int size() {
        if (!sourceBufferParsed)
            parseImports();

        return importList.size();
    }

    protected abstract void parseImports();
    public abstract boolean checkForDuplicateImport(String className, ImportList importList);
}
