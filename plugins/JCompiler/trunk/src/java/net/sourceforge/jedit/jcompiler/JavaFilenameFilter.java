/*
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

package net.sourceforge.jedit.jcompiler;


//jEdit interface
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

//GUI support
import javax.swing.*;
import java.awt.event.ActionEvent;

//build support
import net.sourceforge.jedit.buildtools.*;
import net.sourceforge.jedit.buildtools.msg.*;


//standard java stuff
import java.util.Vector;
import java.io.*;
import java.lang.*;

import net.sourceforge.jedit.pluginholder.*;

/**
@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
public class JavaFilenameFilter implements java.io.FilenameFilter {
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public boolean accept(File dir, String name) {

        if (name.endsWith(".java") == false) {
            return false;
        }

        String clazzName = name.substring(0, name.length()-5) + ".class";

        File clazzFile = new File(dir, clazzName);

        if (clazzFile.exists() == false) {
            return true;
        }

        File srcFile = new File(dir, name);

        if (srcFile.exists() == false) {
            return false;
        }

        long srcTime = srcFile.lastModified(); 
        long clsTime = clazzFile.lastModified();
        if (srcTime >= clsTime) {
            return true;
        }
        return false;
    }


}

