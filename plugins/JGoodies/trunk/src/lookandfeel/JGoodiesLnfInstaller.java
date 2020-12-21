
/*
 * JGoodiesLnfInstaller.java - Look And Feel plugin
 * Copyright (C) 2020 Dale Anson
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package lookandfeel;

import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import com.jgoodies.looks.plastic.*;
import com.jgoodies.looks.LookUtils;

/**
 * Base class for installing the JGoodies look and feel installers
 */
public abstract class JGoodiesLnfInstaller implements LookAndFeelInstaller {

    public String getName() {
        return "JGoodies";
    }

    public abstract String getLnfClassname();
    
    /**
     * Install the look and feel.
     * Can only use this look and feel on Windows, check for OS is handled in services.xml.
     */
    public void install() throws UnsupportedLookAndFeelException {
        String themeName = jEdit.getProperty( JGoodiesLookAndFeelPlugin.JGOODIES_THEME_PROP );
        if ( themeName == null ) {
            themeName = "SkyBlue";
        }
        try {
            String classname = "com.jgoodies.looks.plastic.theme." + themeName;
            Class themeClass = Class.forName(classname);
            PlasticTheme theme = (PlasticTheme)themeClass.newInstance();
            PlasticLookAndFeel.setPlasticTheme(theme);
            UIManager.setLookAndFeel( getLnfClassname() );
            UIManager.put( "ClassLoader", LookUtils.class.getClassLoader() );
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            // TODO: show a message, not available on current OS?
        }
    }

    /**
     * Returns a component used to configure the look and feel.
     */
    public AbstractOptionPane getOptionPane() {
        return new OptionComponent(this);
    }

}
