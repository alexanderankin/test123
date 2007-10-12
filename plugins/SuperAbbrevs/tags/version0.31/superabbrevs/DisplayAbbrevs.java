package superabbrevs;

import java.util.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.jEdit;

public class DisplayAbbrevs{

	private static String line = "--------------------------------------------"+
					"----------------------------------";
	
	private static int lineLength = line.length();
	private static int firstHalfLine = lineLength/2;
	private static int secondHalfLine = firstHalfLine + lineLength%2;
	
	private static void heading(StringBuffer output, String heading){
		int abbrevsLength = heading.length();
		int firstHalfAbbrev = abbrevsLength/2;
		int secondHalfAbbrev = firstHalfAbbrev + abbrevsLength%2;
		
		output.append(line.substring(0,firstHalfLine-firstHalfAbbrev));
		output.append(" " + heading+ " ");
		output.append(line.substring(0,secondHalfLine-secondHalfAbbrev));
		output.append("\n");
	} 
	
	public static void displayModeAbbrevs(View view, JEditTextArea textArea, 
		Buffer buffer) {
		
		StringBuffer output = new StringBuffer();
		
		String mode = SuperAbbrevs.getMode(textArea, buffer);
		
		output.append("Super Abbreviations for the "+mode+" mode\n\n");
		
		// Sort the abbreviations
		SortedMap abbrevs = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		abbrevs.putAll(SuperAbbrevs.loadAbbrevs(mode));
		
		if(abbrevs != null){
			Iterator iter = abbrevs.entrySet().iterator();
			
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry)iter.next();	
				String abbrev = (String)entry.getKey();
				String code = (String)entry.getValue();
				
				heading(output, abbrev);
				
				output.append(code+"\n\n");
			}
		}
		
		jEdit.newFile(view);
		textArea.setSelectedText(output.toString());
	}
	
	public static void displayAllAbbrevs(View view, JEditTextArea textArea,
		Buffer buffer) {
		
		//TODO write out global abbrevs
	}
}

