/*
 * DisplayOptions.java - Immutable display options for JBrowse
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
 * Interface for accessing Display options for JBrowse
 * @author George Latkiewicz
 * @author Andre Kaplan
 * @version $Id$
**/
public interface DisplayOptions
{
    // Display Style options (HOW)

    // constants - for styleIndex
    int STYLE_FIRST  = 0;
    int STYLE_UML    = 0;
    int STYLE_JAVA   = 1;
    int STYLE_CUSTOM = 2;
    int STYLE_LAST   = 2;


    // show arguments, pertains to constuctors and methods, if true, show the 
    // argument type, e.g. int or String
    boolean getShowArguments();
    
    // show argument name, pertains to constructors and methods, if true, show
    // the declared name of the argument, e.g. the x in "int x".
    boolean getShowArgumentNames();
    
    // show qualified nested class or interface names
    boolean getShowNestedName();
    
    // not clear on this one -- appears to mean to show keywords like 'class' or
    // 'interface' beside the icon
    boolean getShowIconKeywords();
    
    // if true, show the other modifiers, the ones other than public, protected,
    // and private, e.g. synchronized, native, transient, etc.
    boolean getShowMiscMod();
    
    // show the uml icons
    boolean getShowIcons();
    
    // show the line number
    boolean getShowLineNum();
    
    // how to sort
    String getSortBy();
    
    // show generic type arguments
    boolean getShowTypeArgs();
    
    // show errors in ErrorList?
    boolean getShowErrors();


    // one of the style constance from above
    int getStyleIndex();


    // if true, use +, #, and - for public, protected, and private respectively
    boolean getVisSymbols();
    
    // if true, show abstract class names and methods in italics
    boolean getAbstractItalic();
    
    // if true, underline all static items
    boolean getStaticUlined();
    
    // if true is returned, then show the method return type after the rest of
    // the method string (UML style), if false, then show it up front (Java style)
    boolean getTypeIsSuffixed();

    // inverts the options, used for tool tips
    DisplayOptions getInverseOptions();
}

