/*:folding=indent:
* ErrorFindingMacros.java - Macros to parse for obvious errors.
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
package uk.co.antroy.latextools.macros;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import uk.co.antroy.latextools.parsers.LaTeXAsset;
import uk.co.antroy.latextools.LaTeXDockable;
import uk.co.antroy.latextools.parsers.LabelParser;


public class ErrorFindingMacros {

    //~ Methods ...............................................................

    public static void _displayDuplicateLabels(final View view, Buffer buff) {

        LaTeXDockable dockable = LaTeXDockable.getInstance();
        dockable.setInfoPanel(new JLabel("<html><font color='#dd0000'>Working..."), 
                              "Orphaned References:");

        LabelParser parser = new LabelParser(view, buff);
        List duplicates = parser.getDuplicateList();
        JComponent out = null;

        if (duplicates.size() == 0) {
            out = new JLabel("Success! No Duplicates Found!");
        } else {

            final JList list = new JList(duplicates.toArray());
            list.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {

                    LaTeXAsset asset = (LaTeXAsset)list.getSelectedValue();
                    TextMacros.visitAsset(view, asset);
                }
            });
            out = new JScrollPane(list, 
                                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }

        dockable.setInfoPanel(out, "Duplicate Labels:");
    }

    public static void _displayOrphanedRefs(final View view, Buffer buff) {
        LaTeXDockable.getInstance().setInfoPanel(new JLabel("<html><font color='#dd0000'>Working..."), 
                                                 "Orphaned References:");

        LabelParser labParser = new LabelParser(view, buff);
        Set labels = labParser.getLabelNameSet();
        LabelParser refParser = new LabelParser(view, buff, LabelParser.REF);
        Set refs = new HashSet(refParser.getLabelList());
        List orphans = new ArrayList();

        for (Iterator it = refs.iterator(); it.hasNext();) {

            LaTeXAsset asset = (LaTeXAsset)it.next();
            String name = asset.getShortString();

            if (!labels.contains(name)) {
                orphans.add(asset);
            }
        }

        JComponent out = null;

        if (orphans.size() == 0) {
            out = new JLabel("Success! No Orphaned References Found!");
        } else {

            final JList list = new JList(orphans.toArray());
            list.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {

                    LaTeXAsset asset = (LaTeXAsset)list.getSelectedValue();
                    TextMacros.visitAsset(view, asset);
                }
            });
            out = new JScrollPane(list, 
                                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }

        LaTeXDockable.getInstance().setInfoPanel(out, "Orphaned References:");
    }

    public static void displayDuplicateLabels(final View view, 
                                              final Buffer buff) {

        Thread t = new Thread(new Runnable() {
            public void run() {
                _displayDuplicateLabels(view, buff);
            }
        });
        t.start();
    }

    public static void displayOrphanedRefs(final View view, final Buffer buff) {

        Thread t = new Thread(new Runnable() {
            public void run() {
                _displayOrphanedRefs(view, buff);
            }
        });
        t.start();
    }
}
