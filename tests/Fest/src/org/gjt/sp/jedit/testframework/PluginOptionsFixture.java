/*
 * PluginOptionsFixture.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009-2012 Eric Le Lay
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
package org.gjt.sp.jedit.testframework;

// {{{ jUnit imports 
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.core.matcher.*;
import org.fest.swing.finder.*;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;
import org.fest.swing.exception.WaitTimedOutError;

import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;
import org.gjt.sp.jedit.testframework.TestUtils;


// }}}

import java.io.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import java.awt.Dialog;

import static org.gjt.sp.jedit.testframework.TestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;

/**
 * simple wrapper around Plugin Options.
 * OK() and Apply() wait until the properties have propagated.
 * $Id$
 */
public class PluginOptionsFixture extends DialogFixture{
	
	PluginOptionsFixture(Robot robot, Dialog target){
		super(robot,target);
	}
	
    /**
     * @param path path in the option panes tree (eg. XML/XML)
     * @param name internal name of the option pane (eg. xml.general)
     * @return an option pane, ensuring that it's visible
     */
    public JPanelFixture optionPane(String path, String name){
		TestUtils.selectPath(this.tree(),"/"+path);

		JPanelFixture pane = this.panel(name);
		pane.requireVisible();
		return pane;
    }
    
    /**
     * click OK and wait for the PropertiesChanged message 
     */
    public void OK(){
    	Runnable clickT = new Runnable(){
    		public void run(){
    			button(JButtonMatcher.withText(jEdit.getProperty("common.ok"))).click();
    		}
    	};
    	
    	doInBetween(clickT, propertiesChanged, 10000);
    }
    
    /**
     * click Cancel 
     */
    public void Cancel(){
		button(JButtonMatcher.withText(jEdit.getProperty("common.cancel"))).click();
    }

	/**
     * click Apply and wait for the PropertiesChanged message 
     */
    public void Apply(){
    	Runnable clickT = new Runnable(){
    		public void run(){
    			button(JButtonMatcher.withText(jEdit.getProperty("common.apply"))).click();
    		}
    	};
    	
    	doInBetween(clickT, propertiesChanged, 10000);
    }

    private EBCondition propertiesChanged  = 
		new EBCondition(){
				public boolean matches(EBMessage msg){
					return msg instanceof PropertiesChanged;
				}
	};
}
