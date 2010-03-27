
package beauty.beautifiers;

import java.io.*;
import java.util.Properties;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;
import beauty.BeautyPlugin;

/**
 * This is a default beautifier to use when there is no specific beautifier
 * available.  This can make for a pretty decent beautifier if configured
 * correctly.
 * I wonder if this is a good place to implement elastic tab stops?
 */
public class DefaultBeautifier extends Beautifier {

    private boolean prePadOperator = false;
    private boolean prePadFunction = false;
    private boolean prePadDigit = false;
    private boolean prePadKeyword1 = false;
    private boolean prePadKeyword2 = false;
    private boolean prePadKeyword3 = false;
    private boolean prePadKeyword4 = false;
    private boolean postPadOperator = false;
    private boolean postPadFunction = false;
    private boolean postPadDigit = false;
    private boolean postPadKeyword1 = false;
    private boolean postPadKeyword2 = false;
    private boolean postPadKeyword3 = false;
    private boolean postPadKeyword4 = false;

    private boolean labelOnSeparateLine = true;

    private String prePadCharacters = "";
    private String postPadCharacters = "";
    private String dontPrePadCharacters = "";
    private String dontPostPadCharacters = "";

    // these are comma separated strings now, not a list of characters
    private String preInsertLineCharacters = "";
    private String postInsertLineCharacters = "";

    private boolean collapseBlankLines = false;

    public DefaultBeautifier( String modeName ) {
        Properties props = BeautyPlugin.getCustomModeProperties( modeName );
        prePadOperator = "true".equals( props.getProperty( "prePadOperators" ) ) ? true : false;
        prePadFunction = "true".equals( props.getProperty( "prePadFunctions" ) ) ? true : false;
        prePadDigit = "true".equals( props.getProperty( "prePadDigits" ) ) ? true : false;
        prePadKeyword1 = "true".equals( props.getProperty( "prePadKeywords1" ) ) ? true : false;
        prePadKeyword2 = "true".equals( props.getProperty( "prePadKeywords2" ) ) ? true : false;
        prePadKeyword3 = "true".equals( props.getProperty( "prePadKeywords3" ) ) ? true : false;
        prePadKeyword4 = "true".equals( props.getProperty( "prePadKeywords4" ) ) ? true : false;
        postPadOperator = "true".equals( props.getProperty( "postPadOperators" ) ) ? true : false;
        postPadFunction = "true".equals( props.getProperty( "postPadFunctions" ) ) ? true : false;
        postPadDigit = "true".equals( props.getProperty( "postPadDigits" ) ) ? true : false;
        postPadKeyword1 = "true".equals( props.getProperty( "postPadKeywords1" ) ) ? true : false;
        postPadKeyword2 = "true".equals( props.getProperty( "postPadKeywords2" ) ) ? true : false;
        postPadKeyword3 = "true".equals( props.getProperty( "postPadKeywords3" ) ) ? true : false;
        postPadKeyword4 = "true".equals( props.getProperty( "postPadKeywords4" ) ) ? true : false;
        labelOnSeparateLine = "true".equals( props.getProperty( "labelOnSeparateLine" ) ) ? true : false;
        prePadCharacters = props.getProperty( "prePadCharacters" ) == null ? "" : props.getProperty( "prePadCharacters" );
        postPadCharacters = props.getProperty( "postPadCharacters" ) == null ? "" : props.getProperty( "postPadCharacters" );
        dontPrePadCharacters = props.getProperty( "dontPrePadCharacters" ) == null ? "" : props.getProperty( "dontPrePadCharacters" );
        dontPostPadCharacters = props.getProperty( "dontPostPadCharacters" ) == null ? "" : props.getProperty( "dontPostPadCharacters" );
        preInsertLineCharacters = props.getProperty( "preInsertLineCharacters" ) == null ? "" : props.getProperty( "preInsertLineCharacters" );
        postInsertLineCharacters = props.getProperty( "postInsertLineCharacters" ) == null ? "" : props.getProperty( "postInsertLineCharacters" );
        collapseBlankLines = "true".equals( props.getProperty( "collapseBlankLines" ) ) ? true : false;
    }

