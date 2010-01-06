package imageviewer;

import java.awt.*;
import java.awt.datatransfer.*;

/**
 * A Transferable for copying an image to the system clipboard.
 */
public class ImageSelection implements Transferable {
    private Image image;
    
    /**
     * Copy the given image to the system clipboard.
     * @param image The image to copy.
     */
    public static void copyImageToClipboard( Image image ) {
        ImageSelection imageSelection = new ImageSelection( image );
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.getSystemClipboard().setContents( imageSelection, null );
    }

    public ImageSelection( Image image ) {
        this.image = image;
    }

    public Object getTransferData( DataFlavor flavor ) throws UnsupportedFlavorException {
        if ( flavor.equals( DataFlavor.imageFlavor ) == false ) {
            throw new UnsupportedFlavorException( flavor );
        }
        return image;
    }

    public boolean isDataFlavorSupported( DataFlavor flavor ) {
        return flavor.equals( DataFlavor.imageFlavor );
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
                   DataFlavor.imageFlavor
               };
    }
}