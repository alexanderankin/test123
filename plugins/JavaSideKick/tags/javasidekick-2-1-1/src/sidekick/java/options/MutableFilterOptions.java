/*
 * MutableFilterOptions.java - Filter options for JBrowse
 *
 * Copyright (c) 1999-2001 George Latkiewicz, Andre Kaplan
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


package sidekick.java.options;


/**
 * JBrowse Filter options
 * @author George Latkiewicz
 * @author Andre Kaplan
 * @version $Id$
**/
public class MutableFilterOptions implements FilterOptions
{
    // Filter options (WHAT)
    private boolean showThrows;
    private boolean showFields;
    private boolean showVariables;
    private boolean showPrimitives;
    private boolean showInitializers;
    private boolean showGeneralizations;

    private int topLevelVisIndex = 0;
    private int memberVisIndex   = 0;


    public final boolean getShowFields()      { return showFields; }
    public final boolean getShowPrimitives()      { return showPrimitives; }
    public final boolean getShowVariables()   { return showVariables; }
    public final boolean getShowInitializers() {return showInitializers;}
    public final boolean getShowGeneralizations() { return showGeneralizations; }


    public final int getTopLevelVisIndex()   { return topLevelVisIndex; }
    public final int getMemberVisIndex()     { return memberVisIndex; }


    public final void setShowFields(boolean flag) {
        showFields = flag;
    }

    public final void setShowVariables(boolean flag) {
        showVariables = flag;   
    }

    public final void setShowPrimitives(boolean flag) {
        showPrimitives = flag;
    }
    
    public final void setShowInitializers(boolean flag) {
        showInitializers = flag;   
    }


    public final void setShowGeneralizations(boolean flag) {
        showGeneralizations = flag;
    }


    public final boolean getShowThrows() {
        return showThrows;   
    }
    public final void setShowThrows(boolean flag) {
        showThrows = flag;
    }


    public final void setTopLevelVisIndex(int level) {
        topLevelVisIndex = level;
    }


    public final void setMemberVisIndex(int level) {
        memberVisIndex = level;
    }


    public String toString() {
        return (
              "What to include:"
            + "\n\tshowFields          = " + showFields
            + "\n\tshowVariables       = " + showVariables
            + "\n\tshowPrimitives      = " + showPrimitives
            + "\n\tshowInitializers    = " + showInitializers
            + "\n\tshowGeneralizations = " + showGeneralizations
            + "\n\ttopLevelVisIndex    = " + topLevelVisIndex
            + "\n\tmemberVisIndex      = " + memberVisIndex
        );
    }
}

