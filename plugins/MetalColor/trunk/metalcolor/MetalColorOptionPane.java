/*
 * MetalColorOptionPane.java - MetalColor plugin
 * Copyright (C) 2008 Jocelyn Turcotte
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
 *
 * $Id$
 */

package metalcolor;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Color;

import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

public class MetalColorOptionPane extends AbstractOptionPane
{
	private ColorWellButton controlColor;
	private ColorWellButton bgColor;
	private ColorWellButton textColor;
    
	public MetalColorOptionPane()
    {
		super( MetalColorPlugin.NAME );
	}

	public void _init()
    {
        controlColor = new ColorWellButton( jEdit.getColorProperty(MetalColorPlugin.CONTROLCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_CONTROLCOLOR) );
		addComponent( jEdit.getProperty("options.metalcolor.controlcolor"), controlColor );

        bgColor = new ColorWellButton( jEdit.getColorProperty(MetalColorPlugin.BGCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_BGCOLOR) );
		addComponent( jEdit.getProperty("options.metalcolor.backgroundcolor"), bgColor );

        textColor = new ColorWellButton( jEdit.getColorProperty(MetalColorPlugin.TEXTCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_TEXTCOLOR) );
		addComponent( jEdit.getProperty("options.metalcolor.textcolor"), textColor );
        
        addSeparator();
		addComponent( new JLabel(jEdit.getProperty("options.metalcolor.note")) );        
	}

	public void _save()
    {
        boolean dirty = false;
        
        Color oldControl = jEdit.getColorProperty( MetalColorPlugin.CONTROLCOLOR_PROPERTY_NAME );
		jEdit.setColorProperty( MetalColorPlugin.CONTROLCOLOR_PROPERTY_NAME, controlColor.getSelectedColor() );
        dirty |= oldControl == null || !controlColor.getSelectedColor().equals(oldControl);
        
        Color oldBg = jEdit.getColorProperty( MetalColorPlugin.BGCOLOR_PROPERTY_NAME );
		jEdit.setColorProperty( MetalColorPlugin.BGCOLOR_PROPERTY_NAME, bgColor.getSelectedColor() );
        dirty |= oldBg == null || !bgColor.getSelectedColor().equals(oldBg);
        
        Color oldText = jEdit.getColorProperty( MetalColorPlugin.TEXTCOLOR_PROPERTY_NAME );
		jEdit.setColorProperty( MetalColorPlugin.TEXTCOLOR_PROPERTY_NAME, textColor.getSelectedColor() );
        dirty |= oldText == null || !textColor.getSelectedColor().equals(oldText);
        
        if( dirty )
            MetalColorPlugin.updateTheme();
	}
}
