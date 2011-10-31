/*
 * StyleSettings.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Evan Wright
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
 
package modelighting;
 
//{{{ Imports
import java.awt.Color;
import java.awt.Font;
import java.util.Locale;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;

import org.gjt.sp.util.Log;
import org.gjt.sp.util.SyntaxUtilities;
//}}}
 
public class StyleSettings
{	
	//{{{ loadStyleSet() methods
	/**
	 * Loads a style set from the properties
	 * @param mode The mode which owns the style set, or null for a global style set.
	 * @param name The name of the style set
	 * @since jEdit 4.5pre1
	*/
	public static StyleSet loadStyleSet(String mode, String name)
	{
		SyntaxStyle defaultStyle = loadDefaultStyle();
		return loadStyleSet(mode, name, defaultStyle);
	}
	
	/**
	 * Loads a style set from the properties, with the given defaults
	 * @param mode The mode which owns the style set, or null for a global style set.
	 * @param name The name of the style set
	 * @param defaultStyle The default style
	 * @since jEdit 4.5pre1
	 */
	public static StyleSet loadStyleSet(String mode, String name, SyntaxStyle defaultStyle)
	{
		Font defaultFont = defaultStyle.getFont();
		String defaultFamily = defaultFont.getFamily();
		int defaultSize = defaultFont.getSize();
		Color defaultColor = defaultStyle.getForegroundColor();
		
		SyntaxStyle[] tokenStyles = new SyntaxStyle[Token.ID_COUNT];
		for (int i = 1; i < Token.ID_COUNT; i++)
		{
			String propertyName = getTokenPropertyName(mode, name, (byte)i);
			String styleString = jEdit.getProperty(propertyName);

			try
			{
				tokenStyles[i] = SyntaxUtilities.
					parseStyle(styleString, defaultFamily, defaultSize, true);
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR, StyleSettings.class,
					"Error loading style set: " + e);
				tokenStyles[i] = defaultStyle;
			}
		}
		tokenStyles[Token.NULL] = defaultStyle;
		
		SyntaxStyle[] foldLineStyles = new SyntaxStyle[StyleSet.FOLD_LEVELS + 1];
		for (int level = 0; level <= StyleSet.FOLD_LEVELS; level++)
		{
			String propertyName = getFoldLinePropertyName(mode, name, level);
			String styleString = jEdit.getProperty(propertyName);
			
			try
			{
				foldLineStyles[level] = SyntaxUtilities.
					parseStyle(styleString, defaultFamily, defaultSize, true);
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR, StyleSettings.class,
					"Error loading style set: " + e);
				foldLineStyles[level] = defaultStyle;
			}
		}
		
		return new StyleSet(mode, name, tokenStyles, foldLineStyles);
	} //}}}
	
	//{{{ getActiveStyleSetName() method
	/**
	 * Gets the name of the active style for the given mode.
	 * @since jEdit 4.5pre1
	 */
	public static String getActiveStyleSetName(String mode)
	{
		return jEdit.getProperty(getActiveStyleSetPropertyName(mode));
	} //}}}

	//{{{ setActiveStyleSet() method
	public static void setActiveStyleSet(String mode, String styleSet)
	{
		String propertyName = getActiveStyleSetPropertyName(mode);
		
		if (styleSet == null)
		{
			jEdit.unsetProperty(propertyName);
		}
		else
		{
			jEdit.setProperty(propertyName, styleSet);
		}
	} //}}}

	//{{{ createStyleSet() method
	public static void createStyleSet(String mode, String name, StyleSet styleSet)
	{	
		// Add the new style set to the list of style sets for this mode
		String propertyName = getStyleSetListPropertyName(mode);
		String oldProperty = jEdit.getProperty(propertyName);
		if (oldProperty == null || oldProperty.equals(""))
		{
			jEdit.setProperty(propertyName, name);
		}
		else
		{
			jEdit.setProperty(propertyName, oldProperty + "," + name);
		}
		
		// Save the new style set to the properties
		String styleProperty, value;
		for (int i = 0; i <= StyleSet.FOLD_LEVELS; i++)
		{
			styleProperty  = getFoldLinePropertyName(
				styleSet.getModeName(),
				styleSet.getName(),
				i);
			value = GUIUtilities.getStyleString(styleSet.getFoldLineStyle(i));
			jEdit.setProperty(styleProperty, value);
		}
		for (int i = 0; i < Token.ID_COUNT; i++)
		{
			styleProperty = getTokenPropertyName(
				styleSet.getModeName(),
				styleSet.getName(),
				(byte)i);
			value = GUIUtilities.getStyleString(styleSet.getTokenStyle((byte)i));
			jEdit.setProperty(styleProperty, value);
		}
	} //}}}
	
	//{{{ deleteStyleSet() method
	/**
	 * Deletes a style set from the properties
	 */
	public static void deleteStyleSet(String mode, String name)
	{	
		// Get the old list of style set names
		String propertyName = getStyleSetListPropertyName(mode);
		String oldProperty = jEdit.getProperty(propertyName);
		String[] styleSetList = oldProperty.split(",");
		if (styleSetList.length == 0 || name == null)
		{
			Log.log(Log.ERROR, StyleSettings.class, "Trying to delete non-existent style set");
			return;
		}
		
		if (styleSetList.length == 1)
		{
			// If we're deleting the last style set, just get rid of the property
			if (!styleSetList[0].equals(name))
			{
				Log.log(Log.ERROR, StyleSettings.class, "Trying to delete non-existent style set");
				return;
			}
			
			jEdit.unsetProperty(propertyName);
		}
		else
		{
			// Otherwise, rebuild the comma-delimited list of style set names, excluding the
			// one we're deleting
			String newProperty;
			if (styleSetList[0].equals(name))
			{
				newProperty = "";
			}
			else
			{
				newProperty = styleSetList[0];
			}
			
			for (int i = 1; i < styleSetList.length; i++)
			{
				if (!styleSetList[i].equals(name))
				{
					if (newProperty.equals(""))
					{
						newProperty = styleSetList[i];
					}
					else
					{
						newProperty = newProperty + "," + styleSetList[i];
					}
				}
			}
			
			jEdit.setProperty(propertyName, newProperty);
		}
		
		// If we delete the active style set for this mode, set the active style
		// set to global default
		String active = getActiveStyleSetName(mode);
		if (name.equals(active))
		{
			String activePropertyName = getActiveStyleSetPropertyName(mode);
			jEdit.unsetProperty(activePropertyName);
		}
		
		// Also clear all of the other properties for this style set
		for (int i = 1; i < Token.ID_COUNT; i++)
		{
			String tokenPropertyName = getTokenPropertyName(mode, name, (byte)i);
			jEdit.unsetProperty(tokenPropertyName);
		}
		for (int i = 0; i <= StyleSet.FOLD_LEVELS; i++)
		{
			String foldPropertyName = getFoldLinePropertyName(mode, name, i);
			jEdit.unsetProperty(foldPropertyName);
		}
	} //}}}
	
	//{{{ setTokenStyle() method
	public static void setTokenStyle(String mode, String name, byte tokenType, SyntaxStyle style)
	{
		String propertyName = getTokenPropertyName(mode, name, tokenType);
		jEdit.setProperty(propertyName, GUIUtilities.getStyleString(style));
	} //}}}
	
	//{{{ setFoldLineStyle() method
	public static void setFoldLineStyle(String mode, String name, int level, SyntaxStyle style)
	{
		String propertyName = getFoldLinePropertyName(mode, name, level);
		jEdit.setProperty(propertyName, GUIUtilities.getStyleString(style));
	} //}}}
	
	//{{{ loadStyleSetNames() method
	/**
	 * Loads the names of the style sets available for the given mode, or the names of
	 * the global style sets if the <code>mode</code> parameter is null
	 * @param mode The mode whose style set names to load
	 * @since jEdit 4.5pre1
	 */
	public static String[] loadStyleSetNames(String mode)
	{
		String styleSetList = jEdit.getProperty(
			getStyleSetListPropertyName(mode));
		
		if (styleSetList == null || styleSetList.trim().equals(""))
		{
			return new String[] {};
		}
		else
		{
			// The style-set-list property is a comma-delimited list of style set names
			return styleSetList.split(",");
		}
	} //}}}
	
	//{{{ loadDefaultStyle() method
	/**
	 * Loads the default style from the properties.
	 * @since jEdit 4.5pre1
	 */
	public static SyntaxStyle loadDefaultStyle()
	{
		Color defaultColor = jEdit.getColorProperty("view.fgColor", Color.black);
		Font defaultFont = jEdit.getFontProperty("view.font");
		
		return new SyntaxStyle(defaultColor, null, defaultFont);
	} //}}}
	
	//{{{ Private members
	
	private static final String PREFIX = "mode-highlighting.";
	
	//{{{ getTokenPropertyName() method
	/**
	 * Gets the property name for the a given token type in a given style set.
	 * @param mode The mode which owns the style set, or null for a global style set
	 * @param name The name of the style set
	 * @param tokenType The type of token - one of the constants defined in the {@link Token}
	 * class
	 * @since jEdit 4.5pre1
	 */
	private static String getTokenPropertyName(String mode, String name, byte tokenType)
	{
		if (name.indexOf("global/") == 0)
		{
			mode = null;
		}
		
		String tokenName = Token.tokenToString(tokenType).toLowerCase(Locale.ENGLISH);
		String modeName = (mode == null) ? "all" : mode;
		
		return PREFIX + modeName + ".style-sets." + name + "." + tokenName;
	} //}}}
	
	//{{{ getFoldLinePropertyName() method
	/**
	 * Gets the property name associated to a given fold level in a given style set
	 * @param mode The mode which owns the style set, or null for a global style set
	 * @param name The name of the style set
	 * @param level The fold level - must be in the range 0 to StyleSet.FOLD_LEVELS
	 * @since jEdit 4.5pre1
	 */
	private static String getFoldLinePropertyName(String mode, String name, int level)
	{
		if(name.indexOf("global/") == 0)
		{
			mode = null;
		}
		
		String modeName = (mode == null) ? "all" : mode;
		return PREFIX + modeName + ".style-sets." + name + ".foldLine." + level;
	} //}}}
	
	//{{{ getActiveStyleSetPropertyName() method
	/**
	 * Gets the property name for the active style of a given mode
	 * @since jEdit 4.5pre1
	 */
	private static String getActiveStyleSetPropertyName(String mode)
	{
		String modeName = (mode == null) ? "all" : mode;
		return PREFIX + modeName + ".active-style";
	} //}}}
	
	//{{{ getStyleSetListPropertyName()
	/**
	 * Get the property name associated to the list of style sets for the given mode, or for the
	 * global style sets if the <code>mode</code> parameter is null
	 * @param mode The mode whose style list property name to look up
	 * @since jEdit 4.5pre1
	 */
	private static String getStyleSetListPropertyName(String mode)
	{
		String modeName = (mode == null) ? "all" : mode;
		return PREFIX + modeName + ".style-set-list";
	} //}}}
	
	//}}}
}
