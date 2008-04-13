/*
Snippet.java
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

import org.gjt.sp.jedit.*;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;

/**
 * A Snipplr code snippet. Contains a number of properties including
 * language, title, comment, and source text. The snippet object is capable of
 * retrieving and saving it's own data from the Snipplr service.
 */
public class Snippet {
    
    //{{{ Snippet constructor
    /**
     * Creates an empty Snippet.
     */
    public Snippet() {
        
    }//}}}
    
    //{{{ Snippet constructor
    /**
     * Creates a new Snippet based on the data given in the given HashMap.
     */
    public Snippet(String id) {
        m_id = id;
    }//}}}
    
    //{{{ Snippet constructor
    /**
     * Creates a new Snippet based on the data given in the given HashMap.
     */
    public Snippet(HashMap<String, Object> map) {
        updateFromMap(map);
    }//}}}
    
    //{{{ updateFromMap()
    
    public void updateFromMap(HashMap<String, Object> map) {
        if (map != null) {
            m_id = getStringValue(map, "id");
            m_userid = getStringValue(map, "user_id");
            m_username = getStringValue(map, "username");
            m_title = getStringValue(map, "title");
            m_language = LanguageMapper.getLanguageByPrettyName(getStringValue(map, "language"));
            m_comment = getStringValue(map, "comment");
            //TODO: created not supported yet.
            m_source = getStringValue(map, "source");
            m_url = getStringValue(map, "snipplr_url");
            m_tags = buildTags(getStringValue(map, "tags"));
        }
    }//}}}
    
    //{{{ getId()
    
    public String getId() {
        return m_id;
    }//}}}
    
    //{{{ getUserId()
    
    public String getUserId() {
        return m_userid;
    }//}}}
    
    //{{{ setUserId()
    
    public String setUserId(String userid) {
        String olduser = m_userid;
        m_userid = userid;
        return olduser;
    }//}}}
    
    //{{{ getUserName()
    
    public String getUserName() {
        return m_username;
    }//}}}
    
    //{{{ setUserName()
    
    public String setUserName(String username) {
        String oldusername = m_username;
        m_username = username;
        return oldusername;
    }//}}}
    
    //{{{ getTitle()
    
    public String getTitle() {
        return m_title;
    }//}}}
    
    //{{{ setTitle()
    
    public String setTitle(String title) {
        String oldtitle = m_title;
        m_title = title;
        return oldtitle;
    }//}}}
    
    //{{{ getLanguage()
    
    public Language getLanguage() {
        return m_language;
    }//}}}
    
    //{{{ setLanguage()
    
    public Language setLanguage(Language language) {
        Language oldlanguage = m_language;
        m_language = language;
        return oldlanguage;
    }//}}}
    
    //{{{ getComment()
    
    public String getComment() {
        return m_comment;
    }//}}}
    
    //{{{ setComment()
    
    public String setComment(String comment) {
        String oldcomment = m_comment;
        m_comment = comment;
        return oldcomment;
    }//}}}
    
    //{{{ getSource()
    
    public String getSource() {
        return m_source;
    }//}}}
    
    //{{{ setSource()
    
    public String setSource(String source) {
        String oldsource = m_source;
        m_source = source;
        return oldsource;
    }//}}}
    
    //{{{ getURL()
    
    public String getURL() {
        return m_url;
    }//}}}
    
    //{{{ setURL()
    
    public String setURL(String url) {
        String oldurl = m_url;
        m_url = url;
        return oldurl;
    }//}}}
    
    //{{{ getTagsString()
    
    public String getTagsString() {
        Iterator itr = getTags();
        StringBuffer buf = new StringBuffer();
        while (itr.hasNext()) {
            buf.append(itr.next().toString());
            if (itr.hasNext()) {
                buf.append(" ");
            }
        }
        return buf.toString();
    }//}}}
    
    //{{{ setTagsString()
    
    public void setTagsString(String tags) {
        m_tags = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(tags);
        while (tok.hasMoreTokens()) {
            addTag(tok.nextToken());
        }
    }//}}}
    
    //{{{ getTags()
    
    public Iterator getTags() {
        return m_tags.iterator();
    }//}}}
    
    //{{{ addTag()
    
    public void addTag(String tag) {
        m_tags.add(tag);
    }//}}}
    
    //{{{ removeTag()
    
    public void removeTag(String tag) {
        m_tags.remove(tag);
    }//}}}
    
    //{{{ getCreated()
    
    // public String getCreated() {
    //     return m_comment;
    // }//}}}
    
    //{{{ toString()
    
    public String toString() {
        String snippet = getTitle();
        if (snippet == null) {
            snippet = m_id;
            if (m_id == null) {
                snippet = super.toString();
            }
        }
        return snippet;
    }//}}}
    
    //{{{ load()
    /**
     * Loads the snippet from the Snipplr server.
     */
    public void load() throws XmlRpcException {
        if (!m_loaded) {
            SnipplrService.snippetGet(this);
            m_loaded = true;
        }
    }//}}}
    
    //{{{ save()
    /**
     * Saves the current snippet to the Snipplr server.
     */
    public void save() throws XmlRpcException {
        if (!m_loaded) {
            SnipplrService.snippetPost(this);
        }
    }//}}}
    
    //{{{ Private Members
    
    //{{{ getStringValue()
    
    private String getStringValue(HashMap<String, Object> map, String key) {
        if (map == null){
            return null;
        }

        Object value = map.get(key);
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }//}}}
    
    //{{{ buildTags()
    
    private ArrayList<String> buildTags(String tags) {
        ArrayList<String> tagsArray = new ArrayList<String>();
        if (tags != null) {
            StringTokenizer tokenizer = new StringTokenizer(tags);
            
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                tagsArray.add(token);
            }
        }
        return tagsArray;
    }//}}}
    
    private String m_id;
    private String m_userid;
    private String m_username;
    private String m_title;
    private Language m_language;
    private String m_comment;
    private Calendar m_created;
    private String m_source;
    private String m_url;
    private ArrayList<String> m_tags = new ArrayList<String>();
    private boolean m_loaded = false;
    
    //}}}
}
