/*
Copyright (c) 2006, Dale Anson
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

import java.awt.Point;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.*;
import sidekick.*;
import sidekick.util.*;
import sidekick.html.parser.html.*;

/**
 * danson, central location to deal with actions as defined in actions.xml.
 */
public class HtmlActions {

	/**
	 * Moves the caret to the matching tag.    
     * Note -- I added this, then realized that sidekick has built-in functionality
     * that does the same thing, almost.  This is actually a little better than
     * the 'go to previous asset' and 'go to next asset' provided by sidekick.
	 */
    public static void matchTag( View view, JEditTextArea textArea ) {
        try {
            // get the asset at the curren caret position
            SideKickParsedData data = SideKickParsedData.getParsedData( view );
            int cp = textArea.getCaretPosition();
            SideKickAsset asset = ( SideKickAsset ) data.getAssetAtOffset( cp );
            SideKickElement element = asset.getElement();
            
            // maybe move the caret and select the matching tag
            if ( element instanceof HtmlDocument.TagBlock ) {
                HtmlDocument.TagBlock block = ( HtmlDocument.TagBlock ) element;
                HtmlDocument.Tag start_tag = block.startTag;
                HtmlDocument.EndTag end_tag = block.endTag;
                if ( cp >= start_tag.getStartPosition().getOffset() && cp <= start_tag.getEndPosition().getOffset() ) {
                    // caret is in start tag, jump to end tag. 
                    textArea.setSelection( new Selection.Range( end_tag.getStartPosition().getOffset(), end_tag.getEndPosition().getOffset() ) );
                    textArea.moveCaretPosition( end_tag.getEndPosition().getOffset() );
                }
                else if ( cp >= end_tag.getStartPosition().getOffset() && cp <= end_tag.getEndPosition().getOffset() ) {
                    // caret is in end tag, jump to end tag
                    textArea.setSelection( new Selection.Range( start_tag.getStartPosition().getOffset(), start_tag.getEndPosition().getOffset() + 1 ) );
                    textArea.moveCaretPosition( start_tag.getStartPosition().getOffset() );
                }
                else {
                    // not in a tag?
                    textArea.getToolkit().beep();
                }
            }
        }
        catch ( Exception e ) {
            // ignore
            //e.printStackTrace();
        }
    }
}
