/*
 * JTidyPlugin.java
 * Copyright (C) 2000, 2001, 2002 Andre Kaplan
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

package jtidy;

import java.io.*;
import java.util.Properties;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

public class JTidyPlugin extends EditPlugin {
    public static final String NAME = "jtidy";

    public void start() {
        JTidyErrorSourceWriter.start();
    }

    public void stop() {
        JTidyErrorSourceWriter.stop();
    }

    // updates the current plugin properties with the given properties
    public static void save(Properties props) {
        if (props == null) {
            return ;
        }
        try {
            File homeDir = jEdit.getPlugin("jtidy.JTidyPlugin").getPluginHome();
            if (!homeDir.exists()) {
                homeDir.mkdir();
            }
            Properties pluginProps = JTidyPlugin.getProperties();
            pluginProps.putAll(props);
            File pluginPropsFile = new File(homeDir, "jtidy.properties");
            BufferedWriter writer = new BufferedWriter(new FileWriter(pluginPropsFile));
            pluginProps.store(writer, "");
            writer.close();
        } catch (Exception ignored) {            // NOPMD
        }
    }
    
    /**
     * @return The currently set properties for this plugin or an empty Properties if
     * no settings are currently stored.
     */
    public static Properties getProperties() {
        try {
            Properties pluginProps = new Properties();
            File homeDir = jEdit.getPlugin("jtidy.JTidyPlugin").getPluginHome();
            File pluginPropsFile = new File(homeDir, "jtidy.properties");
            if (pluginPropsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(pluginPropsFile));
                pluginProps.load(reader);
                reader.close();
                return pluginProps;
            }
        } catch (Exception ignored) {            // NOPMD
        }
        return new Properties();
    }
}

