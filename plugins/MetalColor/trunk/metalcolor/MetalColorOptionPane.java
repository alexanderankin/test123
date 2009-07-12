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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;


import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

public class MetalColorOptionPane extends AbstractOptionPane
{
    private ColorWellButton controlColor;
    private ColorWellButton bgColor;
    private ColorWellButton textColor;
    private ColorWellButton scrollBarColor;
    private JSlider scrollBarWidth;
    
    public MetalColorOptionPane()
    {
        super( MetalColorPlugin.NAME );
    }

    public void _init()
    {
        addSeparator("options.metalcolor.uicolorstitle");
        
        controlColor = new ColorWellButton( jEdit.getColorProperty(MetalColorPlugin.CONTROLCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_CONTROLCOLOR) );
        addComponent( jEdit.getProperty("options.metalcolor.controlcolor"), controlColor );

        bgColor = new ColorWellButton( jEdit.getColorProperty(MetalColorPlugin.BGCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_BGCOLOR) );
        addComponent( jEdit.getProperty("options.metalcolor.backgroundcolor"), bgColor );

        textColor = new ColorWellButton( jEdit.getColorProperty(MetalColorPlugin.TEXTCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_TEXTCOLOR) );
        addComponent( jEdit.getProperty("options.metalcolor.textcolor"), textColor );

        
        addSeparator("options.metalcolor.scrollbartitle");

        scrollBarColor = new ColorWellButton( jEdit.getColorProperty(MetalColorPlugin.SCROLLBARCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_CONTROLCOLOR) );
        addComponent( jEdit.getProperty("options.metalcolor.scrollbarcolor"), scrollBarColor );
        
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put( new Integer(5), new JLabel("5") );
        labelTable.put( new Integer(10), new JLabel("10") );
        labelTable.put( new Integer(15), new JLabel("15") );
        labelTable.put( new Integer(20), new JLabel("20") );
        scrollBarWidth = new JSlider( 5, 20 );
        scrollBarWidth.setValue( jEdit.getIntegerProperty(MetalColorPlugin.SCROLLBARWIDTH_PROPERTY_NAME, MetalColorTheme.DEFAULT_SCROLLBARWIDTH) );
		scrollBarWidth.setLabelTable( labelTable );
		scrollBarWidth.setPaintLabels( true );
		scrollBarWidth.setMinorTickSpacing( 1 );
		scrollBarWidth.setMajorTickSpacing( 5 );
		scrollBarWidth.setPaintTicks( true );
		scrollBarWidth.setSnapToTicks( true );
        addComponent( jEdit.getProperty("options.metalcolor.scrollbarwidth"), scrollBarWidth );

        
        addSeparator();
        addComponent( new JLabel(jEdit.getProperty("options.metalcolor.note")) );
        
        JButton resetDefaultButton = new JButton(new AbstractAction(jEdit.getProperty("options.metalcolor.resetdefault"))
            {
                public void actionPerformed(ActionEvent e)
                {
                    controlColor.setSelectedColor( MetalColorTheme.DEFAULT_CONTROLCOLOR );
                    bgColor.setSelectedColor( MetalColorTheme.DEFAULT_BGCOLOR );
                    textColor.setSelectedColor( MetalColorTheme.DEFAULT_TEXTCOLOR );
                    scrollBarColor.setSelectedColor( MetalColorTheme.DEFAULT_CONTROLCOLOR );
                    scrollBarWidth.setValue( MetalColorTheme.DEFAULT_SCROLLBARWIDTH );
                }
            });
        addComponent( resetDefaultButton );
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
        
        Color oldSb = jEdit.getColorProperty( MetalColorPlugin.SCROLLBARCOLOR_PROPERTY_NAME );
        jEdit.setColorProperty( MetalColorPlugin.SCROLLBARCOLOR_PROPERTY_NAME, scrollBarColor.getSelectedColor() );
        dirty |= oldSb == null || !scrollBarColor.getSelectedColor().equals(oldSb);
        
        int oldSbWidth = jEdit.getIntegerProperty( MetalColorPlugin.SCROLLBARWIDTH_PROPERTY_NAME, 0 );
        jEdit.setIntegerProperty( MetalColorPlugin.SCROLLBARWIDTH_PROPERTY_NAME, scrollBarWidth.getValue() );
        dirty |= oldSbWidth == 0 || oldSbWidth != scrollBarWidth.getValue();
        
        if( dirty )
            MetalColorPlugin.updateTheme();
    }
}
