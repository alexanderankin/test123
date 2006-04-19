/*
 * TokenList.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2002 Slava Pestov
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

import code2html.syntax.ParserRuleSet;
import code2html.syntax.Token;

//{{{ TokenList class
/**
 * Encapsulates a token list.
 * @since jEdit 4.0pre1
 */
public class TokenList
{
    //{{{ getFirstToken() method
    /**
     * Returns the first syntax token.
     * @since jEdit 4.0pre1
     */
    public Token getFirstToken()
    {
        return firstToken;
    } //}}}

    //{{{ getLastToken() method
    /**
     * Returns the last syntax token.
     * @since jEdit 4.0pre1
     */
    public Token getLastToken()
    {
        return lastToken;
    } //}}}

    //{{{ addToken() method
    /**
     * Do not call this method. The only reason it is public
     * is so that classes in the 'syntax' package can call it.
     */
    public void addToken(int length, byte id, ParserRuleSet rules)
    {
        if(length == 0 && id != Token.END)
            return;

        if(firstToken == null)
        {
            firstToken = new Token(length,id,rules);
            lastToken = firstToken;
        }
        else if(lastToken == null)
        {
            lastToken = firstToken;
            firstToken.length = length;
            firstToken.id = id;
            firstToken.rules = rules;
        }
        else if(lastToken.id == id && lastToken.rules == rules)
        {
            lastToken.length += length;
        }
        else if(lastToken.next == null)
        {
            lastToken.next = new Token(length,id,rules);
            lastToken.next.prev = lastToken;
            lastToken = lastToken.next;
        }
        else
        {
            lastToken = lastToken.next;
            lastToken.length = length;
            lastToken.id = id;
            lastToken.rules = rules;
        }
    } //}}}

    private Token firstToken;
    Token lastToken;
} //}}}

