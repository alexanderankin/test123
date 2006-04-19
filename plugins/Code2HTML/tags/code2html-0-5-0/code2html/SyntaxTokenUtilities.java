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


public class SyntaxTokenUtilities
{
    private SyntaxTokenUtilities() {}


    public static SyntaxToken convertTokens(org.gjt.sp.jedit.syntax.Token token) {
        if (token == null) { return null; }

        SyntaxToken syntaxToken = convertToken(token);
        ///syntaxToken.prev = convertPrevTokens(token.prev);
        syntaxToken.next = convertNextTokens(token.next);

        return syntaxToken;
    }


    private static SyntaxToken convertToken(org.gjt.sp.jedit.syntax.Token token) {
        if (token == null) { return null; }

        SyntaxToken syntaxToken = new SyntaxToken();

        syntaxToken.length = token.length;
        syntaxToken.id     = token.id;

        return syntaxToken;
    }


    private static SyntaxToken convertPrevTokens(org.gjt.sp.jedit.syntax.Token token) {
        if (token == null) { return null; }

        SyntaxToken syntaxToken = convertToken(token);

        ///if (token.prev != null) {
        ///    syntaxToken.prev      = convertPrevTokens(token.prev);
        ///    syntaxToken.prev.next = syntaxToken;
        ///}

        return syntaxToken;
    }


    private static SyntaxToken convertNextTokens(org.gjt.sp.jedit.syntax.Token token) {
        if (token == null) { return null; }

        SyntaxToken syntaxToken = convertToken(token);

        if (token.next != null) {
            syntaxToken.next      = convertNextTokens(token.next);
            syntaxToken.next.prev = syntaxToken;
        }

        return syntaxToken;
    }


    public static SyntaxToken convertTokens(code2html.syntax.Token token) {
        if (token == null) { return null; }

        SyntaxToken syntaxToken = convertToken(token);
        syntaxToken.prev = convertPrevTokens(token.prev);
        syntaxToken.next = convertNextTokens(token.next);

        return syntaxToken;
    }


    private static SyntaxToken convertToken(code2html.syntax.Token token) {
        if (token == null) { return null; }

        SyntaxToken syntaxToken = new SyntaxToken();

        syntaxToken.length = token.length;
        syntaxToken.id     = token.id;

        return syntaxToken;
    }


    private static SyntaxToken convertPrevTokens(code2html.syntax.Token token) {
        if (token == null) { return null; }

        SyntaxToken syntaxToken = convertToken(token);

        if (token.prev != null) {
            syntaxToken.prev      = convertPrevTokens(token.prev);
            syntaxToken.prev.next = syntaxToken;
        }

        return syntaxToken;
    }


    private static SyntaxToken convertNextTokens(code2html.syntax.Token token) {
        if (token == null) { return null; }

        SyntaxToken syntaxToken = convertToken(token);

        if (token.next != null) {
            syntaxToken.next      = convertNextTokens(token.next);
            syntaxToken.next.prev = syntaxToken;
        }

        return syntaxToken;
    }
}

