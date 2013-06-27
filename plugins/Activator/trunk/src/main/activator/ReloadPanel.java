package activator;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;

import common.gui.util.ConstraintFactory;

import static activator.PluginManager.*;

public class ReloadPanel extends JPanel implements Observer {
    List<Plugin> plugins = new ArrayList<Plugin>();
    HashMap<File, Plugin> confirmed = new HashMap<File, Plugin>();
    ConstraintFactory cf = new ConstraintFactory();

    public ReloadPanel() {
        setLayout( new GridBagLayout() );
        setBackground( Color.GRAY );
        update();
        PluginList.getInstance().addObserver( this );
    }

    public void update( Observable o, Object arg ) {
        update();
    }

    public void update() {
        plugins.clear();
        removeAll();
        PluginList pl = PluginList.getInstance();
        int numPlugins = pl.size();
        PluginJAR jar;
        for ( int i = 0; i < numPlugins; ++i ) {
            Plugin plugin = pl.get( i );
            jar = plugin.getJAR();
            if ( jar == null || jar.getPlugin() == null ) {
                continue;
            }
            confirmed.put( plugin.getFile(), plugin );

        }

        plugins.addAll( confirmed.values() );
        Collections.sort( plugins, new PluginComparator() );
        int row = 0;

        for ( Plugin plugin : plugins ) {
            jar = plugin.getJAR();

            if ( jar.getPlugin() == null ) {
                continue;
            }
            int status = PluginManager.getStatus( jar );
            StringBuilder displayName = new StringBuilder();
            String display_name = plugin.toString();
            // TODO: use StringBuilder below. Actually a MessageFormat might be cleaner.
            switch ( status ) {
                case LOADED:
                    displayName.append("<html><font color=yellow>&#9830;</font> ").append(display_name);
                    break;
                case ACTIVATED:
                    displayName.append("<html><font color=green>&#9830;</font> ").append( display_name);
                    break;
                case NOT_LOADED:
                    displayName.append("<html><font color=gray>&#9830;</font> ").append(display_name);
                    break;
                case ERROR:
                    displayName.append("<html><font color=red>&#9830;</font> ").append(display_name);
                    break;
                default:
                    displayName.append(display_name);
            }
            JButton button = new JButton( new Reload( this, plugin, displayName.toString() ) );
            button.setToolTipText( jEdit.getProperty( "activator.Click_to_reload", "Click to reload:" ) + " " + plugin.toString() );
            button.setHorizontalAlignment( SwingConstants.LEFT );
            // add(name,cf.buildConstraints(0,row,1,1));
            add( button, cf.buildConstraints( 1, row, 1, 1 ) );
            row++;
        }
    }
}

