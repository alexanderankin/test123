/*
SnipplrService.java
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
import org.gjt.sp.jedit.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

import org.gjt.sp.util.Log;
//}}}

/**
 * Implements service calls to Snipplr. Most of these actions will
 * make XML-RPC calls to the Snipplr RPC service.
 */
public class SnipplrService {
    
    private static final String SERVER = "http://snipplr.com";
    private static final String END_POINT = "/xml-rpc.php";
    private static final String SERVICE_URL = "http://snipplr.com/xml-rpc.php";
    
    /**
     * Specifies title sort
     */
    public static final String TITLE_SORT = "title";
    /**
     * Specifies date sort
     */
    public static final String DATE_SORT = "date";
    /**
     * Specifies random sort
     */
    public static final String RANDOM_SORT = "random";
    
    //{{{ snippetList()
    /**
     * Performs an ansyncronous snippet.list API call to the snipplr service.
     * When the call is complete, a SnipplrListUpdate is sent to the EditBus.
     * @param tags the search string
     * @param sort the sort type. TITLE_SORT, DATE_SORT, or RANDOM_SORT
     * @param limit the number of snippets to limit the search to
     */
    public static void snippetList(String tags, String sort, int limit) {
        List<List<Snippet>> result = new ArrayList<List<Snippet>>();
        List<Exception> error = new ArrayList<Exception>();
        
        SnipplrPlugin.addWorkRequest(new ListRequest(tags, sort, limit, result, error), false);
        SnipplrPlugin.addWorkRequest(new ListAWTRequest(result, error), true);
    }//}}}
    
    //{{{ snippetGet()
    /**
     * Retrieves a fully loaded Snippet object from the Snipplr service. 
     * Performs the snippet.get XML-RPC call and blocks until a response is 
     * recieved.
     * @param id the id of the snippet to retrieve.
     */
    public static Snippet snippetGet(String id) throws XmlRpcException {
        return snippetGet(new Snippet(id));
    }//}}}
    
    //{{{ snippetGet()
    /**
     * Retrieves data for the given Snippet from the Snipplr service. Performs
     * the snippet.get XML-RPC call and blocks until a response is recieved.
     * @param snippet The Snippet whose data needs to be retrieved.
     * @return the Snippet object who's data has been populated.
     */
    public static Snippet snippetGet(Snippet snippet) throws XmlRpcException {
        Object[] params = new Object[]{snippet.getId()};
        Object result = performBlockingCall("snippet.get", params);
        
        Log.log(Log.DEBUG, SnipplrService.class, "Building result snippet");
        if (result instanceof HashMap) {
            snippet.updateFromMap((HashMap<String, Object>)result);
            return snippet;
        } else {
            return null;
        }
    }//}}}
    
    //{{{ snippetPost()
    /**
     * Posts a snippet's data to Snipplr. Performs the snippet.post XML-RPC
     * call and blocks until a response is recieved.
     */
    public static void snippetPost(Snippet snippet) throws XmlRpcException {
        String apiKey = jEdit.getProperty(SnipplrOptionPane.APIKEY);
        if (apiKey != null && apiKey.length() > 0) {
            
            String title = snippet.getTitle();
            String code = snippet.getSource();
            String tags = snippet.getTagsString();
            String language = snippet.getLanguage().getName();
            
            Object[] params = new Object[]{apiKey, title, code, tags, language};
            performBlockingCall("snippet.post", params);
        } else {
            GUIUtilities.error(null,"snipplr.apikey.error", new String[] {});
        }
    }//}}}
    
    //{{{ snippetDelete()
    public static boolean snippetDelete(Snippet snippet) throws XmlRpcException {
        String apiKey = jEdit.getProperty(SnipplrOptionPane.APIKEY);
        if (apiKey != null && apiKey.length() > 0) {
            Object[] params = new Object[]{apiKey, snippet.getId()};
            Object result = performBlockingCall("snippet.delete", params);
            // if (result instanceof Integer) {
            //     return (((Integer)result).intValue() == 1);
            // } else {
            //     return result.toString().equals("1");
            // }
            
            /*
            Snipplr's api doesn't do what the documentation says. It
            seems to return a boolean value rather than a 1 or 0 but
            it returns false even when it is successful.
            
            Not sure how to tell if I was actually successful so
            just return true if no exceptions are thrown.
            */
            return true;
        } else {
            GUIUtilities.error(null,"snipplr.apikey.error", new String[] {});
            return false;
        }
    }//}}}
    
