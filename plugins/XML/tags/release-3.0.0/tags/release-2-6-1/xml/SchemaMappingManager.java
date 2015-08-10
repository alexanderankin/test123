/*
 * SchemaMappingManager.java - select a schema for validation
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, Eric Le Lay
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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.gui.HistoryTextField;

import common.gui.OkCancelButtons;
import ise.java.awt.KappaLayout;

import xml.parser.SchemaMapping;
import static xml.PathUtilities.*;
//}}}

public final class SchemaMappingManager
{
	public static final String SCHEMA_MAPPING_PROP = "xml.schema-mapping";
	public static final String BUFFER_ENABLE_SCHEMA_MAPPING_PROP = "xml.enable-schema-mapping";
	public static final String ENABLE_SCHEMA_MAPPING_PROP = "buffer."+BUFFER_ENABLE_SCHEMA_MAPPING_PROP;
	public static final String BUFFER_SCHEMA_PROP = "xml.validation.schema";
	public static final String BUFFER_AUTO_SCHEMA_PROP = "xml.validation.schema-auto";
	
	private static final String BUILT_IN_SCHEMA = "xml/dtds/schemas.xml";
	
	// {{{ singleton constructor
	private SchemaMappingManager(){}
	// }}}
	
	//{{{ promptSchemaForBuffer() method
	/**
	 * - let the user choose a schema file from the VFSBrowser
	 * TODO: test with VFS
	 * TODO: clarify property on the buffer vs schema-mapping
	 */
	public static void promptSchemaForBuffer(View view, Buffer buffer)
	{
		String oldSchema = buffer.getStringProperty(BUFFER_SCHEMA_PROP);

		String specificSchema = null;
		SchemaMapping lMapping = null;
		SchemaMapping gMapping = null;
		
		boolean schemasEnabled = isSchemaMappingEnabled(buffer);
		
		// local schema-mapping
		if(schemasEnabled){
			specificSchema = MiscUtilities.constructPath(
				MiscUtilities.getParentOfPath(
					buffer.getPath()),SchemaMapping.SCHEMAS_FILE);
		
			lMapping = getLocalSchemaMapping(buffer);
			gMapping = getGlobalSchemaMapping();
		
			// always prefer the explicit schema
			String oldSchemaAuto = buffer.getStringProperty(BUFFER_AUTO_SCHEMA_PROP);
			if(oldSchema == null)oldSchema = oldSchemaAuto;
		}
		
		
		ChooseSchemaDialog choose = new ChooseSchemaDialog(view);
		
        if(choose.choose(buffer.getPath(),urlToPath(oldSchema), true))
        {
			String bufferURL = pathToURL(buffer.getPath());
			URI schemaURL = choose.schemaURL;

        	if(schemasEnabled)
        	{
				// no schemas.xml in the buffer's directory : will create one
				if(lMapping == null)
				{
					try
					{
						lMapping = new SchemaMapping(new URL(pathToURL(specificSchema)));
						if(gMapping != null)lMapping.ensureIncluded(gMapping);
					}
					catch(MalformedURLException ue)
					{
						Log.log(Log.ERROR,SchemaMappingManager.class,ue);
					}
				}
				
				
				SchemaMapping.URIResourceRule newRule = new SchemaMapping.URIResourceRule(null,bufferURL,schemaURL.toString(), false);
				
				lMapping.updateMapping(newRule);
				
				try
				{
					lMapping.toDocument(specificSchema);
				}
				catch(IOException ioe)
				{
					// if saving fails, try to output it in a buffer
					JOptionPane.showMessageDialog(
						view,
						jEdit.getProperty("xml-error-to-document.message",new Object[]{ioe.getClass(),ioe.getMessage()}),
						jEdit.getProperty("xml-error-to-document.title"),
						JOptionPane.ERROR_MESSAGE
						);
					Buffer b = jEdit.newFile(view,lMapping.getBaseURI().toString());
					b.insert(0,lMapping.toString());
					view.getEditPane().setBuffer(buffer);
				}
			}
			else
			{
				buffer.setProperty(BUFFER_SCHEMA_PROP,schemaURL);
			}
			
			// finally, use the new schema mapping
			view.getInputHandler().setRepeatCount(1);
			view.getInputHandler().invokeAction("sidekick-parse");
		}
	}
	//}}}

	// {{{ ChooseSchemaDialog class
	static class ChooseSchemaDialog extends EnhancedDialog
	{
		// {{{ private instance variables
		private JCheckBox relative_cb;
		private JTextField path;
		private JTextField buffer_path;
		private JTextField relative_path;
		private boolean valid;
		// }}}
		
		/** URI of the chosen schema */
		URI schemaURL = null;
		/** is schemaURL relative to the buffer */
		boolean relative = false;
		
		/** did the user click OK */
		boolean confirmed = false;
		
		
		ChooseSchemaDialog(final View view)
		{
			super(view, jEdit.getProperty("xml.choose-schema.title"),true);
			JPanel panel = new JPanel( new KappaLayout() );
			panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
			
			
			ActionListener relativize = new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					relativize();
				}
			};
			
			// reminder : buffer path
			JLabel buffer_label = new JLabel( jEdit.getProperty("xml.choose-schema.buffer-path") );
			buffer_path = new JTextField(40);
			buffer_path.setEditable(false);
			buffer_path.setName("buffer_path");
			
			// choose a path
			JLabel choose_label = new JLabel( jEdit.getProperty("xml.choose-schema.message") );
			path = new HistoryTextField("xml.choose-schema",true);
			path.setName("path");
			path.setText("");
			path.setColumns(40);
			path.addActionListener(relativize);
			
			JButton browse = new JButton( jEdit.getProperty("xml.choose-schema.browse") );
			browse.setMnemonic(KeyEvent.VK_B);
			browse.setName("browse");
			browse.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent ae )
					{
						String[] paths = GUIUtilities.showVFSFileDialog( view, path.getText(), VFSBrowser.OPEN_DIALOG, false );
						if ( paths != null && paths.length > 0 ) {
							path.setText( paths[0] );
							relativize();
						}
					}
				});
			
			// set a relative path
			relative_cb = new JCheckBox(jEdit.getProperty("xml.choose-schema.relative"));
			relative_cb.addActionListener(relativize);
			relative_cb.setName("relative");
			
			relative_path = new JTextField(40);
			relative_path.setEditable(false);
			relative_path.setName("relative_path");
			
			//LTTTTTTT
			//S
			//LLLLLLLL
			//TTTTTTTB
			//S
			//S
			//S
			//CCCCCC
			//TTTTTTT
			//S
			// OK CANCEL
			
			JPanel okCancel = new OkCancelButtons(this);
			
			// add the components to the option panel
			panel.add( "0, 0, 1, 1, W, w, 3", buffer_label );
			panel.add( "1, 0, 6, 1, W, w, 3", buffer_path );
			panel.add( "0, 1, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
			panel.add( "0, 2, 8, 1, W, w, 3", choose_label );
			panel.add( "0, 3, 7, 1, W, w, 3", path );
			panel.add( "7, 3, 1, 1, W, w, 3", browse );
			panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 33, true ) );
			panel.add( "0, 5, 6, 1, 0, w, 0", relative_cb );
			panel.add( "0, 6, 5, 1, W, w, 3", relative_path );
			panel.add( "0, 7, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
			panel.add( "0, 8, 8, 1, 0, w, 0", okCancel );
			
			setContentPane(panel);
			pack();
		}
		
		/**
		 * for unit tests, mainly
		 */
		void init(String buffer, String oldSchema, boolean relativeEnabled)
		{
			buffer_path.setText(buffer);
			if(oldSchema == null)
			{
				path.setText(MiscUtilities.getParentOfPath(buffer));
			}
			else
			{
				path.setText(oldSchema);
			}
			relative_cb.setSelected(false);
			relative_cb.setEnabled(relativeEnabled);
			relativize();
		}
		
		/**
		 * shows the dialog and returns true if the user confirmed
		 * @param	buffer		path to the xml document needing a schema
		 * @param	oldSchema	path to the schema currently assigned or null
		 * @param	relativeEnabled	is a relative path enabled
		 */
		public boolean choose(String buffer, String oldSchema, boolean relativeEnabled)
		{
			confirmed = false;
			init(buffer,oldSchema, relativeEnabled);
			setVisible(true);
			return confirmed;
		}
		
		/**
		 * refuse to close the dialog if there is an error
		 */
		public void ok()
		{
			relativize();
			if(valid)
			{
				confirmed = true;
				setVisible(false);
			}
		}
		
		public void cancel() 
		{
			confirmed = false;
			setVisible(false);
		}
		
		/**
		* transform the contents of path into a relative URL, if relative_cb is checked.
		* TODO: handle a relative URL entered by the user
		* TODO: relativize jar:file:...!etc URLs
		*/
		private void relativize()
		{
			relative = relative_cb.isSelected();
        	try
        	{
        		schemaURL = new URI(pathToURL(path.getText()));
				if(relative)
				{
					String bufferURL = pathToURL(MiscUtilities.getParentOfPath(buffer_path.getText()));
					schemaURL = new URI(bufferURL).relativize(schemaURL);
				}
        		path.setForeground(Color.BLACK);
        		valid = true;
        		relative_path.setText(schemaURL.toString());
        	}
        	catch(URISyntaxException ue)
        	{
        		Log.log(Log.ERROR,SchemaMappingManager.class,ue);
        		path.setForeground(Color.RED);
        		valid = false;
        		relative_path.setText(ue.getMessage());
        	}
		}
	}
	// }}}
	
	//{{{ promptTypeIdForBuffer() method
	/**
	 * - gather all type Ids
	 * - let the user choose one
	 * - update the SchemaMapping
	 */
	public static void promptTypeIdForBuffer(View view, Buffer buffer)
	{
		// local schema-mapping
		String specificSchema = MiscUtilities.constructPath(
			MiscUtilities.getParentOfPath(
				buffer.getPath()),SchemaMapping.SCHEMAS_FILE);
		
		URL specificSchemaURL = null;
		
		try
		{
			specificSchemaURL = new URL(pathToURL(specificSchema));
		}
		catch(MalformedURLException mfue)
		{
			// TODO: react to the error 
			Log.log(Log.ERROR,SchemaMappingManager.class,mfue);
		}
		
		SchemaMapping lMapping = getLocalSchemaMapping(buffer);
		SchemaMapping gMapping = getGlobalSchemaMapping();
		SchemaMapping bMapping = getBuiltInSchemaMapping();
		
		Map<String,SchemaMapping> allTypeIds = new HashMap<String,SchemaMapping>();
		
		String oldTypeId = null;
		
		for(SchemaMapping m: Arrays.asList(lMapping, gMapping, bMapping))
		{
			if(m != null)
			{
				for(SchemaMapping.TypeIdMapping tid : m.getTypeIds())
				{
					allTypeIds.put(tid.getId(),m);
				}
				
				if(oldTypeId == null)
				{
					oldTypeId = m.getTypeIdForDocument(buffer.getPath());
				}
			}
		}
		
		String[] tids = allTypeIds.keySet().toArray(new String[]{});
		if(tids.length == 0)
		{
			view.getToolkit().beep();
			view.getStatus().setMessage("xml.no-type-id.message");
			return;
		}
		
		String tid = (String)JOptionPane.showInputDialog(
			view,
			jEdit.getProperty("xml.choose-type-id.message"),
			jEdit.getProperty("xml.choose-type-id.title"),
            JOptionPane.OK_CANCEL_OPTION,
            null,
            tids,
            oldTypeId == null ? tids[0] : oldTypeId
            );
        
        if(tid!=null)
        {
        	SchemaMapping tidMapping = allTypeIds.get(tid);
			
        	if(isSchemaMappingEnabled(buffer))
        	{
				// no schemas.xml in the buffer's directory : will create one
				if(lMapping == null)
				{
					lMapping = new SchemaMapping(specificSchemaURL);
					if(gMapping != null)lMapping.ensureIncluded(gMapping);
				}
				
				if(lMapping != tidMapping)
				{
					lMapping.ensureIncluded(tidMapping);
				}
				
				String bufferURL = pathToURL(buffer.getPath());
				SchemaMapping.URIResourceRule newRule = new SchemaMapping.URIResourceRule(null,bufferURL,tid, true);
				
				lMapping.updateMapping(newRule);
				
				try
				{
					lMapping.toDocument(specificSchema);
				}
				catch(IOException ioe)
				{
					// if saving fails, try to output it in a buffer
					JOptionPane.showMessageDialog(
						view,
						jEdit.getProperty("xml-error-to-document.message",new Object[]{ioe.getClass(),ioe.getMessage()}),
						jEdit.getProperty("xml-error-to-document.title"),
						JOptionPane.ERROR_MESSAGE
						);
					Buffer b = jEdit.newFile(view,lMapping.getBaseURI().toString());
					b.insert(0,lMapping.toString());
					view.getEditPane().setBuffer(buffer);
				}
				
			}
			else
			{
				SchemaMapping.Result res = tidMapping.resolveTypeId(tid);
				if(res == null)
				{
				}
				else{
					try
					{
						String schemaURL = new URL(res.baseURI,res.target).toString();
						buffer.setStringProperty(BUFFER_SCHEMA_PROP,schemaURL);
					}
					catch(MalformedURLException mfue)
					{
						// pretty rare !
						Log.log(Log.ERROR,SchemaMappingManager.class,
							"unable to resolve schema for typeId : "+res);
						Log.log(Log.ERROR,SchemaMappingManager.class,
							mfue);
					}
				}
			}
				
			// finally, use the new schema mapping
			view.getInputHandler().setRepeatCount(1);
			view.getInputHandler().invokeAction("sidekick-parse");
        }
	}
	//}}}
	
	//{{{ getGlobalSchemaMapping() method
	public static SchemaMapping getGlobalSchemaMapping()
	{
		String schemaURL = jEdit.getProperty(SCHEMA_MAPPING_PROP);
		SchemaMapping mapping = null;
		// if schemaURL is null, it means that there is no settings directory
		if(schemaURL != null)
		{
			Log.log(Log.DEBUG,SchemaMappingManager.class,"global mapping="+schemaURL);
			mapping = SchemaMapping.fromDocument(schemaURL);
		}
		return mapping;
	}
	//}}}
	
	//{{{ getBuiltInSchemaMapping() method
	public static SchemaMapping getBuiltInSchemaMapping()
	{
		URL schemaURL = SchemaMappingManager.class.getClassLoader().getResource(BUILT_IN_SCHEMA);
		
		if(schemaURL == null)
		{
			throw new AssertionError("built-in schemas.xml cant be found !");
		}
		else
		{
			return SchemaMapping.fromDocument(schemaURL.toString());
		}
	}
	//}}}
	
	//{{{ getLocalSchemaMapping() method
	/**
	* @return schemas.xml next to buffer or null if it doesn't exist
	*/
	public static SchemaMapping getLocalSchemaMapping(Buffer buffer)
	{
		SchemaMapping mapping;
		
		// local schema-mapping
		String specificSchema = MiscUtilities.constructPath(
			MiscUtilities.getParentOfPath(
				buffer.getPath()),SchemaMapping.SCHEMAS_FILE);
		
		// TODO: VFS
		File fileSpecificSchema = new File(specificSchema);
		if(fileSpecificSchema.exists())
		{
			try{
				String schemaURL = fileSpecificSchema.toURI().toURL().toString();
				mapping = SchemaMapping.fromDocument(schemaURL);
			}
			catch(MalformedURLException mfue)
			{
				// may happen if specificSchema is relative, but it should not be the case with buffer.getPath()
				Log.log(Log.ERROR,SchemaMappingManager.class,
					"Error converting path for fromDocument : '"+specificSchema+"'");
				Log.log(Log.ERROR,SchemaMappingManager.class,mfue);
				mapping = null;
			}
		}
		else
		{
			Log.log(Log.DEBUG, SchemaMappingManager.class,
				"no schemas.xml in "+buffer.getPath());
			mapping = null;
		}
		return mapping;
	}
	//}}}
	
	//{{{ getSchemaMappingForBuffer() method
	/**
	* @return local SchemaMapping or global SchemaMapping or built-in SchemaMapping
	*/
	public static SchemaMapping getSchemaMappingForBuffer(Buffer buffer)
	{
		SchemaMapping mapping;
		
		// local schema-mapping
		mapping  = getLocalSchemaMapping(buffer);
		
		if(mapping == null)
		{
			mapping = getGlobalSchemaMapping();
		}
		
		if(mapping == null)
		{
			Log.log(Log.DEBUG, SchemaMappingManager.class,
				"no settings => using built-in schema mapping file");
			mapping = getBuiltInSchemaMapping();
		}
		return mapping;
	}
	//}}}
	
	//{{{ initGlobalSchemaMapping() method
	/**
	 * Finds (and creates if needed) the global schema mapping (schemas.xml)
	 * file in the settings directory, for reading or overwriting it.
	 */
	public static void initGlobalSchemaMapping(View view){
		jEdit.unsetProperty(SCHEMA_MAPPING_PROP);
		
		File home = org.gjt.sp.jedit.EditPlugin.getPluginHome(xml.XmlPlugin.class);
		if(home == null)
		{
			// -nosettings
			return;
		}
		else if(!home.exists())
		{
			Log.log(Log.DEBUG,SchemaMappingManager.class, "creating settings directory");
			try
			{
				boolean created = home.mkdirs();
				if(!created)
				{
					GUIUtilities.error( view, "unable to create settings directory: "+home,null);
					return;
				}
			}
			catch(SecurityException se)
			{
				GUIUtilities.error( view, "unable to create settings directory (security exception): "+home,null);
				return;
			}
		}
		File schemas = new File(home,SchemaMapping.SCHEMAS_FILE);
		// create an empty mapping file in settings directory
		// it points to the global schemaMapping, to get all
		// the builtin rules
		if(!schemas.exists()){
			SchemaMapping builtinMapping = getBuiltInSchemaMapping();
			SchemaMapping tmp = new SchemaMapping();
			tmp.ensureIncluded(builtinMapping);
			try{
				tmp.toDocument(schemas.getPath());
			}catch(IOException ioe){
				Log.log(Log.ERROR,SchemaMappingManager.class,"Unable to save default RelaxNG mappings");
				Log.log(Log.ERROR,SchemaMappingManager.class,ioe);
				return;
			}
		}
		try
		{
			jEdit.setProperty(SCHEMA_MAPPING_PROP,schemas.toURL().toString());
		}
		catch(java.net.MalformedURLException mfe)
		{
			Log.log(Log.ERROR,SchemaMappingManager.class,mfe);
		}
	}
	//}}}

	//{{{ isSchemaMappingEnabled() method
	public static boolean isSchemaMappingEnabled(Buffer b){
		return b.getBooleanProperty(BUFFER_ENABLE_SCHEMA_MAPPING_PROP);
	}
	//}}}
}
