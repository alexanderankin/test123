/*
 * XmlPlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

//{{{ Imports
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

import sidekick.SideKickPlugin;
import xml.parser.*;
import java.io.File;
import xml.parser.SchemaMapping;
import java.io.IOException;
//}}}

public class XmlPlugin extends EBPlugin
{
	public static final String SCHEMA_MAPPING_PROP = "xml.schema-mapping";
	//{{{ start() method
	public void start()
	{
		System.setProperty("jaxp.debug","1");
	
		/*System.setProperty("javax.xml.parsers.SAXParserFactory",
			"org.apache.xerces.jaxp.SAXParserFactoryImpl");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
			"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");*/
		Resolver.instance().init();
		Resolver.instance().propertiesChanged();

		XmlActions.propertiesChanged();

		tagMouseHandler = new TagMouseHandler();

		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				panes[i].getTextArea().getPainter()
					.addMouseListener(
					tagMouseHandler);
			}
			view = view.getNext();
		}

		File f = getSchemaMappingFile(view);
		if(f==null)jEdit.unsetProperty(SCHEMA_MAPPING_PROP);
		else
		{
			try
			{
				jEdit.setProperty(SCHEMA_MAPPING_PROP,f.toURL().toString());
			}
			catch(java.net.MalformedURLException mfe)
			{
				Log.log(Log.ERROR,XmlPlugin.class,mfe);
			}
		}

	} //}}}

	//{{{ getSchemaMappingFile() method
	/**
	 * Finds (and creates if needed) the global schema mapping (schemas.xml)
	 * file in the settings directory, for reading or overwriting it.
	 *
	 * @return global schema mappings file (garanteed to exist) or null if no settings are available
	 */
	public static File getSchemaMappingFile(View view){
		File home = EditPlugin.getPluginHome(XmlPlugin.class);
		if(home == null)
		{
			return null;
		}
		else if(!home.exists())
		{
			Log.log(Log.DEBUG,XmlPlugin.class, "creating settings directory");
			try
			{
				boolean created = home.mkdirs();
				if(!created)
				{
					GUIUtilities.error( view, "unable to create settings directory: "+home,null);
					return null;
				}
			}
			catch(SecurityException se)
			{
				GUIUtilities.error( view, "unable to create settings directory (security exception): "+home,null);
			}
		}
		File schemas = new File(home,SchemaMapping.SCHEMAS_FILE);
		// create an empty mapping file in settings directory
		if(!schemas.exists()){
			SchemaMapping map = SchemaMapping.fromDocument(
				XmlPlugin.class.getClassLoader().getResource("xml/dtds/schemas.xml").toString());

			try{
				map.toDocument(schemas.getPath());
			}catch(IOException ioe){
				Log.log(Log.ERROR,XmlPlugin.class,"Unable to save default RelaxNG mappings");
				Log.log(Log.ERROR,XmlPlugin.class,ioe);
				return null;
			}
		}
		return schemas;
	}
	//}}}

	//{{{ stop() method
	public void stop()
	{
		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				panes[i].getTextArea().getPainter()
					.removeMouseListener(
					tagMouseHandler);
			}
			view = view.getNext();
		}

		Resolver.instance().save();

		Resolver.instance().uninit();
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		//{{{ EditPaneUpdate
		if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)msg;
			EditPane editPane = epu.getEditPane();

			if(epu.getWhat() == EditPaneUpdate.CREATED)
			{
				editPane.getTextArea().getPainter().addMouseListener(
					tagMouseHandler);
			}
			else if(epu.getWhat() == EditPaneUpdate.DESTROYED)
			{
				editPane.getTextArea().getPainter().removeMouseListener(
					tagMouseHandler);
			}
		} //}}}
		//{{{ PropertiesChanged
		else if(msg instanceof PropertiesChanged)
		{
			XmlActions.propertiesChanged();
			Resolver.instance().propertiesChanged();
		} //}}}
	} //}}}

	//{{{ isDelegated() method
	/**
	 * Returns if the caret is inside a delegated region in the
	 * specified text area. This is used in a few places.
	 */
	public static boolean isDelegated(JEditTextArea textArea)
	{
		return isDelegated((TextArea)textArea);
	}

	public static boolean isDelegated(TextArea textArea) {
		JEditBuffer buffer = textArea.getBuffer();
		ParserRuleSet rules = buffer.getRuleSetAtOffset(
			textArea.getCaretPosition());

		String rulesetName = rules.getSetName();
		String modeName = rules.getModeName();

		// Am I an idiot?
		if(rulesetName != null && (rulesetName.startsWith("PHP")
			|| rulesetName.equals("CDATA")))
			return true;

		return jEdit.getProperty("mode." + modeName + "."
			+ SideKickPlugin.PARSER_PROPERTY) == null;
	} //}}}

	//{{{ uriToFile() method
    /**
     * This method is hacky and should be rewritten to use
     * java.net.URI. In particular, it does not work if
     * the file:// URI points to a samba share.
     *
     */
	public static String uriToFile(String uri)
	{
		if (uri.startsWith("http:/")) try {
			// TODO: document the usage of uriToFile in Resolver
			//       and gain confidence that it doesn't loop
			String result = Resolver.instance().resolvePublicOrSystem(uri,false);
			if (result != null) return result;
		}
		catch (Exception e) {
			Log.log(Log.ERROR, XmlPlugin.class, e.getMessage());
		}


		if(uri.startsWith("file:/"))
		{
			int start;
			if(uri.startsWith("file:///") && OperatingSystem.isDOSDerived())
				start = 8;
			else if(uri.startsWith("file://"))
				start = 7;
			else
				start = 5;

			StringBuffer buf = new StringBuffer();
			for(int i = start; i < uri.length(); i++)
			{
				char ch = uri.charAt(i);
				if(ch == '/')
					buf.append(java.io.File.separatorChar);
				else if(ch == '%')
				{
					String str = uri.substring(i + 1,i + 3);
					buf.append((char)Integer.parseInt(str,16));
					i += 2;
				}
				else
					buf.append(ch);
			}
			uri = buf.toString();
		}
		return uri;
	} //}}}

	private TagMouseHandler tagMouseHandler;

	//{{{ TagMouseHandler class
	/**
	 * Displays the "Edit Tag Dialog" when a tag is selected from the completion
	 * dialog with the mouse.
	 */
	static class TagMouseHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent evt)
		{
			if(evt.getClickCount() == 2
				&& ((OperatingSystem.isMacOS() && evt.isMetaDown())
				|| (!OperatingSystem.isMacOS() && evt.isControlDown())))
			{
				final View view = GUIUtilities.getView(
					(Component)evt.getSource());
				if(!(SideKickPlugin.getParserForBuffer(view.getBuffer())
					instanceof XmlParser))
					return;

				JEditTextArea textArea = view.getTextArea();
				textArea.setCaretPosition(textArea.xyToOffset(
					evt.getX(),evt.getY()));

				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						XmlActions.showEditTagDialog(view);
					}
				});
			}
		}
	} //}}}
}