    //{{{ languagesList()
    /**
     * Returns a HashMap whose keys are the internal name used by Snipplr
     * and whose values are the pretty names for display to the user.
     * @return a HashMap of languages supported by Snipplr
     */
    public static HashMap<String, String> languagesList() throws XmlRpcException {
        Object[] params = new Object[]{};
        Object result = performBlockingCall("languages.list", params);
        
        if (result instanceof HashMap) {
            HashMap<String, String> languages = (HashMap<String, String>)result;
            return languages;
        } else {
            Log.log(Log.ERROR, SnipplrService.class, "languages.list result not a HashMap!!");
            return null;
        }
    }//}}}
    
    //{{{ userCheckKey()
    /**
     * Checks Snipplr to see if the api key provided is valid. This method
     * performs the user.checkkey XML-RPC call and blocks until it recieves
     * a response.
     * @param apiKey the api key to check
     * @return true if the api key is valid.
     * @throws XmlRpcException if there is an error checking the validity of
     *                         the key
     */
    public boolean userCheckKey(String apiKey) throws XmlRpcException {
        Object[] params = new Object[]{ apiKey };
        Object result = performBlockingCall("user.checkkey", params);
        
        if (result instanceof Integer) {
            return (((Integer)result).intValue() == 1);
        } else {
            return false;
        }
    }//}}}
    
    //{{{ Private members
    
    //{{{ makeListFromArray()
    
    private static List<Snippet> makeListFromArray(Object[] array) {
        List<Snippet> snippets = new ArrayList<Snippet>();
        
        for (int i=0; i<array.length; i++) {
            if (array[i] instanceof HashMap) {
                Snippet snippet = new Snippet((HashMap<String, Object>)array[i]);
                snippets.add(snippet);
            }
        }
        
        return snippets;
    }//}}}
    
    //{{{ ListAWTRequest
    
    private static class ListAWTRequest implements Runnable {
        
        private List<List<Snippet>> m_result;
        private List<Exception> m_error;
        
        public ListAWTRequest(List<List<Snippet>> result, List<Exception> error) {
            m_result = result;
            m_error = error;
        }
        
        public void run() {
            //send a list update
            if (m_error.size() > 0) {
                Log.log(Log.DEBUG, SnipplrService.class, "Snippet list update error");
                EditBus.send(new SnipplrListUpdate(m_error.get(0)));
            } else {
                Log.log(Log.DEBUG, SnipplrService.class, "Sending snippet list update");
                EditBus.send(new SnipplrListUpdate(m_result.get(0)));
            }
        }
        
    }//}}}
    
    //{{{ ListRequest
    private static class ListRequest implements Runnable {
        
        private String m_tags;
        private String m_sort;
        private int m_limit;
        private List<List<Snippet>> m_result;
        private List<Exception> m_error;
        
        public ListRequest(String tags, String sort, int limit, List<List<Snippet>> result, List<Exception> error) {
            m_tags = tags;
            m_sort = sort;
            m_limit = limit;
            m_result = result;
            m_error = error;
        }
        
        public void run() {
            try {
                String apiKey = jEdit.getProperty(SnipplrOptionPane.APIKEY);
                if (apiKey != null && apiKey.length() > 0) {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    config.setServerURL(new URL(SERVICE_URL));
                    XmlRpcClient client = new XmlRpcClient();
                    client.setConfig(config);
                    
                    Log.log(Log.DEBUG, SnipplrService.class, "Performing snippet.list RPC call");
                    
                    Object[] params = new Object[]{apiKey, m_tags, m_sort, m_limit };
                    Object[] result = (Object[])client.execute("snippet.list", params);
                    
                    Log.log(Log.DEBUG, SnipplrService.class, "Building result list");
                    m_result.add(0, makeListFromArray(result));
                } else {
                    m_error.add(0, new RuntimeException(jEdit.getProperty("snipplr.apikey.error.message")));
                }
            } catch (MalformedURLException e) {
                Log.log(Log.ERROR, SnipplrService.class, e);
                GUIUtilities.error(null,"snipplr.request.error", new String[] { e.getMessage() });
            } catch (XmlRpcException e) {
                //Error code 1 means that we didn't match any snippets
                //this isn't really an error.
                if (e.code == 1) {
                    //empty results.
                    m_result.add(0, new ArrayList<Snippet>());
                } else {
                    m_error.add(0, e);
                }
            }
        }
    }//}}}
    
    //{{{ performBlockingCall
    /**
     * Performs a blocking RPC call to the Snipplr service
     */
    private static Object performBlockingCall(String procedure, Object[] params) throws XmlRpcException {
        try {
            
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(SERVICE_URL));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            
            Log.log(Log.DEBUG, SnipplrService.class, "Performing "+procedure+" RPC call");
            
            return client.execute(procedure, params);
            
        } catch (MalformedURLException e) {
            Log.log(Log.ERROR, SnipplrService.class, e);
            GUIUtilities.error(null,"snipplr.request.error", new String[] { e.getMessage() });
            return null;
        }
    }//}}}
    
    //}}}
}
