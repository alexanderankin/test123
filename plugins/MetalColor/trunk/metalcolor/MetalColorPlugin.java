/*
 * MetalColorPlugin.java - MetalColor plugin
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

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import metalcolor.MetalColorTheme;
  
/**
 * The MetalColor plugin
 * 
 * @author Jocelyn Turcotte
 */
public class MetalColorPlugin extends EditPlugin
{
    public static final String NAME = "metalcolor";
    public static final String CONTROLCOLOR_PROPERTY_NAME = "metalcolor.basecolor";
    public static final String TEXTCOLOR_PROPERTY_NAME = "metalcolor.textcolor";
    public static final String BGCOLOR_PROPERTY_NAME = "metalcolor.backgroundcolor";

    public void start()
    {                                               
        updateTheme();
    }
    
    public static void updateTheme()
    {
        try
        {
            Color controlColor = jEdit.getColorProperty( CONTROLCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_CONTROLCOLOR );
            Color textColor = jEdit.getColorProperty( TEXTCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_TEXTCOLOR );
            Color bgColor = jEdit.getColorProperty( BGCOLOR_PROPERTY_NAME, MetalColorTheme.DEFAULT_BGCOLOR );
            
            MetalLookAndFeel.setCurrentTheme( new MetalColorTheme(controlColor, textColor, bgColor) );
            updateAllComponentTreeUIs();
        }
        catch (Exception e)
        {
            Log.log(Log.ERROR, MetalColorPlugin.class, e);
        }
    }
    
    /**
     * Update the component trees of all windows.
     */
    private static void updateAllComponentTreeUIs() throws javax.swing.UnsupportedLookAndFeelException
    {
        if( UIManager.getLookAndFeel() instanceof MetalLookAndFeel )
        {
            // re-install the Metal Look and Feel
            UIManager.setLookAndFeel( new MetalLookAndFeel() );
            
            Frame[] frames = Frame.getFrames();
            for (int i=0; i<frames.length; i++)
            {
                SwingUtilities.updateComponentTreeUI(frames[i]);
            }
        }
    }
}
