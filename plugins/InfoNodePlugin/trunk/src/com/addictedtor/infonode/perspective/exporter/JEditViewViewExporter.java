package com.addictedtor.infonode.perspective.exporter;

import com.addictedtor.infonode.JEditViewView ;

/**
 * Exporter for JEditView. At the moment, this class is a placeholder as it does
 * not do more than the DefaultExporter
 */
public class JEditViewViewExporter extends DefaultExporter implements Exporter {

	/**
	 * Constructor for the JEditViewExporter
	 * 
	 * @param window
	 *            The JEditView to stream to XML
	 */
	public JEditViewViewExporter(JEditViewView window) {
		super(window);
		setAttribute("title", window.getView().getName() );
	}
}
