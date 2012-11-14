package com.lipstikLF.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

public class LipstikGradients implements Paint
{
  /**
   * The start color of the gradient.
   */
  private int startColor;

  /**
   * The end color of the gradient.
   */
  private int endColor;

  /**
   * Is <code>true</code> if the gradient is vertical otherwise horizontal.
   */
  private boolean isVertical;

  /**
   * Is <code>true</code> if the gradient's transparency is ascending.
   *
   private boolean isAscending;
   */
  
  /**
   * Creates a new gradient.
   *
   * @param start The start color of the gradient.
   * @param end The end color of the gradient.
   * @param isVertical If the gradient should be vertical or horizontal.
   */
  public LipstikGradients(int start, int end, boolean isVertical)
  {
    this.startColor = start;
    this.endColor = end;
    this.isVertical = isVertical;
  }

  /**
   * Creates and returns a PaintContext used to generate the color pattern.
   *
   * @param cm The ColorModel that receives the <code>Paint</code> data.
   *           This is used only as a hint.
   * @param r The device space bounding box of the graphics primitive being
   *          rendered.
   * @param r2d The user space bounding box of the graphics primitive being
   *             rendered.
   * @param xform The AffineTransform from user space into device space.
   * @param hints The hint that the context object uses to choose between
   *              rendering alternatives.
   * @return The <code>PaintContext</code> for generating color patterns.
   */
  public synchronized PaintContext createContext(ColorModel cm, Rectangle r,
    Rectangle2D r2d, AffineTransform xform, RenderingHints hints)
  {
    return new FastGradientPaintContext(cm, r, startColor, endColor, isVertical);
  }

  /**
   * Gets the transparency of this gradient.
   *
   * @return <code>TRANSLUCENT</code> id the end and start colors have an alpha
   * channel otherwise <code>OPAQUE</code>.
   */
  public int getTransparency()
  {
    return OPAQUE;
    //((((startColor & endColor) >> 24) & 0xFF) == 0xFF)
    //  ? OPAQUE
    //  : TRANSLUCENT;  
  }

  /**
   * Draws a gradient on the given rectangle.
   *
   * @param g The graphics context.
   * @param start The start color of the gradient.
   * @param end The end color of the gradient.
   * @param w width
   * @param h height 
   * @param isVertical If the gradient should be vertical or horizontal.
   */
  public static void drawGradient(Graphics g, Color start, Color end, int x, int y, int w, int h, boolean isVertical)
  {
    Graphics2D g2D = (Graphics2D)g;    
    Paint gradient = new LipstikGradients(start.getRGB(), (end!=null) ? end.getRGB() : 0, isVertical);
    g2D.setPaint(gradient);
    g2D.fillRect(x,y,w,h);
  }
}