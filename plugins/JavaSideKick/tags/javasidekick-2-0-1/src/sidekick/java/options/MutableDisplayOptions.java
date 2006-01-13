/*
 * MutableDisplayOptions.java - Display options for JBrowse
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
 * JBrowse display options
 * @author George Latkiewicz
 * @author Andre Kaplan
 * @version $Id$
**/
public class MutableDisplayOptions implements DisplayOptions
{
    // Display Style options (HOW)

    private boolean showIcons;
    private boolean showArguments;
    private boolean showArgumentNames;
    private boolean showNestedName;
    private boolean showIconKeywords;
    private boolean showMiscMod;
    private boolean showLineNum;
    private String  sortBy;
    private boolean showTypeArgs; 
    

    private int styleIndex = STYLE_UML;

    private boolean visSymbols;
    private boolean abstractItalic;
    private boolean staticUlined;
    private boolean typeIsSuffixed;


    public final boolean getShowArguments()     { return showArguments; }
    public final boolean getShowArgumentNames() { return showArgumentNames; }
    public final boolean getShowNestedName()    { return showNestedName; }
    public final boolean getShowIconKeywords()  { return showIconKeywords; }
    public final boolean getShowMiscMod()       { return showMiscMod; }
    public final boolean getShowIcons()         { return showIcons; }
    public final boolean getShowLineNum()       { return showLineNum; }
    public final String  getSortBy()            { return sortBy; }
    public final boolean getShowTypeArgs()      { return showTypeArgs; }

    public final int getStyleIndex()            { return styleIndex; }

    public final boolean getVisSymbols()        { return visSymbols; }
    public final boolean getAbstractItalic()    { return abstractItalic; }
    public final boolean getStaticUlined()      { return staticUlined; }
    public final boolean getTypeIsSuffixed()    { return typeIsSuffixed; }


    public final void setShowArguments(boolean flag)
    {
        showArguments = flag;
    }


    public final void setShowArgumentNames(boolean flag) {
        showArgumentNames = flag;
    }


    public final void setShowNestedName(boolean flag) {
        showNestedName = flag;
    }


    public final void setShowIconKeywords(boolean flag) {
        showIconKeywords = flag;
    }


    public final void setShowMiscMod(boolean flag) {
        showMiscMod = flag;
    }


    public final void setShowIcons(boolean flag) {
        showIcons = flag;
    }
    
    public final void setShowLineNum(boolean flag) {
        showLineNum = flag;
    }
    
    public final void setSortBy(String by) {
        sortBy = by;   
    }
    
    public final void setShowTypeArgs(boolean flag) {
        showTypeArgs = flag;   
    }


    public final void setStyleIndex(int index) {
        styleIndex = index;
    }


    public final void setVisSymbols(boolean flag) {
        visSymbols = flag;
    }


    public final void setAbstractItalic(boolean flag) {
        abstractItalic = flag;
    }


    public final void setStaticUlined(boolean flag) {
        staticUlined = flag;
    }


    public final void setTypeIsSuffixed(boolean flag) {
        typeIsSuffixed = flag;
    }


    public final DisplayOptions getInverseOptions() {
        MutableDisplayOptions inverseOpt = new MutableDisplayOptions();

        inverseOpt.showIcons         = !showIcons;
        inverseOpt.showArguments     = !showArguments;
        inverseOpt.showArgumentNames = !showArgumentNames;
        inverseOpt.showNestedName    = !showNestedName;
        inverseOpt.showIconKeywords  = !showIconKeywords;
        inverseOpt.showMiscMod       = !showMiscMod;
        inverseOpt.showLineNum       = !showLineNum;

        inverseOpt.visSymbols      = !visSymbols;
        inverseOpt.abstractItalic  = !abstractItalic;
        inverseOpt.staticUlined    = !staticUlined;
        inverseOpt.typeIsSuffixed  = !typeIsSuffixed;

        if (styleIndex == STYLE_UML) {
            inverseOpt.styleIndex = STYLE_JAVA;
        } else if (styleIndex == STYLE_JAVA) {
            inverseOpt.styleIndex = STYLE_UML;
        }

        return inverseOpt;
    }


    public String toString() {
        return (
              "How to display:"
            + "\n\tshowIcons         = " + showIcons
            + "\n\tshowArguments     = " + showArguments
            + "\n\tshowArgumentNames = " + showArgumentNames
            + "\n\tshowNestedName    = " + showNestedName
            + "\n\tshowIconKeywords  = " + showIconKeywords
            + "\n\tshowMiscMod       = " + showMiscMod
            + "\n\tshowLineNum       = " + showLineNum
            + "\n\tsortBy            = " + sortBy
            + "\n\tshowTypeArgs      = " + showTypeArgs

            + "\n\tstyleIndex        = " + styleIndex

            + "\n\tvisSymbols        = " + visSymbols
            + "\n\tabstractItalic    = " + abstractItalic
            + "\n\tstaticUlined      = " + staticUlined
            + "\n\ttypeIsSuffixed    = " + typeIsSuffixed
        );
    }
}

