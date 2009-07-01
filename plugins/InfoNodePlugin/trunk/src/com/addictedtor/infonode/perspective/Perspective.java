package com.addictedtor.infonode.perspective;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.infonode.docking.RootWindow;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.addictedtor.infonode.InfoNodeDockingLayout;
import com.addictedtor.infonode.InfoNodeDockingWindowManager;
import com.addictedtor.infonode.perspective.exporter.ExporterProvider;
import com.addictedtor.infonode.perspective.importer.ImporterProvider;
import com.addictedtor.infonode.perspective.importer.Snapshot;

/**
 * Loading and saving perspectives based on the InfoNode tree of a root window
 * into XML files
 */
public class Perspective {
	
	private InfoNodeDockingWindowManager wm ;
	private RootWindow root; 
	
	public Perspective(InfoNodeDockingWindowManager wm) {
		this.wm = wm; 
		this.root = wm.getRootWindow() ; 
	}

	/**
	 * Writes an XML definition of the perspective of the root window to the
	 * writer
	 * 
	 * @param out
	 *            where to write the xml content
	 * @param root
	 *            the RootWindow from which to extract the perspective
	 */
	public void save(Writer out) {
		ExporterProvider.getExporter(root).save(out);
	}

	/**
	 * Writes an XML definition of the perspective to the file
	 * 
	 * @param file
	 *            file to write to
	 * @param root
	 *            RootWindow from which to extract the perspective
	 */
	public void save(File file) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(file)));
			save(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the perspective of the current root window to the file
	 * 
	 * @param file
	 *            the file to write to
	 */
	public void save(String file) {
		save(new File(file));
	}

	/**
	 * Loads the perspective contained in the xml file
	 * 
	 * @param file
	 *            the xml file containing the perspective
	 */
	public void load(File file) {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		domFactory.setValidating(false);
		
		try {
			DocumentBuilder documentBuilder = domFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			Node node = document.getFirstChild();
			Snapshot snap = new Snapshot( wm ) ;
			ImporterProvider.getImporter(node, snap).parse(node);
		} catch( Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * Loads the perspective name from the given plugin.
	 * 
	  * @param name name of the perspective
	 */
	public void load(String name) {
		load(getPerspective(name));
	}

	public File getPerspective(String layoutName) {
		return new File( InfoNodeDockingLayout.getLayoutDir() , layoutName ) ;
	}


}
