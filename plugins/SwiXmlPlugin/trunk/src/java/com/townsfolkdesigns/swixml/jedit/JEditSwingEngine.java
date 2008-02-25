/**
 * 
 */
package com.townsfolkdesigns.swixml.jedit;

import org.gjt.sp.jedit.gui.RolloverButton;
import org.swixml.SwingEngine;

/**
 * @author elberry
 * 
 */
public class JEditSwingEngine extends SwingEngine {

	/**
	 * 
	 */
	public JEditSwingEngine() {
		super();
		addJEditTags();
	}

	/**
	 * @param arg0
	 */
	public JEditSwingEngine(Object arg0) {
		super(arg0);
		addJEditTags();
	}

	/**
	 * @param arg0
	 */
	public JEditSwingEngine(String arg0) {
		super(arg0);
		addJEditTags();
	}

	private void addJEditTags() {
		// TODO: This might be a nice place to add some service integration. Allow
		// other plugins to add tags.
		this.getTaglib().registerTag("rolloverbutton", RolloverButton.class);
	}

}
