/*
 * SyntaxToken.java
 * Copyright (C) 1998, 1999, 2000, 2001 Slava Pestov
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


public class SyntaxToken
{
    /**
     * The length of this token.
     */
    public int length = 0;


    /**
     * The id of this token.
     */
    public byte id = 0;


    /**
     * The previous token in the linked list.
     */
    public SyntaxToken prev = null;


    /**
     * The next token in the linked list.
     */
    public SyntaxToken next = null;
}

