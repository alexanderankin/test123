/*
Copyright (c) 2014, Dale Anson
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
package sidekick.json;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import sidekick.json.parser.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import sidekick.util.ParseError;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

public class JsonParser extends SideKickParser {

    private static final String NAME = "json";
    private View currentView = null;
    public static boolean showAll = false;

    public JsonParser() {
        super( NAME );
    }

    /**
     * Parse the current buffer in the current view.
     * TODO: is this used anymore?
     */
    public void parse() {
        if ( currentView != null ) {
            parse( currentView.getBuffer(), null );
        }
    }

    /**
     * Parse the contents of the given buffer.
     * @param buffer the buffer to parse
     * @param errorSource where to send any error messages
     * @return data for the tree
     */
    public SideKickParsedData parse( Buffer buffer, DefaultErrorSource errorSource ) {
        Reader input = null;
        String filename = buffer.getPath();
        SideKickParsedData parsedData = new JsonSideKickParsedData( filename );
        DefaultMutableTreeNode root = parsedData.root;
        SideKickErrorListener errorListener = null;
        try {
            if ( buffer.getLength() <= 0 ) {
                return parsedData;
            }
            // set up the parser to read the buffer
            String contents = buffer.getText( 0, buffer.getLength() );
            input = new StringReader( contents );
            ANTLRInputStream antlrInput = new ANTLRInputStream( input );
            JSONLexer lexer = new JSONLexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            JSONParser jsonParser = new JSONParser( tokens );

            // add an error listener to the parser to capture any errors
            jsonParser.removeErrorListeners();
            errorListener = new SideKickErrorListener();
            jsonParser.addErrorListener( errorListener );

            // parse the buffer contents
            ParseTree tree = jsonParser.json();
            ParseTreeWalker walker = new ParseTreeWalker();
            JSONSideKickListener listener = new JSONSideKickListener();
            walker.walk( listener, tree );

            // build the tree
            JSONNode parserRoot = listener.getRoot();
            root.setUserObject( parserRoot );
            addChildren(root, buffer);
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            handleErrors( buffer, errorSource, errorListener.getErrors() );
        }
        return parsedData;
    }

    private void addChildren( DefaultMutableTreeNode node, Buffer buffer ) {
        JSONNode parent = ( JSONNode ) node.getUserObject();
        Collection<JSONNode> children = parent.getChildren();
        if ( children != null && children.size() > 0 ) {
            // add the children as tree nodes
            for ( JSONNode child : children ) {
                // create a tree node for the child and recursively add the childs children
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode( child );
                node.add( childNode );
                addChildren( childNode, buffer );
            }
        }
    }

    /* the parser accumulates errors as it parses.  This method passed them all
    to the ErrorList plugin. */
    private void handleErrors( Buffer buffer, DefaultErrorSource errorSource, List<sidekick.util.ParseError> errors ) {
        if ( errors == null || errors.isEmpty() ) {
            return;
        }
        for ( sidekick.util.ParseError pe : errors ) {
            errorSource.addError( ErrorSource.ERROR, buffer.getPath(), pe.getLineNumber(), pe.getColumn(), pe.getColumn() + pe.getLength(), pe.getMessage() );
        }
    }
}
