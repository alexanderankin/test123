package com.addictedtor.infonode.perspective.exporter;

import java.io.Writer;

import net.infonode.docking.SplitWindow;

/**
 * Exporter for SplitWindow. Exports the location of the divider and the
 * orientation of the split
 */
public class SplitWindowExporter extends DefaultExporter implements Exporter {

	/**
	 * Constructor for the SplitWindowExporter
	 * 
	 * @param window
	 *            The SplitWindow to stream to XML
	 */
	public SplitWindowExporter(SplitWindow window) {
		super(window);
		setAttribute("dividerLocation", "" + window.getDividerLocation());
		setAttribute("horizontal", "" + window.isHorizontal());
	}

	/**
	 * Writes the left window layout, and then the right window layout
	 * 
	 * @param out
	 *            the stream where to write
	 */
	@Override
	public void content(Writer out) {
		ExporterProvider.getExporter(((SplitWindow) window).getLeftWindow())
				.save(out);
		ExporterProvider.getExporter(((SplitWindow) window).getRightWindow())
				.save(out);
	}

}
