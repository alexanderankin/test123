/*
Copyright (c) 2009, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package imageviewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.text.*;
import java.util.*;
import javax.swing.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

/**
 * A simple image viewer.  Provides scrolling and zooming, and that's it. The
 * zoom in is unrestricted, which means is is possible to run out of memory.
 * Zoom out stops at 2 x 2 pixels.
 * TODO: is a zoom percent spinner necessary?  Right now, all images are loaded
 * at 100%.  With a percent spinner, the initial size could be based on the
 * current spinner value, which might be handy when scanning a directory of
 * small icons or large images.
 */
public class ImageViewer extends JPanel {
    private String filename;
    private JLabel imageLabel;
    private JLabel filenameLabel;
    private JLabel imagesizeLabel;
    private JViewport viewport;
    private JPanel toolbar;
    private JToolBar buttonPanel;
    private JButton zoomIn;
    private JButton zoomOut;
    private JButton rotateCCW;
    private JButton rotateCW;
    private JButton reload;
    private JButton clear;
    private JButton copy;
    private float originalWidth = 0.0f;
    private float originalHeight = 0.0f;
    private float zoomWidth = 0.0f;
    private float zoomHeight = 0.0f;

    private Image image = null;

    public static final double CW90 = 90.0;

    public ImageViewer() {
        installComponents();
        installListeners();
    }

    // create and layout the components
    private void installComponents() {
        setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
        setLayout(new BorderLayout());

        // use a JLabel to actually display the image
        imageLabel = new JLabel();
        imageLabel.setVerticalTextPosition(JLabel.BOTTOM);
        imageLabel.setHorizontalTextPosition(JLabel.CENTER);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scroller = new JScrollPane(imageLabel);
        viewport = scroller.getViewport();
        add(scroller, BorderLayout.CENTER);

        // use another JLabel to show the name of the file being shown
        filenameLabel = new JLabel();
        imagesizeLabel = new JLabel();

        // set up the zoom buttons
        clear = new JButton(GUIUtilities.loadIcon("22x22/actions/edit-clear.png"));
        clear.setToolTipText(jEdit.getProperty("imageviewer.clear", "Clear"));
        copy = new JButton(GUIUtilities.loadIcon("22x22/actions/edit-copy.png"));
        copy.setToolTipText(jEdit.getProperty("imageviewer.copy", "Copy"));
        zoomIn = new JButton(GUIUtilities.loadIcon("22x22/actions/zoom-in.png"));
        zoomIn.setToolTipText(jEdit.getProperty("imageviewer.zoomin", "Zoom In"));
        zoomOut = new JButton(GUIUtilities.loadIcon("22x22/actions/zoom-out.png"));
        zoomOut.setToolTipText(jEdit.getProperty("imageviewer.zoomout", "Zoom Out"));
        ImageIcon cwRotateIcon = (ImageIcon)GUIUtilities.loadIcon("22x22/actions/edit-redo.png");
        ImageIcon ccwRotateIcon = new ImageIcon(mirror(cwRotateIcon.getImage()));
        rotateCCW = new JButton(ccwRotateIcon);
        rotateCCW.setToolTipText(jEdit.getProperty("imageviewer.rotateCCW", "Rotate counter-clockwise"));
        rotateCW = new JButton(cwRotateIcon);
        rotateCW.setToolTipText(jEdit.getProperty("imageviewer.rotateCW", "Rotate clockwise"));
        reload = new JButton(GUIUtilities.loadIcon("22x22/actions/view-refresh.png"));
        reload.setToolTipText(jEdit.getProperty("imageviewer.reload", "Reload"));

        // create toolbar
        buttonPanel = new JToolBar();
        buttonPanel.setFloatable(false);
        buttonPanel.add(clear);
        buttonPanel.add(copy);
        buttonPanel.add(zoomIn);
        buttonPanel.add(zoomOut);
        buttonPanel.add(rotateCCW);
        buttonPanel.add(rotateCW);
        buttonPanel.add(reload);

        // inner panel for the filename and image size
        JPanel dataPanel = new JPanel(new GridLayout(2, 1));
        dataPanel.add(filenameLabel);
        dataPanel.add(imagesizeLabel);

        // create a panel for the toolbar
        toolbar = new JPanel(new BorderLayout());
        toolbar.add(dataPanel, BorderLayout.CENTER);
        toolbar.add(buttonPanel, BorderLayout.EAST);

        // add the toolbar panel
        add(toolbar, BorderLayout.NORTH);
    }

