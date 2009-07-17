package superabbrevs.guice;

import static org.junit.Assert.assertNotNull;

import javax.swing.JDialog;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import superabbrevs.AbbreviationHandler;
import superabbrevs.InputHandler;
import superabbrevs.JEditInterface;
import superabbrevs.gui.AbbreviationDialog;
import superabbrevs.io.PluginDirectory;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class GuiceConfirgurationTest {
	
	private Mockery context = new Mockery();
	private JEditInterface jedit;
	private PluginDirectory pluginDirectory;
	private Injector injector;
	
	@Before
	public void setup() {
		pluginDirectory = context.mock(PluginDirectory.class);
		jedit = context.mock(JEditInterface.class);
		injector = Guice.createInjector(new GuiceConfiguration(jedit));
	}
	
	@Test
	public void inputHandlerCanBeResolved() throws Exception {
    	InputHandler result = injector.getInstance(InputHandler.class);
    	assertNotNull(result);
	}
	
	@Test
	public void abbreviationHandlerCanBeResolved() throws Exception {
    	AbbreviationHandler result = injector.getInstance(AbbreviationHandler.class);
    	assertNotNull(result);
	}
}
