package superabbrevs;

import java.util.*;
import java.io.*;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import javax.swing.event.*;

import superabbrevs.gui.AddAbbrevDialog;

public class SuperAbbrevs {
	
	private static Hashtable modes = new Hashtable();
	
	/**
	 * Adds an abbreviation to the global abbreviation list.
	 * @param abbrev The abbreviation
	 * @param expansion The expansion
	 */
	public static void addGlobalAbbrev(String abbrev, String expansion){
		addModeAbbrev("global",abbrev,expansion);
	}
	
	/**
	 * Adds a mode-specific abbrev.
	 * @param mode The edit mode
	 * @param abbrev The abbrev
	 * @param expansion The expansion
	 */
	public static void addModeAbbrev(String mode, String abbrev, String expansion){
		Hashtable abbrevs = (Hashtable)modes.get(mode);
		
		if (abbrevs==null){
			abbrevs = SuperAbbrevsIO.readAbbrevs(mode);
			modes.put(mode,abbrevs);
		}
		
		abbrevs.put(abbrev,expansion);
		
		SuperAbbrevsIO.write(mode,abbrevs);
	}
	
	public static void saveAbbrevs(String mode, Hashtable abbrevs){
		modes.put(mode,abbrevs);
		SuperAbbrevsIO.write(mode,abbrevs);
	}
	
	public static Hashtable loadAbbrevs(String mode){
		return SuperAbbrevsIO.readAbbrevs(mode);
	}
	
	/**
	 * Expands the abbrev at the caret position in the specified
	 * view.
	 * @param view The view
	 * @param showDialog is true if there should be shown an add abbreviation 
	 *    dialog, if the abbreviation doesn't exists 
	 * @return false if no action was taken
	 */
	public static boolean expandAbbrev(View view, boolean showDialog){
		Buffer buffer = view.getBuffer();
		JEditTextArea textArea = view.getTextArea();
		
		// in the following i will refere to the line where the caret resides 
		// as the current line.
		
		// the line number of the current line 
		int line = textArea.getCaretLine();
		// the start position of the current line in the full text  
		int lineStart = buffer.getLineStartOffset(line);
		// the offset of the caret in the full text 
		int caretPos = textArea.getCaretPosition();
		// the offset of the caret in the current line 
		int caretLinePos = caretPos - lineStart;
		
		// the text on the current line
		String lineText = buffer.getLineText(line);
				
		// get the abbrevation before the caret 
		String abbrev = getAbbrev(caretLinePos,lineText);
		
		// if the abbreviation is empty we return false, to indicate that no 
		// action was taken
		if (abbrev.trim().equals("")){
			return false;
		}
		
		// a string indication the mode of the current buffer 
		String mode = buffer.getMode().getName();
		
		// get the template of the abbreviation in the current mode 
		String template = getTemplateString(mode,abbrev);
		
		if (template==null){
			// if the template doesn't exists try the global mode
			template = getTemplateString("global",abbrev);
		}
		
		if (template!=null){
			// there exists a template for the abbreviation
			
			// indent the template as the current line 
			String indent = getIndent(lineText);
			template = template.replaceAll("\n", "\n"+indent);
			
			int templateStart = caretPos - abbrev.length();
			// remove the abbreviation
			buffer.remove(templateStart,abbrev.length());
			
			
			Template t = new Template(templateStart,template);
			
			textArea.setSelectedText(t.toString(), false);
			
			SelectableField f = t.getCurrentField();
			if (f!=null){
				int  start = f.getOffset();
				int end = start + f.getLength();
				textArea.setCaretPosition(end);
				textArea.addToSelection(new Selection.Range(start,end));
			}
			
			Handler h = new Handler(t,textArea);
			putHandler(buffer,h);
			
			textArea.addCaretListener(new TemplateCaretListener()); 
			
			buffer.addBufferChangeListener(h);
			return true;
		} else if (showDialog){
			// there was no template for the abbreviation
			// so we will show a dialog to create the abbreviation 
			AddAbbrevDialog dialog = new AddAbbrevDialog(view,abbrev);
			return true;
		} else {
			// there was no template for the abbreviation,
			// and the option for showing a "create abbreviation" dialog is false.
			// So we return false to indicate that no action was taken. 
			return  false;
		}
	}
	
	public static void nextAbbrev(JEditTextArea textArea){
		Buffer buffer = textArea.getBuffer();
		Handler h = getHandler(buffer);
		Template t = h.getTemplate();
		
		if (t != null){
			t.nextField();
			SelectableField f = t.getCurrentField();
			if (f!=null){
				int start = f.getOffset(); 
				int end = start + f.getLength();
				textArea.setCaretPosition(end);
				textArea.addToSelection(new Selection.Range(start,end));
			}
		}
	}
	
	
	public static void prevAbbrev(JEditTextArea textArea){
		Buffer buffer = textArea.getBuffer();
		Handler h = getHandler(buffer);
		Template t = h.getTemplate();
		
		if (t != null){
			t.prevField();
			SelectableField f = t.getCurrentField();
			if (f!=null){
				int start = f.getOffset(); 
				int end = start + f.getLength();
				textArea.setCaretPosition(end);
				textArea.addToSelection(new Selection.Range(start,end));
			}
		}
	}
	
	private static String getAbbrev(int caretPosition,String text){
		int i=caretPosition-1;
		while(0<=i && Character.isJavaIdentifierPart(text.charAt(i))){
			i--;
		}
			if (i!=-1){
			return text.substring(i+1,caretPosition);
		}else {
			return text.substring(0,caretPosition);
		}
	}
		
	private static String getTemplateString(String mode,String abbrev){
		String template = null;
		Hashtable abbrevs = (Hashtable)modes.get(mode);
		
		if (abbrevs == null){
			//read mode abbrevs
			abbrevs = SuperAbbrevsIO.readAbbrevs(mode);
		}
		
		if (abbrevs != null){
			template = (String)abbrevs.get(abbrev);
		} 
		
		return template;
	}
	
	private static String getIndent(String line){
		int i=0;
		String output = "";
		while(i<line.length() && (line.charAt(i)==' ' || line.charAt(i)=='\t')){
			output = output + line.substring(i,i+1);
			i++;
		}
		return output; 
	}
	
	private static Hashtable handlers = new Hashtable();
	
	public static void putHandler(Buffer buffer, Handler t){
		Handler h = getHandler(buffer);
		buffer.removeBufferChangeListener(h);
		
		handlers.put(buffer,t);
	}
	
	public static Handler getHandler(Buffer buffer){
		return (Handler)handlers.get(buffer);
	}
	
	public static void removeHandler(Buffer buffer){
		Handler h = getHandler(buffer);
		buffer.removeBufferChangeListener(h);
		handlers.remove(buffer);
	}
	
	public static boolean enabled(Buffer buffer){
		return null != handlers.get(buffer);
	}
	
	public static void makeDefaults(){
		SuperAbbrevsIO.removeOldMacros();
		SuperAbbrevsIO.writeDefaultAbbrevs();
	}
}
