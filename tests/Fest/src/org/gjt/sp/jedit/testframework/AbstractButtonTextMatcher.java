/*
* $Revision: 12715 $
* $Date: 2008-05-27 10:55:43 -0600 (Tue, 27 May 2008) $
* $Author: kerik-sf $
*
* Copyright (C) 2008 Eric Le Lay
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

package org.gjt.sp.jedit.testframework;


//{{{ Imports

//{{{  Java Classpath
import java.awt.Component;
import javax.swing.*;
//}}}



//{{{ FEST...
import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
//}}}



///}}}

public class AbstractButtonTextMatcher {
    static <T extends AbstractButton> GenericTypeMatcher<T> withText( Class<T> classe, final String text ) {
        return new GenericTypeMatcher(
                   AbstractButton.class ) {
                   @Override protected boolean isMatching( Component button ) {
                       return text.equals( ( ( AbstractButton ) button ).getText() );
                   }
               };
    }
}