/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2008 Eric Le Lay
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
package cswilly.jeditPlugins.spell;

 
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Map;
import java.util.HashMap;


/**
 * Class to store string properties and associate listeners to them
 */ 
public class PropertyStore extends PropertyChangeSupport{
	private Map<String,String> values;
	PropertyStore(Object source){
		super(source);
		values = new HashMap<String,String>();
	}
	
	public void put(String name,String value){
		if(name == null)throw new IllegalArgumentException("property name shouldn't be null");
		
		String oldValue = values.get(name);
		
		/* ignore same value */
		if(oldValue == null){
				if(value == null)return;
		}else if(oldValue.equals(value))return;
		
		values.put(name,value);
		firePropertyChange(name,oldValue,value);
	}
	
	public String get(String name){
		return values.get(name);
	}
}
