package sidekick.property.parser.property;

import sidekick.util.Location;
import java.io.Reader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Rewrite of the properties parser without javacc. This seems to be easier to 
 * maintain and does a better job, especially with multi-line properties.
 */
public class PropertyParser2 {

    private LineNumberReader reader = null;
    private int tabSize = 4;
    private List<Property> propertyList = null;
    private Property currentProperty = null;
    private boolean inMultiline = false;

    public PropertyParser2( Reader reader ) {
        if ( reader == null ) {
            throw new IllegalArgumentException( "reader is null" );
        }
        this.reader = new LineNumberReader( reader );
    }

    // this helps set the asset locations correctly
    public void setTabSize( int size ) {
        if ( size < 0 ) {
            throw new IllegalArgumentException( "tab size cannot be less than 0" );
        }
        tabSize = size;
    }

    // the actual parsing starts here
    public List<Property> Properties() {
        try {
            if ( reader == null ) {
                throw new RuntimeException( "reader is null" );
            }

            propertyList = new ArrayList<Property>();

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
            e.printStackTrace();
        }
        return propertyList;
    }

    private void processLine( String line ) {
        if ( isBlankLine( line ) || isComment(line) ) {
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
                inMultiline = false;
                int endLine = reader.getLineNumber();
                int endColumn = getEndColumn( line );
                Location end = new Location( endLine, endColumn );
                currentProperty.setEndLocation( end );
                String[] split = getKeyAndValue( currentProperty.getKey() );
                currentProperty.setKey( split[0] );
                currentProperty.setValue( split[1] );
                propertyList.add( currentProperty );
            }
        } else {
            currentProperty = new Property();
            int startLine = reader.getLineNumber();
            int startColumn = getLeadingWSLength( line );
            Location start = new Location( startLine, startColumn );
            currentProperty.setStartLocation( start );
            boolean hasContinuation = hasContinuation( line );
            if ( hasContinuation ) {
                inMultiline = true;
                currentProperty.setKey( trimLine( line ) );
            } else {
                int endColumn = getEndColumn( line );
                Location end = new Location( startLine, endColumn );
                currentProperty.setEndLocation( end );
                String[] split = getKeyAndValue( line );
                currentProperty.setKey( split[0] );
                currentProperty.setValue( split[1] );
                propertyList.add( currentProperty );
            }
        }
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

    // finds the "equals", that is, the character that separates the key from the value
    private int findEquals( String s ) {
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

    private String trimLine( String s ) {
        return trimContinuation( trimStart( s ) );
    }

    private int getLeadingWSLength( String s ) {
        int length = 0;
        for ( int i = 0; i < s.length(); i++ ) {
            char c = s.charAt( i );
            if ( Character.isWhitespace( c ) ) {
                length += c == '\t' ? tabSize : 1;
            } else {
                break;
            }
        }
        return length;
    }

    private int getEndColumn( String s ) {
        int length = 0;
        for ( int i = 0; i < s.length(); i++ ) {
            length += s.charAt( i ) == '\t' ? tabSize : 1;
        }
        return length + 1;  // column is 1-based
    }

    public List<ParseException> getExceptions() {
        return null;
    }
}