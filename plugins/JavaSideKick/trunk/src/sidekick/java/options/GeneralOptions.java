/*
 * GeneralOptions.java - Options for JBrowse
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
 * JBrowse General Options
 * @author George Latkiewicz
 * @author Andre Kaplan
 * @version $Id$
**/
public class GeneralOptions
{
    private boolean showStatusBar;
    private boolean automaticParse;
    private boolean sort;

    private MutableFilterOptions  filterOpt;  // (WHAT to display)
    private MutableDisplayOptions displayOpt; // (HOW  to display)


    public GeneralOptions() {
        this.filterOpt  = new MutableFilterOptions();
        this.displayOpt = new MutableDisplayOptions();
    }

    public boolean equals(Object o) {
        GeneralOptions go = (GeneralOptions)o;
        return showStatusBar == go.getShowStatusBar() &&
               automaticParse == go.getAutomaticParse() &&
               sort == getSort() &&
               filterOpt.equals(go.getFilterOptions()) &&
               displayOpt.equals(go.getDisplayOptions());
    }

    /**
     * The method that sets the option object's state to reflect the values
     * specified by the passed PropertyAccessor.
     */
    public void load(PropertyAccessor props) {
        // General Options
        setShowStatusBar(
            props.getBooleanProperty("sidekick.java.showStatusBar", true));
        setAutomaticParse(
            props.getBooleanProperty("sidekick.java.automaticParse", false));
        setSort(
            props.getBooleanProperty("sidekick.java.sort", false));

        // Filter Options
        filterOpt.setShowFields(
            props.getBooleanProperty("sidekick.java.showAttr"));
        filterOpt.setShowPrimitives(
            props.getBooleanProperty("sidekick.java.showPrimAttr"));
        filterOpt.setShowVariables(
            props.getBooleanProperty("sidekick.java.showVariables"));
        filterOpt.setShowInitializers(
            props.getBooleanProperty("sidekick.java.showInitializers"));
        filterOpt.setShowGeneralizations(
            props.getBooleanProperty("sidekick.java.showGeneralizations"));
        filterOpt.setShowThrows(
            props.getBooleanProperty("sidekick.java.showThrows"));

        int topLevelVisIndex;
        try {
            topLevelVisIndex = Integer.parseInt(
                    props.getProperty("sidekick.java.topLevelVisIndex"));
        } catch(NumberFormatException nf) {
            topLevelVisIndex = MutableModifier.TOPLEVEL_VIS_PACKAGE;
        }
        if (    (topLevelVisIndex < MutableModifier.TOPLEVEL_VIS_PACKAGE)
             || (topLevelVisIndex > MutableModifier.TOPLEVEL_VIS_PUBLIC)
        ) {
            topLevelVisIndex = MutableModifier.TOPLEVEL_VIS_PACKAGE;
        }
        filterOpt.setTopLevelVisIndex( topLevelVisIndex );

        int memberVisIndex;
        try {
            memberVisIndex = Integer.parseInt(
                    props.getProperty("sidekick.java.memberVisIndex"));
        } catch(NumberFormatException nf) {
            memberVisIndex = MutableModifier.MEMBER_VIS_PRIVATE;
        }
        if (    (memberVisIndex < MutableModifier.MEMBER_VIS_PRIVATE)
             || (memberVisIndex > MutableModifier.MEMBER_VIS_PUBLIC)
        ) {
            memberVisIndex = MutableModifier.MEMBER_VIS_PRIVATE;
        }
        filterOpt.setMemberVisIndex( memberVisIndex );

        // Display Options
        displayOpt.setShowArguments(
                props.getBooleanProperty("sidekick.java.showArgs"));
        displayOpt.setShowArgumentNames(
                props.getBooleanProperty("sidekick.java.showArgNames"));
        displayOpt.setShowTypeArgs(
                props.getBooleanProperty("sidekick.java.showTypeArgs"));
        displayOpt.setShowErrors(
                props.getBooleanProperty("sidekick.java.showErrors"));
        displayOpt.setShowNestedName(
                props.getBooleanProperty("sidekick.java.showNestedName"));
        displayOpt.setShowIconKeywords(
                props.getBooleanProperty("sidekick.java.showIconKeywords"));
        displayOpt.setShowMiscMod(
                props.getBooleanProperty("sidekick.java.showMiscMod"));
        displayOpt.setShowIcons(
                props.getBooleanProperty("sidekick.java.showIcons"));
        displayOpt.setShowLineNum(
                props.getBooleanProperty("sidekick.java.showLineNums"));
        displayOpt.setSortBy(
                props.getProperty("sidekick.java.sortBy"));

        int styleIndex;
        try
        {
            styleIndex = Integer.parseInt(
                    props.getProperty("sidekick.java.displayStyle"));
        } catch (NumberFormatException nf) {
            styleIndex = DisplayOptions.STYLE_UML;
        }
        if (    (styleIndex < DisplayOptions.STYLE_FIRST)
             || (styleIndex > DisplayOptions.STYLE_LAST)
        ) {
            styleIndex = DisplayOptions.STYLE_UML;
        }
        displayOpt.setStyleIndex(styleIndex);

        displayOpt.setVisSymbols(
                props.getBooleanProperty("sidekick.java.custVisAsSymbol"));
        displayOpt.setAbstractItalic(
                props.getBooleanProperty("sidekick.java.custAbsAsItalic"));
        displayOpt.setStaticUlined(
                props.getBooleanProperty("sidekick.java.custStaAsUlined"));
        displayOpt.setTypeIsSuffixed(
                props.getBooleanProperty("sidekick.java.custTypeIsSuffixed"));
    }


