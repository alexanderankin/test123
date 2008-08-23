package qt4jedit;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

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

    public void updateCommand() {
    	ArrayList<String> sl = new ArrayList<String>();
		sl.add(jEdit.getProperty("qt4jedit.path-to-assistant", "assistant"));
		sl.add("-enableRemoteControl");
	    builder.command(sl);
    }
	
	public void start() {
		sm_instance = this;
		builder = new ProcessBuilder();
		updateCommand();
	}
	
	public void startAssistant() {
		if (assistantProcess != null) {
			stopAssistant();
		}
		try {
			assistantProcess = builder.start();
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, ioe, "Unable to start process");
		}
	}

	/**
	 * @return selection, or word under caret
	 */
	public static String getSelectedTextOrWordUnderCaret(JEditTextArea textArea) {
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
			startAssistant();
			os = assistantProcess.getOutputStream();
			try {
				os.write(bar);
				os.flush();
			}
			catch (IOException ioee) {
				Log.log(Log.ERROR, ioee, "sendToAssistant");				
			}
		}
	}
	
	public void activateKeyword(JEditTextArea textArea) {
		String command = "activateKeyword " + getSelectedTextOrWordUnderCaret(textArea) + "\n";
		sendToAssistant(command);
	}
	
	public void activateIdentifier(JEditTextArea textArea) {
		String command = "activateIdentifier " + getSelectedTextOrWordUnderCaret(textArea) + "\n";
		sendToAssistant(command);
	}
	
	public void openURL(JEditTextArea textArea) {
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
		
	
	public void stop() {
		stopAssistant();
	}
	
	
	
	
	
}

