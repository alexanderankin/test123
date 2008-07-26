package qt4jedit;

import java.io.IOException;
import java.io.OutputStream;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;
/**
 * 
 * @author ezust
 *
 */
public class Qt4jEditPlugin extends EditPlugin {
	
	private	ProcessBuilder builder;
	private	Process assistantProcess;
	
	private static Qt4jEditPlugin sm_instance = null;
	public static  Qt4jEditPlugin instance() {
		return sm_instance;
	}
	
	public void start() {
		sm_instance = this;
		StringList sl = new StringList();
		sl.add("assistant");
		sl.add("-enableRemoteControl");
		builder = new ProcessBuilder();
		builder.command(sl);
	}
	
	public void startAssistant() {
		if (assistantProcess != null) {
			stopAssistant();
		}
		try {
			assistantProcess = builder.start();
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, this, "Unable to start process", ioe);
		}
	}

	/**
	 * @return selection, or word under caret
	 */
	public static String getSelectedTextOrWordUnderCaret(TextArea textArea) {
		String text = textArea.getSelectedText();
		int caretPos = textArea.getCaretPosition();
		String currentChar = " ";
		try {
			currentChar = textArea.getText(caretPos, 1);
		}
		catch (Exception e) {}
		if (text == null && !Character.isWhitespace(currentChar.charAt(0)))
		{
			textArea.selectWord();
			text = textArea.getSelectedText();
		}
		return text;
	}

	public void sendToAssistant(String command) {
		int len = command.length();
		byte[] bar = new byte[len + 1];
		for (int i=0; i<len; ++i) {
			bar[i] = (byte) command.charAt(i);
		}
		bar[len]=0;
		
		if (assistantProcess == null) {
			startAssistant();
		}
		OutputStream os = assistantProcess.getOutputStream();
		try {
			os.write(bar);
			os.flush();
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, this, "activateIdentifier", ioe);
		}
	}
	
	public void activateKeyword(TextArea textArea) {
		String command = "activateKeyword " + getSelectedTextOrWordUnderCaret(textArea) + "\n";
		sendToAssistant(command);
	}
	
	public void activateIdentifier(TextArea textArea) {
		String command = "activateIdentifier " + getSelectedTextOrWordUnderCaret(textArea) + "\n";
		sendToAssistant(command);
	}
	
	public void openURL(TextArea textArea) {
		String command = "setSource " + getSelectedTextOrWordUnderCaret(textArea) + "\n";
		sendToAssistant(command);
	}
	
	public void stopAssistant() {
		if (assistantProcess == null) {
			return;
		}
		assistantProcess.destroy();
		try {
			assistantProcess.waitFor();
		}
		catch (InterruptedException ie) {
			
		}
		assistantProcess = null;
	}
		
	
	@Override
	public void stop() {
		stopAssistant();
	}
	
	
	
	
	
}

