package uk.co.antroy.latextools; 
 
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

public class LaTeXMacros { 

	public static void setMainFile(Buffer buffer){
		String currentFile = buffer.getPath();
		jEdit.setProperty("mainTexFile",currentFile);
  }

	public static void resetMainFile(){
		jEdit.setProperty("mainTexFile","");
	}
	
	public static String getMainFile(){
		return jEdit.getProperty("mainTexFile");
	}
	
	public static void repeat(String expression, int start, int no, View view){
		StringBuffer sb = new StringBuffer("");
		
		for (int i=start;i<=no;i++){
			 String replace = ""+i;
			 String exp = expression.replaceAll("#",replace);
			 sb.append(exp).append("\n");	   
		}
	
		view.getTextArea().setSelectedText(sb.toString());
}
	
	public static void repeat(View view, boolean startDialog){
			String expression = Macros.input(view, "Enter expression (# where numbers should go)");
  		int no = Integer.parseInt(Macros.input(view, "Enter number of iterations"));
  		int start;
			if (startDialog){
				start = Integer.parseInt(Macros.input(view, "Enter start number"));
			}else{
			  start = 1;
			}
			repeat(expression, start, no, view);
	}

	public static void surround(View view, String prefix, String suffix){
		JEditTextArea textArea = view.getTextArea();
		
		int caret = textArea.getCaretPosition();
//		prefix = Macros.input(view, "Enter prefix");
//		suffix = Macros.input(view, "Enter suffix");
		if ( prefix == null || prefix.length() == 0) return;
		if ( suffix == null || suffix.length() == 0) suffix = prefix;
		
		String text = textArea.getSelectedText();
		if(text == null) text = "";
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append(text);
		sb.append(suffix);
		textArea.setSelectedText(sb.toString());
		//if no selected text, put the caret between the tags
		if(text.length() == 0)
			textArea.setCaretPosition(caret + prefix.length());
	}
	
	public static void surround(View view){
		String prefix = Macros.input(view, "Enter prefix");
		String suffix = Macros.input(view, "Enter suffix");
		surround(view, prefix, suffix);
	}
	
	public static void newCommand(View view){
		String command = Macros.input(view, "Enter command");
		
		surround(view, "\\" + command + "{", "}");
	}
	
	public static void newEnvironment(View view){
		String env = Macros.input(view, "Enter environment name");
		
		surround(view, "\\begin{" + env + "}", "\\end{" + env + "}");
	}
	
} 
