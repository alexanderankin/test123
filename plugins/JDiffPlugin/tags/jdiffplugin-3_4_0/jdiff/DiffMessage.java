
/*
* Copyright (c) 2008, Dale Anson
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
package jdiff;

import org.gjt.sp.jedit.*;

public class DiffMessage extends EBMessage {
    /**
     * Sent when DualDiff is toggled on.
     */
    public static final Object ON = "ON";

    /**
    * Sent when DualDiff is toggled off.
    */
    public static final Object OFF = "OFF";

    // should be either ON or OFF
    private Object what;

    public DiffMessage( View view, Object what ) {
        super( view );

        if ( what == null ){
            throw new IllegalArgumentException( "What must be non-null" );
        }

        this.what = what;
    }

    public Object getWhat() {
        return what;
    }

    public View getView() {
        return ( View ) getSource();
    }

    public String paramString() {
        return "what=" + what + "," + super.paramString();
    }
}