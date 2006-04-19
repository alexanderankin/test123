/*
 * PropertyAccessor.java
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


public interface PropertyAccessor
{
    /**
     * Fetches a property, returning null if it's not defined.
     * @param name The property
     */
    String getProperty(String name);


    /**
     * Fetches a property, returning the default value if it's not
     * defined.
     * @param name The property
     * @param def The default value
     */
    String getProperty(String name, String def);


    /**
     * Returns the property with the specified name, formatting it with
     * the <code>java.text.MessageFormat.format()</code> method.
     * @param name The property
     * @param args The positional parameters
     */
    String getProperty(String name, Object[] args);


    /**
     * Fetches a boolean property, returning false if it's not defined.
     * @param name The property
     */
    boolean getBooleanProperty(String name);


    /**
     * Fetches a boolean property, returning the default value if it's not
     * defined.
     * @param name The property
     * @param def The default value
     */
    boolean getBooleanProperty(String name, boolean def);


    /**
     * Sets a property to a new value.
     * @param name The property
     * @param value The new value
     */
    void setProperty(String name, String value);


    /**
     * Sets a boolean property to a new value.
     * @param name The property
     * @param value The new value
     */
    void setBooleanProperty(String name, boolean value);
}