    // add any listeners necessary for the installed components
    private void installListeners() {
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                clear();
            }
        }
       );

        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                copy();
            }
        }
       );

        zoomIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                zoomIn();
            }
        }
       );

        zoomOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                zoomOut();
            }
        }
       );

        rotateCCW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                rotateCCW();
            }
        }
       );

        rotateCW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                rotateCW();
            }
        }
       );

        reload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                reload();
            }
        }
       );

        viewport.addMouseMotionListener(mouseAdapter);
        viewport.addMouseListener(mouseAdapter);
        viewport.addMouseWheelListener(mouseAdapter);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent ce) {
                if (ce.getComponent().equals(ImageViewer.this)) {
                    filenameLabel.setText(compressFilename(filename));
                }
            }
        }
       );
    }

    protected JViewport getViewport() {
        return viewport;
    }

    protected JLabel getImageLabel() {
        return imageLabel;
    }

    /*
     * MouseWheel zooms in/out.
     * Mouse drag moves viewport.
     * Double click centers point clicked.
    */
    MMouseAdapter mouseAdapter = new MMouseAdapter() {
        Point previous = null;
        Cursor oldCursor = null;

        /**
         * Default implementation moves the view port in the parent ImageViewer.
         */
        public void mouseDragged(MouseEvent me) {
            if (previous == null) {
                previous = me.getPoint();
                return ;
            }
            Point now = me.getPoint();
            int dx = previous.x - now.x;
            int dy = previous.y - now.y;
            Point current = ImageViewer.this.viewport.getViewPosition();
            Point to = new Point(current.x + dx, current.y + dy);
            ImageViewer.this.viewport.setViewPosition(to);
            previous = now;
        }

        /**
         * Default implementation centers the image in the parent ImageViewer.
         */
        public void mousePressed(MouseEvent me) {
            previous = me.getPoint();
            oldCursor = ImageViewer.this.imageLabel.getCursor();
            ImageViewer.this.imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }

        public void mouseReleased(MouseEvent me) {
            previous = null;
            ImageViewer.this.imageLabel.setCursor(oldCursor != null ? oldCursor : Cursor.getDefaultCursor());
        }

        public void mouseClicked(MouseEvent me) {
            if (me.getClickCount() == 2) {
                ImageViewer.this.center(me.getPoint());
            }
        }

        public void mouseWheelMoved(MouseWheelEvent me) {
            if (me.getWheelRotation() > 0) {
                ImageViewer.this.zoomIn();
            } else {
                ImageViewer.this.zoomOut();
            }
        }
    } ;

    /**
     * Display the image in this ImageViewer.
     * @param filename the name of the image file to display.
     */
    public void showImage(String filename) {
        showImage(filename, false);
    }

    public void showImage(String filename, boolean reload) {
        if (! reload && filename.equals(filenameLabel.getText())) {
            // already showing this image
            return ;
        }
        this.filename = filename;
        if (isValidFilename(filename)) {
            image = Toolkit.getDefaultToolkit().createImage(filename);
            ImageIcon icon = new ImageIcon(image);
            originalWidth = (float) icon.getIconWidth();
            originalHeight = (float) icon.getIconHeight();
            zoomWidth = originalWidth;
            zoomHeight = originalHeight;
            imageLabel.setIcon(icon);
            imageLabel.setSize((int) originalWidth, (int) originalHeight);
            filenameLabel.setText(compressFilename(filename));
            imagesizeLabel.setText((int) originalWidth + "x" + (int) originalHeight);
            refresh();
        }
    }

    protected void refresh() {
        invalidate();
        validate();
    }

    /**
     * @return true if the filename, regardless of case, ends with .jpg, .gif, or .png.
     */
    public static boolean isValidFilename(String filename) {
        if (filename == null) {
            return false;
        }
        String name = filename.toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif") || name.endsWith(".png");
    }

    protected void center(Point p) {
        int cx = viewport.getWidth() / 2;
        int cy = viewport.getHeight() / 2;

        int dx = p.x - cx;
        int dy = p.y - cy;
        Point current = viewport.getViewPosition();
        Point to = new Point(current.x + dx, current.y + dy);
        viewport.setViewPosition(to);
    }

    /**
     * Action to clear the ImageViewer.
     */
    public void clear() {
        imageLabel.setIcon(null);
        ImageViewer.this.refresh();
    }

    /**
     * Action to copy the image to the system clipboard.
     */
    public void copy() {
        ImageSelection.copyImageToClipboard(image);
    }

    /**
     * Action to reload the current image from disk.
     */
    public void reload() {
        showImage(filename, true);

    }

    /**
     * Action to zoom in 10%.
     */
    public void zoomIn() {
        float width = zoomWidth * 1.1f;
        float height = zoomHeight * 1.1f;
        zoom(width, height);
    }

    /**
     * Action to zoom out 10%.
     */
    public void zoomOut() {
        float width = zoomWidth * 0.9f;
        float height = zoomHeight * 0.9f;
        if (width < 1.0 || height < 1.0) {
            return ;
        }
        zoom(width, height);
    }

    /**
     * Zoom an image to the given width and height and refresh the display.
     * @param width the desired width
     * @param height the desired height
     */
    protected void zoom(float width, float height) {
        zoomWidth = width;
        zoomHeight = height;
        if (width > 0 && height > 0) {
            Image zoomImage = getScaledImage(image, (int) width, (int) height);
            ImageIcon icon = new ImageIcon(zoomImage);
            imageLabel.setIcon(icon);
            imageLabel.setSize((int) width, (int) height);
            refresh();
        }
    }

    /**
     * Resizes an image to the given width and height.
     * @param image source image to scale
     * @param width desired width
     * @param height desired height
     * @return the resized image
     */
    protected Image getScaledImage(Image image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImage.createGraphics();
        Map<RenderingHints.Key, Object> renderingHints = new HashMap<RenderingHints.Key, Object>();
        renderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        renderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHints(renderingHints);
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
        return resizedImage;
    }

    protected void rotateCCW() {
        // TODO: figure out why I can't rotate counter-clockwise.  It seems like
        // the rotation value has to be less than pi and greater than 0.
        // Three clockwise rotations works the same, though.
        rotateCW();
        rotateCW();
        rotateCW();
    }

    protected void rotateCW() {
        Image rotatedImage = rotate(CW90);
        ImageIcon icon = new ImageIcon(rotatedImage);
        imageLabel.setIcon(icon);
        imageLabel.setSize(rotatedImage.getWidth(null), rotatedImage.getHeight(null));
        image = rotatedImage;
        refresh();
    }

    /**
     * This is not a general rotate routine.  The transformations assume 90 degree
     * rotations.
     * @param degrees Rotation in degrees.  Only 90 degrees seems to work well.
     */
    protected Image rotate(double degrees) {
        double amount = Math.toRadians(degrees);
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        try {
            mt.waitForID(0);
        } catch (InterruptedException ie) {
        }

        BufferedImage sourceBI = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D) sourceBI.getGraphics();
        g.drawImage(image, 0, 0, null);

        AffineTransform at = new AffineTransform();

        // rotate around image center
        at.rotate(amount, sourceBI.getWidth() / 2.0, sourceBI.getHeight() / 2.0);

        // translate to make sure the rotation doesn't cut off any image data
        AffineTransform translationTransform = findTranslation(at, sourceBI);
        at.preConcatenate(translationTransform);

        // instantiate and apply affine transformation filter
        BufferedImageOp bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return bio.filter(sourceBI, null);
    }

    // find proper translations to keep rotated image correctly displayed
    private AffineTransform findTranslation(AffineTransform at, BufferedImage bi) {
        Point2D p2din, p2dout;

        p2din = new Point2D.Double(0.0, 0.0);
        p2dout = at.transform(p2din, null);
        double ytrans = p2dout.getY();

        p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
        p2dout = at.transform(p2din, null);
        double xtrans = p2dout.getX();

        AffineTransform tat = new AffineTransform();
        tat.translate(- xtrans, - ytrans);
        return tat;
    }
    
    /**
     * All this is used for is to make the rotate clockwise icon into a
     * rotate counter-clockwise icon.  I suppose I could use it as the action
     * for another button on the toolbar.
     */
    protected Image mirror(Image img) {
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics gb = bufferedImage.getGraphics();
        gb.drawImage(img, 0, 0, null);
        gb.dispose();

        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);
        return bufferedImage;
    }

    /**
     * Takes a long filename and converts it to a smaller string with portions removed
     * from the middle so the filename fits in a display area of a certain width.  For
     * example, given a name like "/home/username/mypictures/myfamilypictures/awesomegrouphug.jpg",
     * this method might return "/home/username/.../awesomegrouphug.jpg".  The filename
     * itself will never be removed.
     * @param filename The filename to compress, if necessary, with an ellipsis in the middle.
     */
    private String compressFilename(String filename) {
        if (filename == null) {
            return "";
        }
        int width = toolbar.getWidth() - buttonPanel.getWidth() - 6;
        FontMetrics fm = getGraphics().getFontMetrics();
        if (fm == null) {
            return filename;
        }
        int stringWidth = fm.stringWidth(filename);
        if (stringWidth <= width) {
            return filename;
        }

        BreakIterator bi = BreakIterator.getWordInstance();
        bi.setText(filename);
        java.util.List<String> parts = new ArrayList<String>();
        int start = bi.first();
        for (int end = bi.next(); end != BreakIterator.DONE; start = end, end = bi.next()) {
            parts.add(filename.substring(start, end));
        }

        int middle = parts.size() / 2;
        java.util.List<String> startList = new ArrayList<String>(parts.subList(0, middle));
        java.util.List<String> endList = new ArrayList<String>(parts.subList(middle, parts.size()));
        String beginning = listToString(startList);
        String ending = listToString(endList);

        String combined = beginning + "..." + ending;
        while (fm.stringWidth(combined) > width) {
            if (beginning.length() >= ending.length() || endList.size() == 1) {
                startList.remove(startList.size() - 1);
                beginning = listToString(startList);
            } else {
                endList.remove(0);
                ending = listToString(endList);
            }
            combined = beginning + "..." + ending;
        }
        return combined;
    }

    private String listToString(java.util.List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String part : list) {
            sb.append(part);
        }
        return sb.toString();
    }
}