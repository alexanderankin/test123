/*
* OptionValues.java - Immutable display options for JBrowse
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

import org.gjt.sp.jedit.jEdit;

/**
 * Class for accessing Display options for JavaSideKick
 * @author George Latkiewicz
 * @author Andre Kaplan
 * @version $Id$
**/
public class OptionValues {
    // Display Style options (HOW)

    public static final int STYLE_UML = 0;
    public static final int STYLE_JAVA = 1;
    public static final int STYLE_CUSTOM = 2;

    public static final int SORT_BY_LINE = 0;
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_VISIBILITY = 2;
    
    public static final int PRIVATE = 0;
    public static final int PACKAGE = 1;
    public static final int PROTECTED = 2;
    public static final int PUBLIC = 3;

    // show arguments, pertains to constuctors and methods, if true, show the
    // argument type, e.g. int or String
    public boolean getShowArguments() {
        return jEdit.getBooleanProperty( "sidekick.java.showArgs", true ) ;
    }

    // show argument name, pertains to constructors and methods, if true, show
    // the declared name of the argument, e.g. the x in "int x".
    public boolean getShowArgumentNames() {
        return jEdit.getBooleanProperty( "sidekick.java.showArgNames", false ) ;
    }

    // show qualified nested class or interface names
    public boolean getShowNestedName() {
        return jEdit.getBooleanProperty( "sidekick.java.showNestedName", false );
    }

    // not clear on this one -- appears to mean to show keywords like 'class' or
    // 'interface' beside the icon
    public boolean getShowIconKeywords() {
        return jEdit.getBooleanProperty( "sidekick.java.showIconKeywords", false );
    }

    // if true, show the other modifiers, the ones other than public, protected,
    // and private, e.g. synchronized, native, transient, etc.
    public boolean getShowMiscMod() {
        return jEdit.getBooleanProperty( "sidekick.java.showMiscMod", false );
    }

    // show the uml icons
    public boolean getShowIcons() {
        return jEdit.getBooleanProperty( "sidekick.java.showIcons", true );
    }

    public boolean getShowIconsLikeEclipse() {
        return jEdit.getBooleanProperty( "sidekick.java.showIconsLikeEclipse", false );
    }

    // show the line number
    public boolean getShowLineNum() {
        return jEdit.getBooleanProperty( "sidekick.java.showLineNums", false );
    }
    
    // auto-expand tree to show members of inner classes
    public boolean getExpandClasses() {
        return jEdit.getBooleanProperty( "sidekick.java.expandClasses", true );   
    }

    // how to sort
    public int getSortBy() {
        return jEdit.getIntegerProperty( "sidekick.java.sortBy", SORT_BY_NAME );
    }

    // show generic type arguments
    public boolean getShowTypeArgs() {
        return jEdit.getBooleanProperty( "sidekick.java.showTypeArgs", false );
    }

    // one of the style constance from above
    int getStyleIndex() {
        return jEdit.getIntegerProperty( "sidekick.java.displayStyle", STYLE_UML );
    }


    // if true, use +, #, and - for public, protected, and private respectively
    public boolean getVisSymbols() {
        return jEdit.getBooleanProperty( "sidekick.java.custVisAsSymbol", true );
    }

    // if true, use "public", "protected", and "private"
    public boolean getVisWords() {
        return jEdit.getBooleanProperty( "sidekick.java.custVisAsWord", false );
    }

    // if true, don't show any visibility markup
    public boolean getVisNone() {
        return jEdit.getBooleanProperty( "sidekick.java.custVisAsNone", false );
    }

    // if true, show abstract class names and methods in italics
    public boolean getAbstractItalic() {
        return jEdit.getBooleanProperty( "sidekick.java.custAbsAsItalic", true );
    }

    // if true, underline all static items
    public boolean getStaticUlined() {
        return jEdit.getBooleanProperty( "sidekick.java.custStaAsUlined", true );
    }

    // if true is returned, then show the method return type after the rest of
    // the method string (UML style), if false, then show it up front (Java style)
    public boolean getTypeIsSuffixed() {
        return jEdit.getBooleanProperty( "sidekick.java.custTypeIsSuffixed", true );
    }

    public boolean getShowImports() {
        return jEdit.getBooleanProperty( "sidekick.java.showImports", true );
    }

    public boolean getShowFields() {
        return jEdit.getBooleanProperty( "sidekick.java.showAttr", true );
    }

    public boolean getShowVariables() {
        return jEdit.getBooleanProperty( "sidekick.java.showVariables", false );
    }

    public boolean getShowPrimitives() {
        return jEdit.getBooleanProperty( "sidekick.java.showPrimAttr", true );
    }

    public boolean getShowInitializers() {
        return jEdit.getBooleanProperty( "sidekick.java.showInitializers", false );
    }

    public boolean getShowGeneralizations() {
        return jEdit.getBooleanProperty( "sidekick.java.showGeneralizations", false );
    }

    public boolean getShowThrows() {
        return jEdit.getBooleanProperty( "sidekick.java.showThrows", false );
    }

    public int getTopLevelVisIndex() {
        return jEdit.getIntegerProperty( "sidekick.java.topLevelVisIndex", 1 );
    }

    public int getMemberVisIndex() {
        return jEdit.getIntegerProperty( "sidekick.java.memberVisIndex", 0 );
    }

    public boolean getShowErrors() {
        return jEdit.getBooleanProperty( "sidekick.java.showErrors", true );
    }

	public boolean getIgnoreDirtyBuffers() {
		return jEdit.getBooleanProperty( "sidekick.java.ignoreDirtyBuffers", true );
	}
}
