package sidekick.ecmascript.options;

import javax.swing.*;
import org.gjt.sp.jedit.*;

public class GeneralOptionPane extends AbstractOptionPane {

    private JCheckBox allNodes;

    public GeneralOptionPane() {
        super( "sidekick.ecmascript" );
    }

    protected void _init() {
        addComponent( allNodes = new JCheckBox( jEdit.getProperty(
                    "options.sidekick.ecmascript.general.allNodes.label" ) ) );
        boolean b = jEdit.getBooleanProperty( "sidekick.ecmascript.general.allNodes", false );
        allNodes.setSelected( b );
        System.setProperty("sidekick.ecmascript.general.allNodes", b ? "true" : "false");
    }

    protected void _save() {
        jEdit.setBooleanProperty( "sidekick.ecmascript.general.allNodes", allNodes.isSelected() );
    }
}
