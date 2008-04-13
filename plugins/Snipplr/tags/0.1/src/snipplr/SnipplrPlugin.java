/*
SnipplrPlugin.java
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
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.WorkThreadPool;
import org.apache.xmlrpc.XmlRpcException;
import javax.swing.JOptionPane;
//}}}

public class SnipplrPlugin extends EditPlugin {
    
    public static final String NAME = "Snipplr";
    public static final String OPTION_PREFIX = "options."+NAME+".";
    
    private static WorkThreadPool workPool;
    
    public SnipplrPlugin() {}
    
    public void start() {
        try {
            LanguageMapper.buildLanguageCache();
        } catch (XmlRpcException e) {
            Log.log(Log.ERROR, this, e);
            GUIUtilities.error(null,"snipplr.request.error", new String[] { e.toString() });
        }
    }
    
    public void stop() {}
    
    //{{{ addNewSnippet() method
    
    public static void newSnippet(View view, TextArea textarea) {
        
        //Create new snippet from the buffer
        Snippet snippet = new Snippet();
        
        //Get the right language based on the buffer mode.
        Language lang = LanguageMapper.languageSearch(textarea.getBuffer().getMode().getName());
        
        //Get the current selection and set it as the source code.
        snippet.setLanguage(lang);
        snippet.setSource(textarea.getSelectedText());
        
        new SnippetForm(view, snippet);
    }//}}}
    
    //{{{ addWorkRequest() method
    public static void addWorkRequest(Runnable run, boolean inAWT) {
        if (workPool == null) {
            workPool = new WorkThreadPool("Snipplr",1);
            workPool.start();
        }
        workPool.addWorkRequest(run,inAWT);
    } //}}}
    
}
