/*:folding=indent:
* LaTeXCompletion.java - Code completion.
* Copyright (C) 2003 Anthony Roy
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
package uk.co.antroy.latextools;

import sidekick.SideKickCompletion;


public class LaTeXCompletion
    extends SideKickCompletion {

    //~ Constructors ..........................................................

    public LaTeXCompletion() {
        items.add("Bertrand");
        items.add("Gem Gem");
    }

    //~ Methods ...............................................................

    /**
     * The length of the text being completed (popup will be positioned there).
     * @return ¤
     */
    public int getTokenLength() {

        return 1;
    }

    /**
     * @param selectedIndex -1 if the popup is empty, otherwise the index of
     *        the selected completion.
     * @param keyChar the character typed by the user.
     * @return ¤
     */
    public boolean handleKeystroke(int selectedIndex, char keyChar) {

        return false;
    }

    public void insert(int index) {
    }
}
