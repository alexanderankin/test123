/*
* BuffersOutputDirectory.java - Trang output to jEdit buffers
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2010 Eric Le Lay
*
* The XML plugin is licensed under the GNU General Public License, with
* the following exception:
*
* "Permission is granted to link this code with software released under
* the Apache license version 1.1, for example used by the Xerces XML
* parser package."
*
*/
package xml.translate;


// {{{ Imports
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.io.VFSManager;

import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.xml.out.CharRepertoire;
// }}}

/**
 * channels Trang output to jEdit buffers.
 * For each file Trang wants to write to, a jEdit buffer is opened.
 * FIXME: If the file is read-only, writing to it will fail.
 */
public class BuffersOutputDirectory implements OutputDirectory
{
	private View view;
	
	// maps URIs to filenames
	private final Map<String, String> uriMap;
	private final String mainInputExtension;
	private final String outputExtension;
	private final String outputDirectory;
	private int outputCount;

	private int indent;
	private int lineLength;
	
	public BuffersOutputDirectory(View view, String mainInput, String mainOutput){
		this.view = view;
		this.uriMap = new HashMap<String, String>();
		
		this.mainInputExtension = MiscUtilities.getFileExtension(mainInput);
		this.outputDirectory = MiscUtilities.getParentOfPath(mainOutput);
		this.outputExtension = MiscUtilities.getFileExtension(mainOutput);
		
		uriMap.put(mainInput,MiscUtilities.getFileName(mainOutput));
		
		// init output properties
		Mode outputMode = ModeProvider.instance.getModeForFile(MiscUtilities.getFileName(mainOutput),"");
		if(outputMode == null)
		{
			outputMode = jEdit.getMode("text");
		}
		indent = ((Integer)outputMode.getProperty("indentSize")).intValue();
		lineLength = ((Integer)outputMode.getProperty("maxLineLen")).intValue();
	}
	
	public int getIndent()
	{
		Log.log(Log.DEBUG,BuffersOutputDirectory.class,"getIndent()");
		return indent;
	}
	
	public int getLineLength(){
		Log.log(Log.DEBUG,BuffersOutputDirectory.class,"getLineLength()");
		return lineLength;
	}
	
	public String getLineSeparator(){
		Log.log(Log.DEBUG,BuffersOutputDirectory.class,"getLineSeparator()");
		return "\n";
	}
	
	public OutputDirectory.Stream open(String sourceUri, String ignoredEncoding)
	throws UnsupportedEncodingException
	{
		Log.log(Log.DEBUG,BuffersOutputDirectory.class,"open("+sourceUri+","+ignoredEncoding+")");

		String newSourceURI = MiscUtilities.constructPath(
									null
									,outputDirectory
									,mapFilename(sourceUri)
			);
		Log.log(Log.DEBUG,BuffersOutputDirectory.class,"===>"+newSourceURI);

		Buffer b = jEdit.openFile(view, newSourceURI);
		BufferWriter writer = new BufferWriter(b);
		String encoding = (String)b.getProperty(Buffer.ENCODING);
		CharRepertoire repertoire = CharRepertoire.getInstance(encoding);
		
		outputCount++;
		
		return new OutputDirectory.Stream(writer,encoding, repertoire);
	}
	
	public void setEncoding(String encoding){
		Log.log(Log.DEBUG,BuffersOutputDirectory.class,"setEncoding("+encoding+")");
		// no-op
	}
	
	public void setIndent(int indent){
		Log.log(Log.DEBUG,BuffersOutputDirectory.class,"setIndent("+indent+")");
		// no-op
	}
	
	public String reference(String fromSourceUri, String toSourceUri) {
		Log.log(Log.DEBUG,BuffersOutputDirectory.class,"reference("+fromSourceUri+","+toSourceUri+")");
		Log.log(Log.DEBUG,BuffersOutputDirectory.class,"===>"+mapFilename(toSourceUri)+")");
		return mapFilename(toSourceUri);
	}
	
	public int getOutputCount(){
		return outputCount;
	}
	
	// {{{ private members
	private String mapFilename(String sourceUri) {
		String filename = uriMap.get(sourceUri);
		if (filename == null) {
			filename = chooseFilename(sourceUri);
			uriMap.put(sourceUri, filename);
		}
		return filename;
	}
	
	private String chooseFilename(String sourceUri) {
		String filename = MiscUtilities.getFileName(sourceUri);
		String base;
		if (filename.endsWith(mainInputExtension))
			base = filename.substring(0, filename.length() - mainInputExtension.length());
		else
			base = filename;
		filename = base + outputExtension;
		for (int i = 1; uriMap.containsValue(filename); i++)
			filename = base + Integer.toString(i) + outputExtension;
		return filename;
	}
	// }}}
	
	// {{{ BufferWriter class
	/**
	* writes to a Buffer upon close()
	*/
	public static class BufferWriter extends StringWriter
	{
		private Buffer targetBuffer;
		
		public BufferWriter(Buffer targetBuffer)
		{
			super();
			this.targetBuffer = targetBuffer;
		}
		
		public void close() throws IOException
		{
			super.close();
			VFSManager.waitForRequests();
			targetBuffer.beginCompoundEdit();
			targetBuffer.remove(0,targetBuffer.getLength());
			targetBuffer.insert(0,this.toString());
			targetBuffer.endCompoundEdit();
		}
	}
	// }}}
}
