/*
LanguageMapper.java
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
import org.gjt.sp.jedit.jEdit;

import java.util.*;
import org.apache.xmlrpc.XmlRpcException;

import org.gjt.sp.util.Log;
//}}}

/**
 * Provides a way of mapping jEdit modes to Snipplr languages.
 */
public class LanguageMapper {
    
    private static HashMap<String, Language> m_languageCache;
    
    //{{{ getLanguages()
    
    public static HashMap<String, Language> getLanguages() {
        // buildLanguageCache();
        return m_languageCache;
    }//}}}
    
    //{{{ languageSearch()
    /**
     * Tries to find a Snipplr language for a given jEdit mode.
     * This should work for pretty much any language that matches a jEdit
     * mode.
     * @param mode the jEdit mode
     */
    public static Language languageSearch(String mode) {
        
        // buildLanguageCache();
        
        mode = mode.toLowerCase(); // just to make sure
        
        //First see if there is a property mapping the language names.
        Language lang = m_languageCache.get(jEdit.getProperty("plugin.snipplr.language."+mode));
        if (lang != null) {
            return lang;
        }
        
        //try an exact match.
        lang = m_languageCache.get(mode);
        if (lang != null) {
            return lang;
        }
        
        //Otherwise we are going to look for the closest language
        Iterator<String> itr = m_languageCache.keySet().iterator();
        
        while (itr.hasNext()) {
            
            String language = itr.next();
            String origLanguage = language;
            
            //change plus to +. for things like C++
            language = language.replaceAll("plus", "+");
            if (language.equals(mode)) {
                return m_languageCache.get(origLanguage);
            }
            
            //change sharp to #, for stuff like C#
            language = language.replaceAll("sharp", "#");
            if (language.equals(mode)) {
                return m_languageCache.get(origLanguage);
            }
            
            //remove - characters
            language = language.replaceAll("-", "");
            if (language.equals(mode)) {
                return m_languageCache.get(origLanguage);
            }
            
            //remove numeric characters. Remove version numbers etc.
            language = language.replaceAll("\\n", "");
            if (language.equals(mode)) {
                return m_languageCache.get(origLanguage);
            }
        }
        
        return m_languageCache.get("other");
    }//}}}
    
    //{{{ getLanguage()
    /**
     * Maps the language's url name to the language.
     * @param language the url name of the language
     * @return the matching language
     */
    public static Language getLanguage(String language) {
        // buildLanguageCache();
        return m_languageCache.get(language);
    }//}}}
    
    //{{{ getLanguageByPrettyName()
    /**
     * Maps the language's human readable description to the language.
     * The language is indexed this way because snippet.get returns the
     * language description rather than the url name of the language.
     * @param language the human readable description of the language (pretty name).
     * @return the matching language
     */
    public static Language getLanguageByPrettyName(String language) {
        // buildLanguageCache();
        return m_languageCache.get(language);
    }//}}}
    
    //{{{ buildLanguageCache()
    /**
     * Get the languages.list from Snipplr and create cache.
     */
    public static void buildLanguageCache() throws XmlRpcException {
        if (m_languageCache == null) {
            m_languageCache = new HashMap<String, Language>();
            HashMap<String, String> list = SnipplrService.languagesList();
            Iterator<String> listItr = list.keySet().iterator();
            while (listItr.hasNext()) {
                String key = listItr.next();
                
                Log.log(Log.DEBUG,LanguageMapper.class,"Adding Snipplr language to cache: "+key+" => "+list.get(key));
                
                m_languageCache.put(key, new Language(key, list.get(key)));
                //index via the pretty name rather than the url name
                m_languageCache.put(list.get(key), new Language(key, list.get(key)));
            }
        }
    }//}}}
    
}