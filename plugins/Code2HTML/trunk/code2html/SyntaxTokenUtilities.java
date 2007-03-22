/*
 * SyntaxTokenUtilities.java
 * Copyright (c) 2002 Andre Kaplan
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package code2html;

import org.gjt.sp.jedit.syntax.Token;

/**
 *  Utilities to deal with Syntax Tokens
 *
 * @author     Andre Kaplan
 * @version    0.5
 * @todo       Desambiguate from the local package (i.e. remove about 1/2 the
 *      methods there are) after having removed and/or changed code to use
 *      latest Token class version in jEdit's trunk
 */
public class SyntaxTokenUtilities {
    /**
     *  SyntaxTokenUtilities Constructor - disabled
     */
    private SyntaxTokenUtilities() { }


    /**
     *  Converts a (jEdit) Token list to a SyntaxToken list
     *
     * @param  token  The tokens to convert
     * @return        A SyntaxToken corresponding to the input Token object
     */
    public static SyntaxToken convertTokens(Token token) {
        if (token == null) {
            return null;
        }

        SyntaxToken syntaxToken = convertToken(token);
        ///syntaxToken.prev = convertPrevTokens(token.prev);
        syntaxToken.next = convertNextTokens(token.next);

        return syntaxToken;
    }


    /**
     *  Converts a (code2html) Token list to a SyntaxToken list
     *
     * @param  token  The tokens to convert
     * @return        A SyntaxToken corresponding to the input Token object
     */
    public static SyntaxToken convertTokens(code2html.syntax.Token token) {
        if (token == null) {
            return null;
        }

        SyntaxToken syntaxToken = convertToken(token);
        syntaxToken.prev = convertPrevTokens(token.prev);
        syntaxToken.next = convertNextTokens(token.next);

        return syntaxToken;
    }


    /**
     *  Converts the next tokens in a (jedit) token list to SyntaxTokens
     *
     * @param  token  The current token
     * @return        A list of SyntaxTokens derived from the token->next object
     */
    private static SyntaxToken convertNextTokens(Token token) {
        if (token == null) {
            return null;
        }

        SyntaxToken syntaxToken = convertToken(token);

        if (token.next != null) {
            syntaxToken.next = convertNextTokens(token.next);
            syntaxToken.next.prev = syntaxToken;
        }

        return syntaxToken;
    }


    /**
     *  Converts the next tokens in a (code2html) token list to SyntaxTokens
     *
     * @param  token  The current token
     * @return        A list of SyntaxTokens derived from the token->next object
     */
    private static SyntaxToken convertNextTokens(code2html.syntax.Token token) {
        if (token == null) {
            return null;
        }

        SyntaxToken syntaxToken = convertToken(token);

        if (token.next != null) {
            syntaxToken.next = convertNextTokens(token.next);
            syntaxToken.next.prev = syntaxToken;
        }

        return syntaxToken;
    }


    /**
     *  Converts the previous tokens in a (jedit) token list to SyntaxTokens.
     *
     * @param  token  The current token
     * @return        A list of SyntaxTokens derived from the token->prev object
     * @todo          This method looks bogus. Look into it
     */
    private static SyntaxToken convertPrevTokens(Token token) {
        if (token == null) {
            return null;
        }

        SyntaxToken syntaxToken = convertToken(token);

        ///if (token.prev != null) {
        ///    syntaxToken.prev      = convertPrevTokens(token.prev);
        ///    syntaxToken.prev.next = syntaxToken;
        ///}

        return syntaxToken;
    }


    /**
     *  Converts the previous tokens in a (code2html) token list to
     *  SyntaxTokens.
     *
     * @param  token  The current token
     * @return        A list of SyntaxTokens derived from the token->prev object
     */
    private static SyntaxToken convertPrevTokens(code2html.syntax.Token token) {
        if (token == null) {
            return null;
        }

        SyntaxToken syntaxToken = convertToken(token);

        if (token.prev != null) {
            syntaxToken.prev = convertPrevTokens(token.prev);
            syntaxToken.prev.next = syntaxToken;
        }

        return syntaxToken;
    }


    /**
     *  Converts a single (jedit) token to a SyntaxToken
     *
     * @param  token  The token to convert
     * @return        A SyntaxToken object corresponding to the token
     */
    private static SyntaxToken convertToken(Token token) {
        if (token == null) {
            return null;
        }

        SyntaxToken syntaxToken = new SyntaxToken();

        syntaxToken.length = token.length;
        syntaxToken.id = token.id;

        return syntaxToken;
    }


    /**
     *  Converts a single (code2html) token to a SyntaxToken
     *
     * @param  token  The token to convert
     * @return        A SyntaxToken object corresponding to the token
     */
    private static SyntaxToken convertToken(code2html.syntax.Token token) {
        if (token == null) {
            return null;
        }

        SyntaxToken syntaxToken = new SyntaxToken();

        syntaxToken.length = token.length;
        syntaxToken.id = token.id;

        return syntaxToken;
    }
}

