package superabbrevs.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import superabbrevs.io.PluginDirectory;
import superabbrevs.model.Mode;
import superabbrevs.serialization.ModeSerializer;


public class ModeRepositoryTest {
	private Mockery context = new Mockery();
	private ModeRepository modeRepository;
	private ModeSerializer modeSerializer;
	private PluginDirectory pluginDirectory;
	
	@Before
	public void before() {
		modeSerializer = context.mock(ModeSerializer.class);
		pluginDirectory = context.mock(PluginDirectory.class);
		modeRepository = new FileBasedModeRepository(pluginDirectory, modeSerializer);
	}
	
	@Test
	public void modeRepositoryDelegatesSaveToModeSerializer() throws Exception {
		final Mode mode = new Mode("java");
		final OutputStream output = new ByteArrayOutputStream();
		context.checking(new Expectations() {{
			oneOf(pluginDirectory).openModeFileForWriting(mode.getName());
			will(returnValue(output));
			oneOf(modeSerializer).serialize(output, mode);
		}});
		
		modeRepository.save(mode);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void modeRepositoryDelegatesLoadToModeSerializer() throws Exception {
		final String modeName = "java";
		final InputStream input = new ByteArrayInputStream(new byte[0]); 
		context.checking(new Expectations() {{
			oneOf(pluginDirectory).openModeFileForReading(modeName);
			will(returnValue(input));
			oneOf(modeSerializer).deserialize(input);
		}});
		
		modeRepository.load(modeName);
		
		context.assertIsSatisfied();
	}
}
