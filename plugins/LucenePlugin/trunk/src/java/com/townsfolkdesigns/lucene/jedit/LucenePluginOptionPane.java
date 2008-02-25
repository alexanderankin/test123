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
package com.townsfolkdesigns.lucene.jedit;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.net.URL;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.util.Log;
import org.swixml.SwingEngine;

import com.townsfolkdesigns.swixml.jedit.JEditSwingEngine;

/**
 * @author elberry
 * 
 */
public class LucenePluginOptionPane extends AbstractOptionPane {

	private static final long serialVersionUID = -3394324042468241867L;
	private OptionsForm optionsForm;

	public LucenePluginOptionPane() {
		super("Lucene Plugin Option Pane");
		setOptionsForm(new OptionsForm());
	}

	public OptionsForm getOptionsForm() {
		return optionsForm;
	}

	public void setOptionsForm(OptionsForm optionsForm) {
		this.optionsForm = optionsForm;
	}

	@Override
	protected void _init() {
		SwingEngine swingEngine = new JEditSwingEngine(getOptionsForm());
		URL optionPaneUrl = getClass().getResource("/luceneplugin/OptionsPane.xml");
		Container optionPane = null;
		try {
			optionPane = swingEngine.render(optionPaneUrl);
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Error rendering option pane xml file.", e);
		}
		if (optionPane != null) {
			addComponent(optionPane, GridBagConstraints.BOTH);
		}
	}

	@Override
	protected void _save() {
		// TODO Auto-generated method stub
		super._save();
	}

}
