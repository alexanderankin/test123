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


/** Storage for CTAGS_Entry objects. */
public class CTAGS_Buffer extends ArrayList {
    /** Store list of all files which already parsed into this CTAGS_Buffer */
    private Vector Files;

    /** Parser which generate this buffer */
    private CTAGS_Parser Parser;

    /** Create new CTAGS_Buffer */
    public CTAGS_Buffer(CTAGS_Parser p) {
        super();
        Files = new Vector();
        Parser = p;
    }

    /**
    * Create CTAGS_Buffer which represent ctags output for fn file.
    * Later it can be append to existing CTAGS_Buffer
    */
    public CTAGS_Buffer(CTAGS_Parser p, String fn) {
        super();

        //Add filename to CTAGS_Buffer.Files
        for (int i = 0; i < Files.size(); i++) {
            if (!fn.equals(Files.get(i).toString())) {
                this.addFileName(fn);
            } else {
                this.removeFile(fn);
                Files.add(fn);
            }
        }

        //Set CTAGS_Buffer.Parser
        Parser = p;
    }

    /** Append CTAGS_Buffer.
    * When new file is added to Project we need the
    * way to add it to CTAGS_Buffer.
    */
    public void append(CTAGS_Buffer b) {
        CTAGS_Entry en;

        for (int i = 0; i < b.size(); i++) {
            en = (CTAGS_Entry) b.get(i);
            this.add(en);
            this.addFileName(en.getFileName());
        }
    }

    /**
    * Append CTAGS_Buffer.
    * When new file is added to Workspace we need
        * the way to add it to CTAGS_Buffer.
    */
    public void append(CTAGS_Buffer b, String filename) {
        this.removeFile(filename);

        CTAGS_Entry en;

        for (int i = 0; i < b.size(); i++) {
            en = (CTAGS_Entry) b.get(i);
            this.add(en);
        }

        this.addFileName(filename);
    }

    //TODO: NOT IMPLEMENTED YET!

    /**
    * Refresh CTAGS_Buffer (after removing file from project, for example)
    */
    public void reload() {
        Vector files = Files;
        this.clear();

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // = Parser.parse(files);
    }
     //}}}

    /**
    * remove all tags of spec. file from CTAGS_Buffer
    */
    public void removeFile(String f) {
        Vector files = getTagsByFile(f);

        for (int i = 0; i < files.size(); i++) {
            this.remove((CTAGS_Entry) files.get(i));
        }

        Files.remove(f);
    }

    /** Files (full path) for which CTAGS_Buffer was generated */
    public Vector getFileNames() {
        return Files;
    }

    /**
    * Returns Vector of CTAGS_Entries with spec. tag_name.
    * May be usualy it will return just one entry in vector,
    * but how about repeated tags? For example - all actionPerformed()
    */
    public Vector getEntry(String tag_name) {
        Vector v = new Vector();
        CTAGS_Entry en;

        for (int i = 0; i < this.size(); i++) {
            en = (CTAGS_Entry) this.get(i);

            if (tag_name.equals(en.getTagName())) {
                v.add(en);
            }
        }

        return v;
    }

    public Vector getEntresByStartPrefix(String prefix) {
        Vector v = new Vector();
        CTAGS_Entry en;

        for (int i = 0; i < this.size(); i++) {
            en = (CTAGS_Entry) this.get(i);

            if (en.getTagName().startsWith(prefix)) {
                if (!v.contains(en.getTagName())) {
                    v.add(en.getTagName());
                }
            }
        }

        return v;
    }

    public void addFileName(String f) {
        for (int i = 0; i < Files.size(); i++) {
            if (!f.equals(Files.get(i).toString())) {
                Files.add(f);
            }
        }
    }

    /**
    * Scan entire CTAGS_Buffer for entries from spec. file
       * which spec. signature.
    */
    public Vector getTagsBySignature(String signature) {
        Vector v = new Vector();
        CTAGS_Entry en;

        for (int i = 0; i < this.size(); i++) {
            en = (CTAGS_Entry) this.get(i);

            if (signature.equals(en.getSignature())) {
                v.add(en);
            }
        }

        return v;
    }

    /**
    * Scan entire CTAGS_Buffer for entries from spec. file
    */
    public Vector getTagsByFile(String file) {
        Vector result = new Vector();
        CTAGS_Entry entry;

        for (int i = 0; i < this.size(); i++) {
            entry = (CTAGS_Entry) this.get(i);

            if (file.equals(entry.getFileName())) {
                result.add((Object) entry);
            }
        }

        return result;
    }

    public boolean add(CTAGS_Entry entry) {
        this.addFileName(entry.getFileName());

        return super.add(entry);
    }

    public void clear() {
        Files.clear();
        super.clear();
    }

    public Object get(int index) {
        return super.get(index);
    }
}
