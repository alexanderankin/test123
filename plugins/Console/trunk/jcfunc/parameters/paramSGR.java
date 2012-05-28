/*
 * paramSGR.java - returned values for CF.SGR
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012, Artem Bryantsev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
 
package jcfunc.parameters;

import java.awt.Color;

/**
   Enum <code>paramSGR</code> contains returned values for control function CF.SGR
   and some methods, which are designed to thus values processing.
   Additionally it contains Color-parsing method. 
 */
public enum paramSGR
{
	//{{{ enum's values
	/** default rendition (implementation-defined), cancels the effect of any preceding occurrence of SGR in                                
	    the data stream regardless of the setting of the GRAPHIC RENDITION COMBINATION MODE (GRCM) */
	Reset(0),
	
	/** bold or increased intensity */
	Bright(1),
	
	/** faint, decreased intensity or second colour */
	Faint(2),
	
	/** italicized */
	Italic(3),
	
	/** singly underlined */
	Underline_Single(4),
	
	/** slowly blinking (less then 150 per minute) */
	Blink_Slow(5),
	
	/** rapidly blinking (150 per minute or more) */
	Blink_Rapid(6),
	
	/** negative image */
	Image_NGT(7),
	
	/** concealed characters */
	Conceal(8),
	
	/** crossed-out (characters still legible but marked as to be deleted) */
	CrossedOut(9),
	
	/** primary (default) font */
	Font_Pimary(10),
	
	/** first alternative font */
	Font_Alt1(11),
	
	/** second alternative font */
	Font_Alt2(12),
	
	/** third alternative font */
	Font_Alt3(13),
	
	/** fourth alternative font */
	Font_Alt4(14),
	
	/** fifth alternative font */
	Font_Alt5(15),
	
	/** sixth alternative font */
	Font_Alt6(16),
	
	/** seventh alternative font */
	Font_Alt7(17),
	
	/** eighth alternative font */
	Font_Alt8(18),
	
	/** ninth alternative font */
	Font_Alt9(19),
	
	/** Fraktur (Gothic) */
	Fraktur(20),
	
	/** doubly underlined */
	Underline_Doubly(21),
	
	/** normal colour or normal intensity (neither bold nor faint) */
	Normal_Int(22),
	
	/** not italicized, not fraktur */
	Normal_Style(23),
	
	/** not underlined (neither singly nor doubly) */
	Underline_NGT(24),
	
	/** steady (not blinking) */
	Blink_NGT(25),
	
	/** (reserved for proportional spacing as specified in CCITT Recommendation T.61) */
	Reserved_1(26),
	
	/** positive image */
	Image_PST(27),
	
	/** revealed characters */
	Reveal(28),
	
	/** not crossed out */
	CrossedOut_NGT(29),
	
	/** black display */
	Color_Text_Black(30),
	
	/** red display */
	Color_Text_Red(31),
	
	/** green display */
	Color_Text_Green(32),
	
	/** yellow display */
	Color_Text_Yellow(33),
	
	/** blue display */
	Color_Text_Blue(34),
	
	/** magenta display */
	Color_Text_Magenta(35),
        
	/** cyan display */
	Color_Text_Cyan(36),
	
	/** white display */
	Color_Text_White(37),
	
	/** (reserved for future standardization; intended for setting character foreground                     			       
	    colour as specified in ISO 8613-6 [CCITT Recommendation T.416]) */
	Color_Text_Reserved(38),
	
	/** default display colour (implementation-defined) */
	Color_Text_Reset(39),
	
	/** black background */
	Color_Bkgr_Black(40),
	
	/** red background */
	Color_Bkgr_Red(41),
	
	/** green background */
	Color_Bkgr_Green(42),
	
	/** yellow background */
	Color_Bkgr_Yellow(43),
	
	/** blue background */
	Color_Bkgr_Blue(44),
	
	/** magenta background */
	Color_Bkgr_Magenta(45),
        
	/** cyan background */
	Color_Bkgr_Cyan(46),
	
	/** white background */
	Color_Bkgr_White(47),
	
	/** (reserved for future standardization; intended for setting character background                     			       
	    colour as specified in ISO 8613-6 [CCITT Recommendation T.416]) */
	Color_Bkgr_Reserved(48),
	
	/** default background colour (implementation-defined) */
	Color_Bkgr_Reset(49),
	
	/** (reserved for cancelling the effect of the rendering aspect established by parameter value 26) */
	Reserved_2(50),
	
	/** framed */
	Framed(51),
	
	/** encircled */
	Encircled(52),
	
	/** overlined */
	Overlined(53),
	
	/** not framed, not encircled */
	Outlined_NGT(54),
	
	/** not overlined */
	Overlined_NGT(55),
	
	/** (reserved for future standardization) */
	Reserved_3(56),
	
	/** (reserved for future standardization) */
	Reserved_4(57),
	
	/** (reserved for future standardization) */
	Reserved_5(58),
	
	/** (reserved for future standardization) */
	Reserved_6(59),
	
	/** ideogram underline or right side line */
	Line_BottomOrRight_Single(60),
	
	/** ideogram double underline or double line on the right side */
	Line_BottomOrRight_Double(61),
	
	/** ideogram overline or left side line */
	Line_TopOrLeft_Single(62),
	
	/** ideogram double overline or double line on the left side */
	Line_TopOrLeft_Double(63),
	
	/** ideogram stress marking */
	Stress_Mark(64),
	
	/** cancels the effect of the rendition aspects established by parameter values 60 to 64 */
	Cansel_Any_Line(65),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value;
	
	paramSGR(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSGR val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSGR getEnumValue(int val)
	{
		for ( paramSGR element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
	
	//{{{ getColor() method
	/**
	   Translates enum-value to <code>Color</code>.
	   @param val enum-value
	   @return Color-instance
	 */
	public static Color getColor(paramSGR val)
	{
		switch (val)
		{
			case Color_Text_Black:
			case Color_Bkgr_Black:
				return Color.BLACK;
				
			case Color_Text_Red:
			case Color_Bkgr_Red:
				return Color.RED;
				
			case Color_Text_Green:
			case Color_Bkgr_Green:
				return Color.GREEN;
				
			case Color_Text_Yellow:
			case Color_Bkgr_Yellow:
				return Color.YELLOW;
				
			case Color_Text_Blue:
			case Color_Bkgr_Blue:
				return Color.BLUE;
				
			case Color_Text_Magenta:
			case Color_Bkgr_Magenta:
				return Color.MAGENTA;
				
			case Color_Text_Cyan:
			case Color_Bkgr_Cyan:
				return Color.CYAN;
				
			case Color_Text_White:
			case Color_Bkgr_White:
				return Color.WHITE;
				
			case Color_Text_Reserved:
			case Color_Bkgr_Reserved:
				return null;
				
			default:
				return null;
		}
	} //}}}
}
