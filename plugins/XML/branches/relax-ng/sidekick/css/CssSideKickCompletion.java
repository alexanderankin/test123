/**
 * CssSideKickCompletion.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Jakub Roztocil
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package sidekick.css;

//{{{ Imports
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.xerces.parsers.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.textarea.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sidekick.SideKickCompletion;
import xml.Resolver;
//}}}


public class CssSideKickCompletion extends SideKickCompletion {

	public final static String SPECIAL_URL_PROP = "url...";
	public static String QUOTE;
	private static boolean choosingFile = false;

	//{{{ CssSideKickCompletion constructor
	public CssSideKickCompletion(List completions, String word, boolean selectedProperty) {
		super(jEdit.getActiveView(), word, completions);
		this.selectedProperty = selectedProperty;
		if (!inited) {
			readConfig();
		}
	} //}}}

	//{{{ readConfig() method
	public static void readConfig() {
		QUOTE = jEdit.getProperty(CssSideKickPlugin.OPTION_PREFIX + "quote");
	} //}}}

	//{{{ insert() method
	public void insert(int index) {

		String selected = String.valueOf(get(index));
		JEditBuffer buffer = textArea.getBuffer();
		int caret = textArea.getCaretPosition();
		int moveCaret = 0;
		
		// when "url..." confirmed by ENTER, the this method is called twice
		// it seem as a Sidekick's bug to me.
		if (choosingFile) {
			return;
		}


		// If selected property, handle colon
		if (selectedProperty && jEdit.getBooleanProperty(CssSideKickPlugin.OPTION_PREFIX + "colon")) {

			String textAfter = buffer.getText(caret, buffer.getLength() - caret - 1);

			// If isn't colon after caret, add one after property.
			// Otherwise check, if do we have between property and that colon some mess
			// and if we haven't, move caret after colon
			if (canAddColon(textAfter)) {
				selected += ":";
				if (jEdit.getBooleanProperty(CssSideKickPlugin.OPTION_PREFIX + "space-after-colon")) {
					selected += " ";
				}
			} else if (Pattern.compile("^\\s*:").matcher(textAfter).find()) {
				moveCaret = textAfter.indexOf(":") + 1;
			}

		} else if (selected.equals("url")) {

			selected = "url(" + QUOTE + QUOTE + ")";
			moveCaret = -2;

		} else if (selected.equals(SPECIAL_URL_PROP)){
			choosingFile = true;
			selected = "url(" + QUOTE + Utils.chooseFileAndGetRelativePath() + QUOTE + ")";
			choosingFile = false;

		}



		Selection s = textArea.getSelectionAtOffset(caret);
		int start = (s == null ? caret : s.getStart());
		int end = (s == null ? caret : s.getEnd());
		try {
			buffer.beginCompoundEdit();
			buffer.remove(start - text.length(),text.length());
			buffer.insert(start - text.length(),selected);
		} finally {
			buffer.endCompoundEdit();
		}
		textArea.setCaretPosition(end + selected.length() + moveCaret - text.length());
	} //}}}

	
	//{{{ getCssProperties() method
	public static HashMap getCssProperties() {
		return cssProperties;
	} //}}}
	
	//{{{ getCssUnits() method
	public static ArrayList getCssUnits() {
		return cssUnits;
	} //}}}
	
	//{{{ setInitialized() method
	public static void setInitialized(boolean initialized) {
		CssSideKickCompletion.initialized = initialized;
	} //}}}
	
	//{{{ initialized() method
	public static boolean initialized() {
		return initialized;
	} //}}}
	
	//{{{ initialize() method
	public static void initialize() {
		setInitialized(true);
		readCompletionConfig();
	}
	//}}}
	
	
	//{{{ Private members

	private boolean selectedProperty;
	
	
	private static ArrayList<String> cssUnits;
	private static HashMap<String,ArrayList<String>> cssProperties;
	
	private static boolean initialized;
	/* private static String COMPLETION_CONFIG_FILE = jEdit.getSettingsDirectory() 
													+ File.separator 
													+ jEdit.getProperty(CssSideKickPlugin.OPTION_PREFIX + "completion-config"); */
	private static final String COMPLETION_CONFIG_FILE = "jeditresource:/XML.jar!/xml/completion/css-complete.xml";
	
	private static Pattern HAS_PROP_COLON = Pattern.compile("^[^;}]*:");
	private static boolean inited;

	//{{{ canAddColon() method
	private static boolean canAddColon(String textAfterCaret) {
		return !HAS_PROP_COLON.matcher(textAfterCaret).find();
	} //}}}

	//{{{ readCompletionConfig() method
	private static void readCompletionConfig() {
		try {
			Log.log(Log.DEBUG, CssSideKickCompletion.class, "Parsing configuration file: " + COMPLETION_CONFIG_FILE);
			DOMParser parser = new DOMParser();
			parser.setEntityResolver(Resolver.instance());
			InputSource source = Resolver.instance().resolveEntity(null, COMPLETION_CONFIG_FILE );
			parser.parse(source);
			Document doc = parser.getDocument();
			initCssUnits(doc);
			initCssProperties(doc);

			Log.log(Log.DEBUG,
					CssSideKickCompletion.class,
					"Parsing done, found " + cssProperties.size() + " css properties and " + cssUnits.size() + " css units");

		} catch (FileNotFoundException e) {
			GUIUtilities.error(jEdit.getActiveView(),
								"csssidekick.config-io-error",
								new String[]{
									e.getMessage()
								});
		} catch (IOException e) {

			GUIUtilities.error(jEdit.getActiveView(),
								"csssidekick.config-io-error",
								new String[]{
									e.getMessage()
								});
			Log.log(Log.ERROR, CssSideKickCompletion.class, e);
		} catch (SAXException e) {
			GUIUtilities.error(jEdit.getActiveView(),
								"csssidekick.config-xml-error",
								new String[]{e.getMessage()});
			Log.log(Log.ERROR, CssSideKickCompletion.class, e);
		} catch (Exception e) {
			Log.log(Log.ERROR, CssSideKickCompletion.class, e);
		}
	} //}}}

	//{{{ initCssProperties() method
	private static void initCssProperties(Document doc) {
		cssProperties = new HashMap<String,ArrayList<String>>();

		NodeList propertiesElements = doc.getElementsByTagName("properties");
		if (propertiesElements.getLength() != 1) {
			// throw new Exception("No <properties> found");
		}

		NodeList properties = ((Element)propertiesElements.item(0)).getElementsByTagName("property");
		int propCount = properties.getLength();

		// loop properties
		for (int i = 0; i < propCount; i++) {

			Element propEle = (Element) properties.item(i);
			String propName = propEle.getAttribute("name");

			NodeList values = propEle.getElementsByTagName("*");
			int valCount = values.getLength();
			ArrayList<String> valList = new ArrayList<String>();

			// loop values
			for (int j = 0; j < valCount; j++) {
				Element ele = (Element) values.item(j);
				String name = ele.getAttribute("name");

				if (ele.getTagName().equals("include")) {

					NodeList incValues = doc.getElementById(name).getElementsByTagName("value");
					int incValCount = incValues.getLength();

					// loop included values
					for (int k = 0; k < incValCount; k++) {
						Element incValEle = (Element)incValues.item(k);
						String incValText = incValEle.getFirstChild().getNodeValue();
						if (incValText.equals("url")) {
							valList.add(SPECIAL_URL_PROP);
						}
						valList.add(incValText);
					}
					continue;
				}

				String valText = ele.getFirstChild().getNodeValue();
				if (valText.equals("url")) {
					valList.add(SPECIAL_URL_PROP);
				}
				valList.add(valText);

			}

			cssProperties.put(propName, valList);
		}

	} //}}}

	//{{{ initCssUnits() method
	private static void initCssUnits(Document doc) {

		cssUnits = new ArrayList<String>();
		NodeList unitsElements = doc.getElementsByTagName("units");
		NodeList units = ((Element)unitsElements.item(0)).getElementsByTagName("unit");
		int unitsCount = units.getLength();
		for (int i = 0; i < unitsCount; i++) {
			Element unitEle = (Element)units.item(i);
			String unitText = unitEle.getFirstChild().getNodeValue();
			cssUnits.add(unitText);
		}

	} //}}}

	//}}}

}