    /**
     * The method that sets the passed PropertyAccessor's state to reflect
     * the current state of this Options object.
     */
    public void save(PropertyAccessor props)
    {
        // General Options
        //----------------
        props.setBooleanProperty("sidekick.java.showStatusBar", getShowStatusBar());
        props.setBooleanProperty("sidekick.java.automaticParse", getAutomaticParse());
        props.setBooleanProperty("sidekick.java.sort", getSort());


        // Filter Options
        //---------------
        props.setBooleanProperty("sidekick.java.showAttr",
                filterOpt.getShowFields());
        props.setBooleanProperty("sidekick.java.showPrimAttr",
                filterOpt.getShowPrimitives());
        props.setBooleanProperty("sidekick.java.showVariables",
                filterOpt.getShowVariables());
        props.setBooleanProperty("sidekick.java.showInitializers",
                filterOpt.getShowInitializers());
        props.setBooleanProperty("sidekick.java.showGeneralizations",
                filterOpt.getShowGeneralizations());
        props.setBooleanProperty("sidekick.java.showThrows",
                filterOpt.getShowThrows());

        /* Visibility Level */
        props.setProperty("sidekick.java.topLevelVisIndex",
                String.valueOf(filterOpt.getTopLevelVisIndex()) );
        props.setProperty("sidekick.java.memberVisIndex",
                String.valueOf(filterOpt.getMemberVisIndex()) );


        // Display Options
        //----------------
        props.setBooleanProperty("sidekick.java.showArgs",
                displayOpt.getShowArguments());
        props.setBooleanProperty("sidekick.java.showArgNames",
                displayOpt.getShowArgumentNames());
        props.setBooleanProperty("sidekick.java.showTypeArgs",
                displayOpt.getShowTypeArgs());
        props.setBooleanProperty("sidekick.java.showErrors",
                displayOpt.getShowErrors());
        props.setBooleanProperty("sidekick.java.showNestedName",
                displayOpt.getShowNestedName());
        props.setBooleanProperty("sidekick.java.showIconKeywords",
                displayOpt.getShowIconKeywords());
        props.setBooleanProperty("sidekick.java.showMiscMod",
                displayOpt.getShowMiscMod());
        props.setBooleanProperty("sidekick.java.showIcons",
                displayOpt.getShowIcons());
        props.setBooleanProperty("sidekick.java.showLineNums",
                displayOpt.getShowLineNum());
        props.setProperty("sidekick.java.sortBy",
                displayOpt.getSortBy());

        /* Display Style */
        props.setProperty("sidekick.java.displayStyle",
                String.valueOf(displayOpt.getStyleIndex()) );

        /* Custom Style Options */
        props.setBooleanProperty("sidekick.java.custVisAsSymbol",
                displayOpt.getVisSymbols());
        props.setBooleanProperty("sidekick.java.custAbsAsItalic",
                displayOpt.getAbstractItalic());
        props.setBooleanProperty("sidekick.java.custStaAsUlined",
                displayOpt.getStaticUlined());
        props.setBooleanProperty("sidekick.java.custTypeIsSuffixed",
                displayOpt.getTypeIsSuffixed());
    }


    public final boolean getShowStatusBar() { return showStatusBar; }


    public final void setShowStatusBar(boolean flag) {
        showStatusBar = flag;
    }


    public final boolean getAutomaticParse() { return automaticParse; }


    public final void setAutomaticParse(boolean flag) {
        automaticParse = flag;
    }


    public final boolean getSort() { return sort; }


    public final void setSort(boolean flag) {
        sort = flag;
    }
    

    public final MutableFilterOptions  getFilterOptions()  { return filterOpt; }
    public final MutableDisplayOptions getDisplayOptions() { return displayOpt; }


    public final String toString() {
        return (filterOpt.toString() + "\n" + displayOpt.toString());
    }
}

