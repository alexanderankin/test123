package jimporter.importer;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import jimporter.MissingParameterException;
import jimporter.sorting.SortOnImportOption;
import jimporter.sorting.ImportSorter;

public abstract class ClassImporter{
    protected String importClass;
    protected Buffer sourceBuffer;
    protected boolean sortImportStatements = false;

    /**
     * Set the buffer that we are going to add the import to.
     *
     * @param source The source buffer we are going to add the import to.
     * @see #getSourceBuffer
     */
    public void setSourceBuffer(Buffer source) {
        sourceBuffer = source;
    }

    /**
     * Get the buffer we are going to add the import to.
     *
     * @return A <code>Buffer</code> that is the buffer we are going to add the
     * import into.
     * @see #setSourceBuffer
     */
    public Buffer getSourceBuffer() {
        return sourceBuffer;
    }

    /**
     * Sets the fully qualified name of the class we are going to import.
     *
     * @param fqClassName The new importClass value
     * @see #getImportClass
     */
    public void setImportClass(String fqClassName) {
        importClass = fqClassName;
    }

    /**
     * Gets the name of the class we are going to import.
     *
     * @return A <code>String</code> value containing the fq class name of the
     * class we are going to import.
     * @see #setImportClass
     */
    public String getImportClass() {
        return importClass;
    }

    public boolean addImportToBuffer() {
        //Check prerequisites
        if (importClass == null) {
            throw new MissingParameterException("importClass", "findInsertLocation()");
        }
        if (sourceBuffer == null) {
            throw new MissingParameterException("sourceBuffer", "findInsertLocation()");
        }

        ImportList importList = ImportListFactory.getInstance(sourceBuffer.getMode().getName());

        //Make sure that the class hasn't already been imported
        if (importList.checkForDuplicateImport(importClass, importList)) {
            jEdit.getActiveView().getStatus().setMessageAndClear(importClass + " has already been imported.  Your request to import again has been ignored.");
            return false;
        }

        //Get the location we are going to do our insert at
        int insertLocation = findInsertLocation(importList);

        //Add the import to the source buffer
        sourceBuffer.insert(insertLocation, importClass);

        //See if we are supposed to sort on import.  If so, do it.
        if (new SortOnImportOption().state()) {
            new ImportSorter(jEdit.getActiveView()).sort();
        }

        //Everything seems to have gone well, indicate success to the caller
        return true;
    }

    public abstract int findInsertLocation(ImportList importList);
}
