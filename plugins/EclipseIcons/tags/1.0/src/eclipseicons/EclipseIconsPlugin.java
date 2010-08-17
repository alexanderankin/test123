
package eclipseicons;

import org.gjt.sp.jedit.EditPlugin;
import java.net.URL;
import java.util.HashMap;
import javax.swing.ImageIcon;

public class EclipseIconsPlugin extends EditPlugin {

    private static final HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();
    
    /**
     * Get an ImageIcon from the given filename.
     * @param filename The name of a file as shown in the documentation.  All that is
     * required is the filename, e.g. methpub_obj.gif, no path is necessary.
     * @return The corresponding ImageIcon or null if there is no icon for the filename.
     */
    public static ImageIcon getIcon(String filename) {
        ImageIcon icon = iconCache.get(filename);
        if (icon == null) {
            String name = "/icons/" + filename.substring(0, 1) + "/" + filename;
            URL url = EclipseIconsPlugin.class.getResource(name);
            if (url != null) {
                icon = new ImageIcon(url);
                if (icon != null) {
                    iconCache.put(filename, icon);
                }
            }
        }
        return icon;
    }
}