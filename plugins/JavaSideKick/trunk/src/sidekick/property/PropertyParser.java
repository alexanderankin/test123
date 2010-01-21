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
package sidekick.property;

import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import sidekick.util.*;
import sidekick.property.parser.property.ParseException;
import sidekick.property.parser.property.Property;
import sidekick.property.parser.property.Token;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;


public class PropertyParser extends SideKickParser {

    private static final String NAME = "properties";
    private View currentView = null;
    public static boolean showAll = false;

    public PropertyParser() {
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
        String filename = buffer.getPath();
        SideKickParsedData parsedData = new PropertySideKickParsedData( filename );
        DefaultMutableTreeNode root = parsedData.root;

        StringReader reader = new StringReader( buffer.getText( 0, buffer.getLength() ) );
        try {
            /* create the property parser property Property Parser -- I think
            this thing is going to parse some properties...! */
            sidekick.property.parser.property.PropertyParser parser = new sidekick.property.parser.property.PropertyParser( reader );

            // this makes the locations returned by the parser more accurate
            parser.setTabSize( buffer.getTabSize() );

            /* get the properties as Property objects, convert them to SideKick Assets,
            and add them to the tree */
            List<Property> properties = parser.Properties();
            for ( Property property : properties ) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode( property );
                root.add( node );
            }
            ElementUtil.convert(buffer, root);
            handleErrors( buffer, errorSource, parser.getExceptions() );
        }
        catch ( Exception e ) {
            //e.printStackTrace();
        }
        finally {
            reader.close();
        }
        return parsedData;
    }


    /* the parser accumulates errors as it parses.  This method passed them all
    to the ErrorList plugin. */
    private void handleErrors( Buffer buffer, DefaultErrorSource errorSource, List<ParseException> errors ) {
        for ( ParseException pe : errors ) {
            Location loc = getExceptionLocation( pe );
            errorSource.addError( ErrorSource.ERROR, buffer.getPath(), loc.line, loc.column, loc.column, pe.getMessage() );
        }
    }

    /**
     * @return attempts to return a Location indicating the location of a parser
     * exception.  If the ParseException contains a Token reference, all is well,
     * otherwise, this method attempts to parse the message string for the
     * exception location.
     */
    private Location getExceptionLocation( ParseException pe ) {
        Token t = pe.currentToken;
        if ( t != null ) {
            return new Location( t.next.beginLine - 1, t.next.beginColumn );
        }

        // ParseException message look like: "Parse error at line 116, column 5.  Encountered: }"
        try {
            Pattern p = Pattern.compile( "(.*?)(\\d+)(.*?)(\\d+)(.*?)" );
            Matcher m = p.matcher( pe.getMessage() );
            if ( m.matches() ) {
                String ln = m.group( 2 );
                String cn = m.group( 4 );
                int line_number = -1;
                int column_number = 0;
                if ( ln != null )
                    line_number = Integer.parseInt( ln );
                if ( cn != null )
                    column_number = Integer.parseInt( cn );
                return line_number > -1 ? new Location( line_number - 1, column_number ) : null;
            }
            return new Location( 0, 0 );
        }
        catch ( Exception e ) {
            return new Location( 0, 0 );
        }
    }
}
