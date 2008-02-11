
package jdiff.component;

import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

import jdiff.DualDiff;
import jdiff.LineProcessor;
import jdiff.component.DiffLineModel;
import jdiff.component.ui.*;

import org.gjt.sp.jedit.View;

public class DiffLineOverview extends JComponent implements LineProcessor {

    private static final String uiClassID = "DiffLineOverviewUI";

    private DualDiff dualDiff = null;
    private DiffLineModel diffLineModel = null;

    public DiffLineOverview( DualDiff dualDiff ) {
        if ( dualDiff == null ) {
            throw new IllegalArgumentException();
        }
        this.dualDiff = dualDiff;
        this.updateUI();
    }

    public void processLines( String leftLine, String rightLine ) {
        if ( leftLine == null || rightLine == null ) {
            return ;
        }
        setModel( new DiffLineModel( leftLine, rightLine ) );
        repaint();
    }

    public void setUI( DiffLineOverviewUI ui ) {
        super.setUI( ui );
    }

    public void updateUI() {
        if ( UIManager.get( getUIClassID() ) != null ) {
            setUI( ( DiffLineOverviewUI ) UIManager.getUI( this ) );
        }
        else {
            setUI( new BasicDiffLineOverviewUI() );
        }
    }

    public DiffLineOverviewUI getUI() {
        return ( DiffLineOverviewUI ) ui;
    }

    public String getUIClassID() {
        return uiClassID;
    }

    public View getView() {
        return dualDiff.getView();
    }

    public void setModel( DiffLineModel model ) {
        diffLineModel = model;
    }

    public DiffLineModel getModel() {
        return diffLineModel;
    }

    @Override
    public Color getBackground() {
        return dualDiff.getBackground();
    }

    @Override
    public Font getFont() {
        return dualDiff.getFont();
    }
}
