/*
 * OptionsFixture.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009-2010 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
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
 * simple wrapper arround Plugin Options.
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
		TestUtils.selectPath(this.tree(),"Plugins/"+path);

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
    			button(JButtonMatcher.withText("OK")).click();
    		}
    	};
    	
    	doInBetween(clickT, propertiesChanged, 10000);
    }
    
    /**
     * click Cancel 
     */
    public void Cancel(){
		button(JButtonMatcher.withText("Cancel")).click();
    }

	/**
     * click Apply and wait for the PropertiesChanged message 
     */
    public void Apply(){
    	Runnable clickT = new Runnable(){
    		public void run(){
    			button(JButtonMatcher.withText("Apply")).click();
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
