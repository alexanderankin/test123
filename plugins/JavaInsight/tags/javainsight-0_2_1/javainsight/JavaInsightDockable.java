/*
 * JavaInsightDockable.java
 * Copyright (c) 2000 Andre Kaplan
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


package javainsight;

import java.awt.Component;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindow;


/**
 * Dockable JavaInsight panel
 * @author Andre Kaplan
 * @version $Id$
**/
public class JavaInsightDockable implements DockableWindow
{
    private JavaInsight javaInsight = null;


    public JavaInsightDockable(View view) {
        this.javaInsight = new JavaInsight(view);
    }


    public String getName() {
        return JavaInsightPlugin.DOCKABLE_NAME;
    }


    public Component getComponent() {
        return this.javaInsight;
    }
}

