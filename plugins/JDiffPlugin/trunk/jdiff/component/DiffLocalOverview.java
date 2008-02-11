
package jdiff.component;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import jdiff.component.ui.*;

public class DiffLocalOverview extends JComponent {
    private static final String uiClassID = "DiffLocalOverviewUI";

    public void setUI( DiffLocalOverviewUI ui ) {
        super.setUI( ui );
    }

    public void updateUI() {
        if ( UIManager.get( getUIClassID() ) != null ) {
            setUI( ( DiffLocalOverviewUI ) UIManager.getUI( this ) );
        }
        else {
            setUI( new BasicDiffLocalOverviewUI() );
        }
    }

    public DiffLocalOverviewUI getUI() {
        return ( DiffLocalOverviewUI ) ui;
    }

    public String getUIClassID() {
        return uiClassID;
    }
}
