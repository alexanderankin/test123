package columnruler;

import java.awt.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;

public class LineGuidesOptions extends AbstractOptionPane {
	private JCheckBox caretGuide;
	private JCheckBox wrapGuide;

	public LineGuidesOptions() {
		super("columnruler.lineguides");
	}
	protected void _init() {
		caretGuide = new JCheckBox("Show caret guide",jEdit.getBooleanProperty("options.columnruler.guides.caret"));
		wrapGuide = new JCheckBox("Show wrap guide",jEdit.getBooleanProperty("view.wrapGuide"));
		addComponent(caretGuide);
		addComponent(wrapGuide);
	}

	protected void _save() {
		jEdit.setBooleanProperty("options.columnruler.guides.caret",caretGuide.isSelected());
		jEdit.setBooleanProperty("view.wrapGuide",wrapGuide.isSelected());
	}
}
