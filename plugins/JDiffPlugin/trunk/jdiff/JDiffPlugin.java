/*
 * JDiffPlugin.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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


package jdiff;

import java.awt.Color;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PropertiesChanged;


public class JDiffPlugin extends EBPlugin
{
    
    public static Color highlightChangedColor;
    public static Color highlightDeletedColor;
    public static Color highlightInsertedColor;
    public static Color highlightInvalidColor;
    public static Color leftCursorColor;
    public static Color rightCursorColor;
    
    static {
        propertiesChanged();
    }


    public JDiffPlugin() {
        super();
    }


    public void start() {}


    public void stop() {}


/*    public void createOptionPanes(OptionsDialog dialog) {
        OptionGroup jdiffGroup = new OptionGroup("jdiff");

        jdiffGroup.addOptionPane(new JDiffOptionPane());
        jdiffGroup.addOptionPane(new JDiffOverviewOptionPane());
        jdiffGroup.addOptionPane(new JDiffHighlightOptionPane());

        dialog.addOptionGroup(jdiffGroup);
    }*/


    public void handleMessage(EBMessage message) {
        if (message instanceof PropertiesChanged) {
            DualDiff.propertiesChanged();
            JDiffPlugin.propertiesChanged();
        }
    }


    public static void propertiesChanged() {
        // colors
        highlightChangedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.highlight-changed-color", "#FFFF90")
        );
        highlightDeletedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.highlight-deleted-color", "#FF9090")
        );
        highlightInsertedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.highlight-inserted-color", "#D9FF90")
        );

        highlightInvalidColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.highlight-invalid-color", "#909090")
        );
        leftCursorColor = jEdit.getColorProperty("jdiff.left-cursor-color", jEdit.getColorProperty("view.caretColor", Color.BLACK));
        rightCursorColor = jEdit.getColorProperty("jdiff.right-cursor-color", jEdit.getColorProperty("view.caretColor", Color.BLACK));
    }
}

