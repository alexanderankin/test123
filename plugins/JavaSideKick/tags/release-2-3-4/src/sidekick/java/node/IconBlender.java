package sidekick.java.node;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 * Combines multiple icons into a single icon.  I wrote this to apply the various
 * decorators to the Eclipse icons, but really could be used for just about any
 * ImageIcons.
 * @author Dale Anson, Sep 2006
 */
public class IconBlender {

    /**
     * Blend two icons together into a single icon.
     * @param observer some Component with a valid graphics context
     * @param bottom the icon that will be on the bottom of the two merged icons
     * @param bottomLocation one of SwingUtilities.NORTH_WEST, NORTH, NORTH_EAST,
     * WEST, CENTER, EAST, SOUTH_WEST, SOUTH, SOUTH_EAST.
     * @param top this icon will be placed on top of the bottom icon
     * @param topLocation same values as bottomLocation
     */
    public static ImageIcon blend( Component observer,
        ImageIcon bottom, int bottomLocation,
        ImageIcon top, int topLocation) {
        return IconBlender.blend(observer, bottom, bottomLocation, top, topLocation, null);
    }

    /**
     * Blend two icons together into a single icon.
     * @param observer some Component with a valid graphics context
     * @param bottom the icon that will be on the bottom of the two merged icons
     * @param bottomLocation one of SwingUtilities.NORTH_WEST, NORTH, NORTH_EAST,
     * WEST, CENTER, EAST, SOUTH_WEST, SOUTH, SOUTH_EAST.
     * @param top this icon will be placed on top of the bottom icon
     * @param topLocation same values as bottomLocation
     * @param minimumSize specify a minimum size for the created icon.  Can be null,
     * in which case, the blended icon will use the bottom and top icon sizes to
     * calculate a suitable size.
     */
    public static ImageIcon blend( Component observer,
        ImageIcon bottom, int bottomLocation,
        ImageIcon top, int topLocation,
        Dimension minimumSize) {

        // dumbness checks
        if (observer == null) {
            throw new NullPointerException("Component passed to IconBlender may not be null.");
        }
        if (bottom == null && top == null) {
            return null;
        }
        if (bottom == null) {
            return top;
        }
        if (top == null) {
            return bottom;
        }

        // calculate width and height of combined icons
        Image bottomImage = bottom.getImage();
        Image topImage = top.getImage();
        int bw = bottomImage.getWidth(observer);
        int bh = bottomImage.getHeight(observer);
        int tw = topImage.getWidth(observer);
        int th = topImage.getHeight(observer);
        int width = Math.max(bw, tw);
        int height = Math.max(bh, th);
        if (minimumSize != null) {
            width = Math.max(width, minimumSize.width);
            height = Math.max(height, minimumSize.height);
        }

        // create a new image that is just big enough
        Image newImage = observer.createImage( width, height );
        if ( newImage == null ) {
            return null;
        }

        // draw the bottom image
        Graphics g = newImage.getGraphics();
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, width, height);
        switch(bottomLocation) {
            case SwingUtilities.NORTH_WEST:
                g.drawImage( bottomImage, 0, 0, observer );
                break;
            case SwingUtilities.NORTH:
                g.drawImage( bottomImage, (width - bw)/2, 0, observer );
                break;
            case SwingUtilities.NORTH_EAST:
                g.drawImage( bottomImage, width - bw, 0, observer );
                break;
            case SwingUtilities.WEST:
                g.drawImage( bottomImage, 0, (height - bh)/2, observer );
                break;
            case SwingUtilities.EAST:
                g.drawImage( bottomImage, width - bw, (height - bh)/2, observer );
                break;
            case SwingUtilities.SOUTH_WEST:
                g.drawImage( bottomImage, 0, height - bh, observer );
                break;
            case SwingUtilities.SOUTH:
                g.drawImage( bottomImage, (width - bw)/2, height - bh, observer );
                break;
            case SwingUtilities.SOUTH_EAST:
                g.drawImage( bottomImage, width - bw, height - bh, observer );
                break;
            case SwingUtilities.CENTER:
            default:
                g.drawImage( bottomImage, (width - bw)/2, (height - bh)/2, observer );
                break;
        }
        switch(topLocation) {
            case SwingUtilities.NORTH_WEST:
                g.drawImage( topImage, 0, 0, observer );
                break;
            case SwingUtilities.NORTH:
                g.drawImage( topImage, (width - tw)/2, 0, observer );
                break;
            case SwingUtilities.NORTH_EAST:
                g.drawImage( topImage, width - tw, 0, observer );
                break;
            case SwingUtilities.WEST:
                g.drawImage( topImage, 0, (height - th)/2, observer );
                break;
            case SwingUtilities.EAST:
                g.drawImage( topImage, width - tw, (height - th)/2, observer );
                break;
            case SwingUtilities.SOUTH_WEST:
                g.drawImage( topImage, 0, height - th, observer );
                break;
            case SwingUtilities.SOUTH:
                g.drawImage( topImage, (width - tw)/2, height - th, observer );
                break;
            case SwingUtilities.SOUTH_EAST:
                g.drawImage( topImage, width - tw, height - th, observer );
                break;
            case SwingUtilities.CENTER:
            default:
                g.drawImage( topImage, (width - tw)/2, (height - th)/2, observer );
                break;
        }

        // clean up
        g.dispose();

        // create the combined icon
        return new ImageIcon(newImage);
    }
}
