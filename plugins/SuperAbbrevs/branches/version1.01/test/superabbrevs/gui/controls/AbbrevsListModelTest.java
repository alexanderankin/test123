package superabbrevs.gui.controls;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import superabbrevs.gui.controls.abbreviationlist.AbbrevsListModel;
import superabbrevs.model.Abbreviation;
import superabbrevs.model.Mode;


public class AbbrevsListModelTest {
	private Mode mode;
	private Abbreviation abbrev0;
	private Abbreviation abbrev1;
	private Abbreviation abbrev2;

	@Before 
	public void setup() {
		mode = new Mode("java");
		abbrev0 = new Abbreviation("For-loop", "for", "for (...) {}");
		mode.addAbbreviation(abbrev0);
		abbrev1 = new Abbreviation("While-loop", "while", "while (...) {}");
		mode.addAbbreviation(abbrev1);
		abbrev2 = new Abbreviation("If-statement", "if", "if (...) {}");
		mode.addAbbreviation(abbrev2);
	}
	
	
	@Test
	public void constructAbbrevsListModel() throws Exception {
		AbbrevsListModel model = new AbbrevsListModel(mode.getAbbreviations());
		Abbreviation element0 = model.getElementAt(0);
		assertEquals(abbrev0, element0);
		Abbreviation element1 = model.getElementAt(1);
		assertEquals(abbrev2, element1);
		Abbreviation element2 = model.getElementAt(2);
		assertEquals(abbrev1, element2);
	}
}
