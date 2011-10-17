/*
 * PHPParserOptionPane.java - The PHP Parser
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2010 Matthieu Casanova
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

	private JCheckBox unusedParameter;
	private JCheckBox unassignedVariable;
	private JCheckBox unnecessaryGlobal;
	private JCheckBox caseSemicolon;
	private JCheckBox loadOnStartup;
	private JCheckBox deprecatedVarToken;
	private JCheckBox conditionalExpressionCheck;
	private JCheckBox methodFieldsSameName;
	private JCheckBox phpClosingMissing;

	public static final String PROP_WARN_SHORT_OPENTAG = "gatchan.phpparser.warnings.shortOpenTag";
	public static final String PROP_WARN_FORENDFOR = "gatchan.phpparser.warnings.forEndFor";
	public static final String PROP_WARN_SWITCHENDSWITCH = "gatchan.phpparser.warnings.switchEndSwitch";
	public static final String PROP_WARN_IFENDIF = "gatchan.phpparser.warnings.ifEndSwitch";
	public static final String PROP_WARN_WHILEENDWHILE = "gatchan.phpparser.warnings.whileEndWhile";
	public static final String PROP_WARN_FOREACHENDFOREACH = "gatchan.phpparser.warnings.foreachEndForeach";
	public static final String PROP_WARN_UNUSED_PARAMETERS = "gatchan.phpparser.warnings.methodanalysis.unusedParameters";
	public static final String PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED = "gatchan.phpparser.warnings.methodanalysis.unassignedVariable";
	public static final String PROP_WARN_CASE_SEMICOLON = "gatchan.phpparser.warnings.warnings.caseSemicolon";
	public static final String PROP_WARN_UNNECESSARY_GLOBAL = "gatchan.phpparser.warnings.methodanalysis.unnecessaryGlobal";
	public static final String PROP_WARN_DEPRECATED_VAR_TOKEN = "gatchan.phpparser.warnings.deprecatedphp4.varToken";
	public static final String PROP_WARN_CONDITIONAL_EXPRESSION_CHECK = "gatchan.phpparser.warnings.types.conditionalExpressionCheck";
	public static final String PROP_WARN_MESSAGE_METHOD_FIELD_WITH_SAME_NAME = "gatchan.phpparser.warnings.classes.method_field_same_name";
	public static final String PROP_WARN_MESSAGE_PHP_CLOSING_MISSING = "gatchan.phpparser.warnings.phpclosingmissing";
	public static final String PROP_WARN_DOUBLE_DOLLAR = "gatchan.phpparser.warnings.doubledollar";

	/**
	 * Instantiate the option pane of the PHP Parser.
	 */
	public PHPParserOptionPane()
	{
		super("gatchan.phpparser.files");
	}

	/**
	 * Initialize the form. This method is automatically called by jEdit
	 */
	@Override
	protected void _init()
	{
		addComponent(loadOnStartup = new JCheckBox(jEdit.getProperty("options.gatchan.phpparser.loadOnStartup.text")));
		String startupMode = jEdit.getProperty("plugin.gatchan.phpparser.PHPParserPlugin.activate");
		loadOnStartup.setSelected("startup".equals(startupMode));

		addComponent(new JLabel("Warnings"));
		addComponent(phpClosingMissing = createCheckBox(PROP_WARN_MESSAGE_PHP_CLOSING_MISSING));
		addComponent(deprecatedVarToken = createCheckBox(PROP_WARN_DEPRECATED_VAR_TOKEN));
		addComponent(shortOpenTag = createCheckBox(PROP_WARN_SHORT_OPENTAG));
		addComponent(forEndFor = createCheckBox(PROP_WARN_FORENDFOR));
		addComponent(switchEndSwitch = createCheckBox(PROP_WARN_SWITCHENDSWITCH));
		addComponent(ifEndSwitch = createCheckBox(PROP_WARN_IFENDIF));
		addComponent(whileEndWhile = createCheckBox(PROP_WARN_WHILEENDWHILE));
		addComponent(foreachEndForeach = createCheckBox(PROP_WARN_FOREACHENDFOREACH));
		addComponent(caseSemicolon = createCheckBox(PROP_WARN_CASE_SEMICOLON));
		addComponent(conditionalExpressionCheck = createCheckBox(PROP_WARN_CONDITIONAL_EXPRESSION_CHECK));
		addComponent(doubleDollarCheck = createCheckBox(PROP_WARN_DOUBLE_DOLLAR));
		addComponent(new JLabel("Method analysis"));
		addComponent(unusedParameter = createCheckBox(PROP_WARN_UNUSED_PARAMETERS));
		addComponent(unassignedVariable = createCheckBox(PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED));
		addComponent(unnecessaryGlobal = createCheckBox(PROP_WARN_UNNECESSARY_GLOBAL));

		addComponent(new JLabel("Class analysis"));
		addComponent(methodFieldsSameName = createCheckBox(PROP_WARN_MESSAGE_METHOD_FIELD_WITH_SAME_NAME));
	}

	private static JCheckBox createCheckBox(String property)
	{
		JCheckBox checkbox = new JCheckBox(jEdit.getProperty(property + ".text"));
		checkbox.setSelected(jEdit.getBooleanProperty(property));
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
		jEdit.setBooleanProperty(PROP_WARN_DEPRECATED_VAR_TOKEN, deprecatedVarToken.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_SHORT_OPENTAG, shortOpenTag.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_FORENDFOR, forEndFor.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_SWITCHENDSWITCH, switchEndSwitch.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_IFENDIF, ifEndSwitch.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_WHILEENDWHILE, whileEndWhile.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_FOREACHENDFOREACH, foreachEndForeach.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_UNUSED_PARAMETERS, unusedParameter.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED, unassignedVariable.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_UNNECESSARY_GLOBAL, unnecessaryGlobal.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_CASE_SEMICOLON, caseSemicolon.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_CONDITIONAL_EXPRESSION_CHECK, conditionalExpressionCheck.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_MESSAGE_METHOD_FIELD_WITH_SAME_NAME, methodFieldsSameName.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_MESSAGE_PHP_CLOSING_MISSING, phpClosingMissing.isSelected());
		jEdit.setBooleanProperty(PROP_WARN_DOUBLE_DOLLAR, doubleDollarCheck.isSelected());
	}
}
