/*
 * TrangTranslator.java
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
 * This class has been inspired by 
 * jing-trang-20090818/mod/trang/src/main/com/thaiopensource/relaxng/translate/Driver.java
 */
package xml.translate;

// {{{ Imports
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFailedException;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.input.MultiInputFormat;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputFailedException;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.xml.sax.SAX;


import com.thaiopensource.relaxng.translate.Formats;

import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.GUIUtilities;

import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;

// }}}

public class TrangTranslator
{
	private static DefaultErrorSource errorSource;
	
	static final String[] inputTypes = new String[]{"rng","rnc","dtd","xml"};
	static final String[] outputTypes = new String[]{"rng","rnc","dtd","xsd"};
	
	
	public static void stop()
	{
		if(errorSource != null)
		{
			ErrorSource.unregisterErrorSource(errorSource);
			errorSource = null;
		}
	}
	
	private static void initErrorSource()
	{
		errorSource = new DefaultErrorSource("xml.TrangTranslator");
		ErrorSource.registerErrorSource(errorSource);
	}
	
	
	public static String guessInputType(Buffer buffer){

		String input = buffer.getPath();
		String extension = null;
		if(input.length()>0){
			extension = input.substring(input.length() - 3);
		}

		String inputType;

		if(Arrays.asList(inputTypes).contains(extension))
		{
			inputType = extension;
		}
		else
		{
			inputType = buffer.getMode().getName();
			if(!Arrays.asList(inputTypes).contains(inputType))
			{
				inputType = null;
			}
		}
		return inputType;
	}
	
	public static void translateCurrentBuffer(View view, Buffer buffer, String outputType)
	{
		String inputType;
		
		String input = buffer.getPath();
		
		// infer the input type
		inputType = guessInputType(buffer);
		
		// not guessed => ask the user
		if(inputType == null)
		{
			int res = GUIUtilities.listConfirm(
				  view
				, "xml.translate.choose-input-type"
				, new String[]{}
				, inputTypes);
			
			if(res >=0 && res < inputTypes.length)
			{
				inputType = inputTypes[res];
			}
			else
			{
				return;
			}
		}
		
		List<String> inputs = Collections.singletonList(input);
		
		// infer the output
		String output;
		if(input.endsWith(inputType)){
			output = input.replaceAll(inputType+"$",outputType);
		}else{
			output = input + "." + outputType;
		}
		
		// translate
		translate(view, inputType, inputs, Collections.<String>emptyList(), outputType, output, Collections.<String>emptyList());
	}
	
	public static void translate(View view, String inputType, List<String> inputs, List<String> inputParams, String outputType, String outputFilename, List<String> outputParams)
	{
		if(inputs.isEmpty())throw new IllegalArgumentException("must provide at least one input");

		String mainInput = inputs.get(0);
		
		if(errorSource == null)initErrorSource();
		ErrorHandler eh = new xml.parser.ErrorListErrorHandler(errorSource,mainInput);
		
		if (inputType == null) {
			inputType = MiscUtilities.getFileExtension(mainInput);
			if (inputType.length() > 0)
				inputType = inputType.substring(1);
		}
		
		final InputFormat inputFormat;
		if (inputType.equalsIgnoreCase("dtd")){
			inputFormat = new BufferDtdInputFormat();
		}else if(inputType.equalsIgnoreCase("rng")){
			inputFormat = new BufferSAXParseInputFormat();
		}else if(inputType.equalsIgnoreCase("rnc")){
			inputFormat = new BufferCompactParseInputFormat();
		}else{
			inputFormat = Formats.createInputFormat(inputType);
		}
		
		if (inputFormat == null) {
			throw new IllegalArgumentException("unsupported input format : "+inputType);
		}
		
		String ext = MiscUtilities.getFileExtension(outputFilename);
		if (outputType == null) {
			outputType = ext;
			if (outputType.length() > 0)
				outputType = outputType.substring(1);
		}
		final OutputFormat outputFormat = Formats.createOutputFormat(outputType);
		if (outputFormat == null) {
			throw new IllegalArgumentException("unsupported output format : "+outputType);
		}
		
		Resolver resolver = new EntityResolverWrapper(xml.Resolver.instance(),true);
		
		String[] inputParamArray = inputParams.toArray(new String[inputParams.size()]);
		outputType = outputType.toLowerCase();
		SchemaCollection sc;
		try
		{
			if (inputs.size() > 1) {
				if (!(inputFormat instanceof MultiInputFormat)) {
					throw new IllegalArgumentException("Only XML input type can handle multiple inputs !");
				}
				
				String[] uris = new String[inputs.size()];
				for (int i = 0; i < uris.length; i++)
				{
					uris[i] = xml.PathUtilities.pathToURL(inputs.get(i));
				}
				sc = ((MultiInputFormat)inputFormat).load(uris, inputParamArray, outputType, eh, resolver);
			}
			else
			{
				sc = inputFormat.load(xml.PathUtilities.pathToURL(mainInput), inputParamArray, outputType, eh, resolver);
			}
			
			if (ext.length() == 0) ext = outputType;
			
			BuffersOutputDirectory od = new BuffersOutputDirectory(view, sc.getMainUri(), outputFilename);
			outputFormat.output(sc, od, outputParams.toArray(new String[outputParams.size()]), inputType.toLowerCase(), eh);
			
			String done = jEdit.getProperty("xml.translate.done.message",new String[]{String.valueOf(od.getOutputCount())});
			Log.log(Log.MESSAGE,TrangTranslator.class,done);
			GUIUtilities.message(
				  view
				, "xml.translate.done"
				, new String[]{String.valueOf(od.getOutputCount())});
		}
		catch (OutputFailedException e)
		{
			GUIUtilities.error(view,"xml.translate.failed",new String[]{e.toString()});
			e.printStackTrace();
		}
		catch (InputFailedException e)
		{
			GUIUtilities.error(view,"xml.translate.failed",new String[]{e.toString()});
			e.printStackTrace();
		}
		catch (InvalidParamsException e)
		{
			GUIUtilities.error(view,"xml.translate.failed",new String[]{e.toString()});
			e.printStackTrace();
		}
		catch (IOException e)
		{
			GUIUtilities.error(view,"xml.translate.failed",new String[]{e.toString()});
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			GUIUtilities.error(view,"xml.translate.failed",new String[]{e.toString()});
			e.printStackTrace();
		}
	}
	
}
