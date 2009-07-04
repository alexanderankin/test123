package superabbrevs.guice;

import static org.junit.Assert.*;

import org.gjt.sp.jedit.View;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import superabbrevs.AbbreviationHandler;
import superabbrevs.InputHandler;
import superabbrevs.JEditInterface;
import superabbrevs.io.PluginDirectory;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class GuiceConfirgurationTest {
	
	private Mockery context = new Mockery();
	private JEditInterface jedit;
	private PluginDirectory pluginDirectory;
	
	@Before
	public void setup() {
		pluginDirectory = context.mock(PluginDirectory.class);
		jedit = context.mock(JEditInterface.class);
	}
	
	@Test
	public void inputHandlerCanBeResolved() throws Exception {
		Injector injector = Guice.createInjector(new GuiceConfiguration(jedit));
    	InputHandler result = injector.getInstance(InputHandler.class);
	}
	
	@Test
	public void abbreviationHandlerCanBeResolved() throws Exception {
		Injector injector = Guice.createInjector(new GuiceConfiguration(jedit));
    	AbbreviationHandler result = injector.getInstance(AbbreviationHandler.class);
	}
}
