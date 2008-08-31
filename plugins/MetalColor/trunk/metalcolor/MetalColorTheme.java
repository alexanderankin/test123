/*
 * MetalColorTheme.java - MetalColor plugin
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

import java.awt.Color;
import java.util.Arrays;
import javax.swing.plaf.ColorUIResource; 
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.UIDefaults;

/** Overrides OceanTheme taking 3 base colors in input and keeping the same color ratio. */
public class MetalColorTheme extends OceanTheme
{
    public static final Color DEFAULT_CONTROLCOLOR = new Color(0xA3B8CC);
    public static final Color DEFAULT_TEXTCOLOR = new Color(0x333333);
    public static final Color DEFAULT_BGCOLOR = new Color(0xEEEEEE);
    
    /** Keep default getBlack() here since we override it with text color */
    private static final ColorUIResource REAL_BLACK = new ColorUIResource(0x333333);
    
    private final ColorUIResource primaryColor1;
    private final ColorUIResource primaryColor2;
    private final ColorUIResource primaryColor3;
    private final ColorUIResource secondaryColor1;
    private final ColorUIResource secondaryColor2;
    private final ColorUIResource secondaryColor3;

    private final ColorUIResource borderColor;
    private final ColorUIResource buttonGradientColor1;
    private final ColorUIResource buttonGradientColor2;
    private final ColorUIResource sliderGradientColor1;
    private final ColorUIResource sliderGradientColor2;
    private final ColorUIResource menuGradientColor1;
    private final ColorUIResource menuGradientColor2;
    
    private final ColorUIResource auxColor1;
    private final ColorUIResource auxColor2;
    private final ColorUIResource auxColor3;
    
    private final ColorUIResource blackColor;
    private final ColorUIResource whiteColor;
    
    private final ColorUIResource textColor;

    public MetalColorTheme( Color controlColor, Color textColor, Color bgColor )
    {
        primaryColor1   = getRelativeColor( controlColor, 0.28f, -0.05f );
        primaryColor2   = new ColorUIResource( controlColor );
        primaryColor3   = getRelativeColor( controlColor, 0.0f, 0.10f );
        secondaryColor1 = getRelativeColor( controlColor, 0.0f, -0.20f );
        secondaryColor2 = getRelativeColor( controlColor, 0.0f, 0.10f );
        secondaryColor3 = new ColorUIResource( bgColor );

        borderColor         = getRelativeColor( controlColor, -0.60f, 0.0f );
        buttonGradientColor1= getRelativeColor( controlColor, -0.11f, 0.15f );
        buttonGradientColor2= getRelativeColor( controlColor, -0.80f, 0.20f );
        sliderGradientColor1= getRelativeColor( controlColor, -0.03f, 0.15f );
        sliderGradientColor2= getRelativeColor( controlColor, -0.80f, 0.20f );
        menuGradientColor1  = getRelativeColor( bgColor, 0.0f, 0.07f );
        menuGradientColor2  = getRelativeColor( bgColor, 0.0f, -0.08f );
        auxColor1 = getRelativeColor( controlColor, -0.5f, 0.05f );
        auxColor2 = getRelativeColor( controlColor, -0.08f, 0.14f );
        auxColor3 = getRelativeColor( controlColor, -0.02f, 0.20f );
        
        this.textColor = new ColorUIResource(textColor);
        
        // Override black color since it is used directly in some places (like tooltips)
        blackColor = new ColorUIResource(textColor);
        whiteColor = getRelativeColor( bgColor, 0.0f, 0.25f );
    }

