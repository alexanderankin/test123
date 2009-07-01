package com.addictedtor.infonode.perspective.exporter;

import java.io.Writer;

/**
 * dummy exporter, does not export anything
 */
public class DummyExporter implements Exporter {

	/**
	 * Constructor for the Dummy Exporter
	 */
	public DummyExporter() {
	}

	/**
	 * Writes nothing to the stream
	 * 
	 * @param out
	 *            the stream to write nothing to
	 */
	@Override
	public void save(Writer out) {
	}

}