    public String beautify( String text ) {
        StringBuilder sb = new StringBuilder();
        //long startTime = System.currentTimeMillis();
        sb = padTokens( sb );
        //long padTokensTime = System.currentTimeMillis();
        sb = prePadCharacters( sb );
        //long prePadCharactersTime = System.currentTimeMillis();
        sb = postPadCharacters( sb );
        //long postPadCharactersTime = System.currentTimeMillis();
        sb = preInsertLineSeparators( sb );
        //long preInsertLineSeparatorsTime = System.currentTimeMillis();
        sb = postInsertLineSeparators( sb );
        //long postInsertLineSeparatorsTime = System.currentTimeMillis();
        sb = dontPrePadCharacters( sb );
        //long dontPrePadCharactersTime = System.currentTimeMillis();
        sb = dontPostPadCharacters( sb );
        //long dontPostPadCharactersTime = System.currentTimeMillis();
        sb = collapseBlankLines( sb );
        //long collapseBlankLinesTime = System.currentTimeMillis();

        /*
        System.out.println( "+++++ pad tokens time = " + ( padTokensTime - startTime ) );
        System.out.println( "+++++ pre-pad characters time = " + ( prePadCharactersTime - padTokensTime ) );
        System.out.println( "+++++ post-pad characters time = " + ( postPadCharactersTime - prePadCharactersTime ) );
        System.out.println( "+++++ pre-insert line separators = " + ( preInsertLineSeparatorsTime - postPadCharactersTime ) );
        System.out.println( "+++++ post-insert line separators = " + ( postInsertLineSeparatorsTime - preInsertLineSeparatorsTime ) );
        System.out.println( "+++++ dont pre-pad characters = " + ( dontPrePadCharactersTime - postInsertLineSeparatorsTime ) );
        System.out.println( "+++++ dont post-pad characters = " + ( dontPostPadCharactersTime - dontPrePadCharactersTime ) );
        System.out.println( "+++++ collapse blank lines time = " + ( collapseBlankLinesTime - dontPostPadCharactersTime ) );
        */
        
        return sb.toString();
    }
    
    /**
     * Pad the tokens found by the jEdit syntax highlighting engine. In
     * general, I found that this is pretty horrible since many of the 
     * mode files do a poor job of identifying tokens.
     */
    private StringBuilder padTokens( StringBuilder sb ) {
        if ( prePadFunction ||
                postPadFunction ||
                prePadOperator ||
                postPadOperator ||
                prePadDigit ||
                postPadDigit ||
                prePadKeyword1 ||
                postPadKeyword1 ||
                prePadKeyword2 ||
                postPadKeyword2 ||
                prePadKeyword3 ||
                postPadKeyword3 ||
                prePadKeyword4 ||
                postPadKeyword4 ) {
            int firstLine = 0;
            int lastLine = buffer.getLineCount();
            DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
            for ( int lineNum = firstLine; lineNum < lastLine; lineNum++ ) {
                tokenHandler.init();

                int lineStart = buffer.getLineStartOffset( lineNum );
                buffer.markTokens( lineNum, tokenHandler );
                Token token = tokenHandler.getTokens();
                int tokenStart = lineStart;

                String previousTokenText = "";
                byte previousTokenId = Token.NULL;
                String currentTokenText = buffer.getText( tokenStart, token.length );
                String nextTokenText = token.next != null ? buffer.getText( tokenStart + token.length, token.next.length ) : "";

                while ( token.id != Token.END ) {

                    // maybe pad start
                    if ( !previousTokenText.endsWith( " " ) ) {
                        if ( ( token.id == Token.OPERATOR && prePadOperator && previousTokenId != Token.OPERATOR ) ||                 // NOPMD
                                ( token.id == Token.FUNCTION && prePadFunction ) ||
                                ( token.id == Token.DIGIT && prePadDigit ) ||
                                ( token.id == Token.KEYWORD1 && prePadKeyword1 ) ||
                                ( token.id == Token.KEYWORD2 && prePadKeyword2 ) ||
                                ( token.id == Token.KEYWORD3 && prePadKeyword3 ) ||
                                ( token.id == Token.KEYWORD4 && prePadKeyword4 ) ) {
                            sb.append( ' ' );
                        }
                    }

                    // maybe add a line for a label
                    boolean onlyWhitespace = buffer.getText( lineStart, tokenStart - lineStart ).trim().length() > 0;
                    if ( token.id == Token.LABEL && labelOnSeparateLine && onlyWhitespace ) {
                        sb.append( getLineSeparator() );
                    }

                    // definitely add text of current token
                    sb.append( currentTokenText );

                    // maybe pad after token
                    if ( !nextTokenText.startsWith( " " ) ) {
                        if ( ( token.id == Token.OPERATOR && postPadOperator && token.next.id != Token.OPERATOR ) ||                    // NOPMD
                                ( token.id == Token.FUNCTION && postPadFunction ) ||
                                ( token.id == Token.DIGIT && postPadDigit ) ||
                                ( token.id == Token.KEYWORD1 && postPadKeyword1 ) ||
                                ( token.id == Token.KEYWORD2 && postPadKeyword2 ) ||
                                ( token.id == Token.KEYWORD3 && postPadKeyword3 ) ||
                                ( token.id == Token.KEYWORD4 && postPadKeyword4 ) ) {
                            sb.append( ' ' );
                            currentTokenText += " ";    // NOPMD
                        }
                    }

                    previousTokenText = currentTokenText;
                    previousTokenId = token.id;
                    currentTokenText = nextTokenText;
                    tokenStart += token.length;
                    token = token.next;
                    if ( token.next != null ) {
                        nextTokenText = buffer.getText( tokenStart + token.length, token.next.length );
                    }
                }
                if ( lineNum <= lastLine - 2 ) {
                    sb.append( getLineSeparator() );
                }
            }
        }
        return sb;
    }

