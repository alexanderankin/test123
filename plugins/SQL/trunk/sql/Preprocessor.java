/**
 * Preprocessor.java - Sql Plugin
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
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
package sql;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    22 Февраль 2002 г.
 */
public abstract class Preprocessor
{
  protected boolean enabled;

  public Preprocessor()
  {
    enabled = jEdit.getBooleanProperty( getClass().getName() + ".enabled" );
    Log.log( Log.DEBUG, Preprocessor.class,
     getClass().getName() + " is enabled: " + enabled );
  }

  protected View view;

  public void setView( View v )
  { view = v; }

  public String process( String text )
  {
    if ( !enabled ) return text;
    return doProcess( text );
  }

  protected abstract String doProcess( String text );
}