    public void addCustomEntriesToTable(UIDefaults table)
    {
        // Call OceanTheme's one then override our properties
        super.addCustomEntriesToTable( table );
        
        // The rest was filtered from OpenSDK's OceanTheme.java file.
        java.util.List buttonGradient = Arrays.asList(
                 new Object[] {new Float(.3f), new Float(0f),
                 getButtonGradientColor1(), getButtonGradientColor2(), getSecondary2() });

        java.util.List sliderGradient = Arrays.asList(new Object[] {
            new Float(.3f), new Float(.2f),
            getSliderGradientColor1(), getSliderGradientColor2(), getSecondary2() });

        Object[] defaults = new Object[] {
            "Button.gradient", buttonGradient,
            "Button.toolBarBorderBackground", getInactiveControlTextColor(),
            "Button.disabledToolBarBorderBackground", getBorderColor(),

            "CheckBox.gradient", buttonGradient,

            "CheckBoxMenuItem.gradient", buttonGradient,

            "MenuBar.gradient", Arrays.asList(new Object[] {
                     new Float(1f), new Float(0f),
                     getMenuGradientColor1(), getMenuGradientColor2(),
                     getMenuGradientColor2() }),
            "MenuBar.borderColor", getBorderColor(),

            "InternalFrame.activeTitleGradient", buttonGradient,

            "RadioButton.gradient", buttonGradient,

            "RadioButtonMenuItem.gradient", buttonGradient,

            "ScrollBar.gradient", buttonGradient,

            "Slider.altTrackColor", getAuxColor2(),
            "Slider.gradient", sliderGradient,
            "Slider.focusGradient", sliderGradient,

            "SplitPane.dividerFocusColor", getSliderGradientColor1(),

            "TabbedPane.borderHightlightColor", getPrimary1(),
            "TabbedPane.contentAreaColor", getSliderGradientColor1(),
            "TabbedPane.selected", getSliderGradientColor1(),
            "TabbedPane.tabAreaBackground", getAuxColor1(),
            "TabbedPane.unselectedBackground", getSecondary3(),

            "Table.gridColor", getSecondary1(),
            "TableHeader.focusCellBackground", getSliderGradientColor1(),

            "ToggleButton.gradient", buttonGradient,

            "ToolBar.borderColor", getBorderColor(),

            "Tree.selectionBorderColor", getPrimary1(),
            "Tree.dropLineColor", getPrimary1(),
            "Table.dropLineColor", getPrimary1(),
            "Table.dropLineShortColor", getBlack(),

            "Table.dropCellBackground", getAuxColor3(),
            "Tree.dropCellBackground", getAuxColor3(),
            "List.dropCellBackground", getAuxColor3(),
            "List.dropLineColor", getPrimary1()
        };
        table.putDefaults(defaults);
    }

    public String getName() { return "MetalColorTheme"; }

    public ColorUIResource getBlack() { return blackColor; }
    public ColorUIResource getWhite() { return whiteColor; }
    
    public ColorUIResource getControlTextColor() { return getBlack(); }
    public ColorUIResource getSystemTextColor() { return getBlack(); }
    public ColorUIResource getUserTextColor() { return getBlack(); }
    public ColorUIResource getWindowTitleForeground() { return getBlack(); }
    public ColorUIResource getWindowTitleInactiveForeground() { return REAL_BLACK; }
    public ColorUIResource getMenuForeground() { return  getBlack(); }
    public ColorUIResource getMenuSelectedForeground() { return getBlack(); }
    public ColorUIResource getAcceleratorSelectedForeground() { return getBlack(); }
    
    protected ColorUIResource getPrimary1() { return primaryColor1; }
    protected ColorUIResource getPrimary2() { return primaryColor2; }
    protected ColorUIResource getPrimary3() { return primaryColor3; }
    protected ColorUIResource getSecondary1() { return secondaryColor1; }
    protected ColorUIResource getSecondary2() { return secondaryColor2; }
    protected ColorUIResource getSecondary3() { return secondaryColor3; }
    
    private ColorUIResource getBorderColor() { return borderColor; }

    private ColorUIResource getButtonGradientColor1() { return buttonGradientColor1; }
    private ColorUIResource getButtonGradientColor2() { return buttonGradientColor2; }

    private ColorUIResource getSliderGradientColor1() { return sliderGradientColor1; }
    private ColorUIResource getSliderGradientColor2() { return sliderGradientColor2; }

    private ColorUIResource getMenuGradientColor1() { return menuGradientColor1; }
    private ColorUIResource getMenuGradientColor2() { return menuGradientColor2; }

    private ColorUIResource getAuxColor1() { return auxColor1; }
    private ColorUIResource getAuxColor2() { return auxColor2; }
    private ColorUIResource getAuxColor3() { return auxColor3; }

    private ColorUIResource getRelativeColor( Color baseColor, float saturationDelta, float brightnessDelta )
    {
        float[] hsbVals = new float[3];
        Color.RGBtoHSB( baseColor.getRed()
                      , baseColor.getGreen()
                      , baseColor.getBlue()
                      , hsbVals );
        
        float sat = hsbVals[1] + saturationDelta;
        if( sat < 0.0f )        sat = 0.0f;
        else if( sat > 1.0f )   sat = 1.0f;
        
        float bri = hsbVals[2] + brightnessDelta;
        if( bri < 0.0f )        bri = 0.0f;
        else if( bri > 1.0f )   bri = 1.0f;
        
        int newColor = Color.HSBtoRGB( hsbVals[0] , sat , bri );
        
        return new ColorUIResource( newColor );
    }
}
