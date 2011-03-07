/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

/**
 * Objects implement this interface to be able to work with
 * Nav to add a navigation (web-browser-like back and forward) ability
 * to themselves.
 * @author Dale Anson, danson@grafidog.com
 */
public interface Navable {
    /**
     * Sets the position of the Navable object. Generally, if a Navable
     * object calls this method on itself, it should immediately call
     * <code>Nav.update(o)</code>.
     *
     * @param o  The new position value
     */
    public void setPosition(Object o);
}

