package superabbrevs.guice;

import javax.swing.JDialog;

import superabbrevs.AbbreviationHandler;
import superabbrevs.AbbreviationHandlerImpl;
import superabbrevs.AbbrevsOptionPaneController;
import superabbrevs.AbbrevsOptionPaneControllerImpl;
import superabbrevs.InputHandler;
import superabbrevs.InputHandlerImpl;
import superabbrevs.JEditInterface;
import superabbrevs.ModeService;
import superabbrevs.TemplateBufferListener;
import superabbrevs.TemplateBufferListenerImpl;
import superabbrevs.TemplateHandler;
import superabbrevs.TemplateHandlerImpl;
import superabbrevs.TextAreaHandler;
import superabbrevs.TextAreaHandlerImpl;
import superabbrevs.gui.AbbreviationDialog;
import superabbrevs.io.PluginDirectory;
import superabbrevs.io.PluginDirectoryImpl;
import superabbrevs.repository.AbbreviationTrieRepository;
import superabbrevs.repository.CachedAbbreviationTrieRepository;
import superabbrevs.repository.FileBasedModeRepository;
import superabbrevs.repository.ModeRepository;
import superabbrevs.serialization.ModeSerializer;
import superabbrevs.serialization.XmlModeSerializer;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class GuiceConfiguration implements Module {
	
	private final JEditInterface jedit;
	
	public GuiceConfiguration(JEditInterface jedit) {
		this.jedit = jedit;
	}

	public void configure(Binder binder) {
		binder.bind(JEditInterface.class).toInstance(jedit);
		binder.bind(ModeService.class).toInstance(jedit);
		binder.bind(InputHandler.class).to(InputHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(TextAreaHandler.class).to(TextAreaHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(AbbreviationHandler.class).to(AbbreviationHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(ModeRepository.class).to(FileBasedModeRepository.class).in(Scopes.SINGLETON);
		binder.bind(ModeSerializer.class).to(XmlModeSerializer.class).in(Scopes.SINGLETON);
		binder.bind(PluginDirectory.class).to(PluginDirectoryImpl.class).in(Scopes.SINGLETON);	
		binder.bind(TemplateBufferListener.class).to(TemplateBufferListenerImpl.class).in(Scopes.SINGLETON);
		binder.bind(TemplateHandler.class).to(TemplateHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(AbbreviationTrieRepository.class).to(CachedAbbreviationTrieRepository.class);
		binder.bind(JDialog.class)
			.annotatedWith(Names.named("abbreviationDialog"))
			.to(AbbreviationDialog.class);
		binder.bind(AbbrevsOptionPaneController.class).to(AbbrevsOptionPaneControllerImpl.class);
	}
}
