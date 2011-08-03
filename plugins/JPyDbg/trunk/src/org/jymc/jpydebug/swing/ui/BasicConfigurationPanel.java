/**
* Copyright (C) 2003,2004,2005 Jean-Yves Mengant
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

package org.jymc.jpydebug.swing.ui;

import org.jymc.jpydebug.utils.*;

import java.awt.*;

import java.util.Hashtable;

import javax.swing.*;


/**
 * Parent class used by all netbeans + jedit cross IDE configuration panels
 *
 * @author jean-yves
 */
public class BasicConfigurationPanel extends JPanel
{

  protected Hashtable _labels = new Hashtable();
  protected Hashtable _values = new Hashtable();

  /**
   * Creates a new instance of BasicConfigurationPanel
   */
  public BasicConfigurationPanel(){ }

  /**
   * Set field label
   *
   * @param key   field's key
   * @param value associated text label
   */
  public void set_label( String key, String value )
  {
    _labels.put( key, value );
  }


  /**
   * Set field value 
   *
   * @param key   field's key 
   * @param value associated value
   */
  public void set_value( String key, String value )
  {
    _values.put( key, value );
  }


  /**
   * set boolean field value for check boxes ans friends
   *
   * @param key   field's key
   * @param value fields value
   */
  public void set_booleanValue( String key, boolean value )
  {
    _values.put(
                key,
                new Boolean( value )
             );
  }


  /**
   * set Integer field value
   *
   * @param key   field's key 
   * @param value field integer value
   */
  public void set_integerValue( String key, int value )
  {
    _values.put(
                key,
                new Integer( value )
             );
  }


  /**
   * set Font field value
   *
   * @param key   field's key 
   * @param value field font value
   */
  public void set_fontValue( String key, Font value )
  {
    _values.put( key, value );
  }


  /**
   * set Color field value
   *
   * @param key   field's key 
   * @param value field color value
   */
  public void set_colorValue( String key, Color value )
  {
    _values.put( key, value );
  }


  /**
   * get field label bakc
   *
   * @param  key field key 
   * @param  def default value to be applied when null
   *
   * @return found label value or default
   */
  public String get_label( String key, String def )
  {
    if (_labels.get( key ) != null)
      return (String) _labels.get( key );

    return def;
  }


  /**
   * get field current value back
   *
   * @param  key field key 
   * @param  def default value to be returned when null
   *
   * @return found value or default
   */
  public String get_value( String key, String def )
  {
    if (_values.get( key ) != null)
      return (String) _values.get( key );

    return def;
  }


  /**
   * get field current boolean value back (checkboxes and friends)
   *
   * @param  key field key 
   * @param  def default value to be returned when null
   *
   * @return found value or default
   */
  public boolean get_booleanValue( String key, boolean def )
  {
    if (_values.get( key ) != null)
      return ((Boolean) _values.get( key )).booleanValue();

    return def;
  }


  /**
   * get field current font value back (checkboxes and friends)
   *
   * @param  key field key 
   *
   * @return found value or null
   */
  public Font get_fontValue( String key )
  {
    if (_values.get( key ) != null)
      return ((Font) _values.get( key ));

    return null;
  }


  /**
   * get field current Color value back (checkboxes and friends)
   *
   * @param  key field key 
   *
   * @return found value or null
   */
  public Color get_colorValue( String key )
  {
    if (_values.get( key ) != null)
      return ((Color) _values.get( key ));

    return null;
  }


  /**
   * get field current Integer value back 
   *
   * @param  key field key 
   * @param  def default value to be returned when null
   *
   * @return found value or default
   */
  public int get_integerValue( String key, int def )
  {
    if (_values.get( key ) != null)
      return ((Integer) _values.get( key )).intValue();

    return def;
  }

}
