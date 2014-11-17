package sidekick.property.parser.property;

import sidekick.util.Location;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import org.gjt.sp.jedit.Buffer;

/**
 * Rewrite of the properties parser without javacc. This seems to be easier to
 * maintain and does a better job, especially with multi-line properties.
 */
public class PropertyParser2 {

    private Buffer buffer = null;
    private LineNumberReader reader = null;
    private boolean useSoftTabs = true;
    private int tabSize = 4;
    private List<Property> propertyList = null;
    private Property currentProperty = null;
    private boolean inMultiline = false;
    private int endLine = 0;
    private int endColumn = 0;
    private List<ParseException> exceptions = null;

    // these help set the asset locations correctly
    public void setSoftTabs( boolean b ) {
        useSoftTabs = b;
    }

    public void setTabSize( int size ) {
        if ( size < 0 ) {
            size = 0;
        }
        tabSize = size;
    }

    // the actual parsing starts here. Soft tabs and tab size should be set prior 
    // to calling this method. This simple parser reads the buffer contents line
    // by line. Comment liness and blank lines are ignored.
    public List<Property> parse( Buffer buffer ) {
        propertyList = new ArrayList<Property>();
        if ( buffer == null ) {
            return propertyList;
        }
        this.buffer = buffer;
        this.reader = new LineNumberReader( new StringReader( buffer.getText( 0, buffer.getLength() ) ) );
        try {

            String line = null;
            while ( true ) {
                line = reader.readLine();
                if ( line == null ) {
                    break;
                }
                processLine( line );
            }

            Collections.sort( propertyList );
        } catch ( Exception e ) {
            addException( e );
        } finally {
            try {
                reader.close();
            } catch ( Exception e ) {
                addException( e );
            }
        }
        return propertyList;
    }

    private void processLine( String line ) {
        if ( isBlankLine( line ) || isComment( line ) ) {
            return;
        }
        createProperty( line );
    }

    private void createProperty( String line ) {
        if ( inMultiline ) {
            // accumulate the lines in the property "key" field. This will be
            // split out into key and value when the last line of the property
            // has been read.
            currentProperty.setKey( currentProperty.getKey() + trimLine( line ) );
            boolean hasContinuation = hasContinuation( line );
            if ( ! hasContinuation ) {
                // finish out the multi-line property
                endLine = reader.getLineNumber();
                endColumn = buffer.getLineEndOffset( endLine );
                finishCurrentProperty();
            }
        } else {
            currentProperty = new Property();
            currentProperty.setKey( trimLine( line ) );
            int startLine = reader.getLineNumber();
            int startColumn = getLeadingWSLength( line );
            Location start = new Location( startLine, startColumn );
            currentProperty.setStartLocation( start );
            boolean hasContinuation = hasContinuation( line );
            if ( hasContinuation ) {
                inMultiline = true;
            } else {
                endLine = startLine;
                endColumn = buffer.getLineLength(endLine - 1);
                finishCurrentProperty();
            }
        }
    }

    private void finishCurrentProperty() {
        if ( currentProperty != null ) {
            inMultiline = false;
            Location end = new Location( endLine, endColumn );
            currentProperty.setEndLocation( end );
            String[] split = getKeyAndValue( currentProperty.getKey() );
            currentProperty.setKey( split[0] );
            currentProperty.setValue( split[1] );
            propertyList.add( currentProperty );
        }
        currentProperty = null;
    }

    // splits out the key and value from a single string
    private String[] getKeyAndValue( String s ) {
        int equals = findEquals( s );
        if ( equals > 0 ) {
            String key = s.substring( 0, equals );
            String value = s.substring( equals + 1 );
            return new String[] {trimStart( key ), value};
        } else {
            return new String[] {trimStart( s ), ""};
        }
    }

    // finds the "equals", that is, the character that separates the key from the
    // value, which is the first non-escaped =, :, or whitespace
    private int findEquals( String s ) {
        if ( s == null ) {
            return -1;
        }
        for ( int i = 0; i < s.length(); i++ ) {
            char c = s.charAt( i );
            switch ( c ) {
                case '=':
                case ':':
                    if ( i > 0 ) {
                        char escape = s.charAt( i - 1 );
                        if ( escape == '\\' ) {
                            continue;
                        } else {
                            return i;
                        }
                    }
                default:
                    if ( Character.isWhitespace( c ) && i > 0 ) {
                        char escape = s.charAt( i - 1 );
                        if ( escape == '\\' ) {
                            continue;
                        } else {
                            return i;
                        }
                    }
            }
        }
        return -1;
    }

    // checks if a line has a continuation character (a \) at the end. Note that
    // a line with two \ is not a continuation, rather, it's a line that ends with
    // an escaped \. This method checks if there is an odd number of \ at the
    // end of the line.
    private boolean hasContinuation( String line ) {
        int slashCount = 0;
        for ( int i = line.length() - 1; i >= 0; i-- ) {
            if ( line.charAt( i ) == '\\' ) {
                ++slashCount;
            } else {
                break;
            }
        }
        return slashCount > 0 && slashCount % 2 == 1;
    }

    // checks if a line contains only whitespace
    private boolean isBlankLine( String s ) {
        if ( s == null || s.length() == 0 ) {
            return true;
        }
        for ( int i = 0; i < s.length(); i++ ) {
            if ( !Character.isWhitespace( s.charAt( i ) ) ) {
                return false;
            }
        }
        return true;
    }

    // checks if a line is a comment line
    private boolean isComment( String line ) {
        String trimmed = trimStart( line );
        if ( trimmed.isEmpty() ) {
            return true;
        }
        char first = trimmed.charAt( 0 );
        return first == '#' || first == '!';
    }

    // trim all whitespace from the start of the given string
    private String trimStart( String s ) {
        if ( s == null ) {
            return "";
        }
        StringBuilder sb = new StringBuilder( s );
        while ( sb.length() > 0 && Character.isWhitespace( sb.charAt( 0 ) ) ) {
            sb.deleteCharAt( 0 );
        }
        return sb.toString();
    }

    // removes the last continuation character from the end of the string
    private String trimContinuation( String s ) {
        if ( s.endsWith( "\\" ) ) {
            return s.substring( 0, s.length() - 1 );
        }
        return s;
    }

    // removes all leading whitespace and last continuation character, if any
    private String trimLine( String s ) {
        return trimContinuation( trimStart( s ) );
    }

    // calculate the length of the leading whitespace for the given string
    // taking tab width into account.
    // TODO: check this, are hard tabs properly handled?
    private int getLeadingWSLength( String s ) {
        int length = 0;
        int tabWidth = useSoftTabs ? tabSize : 1;
        for ( int i = 0; i < s.length(); i++ ) {
            char c = s.charAt( i );
            if ( Character.isWhitespace( c ) ) {
                length += c == '\t' ? tabWidth : 1;
            } else {
                break;
            }
        }
        return length;
    }

    // accumulate any exceptions during parsing
    private void addException( Exception e ) {
        if ( e == null ) {
            return;
        }
        e.printStackTrace();
        ParseException pe = new ParseException( e.getMessage() );
        if ( exceptions == null ) {
            exceptions = new ArrayList<ParseException>();
        }
        exceptions.add( pe );
    }

    public List<ParseException> getExceptions() {
        return exceptions;
    }
}