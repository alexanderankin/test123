/*
Copyright (c) 2005, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, 
    this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
    * Neither the name of the <ORGANIZATION> nor the names of its contributors 
    may be used to endorse or promote products derived from this software without 
    specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package sidekick.html;

import java.util.HashMap;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import sidekick.enhanced.SourceTree;

public class HtmlSideKickPlugin extends EditPlugin {
	public final static String NAME = "sidekick.html";
    
    public static final String INIT_DONE = "sidekick.HtmlParser.initDone";
    private static HashMap parserRegister = new HashMap();
    
    public static void registerParser(View view, HtmlParser parser) {
        parserRegister.put(view, parser); 
        initPopup(view);
    }
    
    public static void unregisterParser(View view) {
        parserRegister.remove(view);  
    }
    
    private static void initPopup(View view) {
        Boolean init_done = (Boolean)view.getRootPane().getClientProperty(INIT_DONE);
        if (init_done != null && init_done.booleanValue()){
            return;
        }
        
        // add popup menu items
		DockableWindowManager wm = view.getDockableWindowManager();
		SourceTree tree = (SourceTree) (wm.getDockable("sidekick-tree"));
		if (tree != null) {
            tree.addPopupEntry("htmlsidekick-showall", "Show All");
            view.getRootPane().putClientProperty(INIT_DONE, Boolean.TRUE);
		}
        else {
            view.getRootPane().putClientProperty(INIT_DONE, Boolean.FALSE);   
        }
    }
    
    public static void toggleShowAll(View view) {
        HtmlParser parser = (HtmlParser)parserRegister.get(view);
        if (parser != null) {
            parser.toggleShowAll();
            parser.parse();
        }
    }
    
}

