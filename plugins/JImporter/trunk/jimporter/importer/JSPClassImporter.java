package jimporter.importer;

import jimporter.MissingParameterException;
import jimporter.sorting.SortOnImportOption;
import jimporter.sorting.ImportSorter;
import org.gjt.sp.jedit.jEdit;

public class JSPClassImporter extends ClassImporter {
    public int findInsertLocation(ImportList importList) {
        int insertLocation = 0;

        if (importList.size() == 0) {
            //
            //There weren't any imports in the JSP file, so we'll have to find a place to
            //do the import.
            //
            //MAFTODO: finish implementation

            //See if there is a declaration section at the top of the JSP file
                //See if there is already an import parameter
                    //Add the import statement
                //Add a new import parameter
                    //Add the import statement
            //Add the declaration section to the top of the JSP file

        } else {
            int lineOfLastImport = sourceBuffer.getLineOfOffset(importList.getLast().getStartLocation());
            insertLocation = sourceBuffer.getLineStartOffset(lineOfLastImport + 1);
        }

        return insertLocation;
    }
}
