package superabbrevs.guice;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import superabbrevs.InputHandler;
import superabbrevs.InputHandlerImpl;
import superabbrevs.JEditInterface;
import superabbrevs.JEditInterfaceImpl;
import superabbrevs.TextAreaHandler;
import superabbrevs.TextAreaHandlerImpl;

import com.google.inject.Binder;
import com.google.inject.Module;

public class GuiceConfiguration implements Module {
	
	private final View view;
	private final JEditTextArea textArea;
	private final Buffer buffer;

	public GuiceConfiguration(View view, JEditTextArea textArea, Buffer buffer) {
		this.view = view;
		this.textArea = textArea;
		this.buffer = buffer;
	}
	
	public void configure(Binder binder) {
		binder.bind(View.class).toInstance(view);
		binder.bind(JEditTextArea.class).toInstance(textArea);
		binder.bind(Buffer.class).toInstance(buffer);
		
		binder.bind(JEditInterface.class).to(JEditInterfaceImpl.class);

		binder.bind(TextAreaHandler.class).to(TextAreaHandlerImpl.class);
		binder.bind(InputHandler.class).to(InputHandlerImpl.class);
	}
}
