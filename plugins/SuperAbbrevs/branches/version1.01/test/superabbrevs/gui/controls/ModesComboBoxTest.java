package superabbrevs.gui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import superabbrevs.ModeService;

public class ModesComboBoxTest {
	private Mockery context = new Mockery();

	@Test
	public void actionPerformedIsFiredWhenComboBoxModelIsLoaded()
			throws Exception {
		ModesComboBox comboBox = new ModesComboBox(new ModeServiceMock());
		final ActionListener listener = context.mock(ActionListener.class);
		context.checking(new Expectations() {{
				oneOf(listener).actionPerformed(with(any(ActionEvent.class)));
		}});
		comboBox.addActionListener(listener);
		comboBox.bind();
		context.assertIsSatisfied();
	}

	private class ModeServiceMock implements ModeService {
		public String getCurrentModeName() {
			return "groovy";
		}

		public SortedSet<String> getModesNames() {
			SortedSet<String> modes = new TreeSet<String>();
			modes.add("java");
			modes.add("groovy");
			modes.add("ruby");
			modes.add("C#");
			return modes;
		}
	}
}
