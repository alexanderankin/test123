/*
 * Token.java - Syntax token
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1998, 1999, 2000, 2001 Slava Pestov
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
package code2html.syntax;

/**
 *  A linked list of syntax tokens.
 *
 * @author     Slava Pestov
 * @version    $Id$
 * @todo       Use the actual jEdit code
 */
public class Token {

    /**
     *  The id of this token.
     */
    public byte id;

    //{{{ Instance variables
    /**
     *  The length of this token.
     */
    public int length;

    /**
     *  The next token in the linked list.
     */
    public Token next;

    /**
     *  The previous token in the linked list.
     *
     * @since    jEdit 2.6pre1
     */
    public Token prev;

    /**
     *  The rule set of this token.
     */
    public ParserRuleSet rules;
    /**
     */
    public final static byte COMMENT1 = 1;
    /**
     */
    public final static byte COMMENT2 = 2;
    /**
     */
    public final static byte DIGIT = 12;

    /**
     */
    public final static byte END = 127;
    /**
     */
    public final static byte FUNCTION = 9;  //}}}

        /**
     */
    public final static byte ID_COUNT = 14;
    /**
     */
    public final static byte INVALID = 13;
    /**
     */
    public final static byte KEYWORD1 = 6;
    /**
     */
    public final static byte KEYWORD2 = 7;
    /**
     */
    public final static byte KEYWORD3 = 8;
    /**
     */
    public final static byte LABEL = 5;
    /**
     */
    public final static byte LITERAL1 = 3;
    /**
     */
    public final static byte LITERAL2 = 4;
    /**
     */
    public final static byte MARKUP = 10;
    //{{{ Token types
    /**
     */
    public final static byte NULL = 0;
    /**
     */
    public final static byte OPERATOR = 11;
    //}}}

    //{{{ Token constructor
    /**
     *  Creates a new token.
     *
     * @param  length  The length of the token
     * @param  id      The id of the token
     * @param  rules   The parser rule set that generated this token
     */
    public Token(int length, byte id, ParserRuleSet rules) {
        this.length = length;
        this.id = id;
        this.rules = rules;
    }  //}}}

    //{{{ toString() method
    /**
     *  Returns a string representation of this token.
     *
     * @return
     */
    public String toString() {
        return "[id=" + id + ",length=" + length + "]";
    }  //}}}
}

