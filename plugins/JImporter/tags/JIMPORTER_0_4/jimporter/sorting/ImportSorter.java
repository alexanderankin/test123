/*
 *  ImportSorter.java - Class that will sort the import statements in a jedit 
 *  buffer.   
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.sorting;

import java.util.ArrayList;
import java.util.Iterator;
import jimporter.ImportItem;
import jimporter.sorting.SortedImportList;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

/**
 * Sort all of the import statements in the current buffer.
 *
 * @author Matthew Flower
 */
public class ImportSorter {
    private View sourceView;
    
    /**
     * Standard constructor.
     *
     * @param sourceView a <code>View</code> object that indicates which view
     * contains the buffer we are going to sort imports in.
     */
    public ImportSorter(View sourceView) {
        super();
        this.sourceView = sourceView;
    }
    
    /**
     * Set the <code>View</code> that contains the buffer that we are going to 
     * sort imports in.
     *
     * @param sourceView The view that contains the source file.
     * @see #getSourceView
     */
    public void setSourceView(View sourceView) {
        this.sourceView = sourceView;
    }
    
    /**
     * Get the view that contains the source file where we are going to sort
     * imports.
     * 
     * @return a <code>View</code> that contains a source file.
     * @see #setSourceView
     */
    public View getSourceView() {
        return sourceView;
    }

    /**
     * Sort the import statements in the buffer.
     */
    public void sort() {
        SortedImportList sil = new SortedImportList();
        Buffer buffer = sourceView.getBuffer();
        //String carriageReturnSequence = buffer.getStringProperty(buffer.LINESEP);
        String carriageReturnSequence = "\n";
        
        sil.setSourceBuffer(buffer);
        ArrayList importList = sil.getImportList();
        
        //Delete the range 
        buffer.remove(sil.getStartingOffset(), sil.getEndingOffset() - sil.getStartingOffset());
        
        //Insert all of the imports in the new order
        int insertLocation = sil.getStartingOffset();
        Iterator it = importList.iterator();
        while (it.hasNext()) {
            ImportItem ii = (ImportItem)it.next();
            String toInsert = ii.getImportStatement();
            if (it.hasNext()) {
                toInsert += carriageReturnSequence;
            }
            buffer.insert(insertLocation, toInsert);
            insertLocation += toInsert.length();
        }
    }
}

