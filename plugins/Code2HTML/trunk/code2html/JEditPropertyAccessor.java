/*
 * JEditPropertyAccessor.java
 * Copyright (c) 2002 Andre Kaplan
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
package code2html;

import org.gjt.sp.jedit.jEdit;


/**
 *  Access properties from jEdit
 *
 * @author     Andre Kaplan
 * @version    0.5
 * @todo       this class is really redundant
 */
public class JEditPropertyAccessor implements PropertyAccessor {

    /**
     *  Sets the boolean property of the object
     *
     * @param  name   The new boolean key name
     * @param  value  The new boolean property value
     */
    public void setBooleanProperty(String name, boolean value) {
        jEdit.setBooleanProperty(name, value);
    }


    /**
     *  Sets the property of the object
     *
     * @param  name   The new property key name
     * @param  value  The new property value
     */
    public void setProperty(String name, String value) {
        jEdit.setProperty(name, value);
    }


    /**
     *  Gets the boolean property of the object
     *
     * @param  name  key name
     * @return       The boolean property value
     */
    public boolean getBooleanProperty(String name) {
        return jEdit.getBooleanProperty(name);
    }


    /**
     *  Gets the boolean property of the object
     *
     * @param  name  key name
     * @param  def   default value
     * @return       The boolean property value
     */
    public boolean getBooleanProperty(String name, boolean def) {
        return jEdit.getBooleanProperty(name, def);
    }


    /**
     *  Gets the property of the object
     *
     * @param  name  key name
     * @return       The property value
     */
    public String getProperty(String name) {
        return jEdit.getProperty(name);
    }


    /**
     *  Gets the property of the object
     *
     * @param  name  key name
     * @param  def   default value
     * @return       The property value
     */
    public String getProperty(String name, String def) {
        return jEdit.getProperty(name, def);
    }


    /**
     *  Gets the property of the object
     *
     * @param  name  key name
     * @param  args  Returns the property with the specified name. The elements
     *      of the args array are substituted into the value of the property in
     *      place of strings of the form {n}, where n is an index in the array.
     *      You can find out more about this feature by reading the
     *      documentation for the format method of the 
     *      <code>java.text.MessageFormat<code> class.
     * @return       The property value
     */
    public String getProperty(String name, Object[] args) {
        return jEdit.getProperty(name, args);
    }
}

