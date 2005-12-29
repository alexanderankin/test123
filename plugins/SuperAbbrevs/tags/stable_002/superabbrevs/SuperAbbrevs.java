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
		
		int line = textArea.getCaretLine();
		
		int lineStart = buffer.getLineStartOffset(line);
		int caretPos = textArea.getCaretPosition();
		int caretLinePos = caretPos - lineStart;
		String lineText = buffer.getLineText(line);
				
		String abbrev = getAbbrev(caretLinePos,lineText);
		
		if (abbrev.trim().equals("")){
			return false;
		}
		
		String mode = buffer.getMode().getName();
		
		String template = getTemplateString(mode,abbrev);
		
		if (template==null){
			//try global mode
			template = getTemplateString("global",abbrev);
		}
		
		if (template!=null){
			
			String indent = getIndent(lineText);
			template = template.replaceAll("\n", "\n"+indent);
			
			buffer.remove(caretPos-abbrev.length(),abbrev.length());
			
			Template t = new Template();
			
			
			String text = t.parse(template,textArea.getCaretPosition());
			
			textArea.setSelectedText(text, false);
			
			Range r = t.getCurrentRange();
			if (r!=null){
				textArea.setCaretPosition(r.getTo());
				textArea.addToSelection(new Selection.Range(r.getFrom(),r.getTo()));
			}
			
			Handler h = new Handler(t,textArea);
			putHandler(buffer,h);
			
			textArea.addCaretListener(new CaretListener (){
					public void caretUpdate(CaretEvent e){
						JEditTextArea textArea = (JEditTextArea)e.getSource();
						int caret = textArea.getCaretPosition();
						Buffer buffer = textArea.getBuffer();
						Handler handler = getHandler(buffer);
						if (handler != null){
							Template template = getHandler(buffer).getTemplate();
							if (!template.inTemplate(caret)){
								removeHandler(buffer);
								textArea.removeCaretListener(this);
							}
						} else {
							textArea.removeCaretListener(this);
						}
					} 
			}); 
			
			buffer.addBufferChangeListener(h);
			return true;
		} else if (showDialog){
			//show addAbbrev dialog
			AddAbbrevDialog dialog = new AddAbbrevDialog(view,abbrev);
			return true;
		} else {
			return  false;
		}
	}
	
	public static void nextAbbrev(JEditTextArea textArea){
		Buffer buffer = textArea.getBuffer();
		Handler h = getHandler(buffer);
		Template t = h.getTemplate();
		
		if (t != null){
			Range r = t.getNextRange();
			if (r!=null){
				textArea.setCaretPosition(r.getTo());
				textArea.addToSelection(new Selection.Range(r.getFrom(),r.getTo()));
			}
		}
		
	}
	
	public static void prevAbbrev(JEditTextArea textArea){
		Buffer buffer = textArea.getBuffer();
		Handler h = getHandler(buffer);
		Template t = h.getTemplate();
		
		if (t != null){
			Range r = t.getPrevRange();
			if (r!=null){
				textArea.setCaretPosition(r.getTo());
				textArea.addToSelection(new Selection.Range(r.getFrom(),r.getTo()));
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
