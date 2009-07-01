package com.addictedtor.infonode.perspective.exporter;

import java.io.Writer;

/**
 * Interface used to export a Docking window layout to xml
 */
public interface Exporter {

	/**
	 * Writes the layout of a docking window
	 * 
	 * @param the
	 *            stream to write to
	 */
	public abstract void save(Writer out);
}
