/*
 *  Jump plugin for jEdit
 *  Copyright (c) 2003 Pavlikus
 *
 *  :tabSize=4:indentSize=4:
 *  :folding=explicit:collapseFolds=1:
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
package jump.ctags;

import java.util.ArrayList;
import java.util.Vector;


/**
 * Storage for CTAGS_Entry objects.
 */
public class CtagsBuffer extends ArrayList {
    /** Store list of all files which already parsed into this CTAGS_Buffer */
    private ArrayList filenames;

    /** Create new CTAGS_Buffer */
    public CtagsBuffer() {
        super();
        filenames = new ArrayList();
    }

    /** Append CTAGS_Buffer.
    * When new file is added to Project we need the
    * way to add it to CTAGS_Buffer.
    */
    public void append(CtagsBuffer b) {
        CtagsEntry en;

        for (int i = 0; i < b.size(); i++) {
            en = (CtagsEntry) b.get(i);
            add(en);
            addFileName(en.getFileName());
        }
    }

    /**
    * Append CTAGS_Buffer.
    * When new file is added to Workspace we need
    * the way to add it to CTAGS_Buffer.
    */
    public void append(CtagsBuffer b, String filename) {
        this.removeFile(filename);

        CtagsEntry en;

        for (int i = 0; i < b.size(); i++) {
            en = (CtagsEntry) b.get(i);
            this.add(en);
        }

        this.addFileName(filename);
    }

    /**
    * Refresh CTAGS_Buffer (after removing file from project, for example)
    */

    //  TODO: NEED IMPLEMENTATION
    public void reload() {
    }

    /**
    * remove all tags of spec. file from CTAGS_Buffer
    */
    public void removeFile(String filename) {
        Vector files = getTagsByFile(filename);

        for (int i = 0; i < files.size(); i++) {
            this.remove((CtagsEntry) files.get(i));
        }

        filenames.remove(filename);
    }

    /** Files (full path) for which CTAGS_Buffer was generated */
    public ArrayList getFileNames() {
        return filenames;
    }
    
    public void addFileName(String fn) {
        if (!filenames.contains(fn)) {
            filenames.add(fn);
        }
    }

    /**
    * Returns Vector of CTAGS_Entries with spec. tag_name.
    * May be usualy it will return just one entry in vector,
    * but how about repeated tags? For example - all actionPerformed()
    */
    public Vector getEntries(String tag_name) {
        Vector result = new Vector();
        CtagsEntry en;

        for (int i = 0; i < this.size(); i++) {
            en = (CtagsEntry) this.get(i);

            if (tag_name.equals(en.getTagName())) {
                result.add(en);
            }
        }

        return result;
    }

    public Vector getEntriesByStartPrefix(String prefix) {
        Vector result = new Vector();
        CtagsEntry en;

        for (int i = 0; i < this.size(); i++) {
            en = (CtagsEntry) this.get(i);

            if (en.isTagNameStartsWith(prefix)) {
                result.add(en);
            }
        }

        return result;
    }

    /**
    * Scan entire CTAGS_Buffer for entries from spec. file
    * which spec. signature.
    */
    public Vector getTagsBySignature(String signature) {
        Vector result = new Vector();
        CtagsEntry en;

        for (int i = 0; i < this.size(); i++) {
            en = (CtagsEntry) this.get(i);

            if (signature.equals(en.getSignature())) {
                result.add(en);
            }
        }

        return result;
    }

    /**
    * Scan entire CTAGS_Buffer for entries from spec. file
    */
    public Vector getTagsByFile(String filename) {
        Vector result = new Vector();
        CtagsEntry entry;

        for (int i = 0; i < this.size(); i++) {
            entry = (CtagsEntry) this.get(i);

            if (filename.equals(entry.getFileName())) {
                result.add(entry);
            }
        }

        return result;
    }

    public boolean add(CtagsEntry entry) {
        this.addFileName(entry.getFileName());

        return super.add(entry);
    }

    public void clear() {
        filenames.clear();
        super.clear();
    }

    public Object get(int index) {
        return super.get(index);
    }
}
