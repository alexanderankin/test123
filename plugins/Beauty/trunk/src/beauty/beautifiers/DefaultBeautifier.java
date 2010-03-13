
package beauty.beautifiers;

import java.util.Properties;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;
import beauty.BeautyPlugin;

/**
 * This is a default beautifier to use when there is no specific beautifier
 * available.  This has a fairly limited set of options, mostly concerning
 * whitespace around certain buffer token types: function, digit, keyword, and
 * operator.
 */
public class DefaultBeautifier extends Beautifier {

    private boolean prePadOperator = true;
    private boolean prePadFunction = false;
    private boolean prePadDigit = true;
    private boolean prePadKeyword1 = false;
    private boolean prePadKeyword2 = false;
    private boolean prePadKeyword3 = false;
    private boolean prePadKeyword4 = false;
    private boolean postPadOperator = true;
    private boolean postPadFunction = false;
    private boolean postPadDigit = true;
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
                    if ( ( token.id == Token.OPERATOR && prePadOperator && previousTokenId != Token.OPERATOR ) ||            // NOPMD
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
                    if ( ( token.id == Token.OPERATOR && postPadOperator && token.next.id != Token.OPERATOR ) ||               // NOPMD
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

        sb = prePadCharacters( sb );
        sb = postPadCharacters( sb );
        sb = preInsertLineSeparators( sb );
        sb = postInsertLineSeparators( sb );
        sb = dontPrePadCharacters( sb );
        sb = dontPostPadCharacters( sb );
        sb = collapseBlankLines( sb );

        return sb.toString();
    }

    /**
     * The user may specify a list of characters to pad.
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

    private StringBuilder postPadCharacters( StringBuilder sb ) {
        if ( postPadCharacters.length() == 0 ) {
            return sb;
        }

        String s = sb.toString();
        if ( postPadCharacters.length() > 0 ) {
            for ( int i = 0; i < postPadCharacters.length(); i++ ) {
                char c = postPadCharacters.charAt( i );
                s = s.replaceAll( "[" + (c == '[' || c == ']' ? "\\" : "") + c + "](\\S)", c + "$1" );
            }
        }
        return new StringBuilder( s );
    }

    /**
     * The user may specify a list of characters after which a line separator
     * will be inserted.
     */
    private StringBuilder preInsertLineSeparators( StringBuilder sb ) {
        if ( preInsertLineCharacters.length() == 0 ) {
            return sb;
        }

        String[] parts = preInsertLineCharacters.split( "," );
        String s = sb.toString();

        for ( int i = 0; i < parts.length; i++ ) {
            String c = parts[ i ];
            s = s.replaceAll( "(?m)(?<!(^)(\\s*))" + c, getLineSeparator() + c );
        }
        return new StringBuilder( s );
    }

    // only add a line separator if the string is not followed by a line separator
    private StringBuilder postInsertLineSeparators( StringBuilder sb ) {
        if ( postInsertLineCharacters.length() == 0 ) {
            return sb;
        }

        String[] parts = postInsertLineCharacters.split( "," );
        String s = sb.toString();

        for ( int i = 0; i < parts.length; i++ ) {
            String c = parts[ i ];
            s = s.replaceAll( c + "(?!" + getLineSeparator() + ")", c + getLineSeparator() );
        }
        return new StringBuilder( s );
    }

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

    private StringBuilder collapseBlankLines( StringBuilder sb ) {
        if ( !collapseBlankLines ) {
            return sb;
        }
        String s = sb.toString();
        s = s.replaceAll( "(\\s*" + getLineSeparator() + "){3}", getLineSeparator() + getLineSeparator() );
        return new StringBuilder( s );
    }
}