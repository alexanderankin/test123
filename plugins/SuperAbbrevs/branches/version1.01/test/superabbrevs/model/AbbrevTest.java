package superabbrevs.model;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;


public class AbbrevTest {
	private Abbrev abbrev;

	@Before
	public void setup() {
		abbrev = new Abbrev("Test Abbrev", "Old abbrev", "Old expansion");
	}
	
	@Test
	public void setReplementType() throws Exception {
		ReplacementTypes replacementType = ReplacementTypes.WORD;
		abbrev.whenInvokedAsCommand.replace(replacementType);
		assertEquals(replacementType, abbrev.whenInvokedAsCommand.replace());
	}
	
	@Test
	public void setSelectionReplementType() throws Exception {
		SelectionReplacementTypes replamentType = SelectionReplacementTypes.SELECTION;
		abbrev.whenInvokedAsCommand.onSelection.replace(replamentType);
		assertEquals(replamentType, abbrev.whenInvokedAsCommand.onSelection.replace());
	}
	
	@Test
	public void setAbbreviationFiresPropertyChangeEvent() throws Exception {
		String propertyName = "abbreviation";
		String oldValue = abbrev.getAbbreviation();
		String newValue = "New value";
		
		PropertyChangedTestListener testListener = setExpectedPropertyChangeListener(
				propertyName, oldValue, newValue);
		abbrev.setAbbreviation(newValue);
		testListener.assertIsSatified();
	}
	
	@Test
	public void setExpansionFiresPropertyChangeEvent() throws Exception {
		String propertyName = "expansion";
		String oldValue = abbrev.getExpansion();
		String newValue = "newValue";
		
		PropertyChangedTestListener testListener = setExpectedPropertyChangeListener(
				propertyName, oldValue, newValue);
		abbrev.setExpansion(newValue);
		testListener.assertIsSatified();
	}
	
	@Test
	public void setNameFiresPropertyChangeEvent() throws Exception {
		String propertyName = "name";
		String oldValue = abbrev.getName();
		String newValue = "newValue";
		
		PropertyChangedTestListener testListener = setExpectedPropertyChangeListener(
				propertyName, oldValue, newValue);
		abbrev.setName(newValue);
		testListener.assertIsSatified();
	}

	private PropertyChangedTestListener setExpectedPropertyChangeListener(
			String propertyName, String oldValue, String newValue) {
		PropertyChangeEvent evt = new PropertyChangeEvent(abbrev, propertyName, oldValue, newValue);
		PropertyChangedTestListener testListener = new PropertyChangedTestListener(evt);
		abbrev.addPropertyChangeListener(testListener);
		abbrev.addPropertyChangeListener(propertyName, testListener);
		return testListener;
	}
	
	private class PropertyChangedTestListener implements PropertyChangeListener {
		private final PropertyChangeEvent expected;
		public PropertyChangedTestListener(PropertyChangeEvent expected) {
			this.expected = expected;
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			
			assertEquals("Property name", expected.getPropertyName(), evt.getPropertyName());
			assertEquals("New value", expected.getNewValue(), evt.getNewValue());
			assertEquals("Old value", expected.getOldValue(), evt.getOldValue());
			called++;
		}
		
		private int called = 0;
		
		public void assertIsSatified() {
			assertEquals(2, called);
		}
		
	}
}
