/*
* Copyright (C) 2009, Dale Anson
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
* 
*/

package tasklist;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;

public class ParseBufferMessage extends EBMessage {

    public static Object DO_PARSE = "do-parse";
    public static Object DO_PARSE_ALL = "do-parse-all";
    public static Object APPLY_FILTER = "apply-filter";

    private View view;
    private Buffer buffer;
    private Object what;

    public ParseBufferMessage( EBComponent source ) {
        super( source );
    }

    public ParseBufferMessage( Object source ) {
        super( source );
    }

    public ParseBufferMessage( View view, Buffer buffer, Object what ) {
        super( (Object) null );
        this.view = view;
        this.buffer = buffer;
        this.what = what;
    }

    public View getView() {
        return view;
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public Object getWhat() {
        return what;
    }
}