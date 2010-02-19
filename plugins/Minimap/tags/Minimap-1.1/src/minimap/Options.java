/*
Copyright (C) 2009  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package minimap;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.ColorWellButton;

@SuppressWarnings("serial")
public class Options extends AbstractOptionPane {

	private static final String AUTO_LABEL = "labels.minimap.auto";
	private static final String FONT_LABEL = "labels.minimap.fontFamily";
	private static final String SIZE_LABEL = "labels.minimap.fontSize";
	private static final String SIDE_LABEL = "labels.minimap.side";
	private static final String SCROLL_LABEL = "labels.minimap.scroll";
	private static final String FOLD_LABEL = "labels.minimap.fold";
	private static final String TIME_LABEL = "labels.minimap.time";
	private static final String SQUARECOLOR_LABEL = "labels.minimap.squarecolor";
	private static final String FILLSQUARE_LABEL = "labels.minimap.fillsquare";
	private static final String ALPHASQUARE_LABEL = "labels.minimap.alpha";
	private static final String AUTO_PROP = "options.minimap.auto";
	private static final String FONT_PROP = "options.minimap.font";
	private static final String SIZE_PROP = "options.minimap.size";
	private static final String SIDE_PROP = "options.minimap.side";
	private static final String SCROLL_PROP = "options.minimap.scroll";
	private static final String FOLD_PROP = "options.minimap.fold";
	private static final String TIME_PROP = "options.minimap.time";
	private static final String SQUARECOLOR_PROP = "options.minimap.squarecolor";
	private static final String FILLSQUARE_PROP = "options.minimap.fillsquare";
	private static final String ALPHASQUARE_PROP = "options.minimap.alpha";
	private JComboBox font;
	private JTextField size;
	private JCheckBox auto;
	private JComboBox side;
	private JCheckBox scroll;
	private JCheckBox fold;
	private JSlider time;
	private ColorWellButton squareColor;
	private JCheckBox squareFilled;
	private JSlider alpha;
	public static final String LEFT = "Left";
	public static final String RIGHT = "Right";
	private static final String[] SIDES = { LEFT, RIGHT };
	
	public Options() {
		super("minimap");
	}
	
	@Override
	public void _init() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String [] fonts = ge.getAvailableFontFamilyNames();
		font = new JComboBox(fonts);
		addComponent(jEdit.getProperty(FONT_LABEL), font);
		size = new JTextField();
		addComponent(jEdit.getProperty(SIZE_LABEL), size);
		auto = new JCheckBox(jEdit.getProperty(AUTO_LABEL));
		addComponent(auto);
		side = new JComboBox(SIDES);
		addComponent(jEdit.getProperty(SIDE_LABEL), side);
		scroll = new JCheckBox(jEdit.getProperty(SCROLL_LABEL));
		addComponent(scroll);
		fold = new JCheckBox(jEdit.getProperty(FOLD_LABEL));
		addComponent(fold);
		time = new JSlider(500, 5000);
		time.setMajorTickSpacing(500);
		time.setMinorTickSpacing(100);
		time.setPaintLabels(true);
		time.setPaintTicks(true);
		addComponent(jEdit.getProperty(TIME_LABEL), time);
		squareColor = new ColorWellButton(jEdit.getColorProperty(Options.SQUARECOLOR_PROP, Color.RED));
		addComponent(new JLabel(jEdit.getProperty(SQUARECOLOR_LABEL)),squareColor);
		squareFilled = new JCheckBox(jEdit.getProperty(FILLSQUARE_LABEL), jEdit.getBooleanProperty(FILLSQUARE_PROP));
		addComponent(squareFilled);
		addComponent(new JLabel(jEdit.getProperty(ALPHASQUARE_LABEL)),
                 alpha = new JSlider(0,
                               100,
                               jEdit.getIntegerProperty(ALPHASQUARE_PROP, 50)));

		font.setSelectedItem(getFontProp());
		size.setText(String.valueOf(getSizeProp()));
		auto.setSelected(getAutoProp());
		side.setSelectedItem(getSideProp());
		scroll.setSelected(getScrollProp());
		fold.setSelected(getFoldProp());
		time.setValue(getTimeProp());
	}

	@Override
	public void _save() {
		jEdit.setProperty(FONT_PROP, font.getSelectedItem().toString());
		double d = 2.0;
		try {
			d = Double.valueOf(size.getText());
		} catch (Exception e) {
			// Ignore, use default
		}
		jEdit.setDoubleProperty(SIZE_PROP, d);
		jEdit.setBooleanProperty(AUTO_PROP, auto.isSelected());
		jEdit.setProperty(SIDE_PROP, side.getSelectedItem().toString());
		jEdit.setBooleanProperty(SCROLL_PROP, scroll.isSelected());
		jEdit.setBooleanProperty(FOLD_PROP, fold.isSelected());
		jEdit.setIntegerProperty(TIME_PROP, time.getValue());
		jEdit.setColorProperty(SQUARECOLOR_PROP, squareColor.getSelectedColor());
		jEdit.setBooleanProperty(FILLSQUARE_PROP, squareFilled.isSelected());
		jEdit.setIntegerProperty(ALPHASQUARE_PROP, alpha.getValue());
	}
	
	public static String getFontProp() {
		return jEdit.getProperty(FONT_PROP, jEdit.getFontProperty("view.font").getFamily());
	}
	public static double getSizeProp() {
		return jEdit.getDoubleProperty(SIZE_PROP, 2.0);
	}
	public static boolean getAutoProp() {
		return jEdit.getBooleanProperty(AUTO_PROP);
	}
	public static String getSideProp() {
		String s = jEdit.getProperty(SIDE_PROP, SIDES[0]);
		for (String sideOption: SIDES)
			if (s.equalsIgnoreCase(sideOption))
				return sideOption;
		return SIDES[0];
	}
	public static boolean getScrollProp() {
		return jEdit.getBooleanProperty(SCROLL_PROP);
	}
	public static boolean getFoldProp() {
		return jEdit.getBooleanProperty(FOLD_PROP, true);
	}
	public static int getTimeProp() {
		return jEdit.getIntegerProperty(TIME_PROP, 500);
	}

	public static Color getSquareColor() {
		return jEdit.getColorProperty(SQUARECOLOR_PROP, Color.RED);
	}

	public static boolean isSquareFilled() {
		return jEdit.getBooleanProperty(FILLSQUARE_PROP);
	}

	public static float getAlpha() {
		return ((float)jEdit.getIntegerProperty(Options.ALPHASQUARE_PROP, 50)) / 100f;
	}
}
