package com.lipstikLF.theme;

import java.awt.Color;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public abstract class LipstikColorTheme extends DefaultMetalTheme
{

	public abstract ColorUIResource getBorderDisabled();

	public abstract ColorUIResource getBorderNormal();

	public abstract ColorUIResource getBorderBrighter();

	public abstract ColorUIResource getBorderSelected();

	public abstract ColorUIResource getMenuItemSelectedBackground();

	public abstract ColorUIResource getMenuItemSelectedFrame();

	public abstract ColorUIResource getMenuBarBackground();

	public abstract ColorUIResource getMenuBackground();

	public abstract ColorUIResource getMenuGradient();

	public abstract ColorUIResource getControl();

	public abstract ColorUIResource getControlDarkShadow();

	public abstract ColorUIResource getControlShadow();

	public abstract ColorUIResource getControlHighlight();

	public abstract ColorUIResource getProgressForeground();

	public abstract ColorUIResource getTextHighlightColor();

	public abstract ColorUIResource getTooltipBackground();

	public abstract ColorUIResource getPrimary1();

	public abstract ColorUIResource getPrimary3();

	public abstract ColorUIResource getSecondary1();

	public abstract ColorUIResource getSecondary3();

	public abstract ColorUIResource getInternalButtonBackground();

	public abstract ColorUIResource getInternalButtonInactive();

	public abstract ColorUIResource getInternalButtonHighlight();

	public abstract FontUIResource getStdFont();

	public abstract FontUIResource getStdBoldFont();
    
    public ColorUIResource getWindowBorder() { return new ColorUIResource(Color.BLACK); }
    
    public ColorUIResource getWindowText() { return new ColorUIResource(Color.BLACK); }
    
    public ColorUIResource getMenuText() { return new ColorUIResource(Color.BLACK); }
    
    public ColorUIResource getTextColor() { return new ColorUIResource(Color.BLACK); }
    
    public ColorUIResource getControlText() { return new ColorUIResource(Color.BLACK); }
    
    public ColorUIResource getControlDarkText() { return new ColorUIResource(Color.BLACK); }

}