    /**
     * The user may specify a list of characters to pad in front of.
     */
    private StringBuilder prePadCharacters( StringBuilder sb ) {
        if ( prePadCharacters.length() == 0 ) {
            return sb;
        }

        String s = sb.toString();
        if ( prePadCharacters.length() > 0 ) {
            for ( int i = 0; i < prePadCharacters.length(); i++ ) {
                char c = prePadCharacters.charAt( i );
                s = s.replaceAll( "(\\S)[" + c + "]", "$1" + c );
            }
        }
        return new StringBuilder( s );
    }

    /**
     * The user may specify a list of characters to pad after.
     */
    private StringBuilder postPadCharacters( StringBuilder sb ) {
        if ( postPadCharacters.length() == 0 ) {
            return sb;
        }

        String s = sb.toString();
        if ( postPadCharacters.length() > 0 ) {
            for ( int i = 0; i < postPadCharacters.length(); i++ ) {
                char c = postPadCharacters.charAt( i );
                s = s.replaceAll( "[" + ( c == '[' || c == ']' ? "\\" : "" ) + c + "](\\S)", c + "$1" );
            }
        }
        return new StringBuilder( s );
    }

    /**
     * The user may specify a comma separated list of strings before which a 
     * line separator will be inserted.  
     */
    private StringBuilder preInsertLineSeparators( StringBuilder sb ) {
        if ( preInsertLineCharacters.length() == 0 ) {
            return sb;
        }

        String[] chars = preInsertLineCharacters.split( "," );
        for ( String c : chars ) {
            sb = preInsertLineSeparators( sb, c );
        }
        return sb;
    }

    private StringBuilder preInsertLineSeparators( StringBuilder sb, String c ) {
        String s = sb.toString();
        try {
            // I'm doing this line by line since this is way faster than the
            // original regex implementation -- 6 seconds with regex on my test
            // file versus 22 milliseconds doing line by line.
            BufferedReader reader = new BufferedReader( new StringReader( s ) );
            StringWriter stringWriter = new StringWriter();
            BufferedWriter writer = new BufferedWriter( stringWriter );

            String line;
            String ls = getLineSeparator();

            while ( ( line = reader.readLine() ) != null ) {
                String unc = c.startsWith( "\\" ) ? c.substring( 1 ) : c;
                // there may be more than one 'c' on the line
                String[] lineParts = line.split( c, Integer.MAX_VALUE );
                for ( int j = 0; j < lineParts.length; j++ ) {
                    String part = lineParts[ j ];
                    writer.write( part );
                    writer.write( ls );
                    if ( j < lineParts.length - 1 ) {
                        writer.write( unc );
                    }
                }
            }
            reader.close();
            writer.flush();
            writer.close();
            return new StringBuilder( stringWriter.toString() );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return new StringBuilder( s );
        }
    }

