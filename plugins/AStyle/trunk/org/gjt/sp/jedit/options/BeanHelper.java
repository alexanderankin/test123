/*
 * BeanHelper.java - a helper class to create and initialize beans
 * Copyright (C) 2001 Dirk Moebius
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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
 */


package org.gjt.sp.jedit.options;


import java.lang.reflect.Method;
import java.beans.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;


/**
 * A utility class to save, load and initialize the properties of a bean
 * to and from jEdit properties.
 *
 * @author Dirk Moebius (<a href="mailto:dmoebius@gmx.net">dmoebius@gmx.net</a>)
 *
 */
public class BeanHelper {

	public BeanHelper(String optionsKey, String beanClassName) {
		this(optionsKey, beanClassName, null);
	}


	public BeanHelper(String optionsKey, String beanClassName, ClassLoader beanClassLoader) {
		this.optionsKey = optionsKey;
		this.beanClassName = beanClassName;
		this.beanClassLoader = beanClassLoader;
	}


	/**
	 * Tries to instantiate a new bean object and to initialize the bean
	 * with the values stored in the jEdit properties, calling
	 * <code>initBean(Object)</code> on the newly created instance.
	 *
	 * @return a new bean object instance, or null, if an error occurred.
	 * @see #initBean(java.lang.Object)
	 */
	public Object createBean() {
		try {
			Object beanObject = Beans.instantiate(beanClassLoader, beanClassName);
			initBean(beanObject);
			return beanObject;
		}
		catch (Exception ex) {
			Log.log(Log.ERROR, this, ex);
		}
		return null;
	}


	/**
	 * Sets the properties of the bean object instance to values stored in
	 * the jEdit properties, using the <code>setXXX()</code> methods of the
	 * bean. The values are retrieve from jEdit using the keys
	 * <code>optionsKey.<i>propertyName</i></code>, where
	 * <code>optionsKey</code> is the prefix you specified in the constructor
	 * of <code>BeanHelper</code>.
	 */
	public void initBean(Object beanObject) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(beanObject.getClass());

			if (beanInfo == null)    // cannot work without a BeanInfo
				throw new Exception("BeanInfo not found for class " + beanClassName + "! Maybe this is not a bean?!?");

			// Get all property descriptions:
			PropertyDescriptor[] beanProperties = beanInfo.getPropertyDescriptors();
			if (beanProperties == null) {
				Log.log(Log.WARNING, this, "no bean properties found for bean " + beanClassName);
				return;
			}

			initBeanPropertyEditors(beanProperties);

			// Traverse all properties:
			for (int i = 0; i < beanProperties.length; i++) {
				PropertyDescriptor pd = beanProperties[i];
				String name = pd.getName();

				// Get value of property from jEdit properties and set it in the beanObject:
				String valueText = jEdit.getProperty(optionsKey + "." + name);
				if (valueText == null)
					continue;

				PropertyEditor propertyEditor = (PropertyEditor) beanPropertyEditors.get(name);
				if (propertyEditor == null)
					continue; // can't set jEdit property value without PropertyEditor

				Method method = pd.getWriteMethod();
				if (method == null) {
					Log.log(Log.WARNING, this, "property " + name + ": no write method to set property value");
					continue;
				}

				// Use the PropertyEditor to cast the String valueText into a BeanObject value:
				propertyEditor.setAsText(valueText);
				Object value = propertyEditor.getValue();

				// Set new value:
				try {
					method.invoke(beanObject, new Object[] { value });
					Log.log(Log.DEBUG, this, "set property " + name + "=" + valueText + " (value=" + value + ")");
				}
				catch (Exception ex) {
					Log.log(Log.ERROR, this, "property " + name + ": could not set value " + valueText + ", got exception:");
					Log.log(Log.ERROR, this, ex);
				}
			} // for all properties
		}
		catch (Exception ex) {
			Log.log(Log.ERROR, this, ex);
		}
	}


	/**
	 * Get bean property editors for all properties of the bean class.
	 *
	 * The keys of the returned Hashtable are the property names and the
	 * associated values are the property editors, which are instances of
	 * <code>java.beans.PropertyEditor</code>.
	 *
	 * Once created, the Hashtable is cached in a private static member
	 * field. It will not be changed once it is created.
	 *
	 * Properties listed in the special jEdit property
	 * <code>options.<i>optionsKey</i>.hidden.properties</code> are omitted,
	 * ie. no PropertyEditor is created for them.
	 *
	 * @return a hashtable that associates bean property names with
	 *         <code>java.beans.PropertyEditor</code>s.
	 */
	public Hashtable getPropertyEditors(PropertyDescriptor[] beanProperties) throws Exception {
		initBeanPropertyEditors(beanProperties);
		return beanPropertyEditors;
	}


	private void initBeanPropertyEditors(PropertyDescriptor[] beanProperties) throws Exception {
		if (beanPropertyEditors != null)
			return; // already initialized

		beanPropertyEditors = new Hashtable();

		// Get the set of properties that should not be displayed.
		// This is set by a property "options.<optionskey>.hidden.properties",
		// e.g. "options.astyleplugin.hidden.properties=useTabs,tabIndentation".
		Vector hiddenProperties = new Vector();
		String hidden = jEdit.getProperty("options." + optionsKey + ".hidden.properties");
		if (hidden != null) {
			StringTokenizer st = new StringTokenizer(hidden, " ,;:|\t\n");
			while (st.hasMoreTokens())
				hiddenProperties.addElement(st.nextToken());
		}

		for (int i = 0; i < beanProperties.length; i++) {
			PropertyDescriptor pd = beanProperties[i];
			String name = pd.getName();

			if (pd.isHidden() || hiddenProperties.contains(name))
				continue; // property shall not be displayed, therefore there is no jEdit property for it

			Class propertyClass = pd.getPropertyType();
			if (propertyClass == null)
				continue; // this is an indexed property that doesn't allow unindexed access.

			// Try to find user defined property editor:
			PropertyEditor propertyEditor;
			Class propertyEditorClass = pd.getPropertyEditorClass();
			if (propertyEditorClass != null) {
				// PropertyEditor has been explicitly set:
				if (!PropertyEditor.class.isAssignableFrom(propertyEditorClass)) {
					// Error: this is not a property editor:
					Log.log(Log.ERROR, this, "property " + name + ": property editor is not a java.beans.PropertyEditor");
					continue;
				}
				propertyEditor = (PropertyEditor) propertyEditorClass.newInstance();
			} else {
				// No own property editor; find a default property editor:
				propertyEditor = PropertyEditorManager.findEditor(propertyClass);
				if (propertyEditor == null) {
					Log.log(Log.WARNING, this, "property " + name + ": no property editor found for type " + propertyClass);
					continue;
				}
			}

			beanPropertyEditors.put(name, propertyEditor);
		}
	}


	private String optionsKey;
	private String beanClassName;
	private ClassLoader beanClassLoader;

	private static Hashtable beanPropertyEditors;

}

