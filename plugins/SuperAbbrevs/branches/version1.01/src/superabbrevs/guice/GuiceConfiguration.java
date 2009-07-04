package superabbrevs.guice;

import superabbrevs.AbbreviationHandler;
import superabbrevs.AbbreviationHandlerImpl;
import superabbrevs.InputHandler;
import superabbrevs.InputHandlerImpl;
import superabbrevs.JEditInterface;
import superabbrevs.TemplateBufferListener;
import superabbrevs.TemplateBufferListenerImpl;
import superabbrevs.TemplateCaretListener;
import superabbrevs.TemplateCaretListenerImpl;
import superabbrevs.TemplateHandler;
import superabbrevs.TemplateHandlerImpl;
import superabbrevs.TextAreaHandler;
import superabbrevs.TextAreaHandlerImpl;
import superabbrevs.io.PluginDirectory;
import superabbrevs.io.PluginDirectoryImpl;
import superabbrevs.repository.FileBasedModeRepository;
import superabbrevs.repository.ModeRepository;
import superabbrevs.serialization.ModeSerializer;
import superabbrevs.serialization.XmlModeSerializer;

import com.google.inject.Binder;
import com.google.inject.Module;

public class GuiceConfiguration implements Module {
	
	private final JEditInterface jedit;
	
	public GuiceConfiguration(JEditInterface jedit) {
		this.jedit = jedit;
	}

	public void configure(Binder binder) {
		binder.bind(JEditInterface.class).toInstance(jedit);
		binder.bind(InputHandler.class).to(InputHandlerImpl.class);
		binder.bind(TextAreaHandler.class).to(TextAreaHandlerImpl.class);
		binder.bind(AbbreviationHandler.class).to(AbbreviationHandlerImpl.class);
		binder.bind(ModeRepository.class).to(FileBasedModeRepository.class);
		binder.bind(ModeSerializer.class).to(XmlModeSerializer.class);
		binder.bind(PluginDirectory.class).to(PluginDirectoryImpl.class);	
		binder.bind(TemplateCaretListener.class).to(TemplateCaretListenerImpl.class);
		binder.bind(TemplateBufferListener.class).to(TemplateBufferListenerImpl.class);
		binder.bind(TemplateHandler.class).to(TemplateHandlerImpl.class);
	}
}
