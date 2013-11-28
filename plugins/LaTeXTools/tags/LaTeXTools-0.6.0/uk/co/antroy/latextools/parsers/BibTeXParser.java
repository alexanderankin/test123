/*
* BibTeXParser.java - BibTeX Parser
* Copyright (C) 2003 Anthony Roy
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package uk.co.antroy.latextools.parsers;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import uk.co.antroy.latextools.macros.ProjectMacros;


public class BibTeXParser {

    //~ Instance/static variables .............................................

    private List bibEntries = new ArrayList();
    private List bibFiles = new ArrayList();
    private Buffer buffer;
    private View view;
    private RE refRe;
//    private RE titleRe;
//    private RE authorRe;
    private RE contentsRe;

    //~ Constructors ..........................................................

    public BibTeXParser(View view, Buffer buff) {
        this.buffer = buff;
        this.view = view;

        try {
        	// Matches the begining of a bibtex entry, such as '@article{Budk:87,'
            refRe = new RE("@\\w+?\\s*?\\{\\s*?(.+?),"); // WARN.: match includes trailing whitespace
//            titleRe = new RE("\\btitle\\s*=\\s*\\({|\")(.+?)\\(}|\")");
//            authorRe = new RE("\\bauthor\\s*=\\s*\\({|\")(.*?)\\}\\s*(?:,|\\(}|\"))");
            // Note: (?:expr) is the same as (expr) but doesn't save the content
            // values may be enclosed in {..} or "..". Entries are separated by ','.
            // Ex: author = "Jara Cimrmam"
//            contentsRe = new RE("((?:\\bauthor)|(?:\\bjournal)|(?:\\btitle))\\s*=\\s*(?:\\{|\")(.*?)(?:\\}|\")\\s*(?:,|\\})", 
//                                RE.REG_MULTILINE | RE.REG_DOT_NEWLINE);
            contentsRe = new gnu.regexp.RE("((?:\\bauthor)|(?:\\bjournal)|(?:\\btitle))\\s*=\\s*(?:\\{|\")(.*?)(?:\\}|\")\\s*(?:,|\\})",gnu.regexp.RE.REG_MULTILINE | gnu.regexp.RE.REG_DOT_NEWLINE);
        } catch (REException e) {
            e.printStackTrace();

            return;
        }

        parse();
    }

    //~ Methods ...............................................................

    public List getBibEntries() {

        return bibEntries;
    }

    public Object[] getBibEntryArray() {

        return bibEntries.toArray();
    }

    public void parse() {
        bibEntries.clear();
        bibFiles.clear();

        if (ProjectMacros.isBibFile(buffer)) {
            File bibfile = new File(buffer.getPath());
            bibFiles.add(bibfile);
            loadBibEntries();

            return;
        }

        File texFile = new File(buffer.getPath());
        DefaultMutableTreeNode files = ProjectMacros.getProjectFiles(view, 
                                                                     buffer);
filesLoop: 
        for (Enumeration it = files.getLastLeaf().pathFromAncestorEnumeration(
                                          files); it.hasMoreElements();) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode)it.nextElement();
            File in = (File)node.getUserObject();
            Buffer buff = jEdit.openTemporary(view, in.getParent(), 
                                              in.getName(), false);
bufferLoop: 
            for (int i = buff.getLineCount() - 1; i > 0; i--) {

                String nextLine = buff.getLineText(i);

                if (nextLine.indexOf("%Bibliography") != -1 || 
                    nextLine.indexOf("\\begin{document}") != -1) {

                    break bufferLoop;
                }

                RE bibRe = null;
                RE theBibRe = null;

                try {
                    bibRe = new RE("[^%]*?\\\\bibliography\\{(.+?)\\}.*");
                    theBibRe = new RE("[^%]*?\\\\begin\\{thebibliography\\}.*");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean index = bibRe.isMatch(nextLine);
                boolean tbindex = theBibRe.isMatch(nextLine);

                if (index) {

                    StringTokenizer st = new StringTokenizer(bibRe.getMatch(nextLine).toString(
                                                                         1), 
                                                             ",");

                    //Add referenced bibtex files to bibFiles list.
                    while (st.hasMoreTokens()) {

                        String s = st.nextToken().trim();

                        if (!s.endsWith(".bib")) {
                            s = s + ".bib";
                        }

                        File f = new File(ProjectMacros.getMainTeXDir(buffer), 
                                          s);

                        if (!f.exists()) {
                            f = new File(s);
                        }

                        bibFiles.add(f);
                    }

                    loadBibEntries();

                    return;
                } else if (tbindex) {
                    loadTheBibliography(i);

                    return;
                }
            }
        }
    }

    /**
     * Parse the content of a BibTex reference.
     * Ex: '@article{Budk:87, key1 = "value1", key2 = {value2} ...'
     * @param segment The text of the reference includng the beginning' @article{Budk:87,'
     * @param ref The beginning (type and name) of the reference (e.g. '@article{Budk:87,').
     * @return BibEntry carrying info about the parsed bibtex entry  
     */
    private BibEntry getEntryIn(String segment, String ref) {

        REMatch[] entries = contentsRe.getAllMatches(segment);
        BibEntry out = new BibEntry(ref, "", "");

        for (int i = 0; i < entries.length; i++) {

            String key = entries[i].toString(1);		 // references the 1st (group)
            String description = entries[i].toString(2); // references the 2nd (group)

            if (key.equals("title")) {
                out.setTitle(description);
            } else if (key.equals("author")) {
                out.setAuthor(description);
            } else if (key.equals("journal")) {
                out.setJournal(description);
            }
        }

        return out;
    }

    private void loadBibEntries() {

        Iterator it = bibFiles.iterator();
        File bib;

        while (it.hasNext()) {
            bib = (File)it.next();
            parseBibEntriesForFile(bib);
        }

        Collections.sort(bibEntries);
    }

    private void loadTheBibliography(int startIndex) {

        int index = startIndex;
        int refStart;
        int end = buffer.getLineText(index).indexOf("\\end{thebibliography}");

        while (end == -1) {

            String line = buffer.getLineText(index);
            refStart = line.indexOf("\\bibitem{");

            if (refStart != -1) {

                int refEnd = line.indexOf("}");
                String bibRef = line.substring(refStart + 9, refEnd);
                BibEntry be = new BibEntry(bibRef.trim(), "", "");
                bibEntries.add(be);
            }

            end = buffer.getLineText(++index).indexOf("\\end{thebibliography}");
        }

        Collections.sort(bibEntries);
    }

    private void parseBibEntriesForFile(File bib) {

        Buffer buff = jEdit.openTemporary(view, bib.getParent(), bib.getName(), 
                                          false);
        // find all stuff like '@article{Budk:87,'
        REMatch[] references = refRe.getAllMatches(buff.getText(0, 
                                                                buff.getLength() - 1));

        REMatch second = null;
        
        // Parse each of the bibtex entries for 'key = value' pairs
        // WARN: likely doesn't work if there are < 2 bibtex references
        for (int i = 0; i < references.length-1; i++) {

            REMatch first = references[i];
            second = references[i + 1];
            int end = second.getStartIndex() - first.getStartIndex();
            String segment = buff.getText(first.getStartIndex(), end);
            BibEntry be = getEntryIn( segment, first.toString(1).trim() );
            bibEntries.add(be);
        }
        
        String segment = buff.getText(second.getStartIndex(), buff.getLength() - second.getStartIndex());
        BibEntry be = getEntryIn(segment, second.toString(1));
        bibEntries.add(be);
        
    }
}
