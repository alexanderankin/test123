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


import net.sourceforge.jedit.buildtools.java.packagebrowser.*;
import net.sourceforge.jedit.buildtools.msg.*;
import java.io.*;

/**
A test suite for jcompiler

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
public class Test{

    public static void main(String[] args) {

        BuildMessage message = BuildMessage.getBuildMessage( "        at org.apache.tomcat.core.Response.getWriter(Response.java:217)" );
        message = BuildMessage.getBuildMessage( "        at java.io.FileInputStream.read(FileInputStream.java)");
        //message.dump();
    }

}
