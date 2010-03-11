/*
*  ColorTabs.java - Part of the BufferTabs plugin for jEdit.
*  Copyright (C) 2003 Chris Samuels
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU General Public License
*  as published by the Free Software Foundation; either version 2
*  of the License, or any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with this program; if not, write to the Free Software
*  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/


package buffertabs;

//{{{ Imports
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
//}}}


/**
*  An add-on to BufferTabs to allow colored backgrounds on tabs
*
* @author     Chris Samuels
* @created    24 February 2003
*/
public class ColorTabs
{
	private static final int JND = 4;
	
	private static final int DARKEN_LOWEST_COLOR = 0;
	private static final int DARKEN_HIGHEST_COLOR = 150;
	private static final int DARKEN_RANGE = DARKEN_HIGHEST_COLOR - DARKEN_LOWEST_COLOR;
	private static final float DARKEN_RATIO = ((float) DARKEN_RANGE / 254);
	
	private static final int MUTE_LOWEST_COLOR = 150;
	private static final int MUTE_HIGHEST_COLOR = 230;
	private static final int MUTE_RANGE = MUTE_HIGHEST_COLOR - MUTE_LOWEST_COLOR;
	private static final float MUTE_RATIO = ((float) MUTE_RANGE / 254);
	
	private static ColorTabs colorTabs = null;
	
	private boolean enabled             = false;
	private boolean selectedColorized   = true;
	private boolean selectedForegroundColorized = false;
	private boolean foregroundColorized = false;
	private boolean muteColors          = true;
	private boolean colorVariation      = true;
	
	private List<ColorEntry> colors;
	private final Map<String,Color> colorsAssigned = new HashMap<String,Color>();
	private final Object lock = new Object();
	private Random rnd = null;
	
	
	//{{{ ColorTabs constructor
	/**
	 *  Singleton class
	 */
	private ColorTabs() { }
	//}}}
	
	//{{{ isEnabled() method
	public boolean isEnabled() 
	{
		return this.enabled;
	} //}}}

