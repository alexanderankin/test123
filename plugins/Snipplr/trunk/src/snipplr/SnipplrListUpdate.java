/*
SnipplrListUpdate.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

This file written by Ian Lewis (IanLewis@member.fsf.org)
Copyright (C) 2006 Ian Lewis

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package snipplr;

import org.gjt.sp.jedit.*;

import java.util.List;

/**
 * Update passed when asyncronous RPC calls finish.
 */
public class SnipplrListUpdate extends EBMessage {
	
    private List<Snippet> m_result;
    private Exception m_error;
    
    public SnipplrListUpdate(List<Snippet> result) {
		super(null);
        m_result = result;
	}
    
    public SnipplrListUpdate(Exception error) {
		super(null);
        m_error = error;
	}
    
    public List<Snippet> getResult() {
        return m_result;
    }
    
    public boolean isError() {
        return (m_error != null);
    }
    
    public Exception getError() {
        return m_error;
    }
}
