/*
Language.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

This file written by Ian Lewis (IanLewis@member.fsf.org)
Copyright (C) 2007 Ian Lewis

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

//{{{ Imports
import java.util.*;
import org.apache.xmlrpc.XmlRpcException;
//}}}

/**
 * Represents a language that is supported by the Snipplr service.
 * Languages should only be created by the LanguageMapper which maintains
 * the language cache.
 */
public class Language {
    
    //{{{ Language constructor
    
    Language(String name, String humanReadableName) {
        m_name = name;
        m_humanReadableName = humanReadableName;
    }//}}}
    
    //{{{ getName()
    
    public String getName() {
        return m_name;
    }//}}}
    
    //{{{ getHumanReadableName()
    
    public String getHumanReadableName() {
        return m_humanReadableName;
    }//}}}
    
    //{{{ toString()
    
    public String toString() {
        return m_humanReadableName;
    }//}}}
    
    //{{{ equals()
    
    public boolean equals(Object obj) {
        if (obj instanceof Language) {
            Language lang = (Language)obj;
            return (getName().equals(lang.getName()) &&
                    getHumanReadableName().equals(lang.getHumanReadableName()));
        } else {
            return false;
        }
    }//}}}
    
    //{{{ Private members
    private String m_name;
    private String m_humanReadableName;
    //}}}
}