package superabbrevs;

import java.util.*;
import java.io.*;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.Buffer;
import javax.swing.event.*;

import superabbrevs.gui.AddAbbrevDialog;
import superabbrevs.template.*;

import bsh.*;

import javax.swing.JOptionPane;

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
		System.out.println("Mode: "+mode);
		Hashtable abbrevs = (Hashtable)modes.get(mode);
		
		if (abbrevs==null){
			// try to read abbrevs from file 
			abbrevs = SuperAbbrevsIO.readAbbrevs(mode);
			
			if (abbrevs == null){
				// if the abbrevs is not defined, define them
				abbrevs = new Hashtable();
			}
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

	private static boolean expandAbbrev(String abbrev, View view, boolean showDialog){
		Buffer buffer = view.getBuffer();
		JEditTextArea textArea = view.getTextArea();
		
		// the line number of the current line 
		int line = textArea.getCaretLine();
		// the offset of the caret in the full text
		int caretPos = textArea.getCaretPosition();
		// the text on the current line
		String lineText = buffer.getLineText(line);
		
		// a string indication the mode of the current buffer 
		String mode = buffer.getRuleSetAtOffset(caretPos).getModeName();
		
		// get the template of the abbreviation in the current mode 
		String template = getTemplateString(mode,abbrev);
		
		if (template==null){
			// if the template doesn't exists try the global mode
			template = getTemplateString("global",abbrev);
		}
		
		if (template!=null){
			// there exists a template for the abbreviation
			
			
			
			
			Interpreter interpreter = new Interpreter();
			String selection = "";
			if(textArea.getSelectionCount() == 1){
				selection = textArea.getSelectedText();
				textArea.setSelectedText("");
				// the offset of the caret in the full text
				caretPos = textArea.getCaretPosition();
			}
			
			
			Template t;
			try{
				interpreter.set("selection", selection);
				
				// indent the template as the current line 
				String indent = getIndent(lineText);
				
				t = TemplateFactory.createTemplate(template, interpreter, indent);
				t.setOffset(caretPos);
			} catch ( TargetError e ) {
				// The script threw an exception
				System.out.println("TargetError");
				System.out.println(e.getMessage());
				return false;
			} catch ( ParseException e ) {
				// Parsing error
				System.out.println("ParseException");
				System.out.println(e.getMessage());
				return false;
			} catch ( EvalError e ) {
				// General Error evaluating script
				System.out.println("EvalError");
				System.out.println(e.getErrorLineNumber()); 
				System.out.println(e.getMessage());
				return false;
			} catch ( IOException e){
				// Input output error
				System.out.println("IOException");
				System.out.println(e.getMessage());
				return false;
			}
			
			textArea.setSelectedText(t.toString(), false);
			
			SelectableField f = t.getCurrentField();
			int  start = f.getOffset();
			int end = start + f.getLength();
			textArea.setCaretPosition(end);
			textArea.addToSelection(new Selection.Range(start,end));
			
			Handler h = new Handler(t,textArea);
			putHandler(buffer,h);
			
			putCaretListener(textArea, new TemplateCaretListener()); 
			
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
		
		int templateStart = caretPos - abbrev.length();
		// remove the abbreviation
		buffer.remove(templateStart,abbrev.length());
		
		if (!expandAbbrev(abbrev, view, showDialog)){
			buffer.insert(templateStart,abbrev);
			return false;
		} 
		
		return true; 
	}
	
	/**
	 * Method showAbbrevDialog()
	 * show a dialog to type in the abbrev
	 */
	public static boolean showAbbrevDialog(View view) {
		// TODO replace with a live search
		String abbrev = JOptionPane.showInputDialog(view, "Type in an abbreviation", "Abbreviation Input", JOptionPane.INFORMATION_MESSAGE);
		return expandAbbrev(abbrev,view,false);
	}
	
	public static void nextAbbrev(JEditTextArea textArea){
		System.out.println("nextAbbrev start");
		Buffer buffer = textArea.getBuffer();
		Handler h = getHandler(buffer);
		Template t = h.getTemplate();
		
		if (t != null){
			TemplateCaretListener listener = removeCaretListener(textArea);
			t.nextField();
			SelectableField f = t.getCurrentField();
			if (f!=null){
				int start = f.getOffset(); 
				int end = start + f.getLength();
				textArea.setCaretPosition(end);
				textArea.addToSelection(new Selection.Range(start,end));
			}
			putCaretListener(textArea,listener);
		}
		
	}
	
	
	public static void prevAbbrev(JEditTextArea textArea){
		Buffer buffer = textArea.getBuffer();
		Handler h = getHandler(buffer);
		Template t = h.getTemplate();
		
		if (t != null){
			TemplateCaretListener listener = removeCaretListener(textArea);
			t.prevField();
			SelectableField f = t.getCurrentField();
			if (f!=null){
				int start = f.getOffset(); 
				int end = start + f.getLength();
				textArea.setCaretPosition(end);
				textArea.addToSelection(new Selection.Range(start,end));
			}
			putCaretListener(textArea,listener);
		}
	}
	
	private static String getAbbrev(int caretPosition,String text){
		if(caretPosition < text.length() && Character.isJavaIdentifierPart(text.charAt(caretPosition))){
			return "";
		}
		int i=caretPosition-1;
		while(0<=i && Character.isJavaIdentifierPart(text.charAt(i))){
			i--;
		}
		return text.substring(i+1,caretPosition);
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
		buffer.addBufferChangeListener(t);
		handlers.put(buffer,t);
	}
	
	public static Handler getHandler(Buffer buffer){
		return (Handler)handlers.get(buffer);
	}
	
	public static Handler removeHandler(Buffer buffer){
		Handler h = getHandler(buffer);
		buffer.removeBufferChangeListener(h);
		handlers.remove(buffer);
		return h;
	}
	
	private static Hashtable caretListeners = new Hashtable();
	
	public static void putCaretListener(JEditTextArea textArea, TemplateCaretListener l){
		textArea.removeCaretListener(getCaretListener(textArea));
		caretListeners.put(textArea,l);
		textArea.addCaretListener(l);
	}
	
	public static TemplateCaretListener getCaretListener(JEditTextArea textArea){
		return (TemplateCaretListener)caretListeners.get(textArea);
	}
	
	public static TemplateCaretListener removeCaretListener(JEditTextArea textArea){
		TemplateCaretListener l = getCaretListener(textArea);
		textArea.removeCaretListener(l);
		caretListeners.remove(textArea);
		return l;
	}
	
	
	public static boolean enabled(Buffer buffer){
		return null != handlers.get(buffer);
	}
	
	public static void makeDefaults(){
		SuperAbbrevsIO.removeOldMacros();
		SuperAbbrevsIO.writeDefaultAbbrevs();
		SuperAbbrevsIO.writeDefaultAbbrevFunctions();
		SuperAbbrevsIO.writeDefaultTemplateGenerationFunctions();
	}
}
