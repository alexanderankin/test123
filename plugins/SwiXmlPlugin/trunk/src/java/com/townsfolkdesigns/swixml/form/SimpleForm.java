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
package com.townsfolkdesigns.swixml.form;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

import org.gjt.sp.util.Log;
import org.swixml.SwingEngine;

import com.townsfolkdesigns.swixml.form.converter.FieldConverter;
import com.townsfolkdesigns.swixml.jedit.JEditSwingEngine;

/**
 * @author elberry
 * 
 */
public abstract class SimpleForm implements Form {
	
	private Map<Class, FieldConverter> fieldConverters = createDefaultFieldConverters();
	
	private Component formView;

	private Map<String, Object> idMap;

	private SwingEngine swingEngine;
	
	protected SimpleForm(String formViewPath) {
		setSwingEngine(new JEditSwingEngine(this));
		URL formViewUrl = getClass().getResource(formViewPath);
		Container formView = null;
		try {
			formView = getSwingEngine().render(formViewUrl);
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Error rendering option pane xml file.", e);
		}
		setFormView(formView);
		setIdMap(getSwingEngine().getIdMap());
	}
	
	protected Object getElementById(String id) {
		return getIdMap().get(id);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.townsfolkdesigns.common.ui.Form#getFormView()
	 */
	public Component getFormView() {
		return formView;
	}

	public SwingEngine getSwingEngine() {
		return swingEngine;
	}
/*
	protected void bindField(Field field, Object fieldElement) {
		Class fieldType = field.getType();
		FieldConverter converter = getFieldConverter(fieldType);
		if(converter != null) {
			field.set(this, converter.convertComponent(component));
		}
		if(field.getType().isPrimitive()) {
			bindPrimitive(field, component);
		} else {
			bindSynthetic(field, component);
		}
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
		if (Modifier.isPrivate(field.getModifiers())) {
			// use accessor method instead of field.
		} else {
			// use field directly.
			try {
				field.set(this, fieldValue);
			} catch (Exception e) {
				Log.log(Log.ERROR, this, "Error binding field - name: " + fieldName, e);
			}
		}
	}

	protected void bindFields() {
		Field[] declaredFields = getClass().getDeclaredFields();
		// Field[] publicAndInheritedFields = getClass().getFields();
		String fieldName = null;
		Object fieldElement = null;
		// loop through the declared fields first.
		for (Field field : declaredFields) {
			fieldName = field.getName();
			fieldElement = getIdMap().get(fieldName);
			if (fieldElement != null) {
				bindField(field, fieldElement);
			} else {
				Log.log(Log.WARNING, this, "Couldn't find element for field - name: " + fieldName);
			}
		}
	}

	protected void bindPrimitive(Field field, Component component) {
		Class fieldType = field.getType();
		Object fieldValue = getFieldValue(field.getName(), component);
		try {
			field.set(this, fieldValue);
		} catch(Exception e) {
			Log.log(Log.ERROR, this, "Error binding field - name: " + field.getName() + " | value: " + fieldValue, e);
		}
   }

	protected void bindSynthetic(Field field, Component component) {
		Class fieldType = field.getType();
		Object fieldValue = getFieldValue(field.getName(), component);
		try {
			field.set(this, fieldValue);
		} catch(Exception e) {
			Log.log(Log.ERROR, this, "Error binding field - name: " + field.getName() + " | value: " + fieldValue, e);
		}
   }
*/
	protected void cancelAction() {
		doCancel();
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

	protected FieldConverter getFieldConverter(Class clazz) {
		return fieldConverters.get(clazz);
	}

	protected Object getFieldValue(String fieldName, Component component) {
		Object fieldValue = null;
		if(component instanceof JTextComponent) {
			fieldValue = getTextFieldValue(fieldName);
		} else if (component instanceof JCheckBox) {
			fieldValue = getCheckBoxValue(fieldName);
		}
		return fieldValue;
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

	protected void setFieldConverter(Class clazz, FieldConverter converter) {
		fieldConverters.put(clazz, converter);
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

	protected void submitAction() {
		//bindFields();
		doSubmit();
	}

	private Map<Class, FieldConverter> createDefaultFieldConverters() {
	   Map<Class, FieldConverter> fieldConverters = new ConcurrentHashMap<Class, FieldConverter>();
	   return fieldConverters;
   }

	private void setSwingEngine(SwingEngine swingEngine) {
		this.swingEngine = swingEngine;
	}

	/**
    * @return the elements with IDs.
    */
   protected Map<String, Object> getIdMap() {
   	return idMap;
   }

	/**
    * @param idElements the idElements to set
    */
   private void setIdMap(Map<String, Object> idElements) {
   	this.idMap = idElements;
   }

}