    /**
     * The user may specify a comma separated list of strings after which a 
     * line separator will be inserted.  
     */
    private StringBuilder postInsertLineSeparators( StringBuilder sb ) {
        if ( postInsertLineCharacters.length() == 0 ) {
            return sb;
        }

        String[] chars = postInsertLineCharacters.split( "," );
        for ( String c : chars ) {
            sb = postInsertLineSeparators( sb, c );
        }
        return sb;
    }

    private StringBuilder postInsertLineSeparators( StringBuilder sb, String c ) {
        String s = sb.toString();
        try {
            // I'm doing this line by line because I need to be able to avoid
            // inserting extra blank lines.  The original regex implementation
            // was fast enough, but inserted extra blank lines.
            BufferedReader reader = new BufferedReader( new StringReader( s ) );
            StringWriter stringWriter = new StringWriter();
            BufferedWriter writer = new BufferedWriter( stringWriter );

            String line;
            String ls = getLineSeparator();
            boolean wroteLS = false;
            
            while ( ( line = reader.readLine() ) != null ) {
                // don't add extra blank lines
                if (wroteLS && line.trim().equals("")) {
                    wroteLS = false;
                    continue;   
                }
                
                String unc = c.startsWith( "\\" ) ? c.substring( 1 ) : c;
                // there may be more than one 'c' on the line
                String[] lineParts = line.split( c, Integer.MAX_VALUE );
                for ( int j = 0; j < lineParts.length; j++ ) {
                    String part = lineParts[ j ];
                    writer.write( part );
                    if ( j < lineParts.length - 1 ) {
                        writer.write( unc );
                        writer.write( ls );
                        wroteLS = true;
                    }
                    else if (j == lineParts.length - 1) {
                        writer.write(ls);   
                        wroteLS = true;
                    }
                }
            }
            reader.close();
            writer.flush();
            writer.close();
            return new StringBuilder( stringWriter.toString() );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return new StringBuilder( s );
        }
    }

    /**
     * Remove a single whitespace character from before a character.  Only
     * one whitespace character is removed.  The intent here is that a
     * character may be mis-identified as an operator token in the mode file,
     * so it gets padded as an operator.  The user can add this character to
     * the don't pad list and have that padding removed.  An example is the
     * javascript mode, where ; is defined as an operator.
     */
    private StringBuilder dontPrePadCharacters( StringBuilder sb ) {
        if ( dontPrePadCharacters.length() == 0 ) {
            return sb;
        }

        String s = sb.toString();
        if ( dontPrePadCharacters.length() > 0 ) {
            for ( int i = 0; i < dontPrePadCharacters.length(); i++ ) {
                char c = dontPrePadCharacters.charAt( i );
                s = s.replaceAll( "\\s[" + c + "]", String.valueOf( c ) );
            }
        }
        return new StringBuilder( s );
    }
    
    /**
     * Remove a single whitespace character from after a character.  Only
     * one whitespace character is removed.  The intent here is that a
     * character may be mis-identified as an operator token in the mode file,
     * so it gets padded as an operator.  The user can add this character to
     * the don't pad list and have that padding removed.  An example is the
     * javascript mode, where ; is defined as an operator.
     */
    private StringBuilder dontPostPadCharacters( StringBuilder sb ) {
        if ( dontPostPadCharacters.length() == 0 ) {
            return sb;
        }

        String s = sb.toString();
        if ( dontPostPadCharacters.length() > 0 ) {
            for ( int i = 0; i < dontPostPadCharacters.length(); i++ ) {
                char c = dontPostPadCharacters.charAt( i );
                s = s.replaceAll( "[" + c + "]\\s", String.valueOf( c ) );
            }
        }
        return new StringBuilder( s );
    }
    
    /**
     * Collapse two or more blank lines to a single blank line.    
     */
    private StringBuilder collapseBlankLines( StringBuilder sb ) {
        if ( !collapseBlankLines ) {
            return sb;
        }
        String s = sb.toString();
        s = s.replaceAll( "(\\s*" + getLSString() + "){3}", getLineSeparator() + getLineSeparator() );
        return new StringBuilder( s );
    }
    
    /**
     * @return A string representing the line separator escaped for using it
     * in a regular expression.
     */
    private String getLSString() {
        String ls = getLineSeparator();
        if ( "\r".equals( ls ) ) {
            return "\\\\r";
        }
        if ( "\r\n".equals( ls ) ) {
            return "\\\\r\\\\n";
        }
        return "\\\\n";
    }
}