/*
 * StyleSet.java - A set of styles for an edit mode
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
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.util.Log;
//}}}

/**
 * A set of styles for an edit mode. This class provides mapping from each token
 * type and fold level to a {@link SyntaxStyle}.
 */
public class StyleSet
{
	/** Number of fold levels to store styles for, not including the "all higher fold levels"
	    style **/
	public static final int FOLD_LEVELS = 3;
	
	//{{{ StyleSet constructors
	/**
	 * Creates a new empty style set
	 * @param mode The mode that this StyleSet is associated to, or null if it is a global
	 *   style set
	 * @param name The name of the style set - must be unique within its mode and contain
	 *   no whitespace
	 */
	public StyleSet(String mode, String name)
	{
		this(mode, name, null, null);
	}
	
	/**
	 * Creates a new style set with every style set to a given default.
	 * @param mode The mode that this style set is associated to, or null if this is a global
	 *   style set
	 * @param name The name of the style set - must be unique within its mode and contain
	 *   no whitespace
	 * @param style The style to set every token style and fold line style to
	 */
	public StyleSet(String mode, String name, SyntaxStyle style)
	{
		this.mode = mode;
		this.name = name;
		
		this.tokenStyles = new SyntaxStyle[Token.ID_COUNT];
		for (int i = 0; i < Token.ID_COUNT; i++)
		{
			this.tokenStyles[i] = style;
		}
		
		this.foldLineStyles = new SyntaxStyle[FOLD_LEVELS + 1];
		for (int i = 0; i < FOLD_LEVELS + 1; i++)
		{
			this.foldLineStyles[i] = style;
		}
	}
	
	/**
	 * Creates a new style set
	 * @param mode The mode that this style set is associated to, or null if this is a global
	 *   style set
	 * @param name The name of the style set - must be unique within its mode and contain
	 *   no whitespace
	 * @param tokenStyles The styles for each token
	 * @param foldLineStyles The styles for each fold level
	 */
	public StyleSet(String mode, String name, SyntaxStyle[] tokenStyles,
			SyntaxStyle[] foldLineStyles)
	{	
		this.mode = mode;
		this.name = name;
		
		this.tokenStyles = new SyntaxStyle[Token.ID_COUNT];
		if (tokenStyles != null)
		{
			if (tokenStyles.length < Token.ID_COUNT)
			{
				Log.log(Log.MESSAGE, this, "Tried to initialize token styles" + 
					"with array of incorrect size");
			}
			else
			{
				System.arraycopy(tokenStyles, 0, this.tokenStyles, 0,
					Token.ID_COUNT);
			}
		}
		
		this.foldLineStyles = new SyntaxStyle[FOLD_LEVELS + 1];
		if (foldLineStyles != null)
		{
			if (foldLineStyles.length < FOLD_LEVELS + 1)
			{
				Log.log(Log.MESSAGE, this, "Tried to initialize fold line " +
					"styles with array of incorrect size");
			}
			else
			{
				System.arraycopy(foldLineStyles, 0, this.foldLineStyles, 0,
					FOLD_LEVELS + 1);
			}
		}
	} //}}}
	
	//{{{ setTokenStyles() method
	/**
	 * Sets the styles for painting tokens in this style set
	 * @param styles The new list of styles - must have Token.ID_COUNT elements 
	 */
	public void setTokenStyles(SyntaxStyle[] styles)
	{
		if (styles.length < Token.ID_COUNT)
		{
			Log.log(Log.MESSAGE, this, "Tried to set style list of wrong size");
			return;
		}

		this.tokenStyles = new SyntaxStyle[Token.ID_COUNT];
		System.arraycopy(styles, 0, this.tokenStyles, 0, Token.ID_COUNT);
	} //}}}
	
	//{{{ getTokenStyles() method
	/**
	 * Gets the token syntax styles for this style set
	 */
	public SyntaxStyle[] getTokenStyles()
	{
		return tokenStyles;
	} //}}}
	
	//{{{ setTokenStyle() method
	/**
	 * Sets the style for a particular token in this style set
	 * @param tokenType The token type - one of the constants in the <code>Token</code> class
	 * @param newStyle The new style for this token type
	 */
	public void setTokenStyle(byte tokenType, SyntaxStyle newStyle)
	{
		if (tokenType < 0 || tokenType > Token.ID_COUNT)
		{
			Log.log(Log.MESSAGE, this,
				"Tried to set style for non-existent token type");
			return;
		}
		
		tokenStyles[tokenType] = newStyle;
	} //}}}
	
	//{{{ getTokenStyle() method
	/**
	 * Gets the syntax style for a particular token type in this style set. Does no
	 *   bounds checking.
	 * @param tokenType The token type - one of the constants in the <code>Token</code> class
	 */
	public SyntaxStyle getTokenStyle(byte tokenType)
	{
		return tokenStyles[tokenType];
	} //}}}	
	
	//{{{ getFoldLineStyles() method
	/**
	 * Returns the fold line style. The first element is the style for
	 * lines with a fold level greater than FOLD_LEVELS. The remaining elements
	 * are the styles for the corresponding fold level.
	 */
	public SyntaxStyle[] getFoldLineStyles()
	{
		return foldLineStyles;
	} //}}}

	//{{{ setFoldLineStyles() method
	/**
	 * Sets the fold line style. The first element is the style for
	 * lines with a fold level greater than FOLD_LEVELS. The remaining elements
	 * are the styles for the corresponding fold level.
	 * @param foldLineStyles The fold line style
	 */
	public void setFoldLineStyles(SyntaxStyle[] foldLineStyles)
	{
		if (foldLineStyles.length < FOLD_LEVELS + 1)
		{
			Log.log(Log.MESSAGE, this,
				"Tried to set fold line style list of wrong size");
			return;
		}	
		
		this.foldLineStyles = new SyntaxStyle[FOLD_LEVELS + 1];
		System.arraycopy(foldLineStyles, 0, this.foldLineStyles, 0, FOLD_LEVELS + 1);
	} //}}}
	
	//{{{ setFoldLineStyle() method
	/**
	 * Sets a particular fold line style. If level is 0, then this sets the
	 * style for lines with a fold level greater than FOLD_LEVELS, and otherwise this
	 * sets the style for lines with a fold level equal to level.
	 * @param level The fold level to change - must be in the range 0 to FOLD_LEVELS
	 * @param newStyle The new style
	 */
	public void setFoldLineStyle(int level, SyntaxStyle newStyle)
	{
		if (level < 0 || level > FOLD_LEVELS)
		{
			Log.log(Log.MESSAGE, StyleSet.class,
				"Tried to set fold style for invalid fold level");
			return;
		}
		
		foldLineStyles[level] = newStyle;
	} //}}}
	
	//{{{ getFoldLineStyle() method
	/**
	 * Gets the syntax style for a particular fold level in this mode. Performs no
	 *   bounds checking.
	 * @param level The fold level - must be in the range 0 to FOLD_LEVELS 
	 */
	public SyntaxStyle getFoldLineStyle(int level)
	{
		return foldLineStyles[level];
	} //}}}
	
	//{{{ Accessor methods
	public String getName()
	{
		return name;
	}
	
	public String getModeName()
	{
		return mode;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setModeName(String modeName)
	{
		this.mode = modeName;
	} //}}}
	
	//{{{ Private members
	private String name;
	private String mode;
	private SyntaxStyle[] tokenStyles;
	private SyntaxStyle[] foldLineStyles;
	//}}}
}
