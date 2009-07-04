package superabbrevs.guice;

import superabbrevs.AbbreviationHandler;
import superabbrevs.AbbreviationHandlerImpl;
import superabbrevs.InputHandler;
import superabbrevs.InputHandlerImpl;
import superabbrevs.JEditInterface;
import superabbrevs.TemplateBufferListener;
import superabbrevs.TemplateBufferListenerImpl;
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
import com.google.inject.Scopes;

public class GuiceConfiguration implements Module {
	
	private final JEditInterface jedit;
	
	public GuiceConfiguration(JEditInterface jedit) {
		this.jedit = jedit;
	}

	public void configure(Binder binder) {
		binder.bind(JEditInterface.class).toInstance(jedit);
		binder.bind(InputHandler.class).to(InputHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(TextAreaHandler.class).to(TextAreaHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(AbbreviationHandler.class).to(AbbreviationHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(ModeRepository.class).to(FileBasedModeRepository.class).in(Scopes.SINGLETON);
		binder.bind(ModeSerializer.class).to(XmlModeSerializer.class).in(Scopes.SINGLETON);
		binder.bind(PluginDirectory.class).to(PluginDirectoryImpl.class).in(Scopes.SINGLETON);	
		binder.bind(TemplateBufferListener.class).to(TemplateBufferListenerImpl.class).in(Scopes.SINGLETON);
		binder.bind(TemplateHandler.class).to(TemplateHandlerImpl.class).in(Scopes.SINGLETON);
	}
}
