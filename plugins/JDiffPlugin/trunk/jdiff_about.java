/*
 * jdiff_about.java
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

import java.awt.event.ActionEvent;

import org.gjt.sp.util.Log;

public class jdiff_about
    extends jdiff.gui.JDiffAction
{
    public jdiff_about() {  
        super("jdiff-about");
    }

    public void actionPerformed(ActionEvent evt) {
        Log.log(Log.DEBUG, this, "JDiff Plugin by Andre Kaplan");
    }   
}
