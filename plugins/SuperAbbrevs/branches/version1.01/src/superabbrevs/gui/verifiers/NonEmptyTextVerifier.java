package superabbrevs.gui.verifiers;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public class NonEmptyTextVerifier extends InputVerifier {
	@Override
	public boolean verify(JComponent input) {
		return !"".equals(((JTextComponent)input).getText());
	}
}