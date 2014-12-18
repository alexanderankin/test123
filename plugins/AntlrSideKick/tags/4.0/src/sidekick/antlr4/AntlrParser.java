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
package sidekick.antlr4;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import sidekick.antlr4.parser.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import sidekick.util.*;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

public class AntlrParser extends SideKickParser {

    private static final String NAME = "antlr4";
    private View currentView = null;
    public static boolean showAll = false;

    public AntlrParser() {
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
        SideKickParsedData parsedData = new AntlrSideKickParsedData( filename );
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
            ANTLRv4Lexer lexer = new ANTLRv4Lexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            ANTLRv4Parser antlrParser = new ANTLRv4Parser( tokens );
            
            // add an error listener to the parser to capture any errors
            antlrParser.removeErrorListeners();
            errorListener = new SideKickErrorListener();
            antlrParser.addErrorListener(errorListener);
            
            // parse the buffer contents
            ParseTree tree = antlrParser.grammarSpec();
            ParseTreeWalker walker = new ParseTreeWalker();
            AntlrSideKickListener listener = new AntlrSideKickListener();
            walker.walk( listener, tree );

            // build the tree
            List<AntlrNode> lexerRules = listener.getLexerRules();
            List<AntlrNode> parserRules = listener.getParserRules();
            if ( lexerRules != null && lexerRules.size() > 0 ) {
                DefaultMutableTreeNode lexerTreeNode = new DefaultMutableTreeNode( "Lexer Rules" );
                root.add( lexerTreeNode );
                for ( AntlrNode rule : lexerRules ) {
                    lexerTreeNode.add(  new DefaultMutableTreeNode( rule ) );
                }
            }
            if ( parserRules != null && parserRules.size() > 0 ) {
                DefaultMutableTreeNode parserTreeNode = new DefaultMutableTreeNode( "Parser Rules" );
                root.add( parserTreeNode );
                for ( AntlrNode rule : parserRules ) {
                    parserTreeNode.add(  new DefaultMutableTreeNode( rule ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            handleErrors( buffer, errorSource, errorListener.getErrors() );
        }
        return parsedData;
    }

    /* the parser accumulates errors as it parses.  This method passed them all
    to the ErrorList plugin. */
    private void handleErrors( Buffer buffer, DefaultErrorSource errorSource, List<ParseException> errors ) {
        if (errors == null || errors.isEmpty()) {
            return;   
        }
        for ( ParseException pe : errors ) {
            errorSource.addError( ErrorSource.ERROR, buffer.getPath(), pe.getLineNumber(), pe.getColumn(), pe.getColumn() + pe.getLength(), pe.getMessage() );
        }
    }
    
}
