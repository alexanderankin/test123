/*
 * FilePropertyAccessor.java
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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;


public class FilePropertyAccessor implements PropertyAccessor
{
    private Properties props = new Properties();


    public FilePropertyAccessor() {}


    //{{{ loadProps() method
    /**
     * Loads the properties from the specified input stream. This
     * calls the <code>load()</code> method of the properties object
     * and closes the stream.
     * @param in The input stream
     * @exception IOException if an I/O error occured
     */
    /* package-private */ void loadProps(InputStream in)
        throws IOException
    {
        in = new BufferedInputStream(in);
        this.props.load(in);
        in.close();
    } //}}}


    //{{{ getProperty() method
    /**
     * Fetches a property, returning null if it's not defined.
     * @param name The property
     */
    public final String getProperty(String name) {
        return this.props.getProperty(name);
    } //}}}


    //{{{ getProperty() method
    /**
     * Fetches a property, returning the default value if it's not
     * defined.
     * @param name The property
     * @param def The default value
     */
    public final String getProperty(String name, String def) {
        return this.props.getProperty(name, def);
    } //}}}


    //{{{ getProperty() method
    /**
     * Returns the property with the specified name, formatting it with
     * the <code>java.text.MessageFormat.format()</code> method.
     * @param name The property
     * @param args The positional parameters
     */
    public final String getProperty(String name, Object[] args) {
        if (name == null) {
            return null;
        }
        if (args == null) {
            return this.props.getProperty(name);
        } else {
            String value = this.props.getProperty(name);
            if (value == null) {
                return null;
            } else {
                return MessageFormat.format(value, args);
            }
        }
    } //}}}


    //{{{ getBooleanProperty() method
    /**
     * Returns the value of a boolean property.
     * @param name The property
     */
    public final boolean getBooleanProperty(String name) {
        return this.getBooleanProperty(name, false);
    } //}}}


    //{{{ getBooleanProperty() method
    /**
     * Returns the value of a boolean property.
     * @param name The property
     * @param def The default value
     */
    public final boolean getBooleanProperty(String name, boolean def) {
        String value = this.getProperty(name);
        if (value == null) {
            return def;
        } else if (
            value.equals("true") || value.equals("yes") || value.equals("on")
        ) {
            return true;
        } else if (
            value.equals("false") || value.equals("no") || value.equals("off")
        ) {
            return false;
        } else {
            return def;
        }
    } //}}}


    //{{{ setProperty() method
    /**
     * Sets a property to a new value.
     * @param name The property
     * @param value The new value
     */
    public final void setProperty(String name, String value) {
        if (value == null || value.length() == 0) {
            props.remove(name);
        } else {
            props.put(name, value);
        }
    } //}}}


    //{{{ setBooleanProperty() method
    /**
     * Sets a boolean property.
     * @param name The property
     * @param value The value
     */
    public final void setBooleanProperty(String name, boolean value) {
        this.setProperty(name, value ? "true" : "false");
    } //}}}
}

