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
package sidekick.css;

import java.io.StringReader;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

import sidekick.*;
import sidekick.util.*;

import sidekick.css.parser.CSSNode;
import sidekick.css.parser.CSS3Parser;

/**
 * @author    Dale Anson
 * @version   $Revision$
 */
public class CSS2SideKickParser extends SideKickParser {

    private View currentView = null;

    public static boolean showAll = true;
    private int lineOffset = 0;

    public CSS2SideKickParser() {
        super( "css" );
    }

    /**
     * If called by another parser to parse part of a file (for example, to parse
     * a style tag in an html document), this can be set to the offset of the
     * style tag so that the node locations can be set correctly.
     */
    public void setLineOffset( int offset ) {
        if ( offset > 0 ) {
            lineOffset = offset;
        }
    }

    public void parse() {
        if ( currentView != null ) {
            parse( currentView.getBuffer(), null );
        }
    }

    /**
     * Parse the contents of the given buffer.  This is the standard entry point
     * and will cause the entire text of the buffer to be parsed.
     *
     * @param buffer       the buffer to parse
     * @param errorSource  where to send errors
     * @return             Description of the Returned Value
     */
    public SideKickParsedData parse( Buffer buffer, DefaultErrorSource errorSource ) {
        setLineOffset( 0 );
        return parse( buffer, buffer.getText( 0, buffer.getLength() ), errorSource );
    }

    /**
     * Parse the contents of the given text.  This is the entry point to use when
     * only a portion of the buffer text is to be parsed.  Note that <code>setLineOffset</code>
     * should be called prior to calling this method, otherwise, tree node positions
     * may be off.
     *
     * @param buffer       the buffer to parse
     * @param errorSource  where to send errors
     * @return             Description of the Returned Value
     */
    public SideKickParsedData parse( Buffer buffer, String text, DefaultErrorSource errorSource ) {

        String filename = buffer.getPath();
        SideKickParsedData parsedData = new CSSParsedData( buffer.getName() );
        DefaultMutableTreeNode root = parsedData.root;

        StringReader reader = new StringReader( text );
        try {
            // create parser
            CSS3Parser parser = new CSS3Parser( reader );

            // set line offset, the parser uses this to adjust line numbers in the
            // case of a partial file, like when the stylesheet is embedded inside an
            // html document
            parser.setLineOffset( lineOffset );

            // set tab size so that the parser can accurately calculate line and
            // column positions
            parser.setTabSize( buffer.getTabSize() );

            // should proprieatary properties be considered an error?  True or
            // false, the file will parse, if set to true and there are
            // proprietary property names, then an error will be passed to
            // ErrorList.
            parser.setProprietaryAsError( jEdit.getBooleanProperty( CssSideKickPlugin.OPTION_PREFIX + "showProprietaryAsError" ) );

            // parse the text
            CSSNode ss = parser.styleSheet();

            // make a tree
            root.setUserObject( ss );
            addTreeNodes( root, ss );

            // need to convert the CSSNodes that are currently the user objects
            // in the tree nodes to SideKick Assets
            ElementUtil.convert( buffer, root );

            if ( !buffer.isDirty() && errorSource != null ) {
                /* only handle errors when buffer is saved. Otherwise, there will be a lot
                of spurious errors shown when code completion is on and the user is in the
                middle of typing something. */
                List<ParseError> parseErrors = parser.getParseErrors();
                for ( ParseError pe : parseErrors ) {
                    String message = pe.message;
                    Range range = pe.range;
                    // addError is lame -- what if the error spans more than one line?
                    // Need to just deal with it...
                    if ( range.endLine != range.startLine ) {
                        range.endColumn = range.startColumn;
                    }
                    errorSource.addError( ErrorSource.ERROR, filename, range.startLine, range.startColumn, range.endColumn, message );
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        finally {
            reader.close();
        }
        return parsedData;
    }

    private void addTreeNodes( DefaultMutableTreeNode root, CSSNode ss ) {
        if ( ss.hasChildren() ) {
            for ( Iterator it = ss.getChildren().iterator(); it.hasNext(); ) {
                CSSNode cssChild = ( CSSNode ) it.next();
                if ( cssChild != null ) {
                    DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode( cssChild );
                    root.add( dmtNode );
                    addTreeNodeChildren( dmtNode, cssChild );
                }
            }
        }
    }

    private void addTreeNodeChildren( DefaultMutableTreeNode dmtNode, CSSNode cssNode ) {
        if ( cssNode.hasChildren() ) {
            for ( Iterator it = cssNode.getChildren().iterator(); it.hasNext(); ) {
                CSSNode cssChild = ( CSSNode ) it.next();
                if ( cssChild != null ) {
                    DefaultMutableTreeNode dmtChild = new DefaultMutableTreeNode( cssChild );
                    dmtNode.add( dmtChild );
                    addTreeNodeChildren( dmtChild, cssChild );
                }
            }
        }
    }

    public boolean supportsCompletion() {
        return true;
    }

    public boolean canCompleteAnywhere() {
        return true;
    }

    public SideKickCompletion complete( EditPane editPane, int caret ) {
        CompletionRequest cr = new CompletionRequest( editPane, caret );
        return cr.getSideKickCompletion();
    }

}