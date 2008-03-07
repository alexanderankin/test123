/*
 * Copyright (c) 2008 Eric Berry <elberry@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * 
 */
package com.townsfolkdesigns.common.form.controller;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;
import org.gjt.sp.util.Log;
import org.swixml.SwingEngine;

import com.townsfolkdesigns.swixml.jedit.JEditSwingEngine;

/**
 * @author elberry
 * 
 */
public abstract class SimpleFormController<T> implements FormController<T> {

	private T backingObject;

	private Class<T> backingObjectClass;

	private Action cancelAction = new AbstractAction() {

		public void actionPerformed(ActionEvent arg0) {
			doCancel();
		}

	};

	private Component formView;

	private Action submitAction = new AbstractAction() {

		public void actionPerformed(ActionEvent arg0) {
			createBackingObject();
			bindBackingObject();

			doSubmit();
		}

	};

	private SwingEngine swingEngine;

	protected SimpleFormController(String formViewPath, Class<T> backingObjectClass) {
		setSwingEngine(new JEditSwingEngine(this));
		setBackingObjectClass(backingObjectClass);
		URL formViewUrl = getClass().getResource(formViewPath);
		Container formView = null;
		try {
			formView = getSwingEngine().render(formViewUrl);
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Error rendering option pane xml file.", e);
		}
		setFormView(formView);
	}

	public T getBackingObject() {
		return backingObject;
	}

	public Action getCancelAction() {
		return cancelAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.townsfolkdesigns.common.ui.Form#getFormView()
	 */
	public Component getFormView() {
		return formView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.townsfolkdesigns.common.ui.Form#getSubmitAction()
	 */
	public Action getSubmitAction() {
		return submitAction;
	}

	public SwingEngine getSwingEngine() {
		return swingEngine;
	}

	protected void bindBackingObject() {
		Field[] declaredFields = backingObjectClass.getDeclaredFields();
		Field[] publicAndInheritedFields = backingObjectClass.getFields();
		String fieldName = null;
		Component component = null;
		// loop through the declared fields first.
		for (Field field : declaredFields) {
			fieldName = field.getName();
			component = getSwingEngine().find(fieldName);
			if (component != null) {
				bindField(field, component);
			} else {
				Log.log(Log.WARNING, this, "Couldn't find component for field - name: " + fieldName);
			}
		}
	}

	protected void createBackingObject() {
		T backingObject = null;
		try {
			backingObject = getBackingObjectClass().newInstance();
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Error creating new instance of backing object - class: " + getBackingObjectClass(),
			      e);
		}
		setBackingObject(backingObject);
	}

	protected void doCancel() {

	}

	protected void doSubmit() {

	}

	protected boolean getCheckBoxValue(String fieldName) {
		boolean checkBoxValue = false;
		Component component = getComponent(fieldName);
		if (component != null) {
			if (component instanceof JCheckBox) {
				checkBoxValue = ((JCheckBox) component).isSelected();
			} else {
				Log.log(Log.WARNING, this, "Component \"" + fieldName + "\" is not an instance of JCheckBox.");
			}
		}
		return checkBoxValue;
	}

	protected Component getComponent(String componentName) {
		Component component = swingEngine.find(componentName);
		if (component == null) {
			Log.log(Log.WARNING, this, "Component \"" + componentName + "\" not found in SwingEngine.");
		}
		return component;
	}

	protected String getTextFieldValue(String fieldName) {
		String fieldValue = null;
		Component component = getComponent(fieldName);
		if (component != null) {
			if (component instanceof JTextComponent) {
				fieldValue = ((JTextComponent) component).getText();
			} else {
				Log.log(Log.WARNING, this, "Component \"" + fieldName + "\" is not an instance of JTextComponent.");
			}
		}
		return fieldValue;
	}

	protected void setCheckBoxValue(String fieldName, boolean selected) {
		Component component = getComponent(fieldName);
		if (component != null) {
			if (component instanceof JCheckBox) {
				((JCheckBox) component).setSelected(selected);
			} else {
				Log.log(Log.WARNING, this, "Component \"" + fieldName + "\" is not an instance of JCheckBox.");
			}
		}
	}

	protected void setFormView(Component formView) {
		this.formView = formView;
	}

	protected void setTextFieldValue(String fieldName, String fieldValue) {
		Component component = getComponent(fieldName);
		if (component != null) {
			if (component instanceof JTextComponent) {
				((JTextComponent) component).setText(fieldValue);
			} else {
				Log.log(Log.WARNING, this, "Component \"" + fieldName + "\" is not an instance of JTextComponent.");
			}
		}
	}

	private void bindField(Field field, Component component) {
		// TODO For the love of java change this over to a binder object arch.
		String fieldName = field.getName();
		String methodName = "set" + StringUtils.capitalize(fieldName);
		Class fieldType = field.getType();
		Object fieldValue = null;
		if (fieldType.isAssignableFrom(String.class)) {
			// field is a string.
			fieldValue = getTextFieldValue(fieldName);
		} else if (fieldType.isAssignableFrom(Integer.class) || fieldType.isAssignableFrom(int.class)) {
			// field is an integer.
			fieldValue = getTextFieldValue(fieldName);
			try {
				fieldValue = Integer.parseInt((String) fieldValue);
			} catch (Exception e) {
				Log.log(Log.ERROR, this, "Integer value from field \"" + fieldName + "\" expected - was: '" + fieldValue
				      + "'", e);
			}
		} else if (fieldType.isAssignableFrom(Boolean.class) || fieldType.isAssignableFrom(boolean.class)) {
			// field is a boolean.
			if (component instanceof JTextComponent) {
				// component is a text field.
				fieldValue = Boolean.valueOf(getTextFieldValue(fieldName));
			} else if (component instanceof JCheckBox) {
				// component is a check box.
				fieldValue = getCheckBoxValue(fieldName);
			}
		}
		if(Modifier.isPrivate(field.getModifiers())) {
			// use accessor method instead of field.
		} else {
			// use field directly.
			try {
				field.set(backingObject, fieldValue);
			} catch (Exception e) {
				Log.log(Log.ERROR, this, "Error binding field - name: " + fieldName, e);
			}
		}
	}

	private Class<T> getBackingObjectClass() {
		return backingObjectClass;
	}

	private void setBackingObject(T backingObject) {
		this.backingObject = backingObject;
	}

	private void setBackingObjectClass(Class<T> backingObjectClass) {
		this.backingObjectClass = backingObjectClass;
	}

	private void setSwingEngine(SwingEngine swingEngine) {
		this.swingEngine = swingEngine;
	}

}