	//{{{ setEnabled() method
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	} //}}}
	
	//{{{ isSelectedColorized() method
	public boolean isSelectedColorized()
	{
		return this.selectedColorized;
	} //}}}

	//{{{ setSelectedColorized() method
	public void setSelectedColorized(boolean selectedColorized) 
	{
		this.selectedColorized = selectedColorized;
	} //}}}

	//{{{ isForegroundColorized() method
	public boolean isForegroundColorized() 
	{
		return this.foregroundColorized;
	} //}}}

	//{{{ isSelectedForegroundColorized() method
	public boolean isSelectedForegroundColorized() 
	{
		return this.selectedForegroundColorized;
	} //}}}
	
	//{{{ setForegroundColorized() method
	public void setForegroundColorized(boolean foregroundColorized) 
	{
		this.foregroundColorized = foregroundColorized;
	} //}}}
	
	//{{{ setSelectedForegroundColorized() method
	public void setSelectedForegroundColorized(boolean foregroundColorized) 
	{
		this.selectedForegroundColorized = foregroundColorized;
	} //}}}
	
	//{{{ hasMuteColors() method
	public boolean hasMuteColors() 
	{
		return this.muteColors;
	} //}}}
	
	//{{{ setMuteColors() method
	public void setMuteColors(boolean muteColors) 
	{
		this.muteColors = muteColors;
	} //}}}
	
	//{{{ hasColorVariation() method
	public boolean hasColorVariation() 
	{
		return this.colorVariation;
	} //}}}

	//{{{ setColorVariation() method
	public void setColorVariation(boolean colorVariation) 
	{
		this.colorVariation = colorVariation;
	} //}}}
	
	//{{{ alterColorDarken() method
	/**
	 *  Creates colors suitable for reading text labels. Uniformly moves the
	 *  color range to a darker range.
	 *
	 * @param  color
	 * @return
	 */
	Color alterColorDarken(Color color)
	{
		if (color == null)
		{
			return Color.black;
		}
		
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		
		r = (int) (DARKEN_HIGHEST_COLOR - (r * DARKEN_RATIO));
		g = (int) (DARKEN_HIGHEST_COLOR - (g * DARKEN_RATIO));
		b = (int) (DARKEN_HIGHEST_COLOR - (b * DARKEN_RATIO));
		
		if (this.hasColorVariation())
		{
			r -= rnd.nextInt(5) * JND;
			g -= rnd.nextInt(5) * JND;
			b -= rnd.nextInt(5) * JND;
			r = r / JND * JND;
			g = g / JND * JND;
			b = b / JND * JND;
		}
		
		r = Math.max(DARKEN_LOWEST_COLOR, Math.min(r, DARKEN_HIGHEST_COLOR));
		g = Math.max(DARKEN_LOWEST_COLOR, Math.min(g, DARKEN_HIGHEST_COLOR));
		b = Math.max(DARKEN_LOWEST_COLOR, Math.min(b, DARKEN_HIGHEST_COLOR));
		
		return new Color(r, g, b);
	} //}}}
	
	//{{{ alterColorHighlight() method
	/**
	 *  Creates colors suitable for highlighting an active tab. Boosts the
	 *  brightness and lowers saturation to achieve this.
	 *
	 * @param  color
	 * @return
	 */
	Color alterColorHighlight(Color color)
	{
		if (color == null)
		{
			return Color.lightGray;
		}
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		
		float[] hsb = Color.RGBtoHSB(r, g, b, null);
		
		float s = hsb[1];
		float v = hsb[2];
		
		s *= 0.6;
		s = Math.max(0.0f, Math.min(s, 1f));
		
		v *= 1.6;
		v = Math.max(0.0f, Math.min(v, 0.8f));
		
		return Color.getHSBColor(hsb[0], s, v);
	} //}}}
	
	//{{{ alterColorMute() method
	/**
	 *  Creates colors suitable for backgrounds. Uniformly moves the color
	 *  range to a lighter paler range.
	 *
	 *@param  color
	 *@return
	 */
	Color alterColorMute(Color color)
	{
		if (color == null)
		{
			return Color.gray;
		}
		
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		
		r = (int) (MUTE_LOWEST_COLOR + (r * MUTE_RATIO));
		g = (int) (MUTE_LOWEST_COLOR + (g * MUTE_RATIO));
		b = (int) (MUTE_LOWEST_COLOR + (b * MUTE_RATIO));
		
		if (this.hasColorVariation())
		{
			r += rnd.nextInt(5) * JND;
			g += rnd.nextInt(5) * JND;
			b += rnd.nextInt(5) * JND;
			r = r / JND * JND;
			g = g / JND * JND;
			b = b / JND * JND;
		}
		
		r = Math.max(MUTE_LOWEST_COLOR, Math.min(r, MUTE_HIGHEST_COLOR));
		g = Math.max(MUTE_LOWEST_COLOR, Math.min(g, MUTE_HIGHEST_COLOR));
		b = Math.max(MUTE_LOWEST_COLOR, Math.min(b, MUTE_HIGHEST_COLOR));
		
		return new Color(r, g, b);
	} //}}}
	
	//{{{ getDefaultColorFor() method
	/**
	 *  Gets the defaultColorFor attribute of the ColorTabs class
	 *
	 * @param  name
	 * @return       The defaultColorFor value
	 */
	public Color getDefaultColorFor(String name)
	{
		synchronized (lock)
		{
			if (colors == null)
			{
				loadColors();
			}
			
			if (colorsAssigned.containsKey(name))
			{
				return colorsAssigned.get(name);
			}
			
			for (int i = 0; i < colors.size(); i++)
			{
				ColorEntry entry = colors.get(i);
				if (entry.re.matcher(name).matches())
				{
					Color newColor = null;
					if (this.hasMuteColors())
					{
						if (this.isForegroundColorized())
						{
							newColor = alterColorDarken(entry.color);
						}
						else
						{
							newColor = alterColorMute(entry.color);
						}
					}
					else
					{
						newColor = entry.color;
					}
					
					colorsAssigned.put(name, newColor);
					return newColor;
				}
			}
			
			return null;
		}
	} //}}}
	
	//{{{ instance() method
	/**
	 * Returns access to the Singleton ColorTabs class
	 *
	 * @return    Singleton ColorTabs
	 */
	public static ColorTabs instance()
	{
		if (colorTabs == null)
		{
			colorTabs = new ColorTabs();
		}
		return colorTabs;
	} //}}}
	
	//{{{ loadColors() method
	/**
	 *  Load the colors from 'File system browser' color options
	 */
	private void loadColors()
	{
		synchronized (lock)
		{
			colors = new ArrayList<ColorEntry>();
			
			if (!jEdit.getBooleanProperty("vfs.browser.colorize"))
			{
				return;
			}
			
			String glob;
			int i = 0;
			while ((glob = jEdit.getProperty("vfs.browser.colors." + i + ".glob")) != null)
			{
				try
				{
					colors.add(new ColorEntry(
								  Pattern.compile(StandardUtilities.globToRE(glob)),
								  jEdit.getColorProperty("vfs.browser.colors." + i + ".color",
											 Color.black)));
					i++;
				}
				catch (PatternSyntaxException e)
				{
					Log.log(Log.ERROR, ColorTabs.class, "Invalid regular expression: " + glob);
					//Log.log( Log.ERROR, ColorTabs.class, e );
					//Log.flushStream();
				}
			}
		}
	} //}}}
	
	//{{{ propertiesChanged() method
	public void propertiesChanged()
	{
		loadColors();
		colorsAssigned.clear();
		//Set seed so color variation are 'mostly' consistent during a session
		rnd = new java.util.Random(20020212);
	} //}}}
	
	//{{{ ColorEntry class
	/**
	 * Class to store color match data
	 *
	 * @author     Chris Samuels
	 * @created    24 February 2003
	 */
	static class ColorEntry
	{
		Color color;
		Pattern re;
		
		
		ColorEntry(Pattern re, Color color)
		{
			this.re = re;
			this.color = color;
		}
	} //}}}
}

