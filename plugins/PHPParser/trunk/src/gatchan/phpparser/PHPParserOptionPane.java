/*
 * PHPParserOptionPane.java - The PHP Parser
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.phpparser;

import gatchan.phpparser.parser.WarningMessageClass;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;

/**
 * The option pane of the PHPParserPlugin.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class PHPParserOptionPane extends AbstractOptionPane
{
	private JCheckBox shortOpenTag;
	private JCheckBox forEndFor;
	private JCheckBox switchEndSwitch;
	private JCheckBox ifEndSwitch;
	private JCheckBox whileEndWhile;
	private JCheckBox foreachEndForeach;
	private JCheckBox doubleDollarCheck;
	private JCheckBox labelStatement;
	private JCheckBox gotoStatement;

	private JCheckBox unusedParameter;
	private JCheckBox unassignedVariable;
	private JCheckBox unnecessaryGlobal;
	private JCheckBox caseSemicolon;
	private JCheckBox loadOnStartup;
	private JCheckBox deprecatedVarToken;
	private JCheckBox conditionalExpressionCheck;
	private JCheckBox methodFieldsSameName;
	private JCheckBox unusedLabel;

	/**
	 * Instantiate the option pane of the PHP Parser.
	 */
	public PHPParserOptionPane()
	{
		super("gatchan.phpparser.option-pane");
	}

	/**
	 * Initialize the form. This method is automatically called by jEdit
	 */
	@Override
	protected void _init()
	{
		addComponent(loadOnStartup =
				     new JCheckBox(jEdit.getProperty("options.gatchan.phpparser.loadOnStartup.text")));
		String startupMode = jEdit.getProperty("plugin.gatchan.phpparser.PHPParserPlugin.activate");
		loadOnStartup.setSelected("startup".equals(startupMode));

		addComponent(new JLabel("Warnings"));
		addComponent(deprecatedVarToken = createCheckBox(WarningMessageClass.varToken));
		addComponent(shortOpenTag = createCheckBox(WarningMessageClass.shortOpenTag));
		addComponent(forEndFor = createCheckBox(WarningMessageClass.forEndFor));
		addComponent(switchEndSwitch = createCheckBox(WarningMessageClass.switchEndSwitch));
		addComponent(ifEndSwitch = createCheckBox(WarningMessageClass.ifEndIf));
		addComponent(whileEndWhile = createCheckBox(WarningMessageClass.whileEndWhile));
		addComponent(foreachEndForeach = createCheckBox(WarningMessageClass.foreachEndForeach));
		addComponent(caseSemicolon = createCheckBox(WarningMessageClass.caseSemicolon));
		addComponent(conditionalExpressionCheck = createCheckBox(WarningMessageClass.conditionalExpressionCheck));
		addComponent(doubleDollarCheck = createCheckBox(WarningMessageClass.doubledollar));
		addComponent(labelStatement = createCheckBox(WarningMessageClass.labelstatement));
		addComponent(unusedLabel = createCheckBox(WarningMessageClass.unusedLabel));
		addComponent(gotoStatement = createCheckBox(WarningMessageClass.gotostatement));
		addComponent(new JLabel("Method analysis"));
		addComponent(unusedParameter = createCheckBox(WarningMessageClass.unusedParameters));
		addComponent(unassignedVariable = createCheckBox(WarningMessageClass.unassignedVariable));
		addComponent(unnecessaryGlobal = createCheckBox(WarningMessageClass.unnecessaryGlobal));

		addComponent(new JLabel("Class analysis"));
		addComponent(methodFieldsSameName = createCheckBox(WarningMessageClass.methodFieldNameCollision));
	}

	private static JCheckBox createCheckBox(WarningMessageClass warningMessageClass)
	{
		JCheckBox checkbox =
			new JCheckBox(jEdit.getProperty("gatchan.phpparser.warnings." + warningMessageClass + ".text"));
		checkbox.setSelected(jEdit.getBooleanProperty("gatchan.phpparser.warnings." + warningMessageClass));
		return checkbox;
	}

	/**
	 * Save the properties. This method is automatically called by jEdit
	 */
	@Override
	protected void _save()
	{
		if (loadOnStartup.isSelected())
		{
			jEdit.setProperty("plugin.gatchan.phpparser.PHPParserPlugin.activate", "startup");
		}
		else
		{
			jEdit.setProperty("plugin.gatchan.phpparser.PHPParserPlugin.activate", "defer");
		}
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.varToken, deprecatedVarToken.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.shortOpenTag, shortOpenTag.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.forEndFor, forEndFor.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.switchEndSwitch, switchEndSwitch.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.ifEndIf, ifEndSwitch.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.whileEndWhile, whileEndWhile.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.foreachEndForeach, foreachEndForeach.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.unusedParameters, unusedParameter.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.unassignedVariable, unassignedVariable.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.unnecessaryGlobal, unnecessaryGlobal.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.caseSemicolon, caseSemicolon.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.conditionalExpressionCheck,
					 conditionalExpressionCheck.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.methodFieldNameCollision,
					 methodFieldsSameName.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.doubledollar, doubleDollarCheck.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.unusedLabel, unusedLabel.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.labelstatement, labelStatement.isSelected());
		jEdit.setBooleanProperty("gatchan.phpparser.warnings." + WarningMessageClass.gotostatement, gotoStatement.isSelected());
	}
}
