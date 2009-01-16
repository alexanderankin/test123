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

import java.awt.GraphicsEnvironment;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class Options extends AbstractOptionPane {

	private static final String AUTO_LABEL = "labels.minimap.auto";
	private static final String FONT_LABEL = "labels.minimap.fontFamily";
	private static final String SIZE_LABEL = "labels.minimap.fontSize";
	private static final String SIDE_LABEL = "labels.minimap.side";
	private static final String TIME_LABEL = "labels.minimap.time";
	private static final String AUTO_PROP = "options.minimap.auto";
	private static final String FONT_PROP = "options.minimap.font";
	private static final String SIZE_PROP = "options.minimap.size";
	private static final String SIDE_PROP = "options.minimap.side";
	private static final String TIME_PROP = "options.minimap.time";
	private JComboBox font;
	private JTextField size;
	private JCheckBox auto;
	private JComboBox side;
	private JSlider time;
	public static final String LEFT = "Left";
	public static final String RIGHT = "Right";
	private static final String[] SIDES = { LEFT, RIGHT };
	
	public Options() {
		super("minimap");
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
		time = new JSlider(500, 5000);
		time.setMajorTickSpacing(500);
		time.setMinorTickSpacing(100);
		time.setPaintLabels(true);
		time.setPaintTicks(true);
		addComponent(jEdit.getProperty(TIME_LABEL), time);
	}
	
	public void _init() {
		font.setSelectedItem(getFontProp());
		size.setText(String.valueOf(getSizeProp()));
		auto.setSelected(getAutoProp());
		side.setSelectedItem(getSideProp());
		time.setValue(getTimeProp());
	}
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
		jEdit.setIntegerProperty(TIME_PROP, time.getValue());
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
	public static int getTimeProp() {
		return jEdit.getIntegerProperty(TIME_PROP, 500);
	}
}
