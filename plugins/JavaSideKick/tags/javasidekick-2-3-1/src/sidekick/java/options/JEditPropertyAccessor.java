/*
 * JEditPropertyAccessor.java
 *
 * Copyright (c) 1999-2000 George Latkiewicz, Andre Kaplen
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


package sidekick.java.options;

import org.gjt.sp.jedit.jEdit;


/**
 * jEdit property accessor for JBrowse
 * @author George Latkiewicz
 * @author Andre Kaplan
 * @version $Id$
**/
public class JEditPropertyAccessor
    implements PropertyAccessor
{
    public String getProperty(String name) {
        return jEdit.getProperty(name);
    }


    public String getProperty(String name, String def) {
        return jEdit.getProperty(name, def);
    }


    public String getProperty(String name, Object[] args) {
        return jEdit.getProperty(name, args);
    }


    public boolean getBooleanProperty(String name) {
        return jEdit.getBooleanProperty(name);
    }


    public boolean getBooleanProperty(String name, boolean def) {
        return jEdit.getBooleanProperty(name, def);
    }


    public void setProperty(String name, String value) {
        jEdit.setProperty(name, value);
    }


    public void setBooleanProperty(String name, boolean value) {
        jEdit.setBooleanProperty(name, value);
    }
}

