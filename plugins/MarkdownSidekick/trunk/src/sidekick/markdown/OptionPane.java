package sidekick.markdown;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

public class OptionPane extends AbstractOptionPane {

    private JCheckBox showParagraphs;
    private JCheckBox showQuotes;
    private JCheckBox showCode;

    public OptionPane() {
        super( "markdown" );
    }

    public void _init() {
        setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6 ) );

        showParagraphs = new JCheckBox(jEdit.getProperty("sidekick.markdown.showParagraphs.label", "Show Paragraphs"));
        showQuotes = new JCheckBox(jEdit.getProperty("sidekick.markdown.showQuotes.label", "Show Block Quotes"));
        showCode = new JCheckBox(jEdit.getProperty("sidekick.markdown.showCode.label", "Show Code Blocks"));
        
        showParagraphs.setSelected(jEdit.getBooleanProperty("sidekick.markdown.showParagraphs", true));
        showQuotes.setSelected(jEdit.getBooleanProperty("sidekick.markdown.showQuotes", true));
        showCode.setSelected(jEdit.getBooleanProperty("sidekick.markdown.showCode", true));
        
        
        addComponent(showParagraphs);
        addComponent(showQuotes);
        addComponent(showCode);
    }

    public void _save() {
    	jEdit.setBooleanProperty("sidekick.markdown.showParagraphs", showParagraphs.isSelected());
    	jEdit.setBooleanProperty("sidekick.markdown.showQuotes", showQuotes.isSelected());
    	jEdit.setBooleanProperty("sidekick.markdown.showCode", showCode.isSelected());
    }
}