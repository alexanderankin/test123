package superabbrevs.serialization;

import org.jmock.Mockery;
import org.junit.Before;

public class XmlModeSerializationTest {
	private ModeSerializer modeSerializer;
	private Mockery context = new Mockery();
	
	@Before
	public void before() {
		modeSerializer = new XmlModeSerializer();
	}
}
