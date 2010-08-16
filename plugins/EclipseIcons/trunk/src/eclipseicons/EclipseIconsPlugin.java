
package eclipseicons;

import org.gjt.sp.jedit.EditPlugin;
import java.net.URL;
import java.util.HashMap;
import javax.swing.ImageIcon;

public class EclipseIconsPlugin extends EditPlugin {

    private static final HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();

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