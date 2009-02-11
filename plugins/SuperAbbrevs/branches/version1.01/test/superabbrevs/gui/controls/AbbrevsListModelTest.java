package superabbrevs.gui.controls;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import superabbrevs.gui.controls.abbreviationlist.AbbrevsListModel;
import superabbrevs.model.Abbrev;
import superabbrevs.model.Mode;


public class AbbrevsListModelTest {
	private Mode mode;
	private Abbrev abbrev0;
	private Abbrev abbrev1;
	private Abbrev abbrev2;

	@Before 
	public void setup() {
		mode = new Mode("java");
		abbrev0 = new Abbrev("For-loop", "for", "for (...) {}");
		mode.addAbbreviation(abbrev0);
		abbrev1 = new Abbrev("While-loop", "while", "while (...) {}");
		mode.addAbbreviation(abbrev1);
		abbrev2 = new Abbrev("If-statement", "if", "if (...) {}");
		mode.addAbbreviation(abbrev2);
	}
	
	
	@Test
	public void constructAbbrevsListModel() throws Exception {
		AbbrevsListModel model = new AbbrevsListModel(mode.getAbbreviations());
		Abbrev element0 = model.getElementAt(0);
		assertEquals(abbrev0, element0);
		Abbrev element1 = model.getElementAt(1);
		assertEquals(abbrev2, element1);
		Abbrev element2 = model.getElementAt(2);
		assertEquals(abbrev1, element2);
	}
}
