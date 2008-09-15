package org.jedit.plugins.columnruler;

import java.awt.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;

/**
 *  Option Pane for general ruler settings and colors.
 *
 * @author     Brad Mace
 * @version    $Revision: 1.4 $ $Date: 2006-03-27 16:21:28 $
 */
public class ColumnRulerOptions extends AbstractOptionPane {
	private JCheckBox activeByDefault;
	private JCheckBox tabIndicator;

	ButtonGroup numberingOptions = new ButtonGroup();
	private JRadioButton numberTicks;
	private JRadioButton numberChars;
	
	ButtonGroup borderOptions = new ButtonGroup();
	private JRadioButton noBorder;
	private JRadioButton useGutterBorder;
	private JRadioButton customBorder;
	private ColorWellButton borderColor;

	private ButtonGroup backgroundOptions = new ButtonGroup();
	private JRadioButton useTextAreaBackground;
	private JRadioButton useGutterBackground;
	private JRadioButton customBackgroundColor;
	private ColorWellButton bgcolor;

	public ColumnRulerOptions() {
		super("columnruler.ruler");
	}

	protected void _init() {
		activeByDefault = new JCheckBox("Active by Default", jEdit.getProperty("plugin.org.jedit.plugins.columnruler.ColumnRulerPlugin.activate", "defer").equals("startup"));
		tabIndicator = new JCheckBox("Draw indicator for next tab stop", jEdit.getBooleanProperty("options.columnruler.nextTab"));
		JPanel mainPanel = new JPanel(new GridLayout(2, 1));
		mainPanel.add(activeByDefault);
		mainPanel.add(tabIndicator);
		mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "General"));
		addComponent(mainPanel);

		numberTicks = new JRadioButton("Number Ticks", jEdit.getProperty("options.columnruler.numbering", "ticks").equals("ticks"));
		numberChars = new JRadioButton("Number Characters", jEdit.getProperty("options.columnruler.numbering", "ticks").equals("chars"));
		numberingOptions.add(numberTicks);
		numberingOptions.add(numberChars);
		JPanel numberingPanel = new JPanel(new GridLayout(2, 1));
		numberingPanel.add(numberTicks);
		numberingPanel.add(numberChars);
		numberingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Ruler Numbering"));
		addComponent(numberingPanel);

		String bgSrc = jEdit.getProperty("options.columnruler.background.src", "custom");
		useTextAreaBackground = new JRadioButton("Use Text Area's Background", bgSrc.equals("textarea"));
		useGutterBackground = new JRadioButton("Use Gutter's Background", bgSrc.equals("gutter"));
		customBackgroundColor = new JRadioButton("Choose Background:", bgSrc.equals("custom"));
		bgcolor = new ColorWellButton(jEdit.getColorProperty("options.columnruler.background.color", Color.WHITE));
		backgroundOptions.add(useTextAreaBackground);
		backgroundOptions.add(useGutterBackground);
		backgroundOptions.add(customBackgroundColor);

		JPanel backgroundPanel = new JPanel(new GridLayout(3, 2));
		backgroundPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Background Color"));
		backgroundPanel.add(useTextAreaBackground);
		backgroundPanel.add(new JLabel());
		backgroundPanel.add(useGutterBackground);
		backgroundPanel.add(new JLabel());
		backgroundPanel.add(customBackgroundColor);
		backgroundPanel.add(bgcolor);
		addComponent(backgroundPanel);

		String borderSrc = jEdit.getProperty("options.columnruler.border.src", "none");
		noBorder = new JRadioButton("No Border", borderSrc.equals("none"));
		useGutterBorder = new JRadioButton("Same as gutter border", borderSrc.equals("gutter"));
		customBorder = new JRadioButton("Custom color:", borderSrc.equals("custom"));
		borderOptions.add(noBorder);
		borderOptions.add(useGutterBorder);
		borderOptions.add(customBorder);

		borderColor = new ColorWellButton(jEdit.getColorProperty("options.columnruler.border.color", Color.WHITE));

		JPanel borderPanel = new JPanel(new GridLayout(3, 2));
		borderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Border w/ Text Area"));
		borderPanel.add(noBorder);
		borderPanel.add(new JLabel());
		borderPanel.add(useGutterBorder);
		borderPanel.add(new JLabel());
		borderPanel.add(customBorder);
		borderPanel.add(borderColor);
		addComponent(borderPanel);
	}

	protected void _save() {
		jEdit.setProperty("plugin.org.jedit.plugins.columnruler.ColumnRulerPlugin.activate", activeByDefault.isSelected() ? "startup" : "defer");
		jEdit.setBooleanProperty("options.columnruler.nextTab", tabIndicator.isSelected());
		jEdit.setProperty("options.columnruler.numbering", numberTicks.isSelected() ? "ticks" : "chars");
		
		if (useTextAreaBackground.isSelected()) {
			jEdit.setProperty("options.columnruler.background.src", "textarea");
		} else if (useGutterBackground.isSelected()) {
			jEdit.setProperty("options.columnruler.background.src", "gutter");
		} else {
			jEdit.setProperty("options.columnruler.background.src", "custom");
			jEdit.setColorProperty("options.columnruler.background.color", bgcolor.getSelectedColor());
		}
		if (noBorder.isSelected()) {
			jEdit.setProperty("options.columnruler.border.src", "none");
		} else if (useGutterBorder.isSelected()) {
			jEdit.setProperty("options.columnruler.border.src", "gutter");
		} else {
			jEdit.setProperty("options.columnruler.border.src", "custom");
			jEdit.setColorProperty("options.columnruler.border.color", borderColor.getSelectedColor());
		}
	}
}

